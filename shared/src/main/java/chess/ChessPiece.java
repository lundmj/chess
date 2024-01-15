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
        throw new RuntimeException("Not implemented");
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
        if (this.type == PieceType.BISHOP)
            return bishopMoves(board);
        else if (this.type == PieceType.KNIGHT)
            return knightMoves(board);
        else if (this.type == PieceType.ROOK)
            return rookMoves(board);
        else if (this.type == PieceType.PAWN)
            return pawnMoves(board);
        else if (this.type == PieceType.QUEEN)
            return queenMoves(board);
        else if (this.type == PieceType.KING)
            return kingMoves(board);
        else return null;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board) {
        return new ArrayList<>();
    }
    private Collection<ChessMove> knightMoves(ChessBoard board) {
        return new ArrayList<>();
    }
    private Collection<ChessMove> rookMoves(ChessBoard board) {
        return new ArrayList<>();
    }
    private Collection<ChessMove> pawnMoves(ChessBoard board) {
        return new ArrayList<>();
    }
    private Collection<ChessMove> queenMoves(ChessBoard board) {
        return new ArrayList<>();
    }
    private Collection<ChessMove> kingMoves(ChessBoard board) {
        return new ArrayList<>();
    }


}
