package chess;

import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import chess.ChessGame.TeamColor;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final PieceType type;
    public ChessPiece(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        this.color = color;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public String getSymbol() {
        return switch (getPieceType()){
            case PieceType.BISHOP -> "B";
            case PieceType.KNIGHT -> "N";
            case PieceType.ROOK -> "R";
            case PieceType.PAWN -> "P";
            case PieceType.QUEEN -> "Q";
            case PieceType.KING -> "K";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        // Controller to decide which piece moves function to call.
        return switch (this.type) {
            case PieceType.BISHOP -> bishopMoves(board, position);
            case PieceType.KNIGHT -> knightMoves(board, position);
            case PieceType.ROOK -> rookMoves(board, position);
            case PieceType.PAWN -> pawnMoves(board, position);
            case PieceType.QUEEN -> queenMoves(board, position);
            case PieceType.KING -> kingMoves(board, position);
        };
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        ArrayList<ChessMove> moves = new ArrayList<>();

        for (int distance = 1; distance <= 7; distance++) { // up-right
            ChessPosition checkPosition = new ChessPosition(row + distance, col + distance);
            if (checkIfBlockedAndAddPosition(board, position, checkPosition, moves))
                break;
        }
        for (int distance = 1; distance <= 7; distance++) { // down-right
            ChessPosition checkPosition = new ChessPosition(row - distance, col + distance);
            if (checkIfBlockedAndAddPosition(board, position, checkPosition, moves))
                break;
        }
        for (int distance = 1; distance <= 7; distance++) { // down-left
            ChessPosition checkPosition = new ChessPosition(row - distance, col - distance);
            if (checkIfBlockedAndAddPosition(board, position, checkPosition, moves))
                break;
        }
        for (int distance = 1; distance <= 7; distance++) { // up-left
            ChessPosition checkPosition = new ChessPosition(row + distance, col - distance);
            if (checkIfBlockedAndAddPosition(board, position, checkPosition, moves))
                break;
        }

        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        ArrayList<ChessMove> moves = new ArrayList<>();
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row+2, col+1), moves); // 1:00
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row+1, col+2), moves); // 2:00
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row-1, col+2), moves); // 4:00
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row-2, col+1), moves); // 5:00
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row-2, col-1), moves); // 7:00
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row-1, col-2), moves); // 8:00
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row+1, col-2), moves); // 10:00
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row+2, col-1), moves); // 11:00
        return moves;
    }
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        ArrayList<ChessMove> moves = new ArrayList<>();

        for (int distance = 1; distance <= 7; distance++) { // up
            ChessPosition checkPosition = new ChessPosition(row + distance, col);
            if (checkIfBlockedAndAddPosition(board, position, checkPosition, moves))
                break;
        }
        for (int distance = 1; distance <= 7; distance++) { // right
            ChessPosition checkPosition = new ChessPosition(row, col + distance);
            if (checkIfBlockedAndAddPosition(board, position, checkPosition, moves))
                break;
        }
        for (int distance = 1; distance <= 7; distance++) { // down
            ChessPosition checkPosition = new ChessPosition(row - distance, col);
            if (checkIfBlockedAndAddPosition(board, position, checkPosition, moves))
                break;
        }
        for (int distance = 1; distance <= 7; distance++) { // left
            ChessPosition checkPosition = new ChessPosition(row, col - distance);
            if (checkIfBlockedAndAddPosition(board, position, checkPosition, moves))
                break;
        }

        return moves;
    }
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        ArrayList<ChessMove> moves = new ArrayList<>();
        boolean isWhite = getTeamColor() == TeamColor.WHITE;
        int rowStep = (isWhite) ? 1 : -1; // to decide if the piece moves up or down
        int rowForDoubleMove = (isWhite) ? 2 : 7; // which row does a pawn need to be on to be able to double advance
        int rowForPromotion = (isWhite) ? 8 : 1;

        ChessPosition advance = new ChessPosition(row+rowStep, col);
        ChessPosition advance_left = new ChessPosition(row+rowStep, col-1);
        ChessPosition advance_right = new ChessPosition(row+rowStep, col+1);
        if (inBoard(advance) && board.getPiece(advance) == null) {
            doPawnMove(moves, position, advance, rowForPromotion); // TODO: MUST ADD PROMOTION
        }
        if (inBoard(advance_left) && board.getPiece(advance_left) != null
                && board.getPiece(advance_left).getTeamColor() != getTeamColor()) {
            doPawnMove(moves, position, advance_left, rowForPromotion); // TODO: MUST ADD PROMOTION
        }
        if (inBoard(advance_right) && board.getPiece(advance_right) != null
                && board.getPiece(advance_right).getTeamColor() != getTeamColor()) {
            doPawnMove(moves, position, advance_right, rowForPromotion); // TODO: MUST ADD PROMOTION
        }

        if (position.getRow() == rowForDoubleMove) {
            ChessPosition doubleAdvance = new ChessPosition(row+(2*rowStep), col);
            if (board.getPiece(advance) == null && board.getPiece(doubleAdvance) == null) {
                doPawnMove(moves, position, doubleAdvance, rowForPromotion);
            }
        }

        return moves;
    }
    private void doPawnMove(ArrayList<ChessMove> moves, ChessPosition position, ChessPosition dest, int rowForPromotion) {
        if (dest.getRow() != rowForPromotion) {
            moves.add(new ChessMove(position, dest, null));
        } else {
            moves.add(new ChessMove(position, dest, PieceType.QUEEN));
            moves.add(new ChessMove(position, dest, PieceType.ROOK));
            moves.add(new ChessMove(position, dest, PieceType.BISHOP));
            moves.add(new ChessMove(position, dest, PieceType.KNIGHT));
        }
    }
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition position) { // Literally just rook + bishop
        ArrayList<ChessMove> moves = new ArrayList<>(rookMoves(board, position));
        moves.addAll(bishopMoves(board, position));
        return moves;
    }
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        ArrayList<ChessMove> moves = new ArrayList<>();
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row+1, col-1), moves);
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row+1, col), moves);
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row+1, col+1), moves);
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row, col-1), moves);
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row, col+1), moves);
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row-1, col-1), moves);
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row-1, col), moves);
        checkIfBlockedAndAddPosition(board, position, new ChessPosition(row-1, col+1), moves);
        return moves;
    }
    private boolean inBoard(ChessPosition pos) {
        return (1 <= pos.getRow() && pos.getRow() <= 8 &&
                1 <= pos.getColumn() && pos.getColumn() <= 8);
    }
    private boolean checkIfBlockedAndAddPosition(ChessBoard board, ChessPosition position, ChessPosition checkPosition, ArrayList<ChessMove> moves) {
        // Returns true if the space is clear
        if (!inBoard(checkPosition))
            return true;
        if (board.getPiece(checkPosition) == null) {
            moves.add(new ChessMove(position, checkPosition, null));
            return false;
        }
        if (board.getPiece(checkPosition).getTeamColor() != this.color) {
            moves.add(new ChessMove(position, checkPosition, null));
        }
        return true;
    }
}
