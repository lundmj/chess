package chess;

import java.util.ArrayList;
import java.util.Collection;

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

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
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
        return new ArrayList<>();
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
        return new ArrayList<>();
    }
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition position) {
        return new ArrayList<>();
    }
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition position) {
        return new ArrayList<>();
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
