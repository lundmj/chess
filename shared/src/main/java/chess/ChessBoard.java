package chess;

import java.util.Arrays;
import java.util.Iterator;

import static chess.EscapeSequences.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board;

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
    }

    public ChessBoard(ChessBoard original) {
        this.board = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (original.board[i][j] != null) {
                    this.board[i][j] = original.board[i][j].clone(); // Assuming ChessPiece has a clone method
                }
            }
        }
    }

    // Clone method

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public void removePiece(ChessPosition position) {
        board[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board[position.getRow() - 1][position.getColumn() - 1];
    }

    public void makeMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = getPiece(start);
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        addPiece(end, piece);
        removePiece(start);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i <= 7; i++){
            for (int j = 0; i <= 7; i++){
                board[i][j] = null;
            }
        }
        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        for (int i = 0; i <= 7; i++){
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }

        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        for (int i = 0; i <= 7; i++){
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (ChessPiece[] row : board) {
            for (ChessPiece piece : row) {
                if (piece == null) {
                    output.append("| ");
                } else {
                    output.append(String.format("|%s", piece.getSymbol()));
                }
            }
            output.append("|\n");
        }
        return output.toString();
    }
    public String getWhitePerspective() {
        ChessPiece[][] inverted = this.invert();
        return getString(inverted);
    }
    public String getBlackPerspective() {
        return getString(board);
    }
    private String getString(ChessPiece[][] myBoard) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < myBoard.length; i++) {
            for (int j = 0; j < myBoard[i].length; j++) {
                output.append((j % 2 == i % 2) ? SET_BG_COLOR + "1m" : SET_BG_COLOR_LIGHT_GREY);
                ChessPiece piece = myBoard[i][j];
                if (piece != null) output.append(piece);
                else output.append(EMPTY);
                output.append(RESET_BG_COLOR);
            }
            output.append('\n');
        }
        return output.toString();
    }


    private ChessPiece[][] invert() {
        ChessPiece[][] inverted = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                inverted[i][j] = board[7-i][7-j];
            }
        }
        return inverted;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public BoardIterator iterator() {
        return new BoardIterator();
    }
    public static class BoardIterator implements Iterator {

        private int row = 1;
        private int col = 0;
        @Override
        public boolean hasNext() {
            return col < 8 || row < 8;
        }
        @Override
        public ChessPosition next() {
            if (col < 8) {
                col++;
            } else {
                col = 1;
                row++;
            }
            return new ChessPosition(row, col);
        }
    }
}