package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param position the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        Collection<ChessMove> moves = piece.pieceMoves(board, position);
        moves.removeIf(move -> !isMoveValid(move));
        return moves;
    }

    private boolean isMoveValid(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            return false;
        }
        ChessBoard simulatedBoard = new ChessBoard(board); // clone
        simulatedBoard.makeMove(move); // Note this is not the same as the method in this class
        return !isInCheckSimulated(piece.getTeamColor(), simulatedBoard);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException(String.format("Move %s invalid.", move));
        }
        if (isMoveValid(move)) {
            board.makeMove(move);
        } else {
            throw new InvalidMoveException(String.format("Move %s invalid.", move));
        }
        setTeamTurn((getTeamTurn() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE);


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
         ChessBoard.BoardIterator boardIterator = board.iterator();
         while (boardIterator.hasNext()) {
             ChessPosition pos = boardIterator.next();
             ChessPiece piece = board.getPiece(pos);
             if (piece == null || piece.getTeamColor() == teamColor)
                 continue;
             // now we know we have a piece that is the other team's color
             for (ChessMove move : piece.pieceMoves(board, pos)) {
                 ChessPiece targetPiece = board.getPiece(move.getEndPosition());
                 if (targetPiece != null &&
                     targetPiece.getPieceType() == ChessPiece.PieceType.KING &&
                     targetPiece.getTeamColor() == teamColor) {
                     return true;
                 }
             }
         }
         return false;
    }

    /**
     * Checks to see if a simulated board would leave a color in check.
     * Temporarily modifies this.board, but switches it back after.
     */
    private boolean isInCheckSimulated(TeamColor teamColor, ChessBoard simulatedBoard) {
        ChessBoard oldBoard = this.board;
        this.board = simulatedBoard; // make board be the board with the potential move
        boolean wouldBeInCheck = isInCheck(teamColor);
        this.board = oldBoard; // restore board
        return wouldBeInCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (isInCheck(teamColor)) && (cannotMove(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (!isInCheck(teamColor)) && (cannotMove(teamColor)) && (getTeamTurn() == teamColor);
    }

    /**
     * Returns whether the given team is unable to make any moves.
     * Useful for testing checkmate and stalemate.
     */
    private boolean cannotMove(TeamColor teamColor) {
        ChessBoard.BoardIterator boardIterator = board.iterator();
        while (boardIterator.hasNext()) {
            ChessPosition pos = boardIterator.next();
            ChessPiece piece = board.getPiece(pos);
            if (piece == null || piece.getTeamColor() != teamColor)
                continue;
            if (!validMoves(pos).isEmpty())
                return false;
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
