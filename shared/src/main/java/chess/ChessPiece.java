package chess;

import chess.moveCalculators.BishopCalculator;
import chess.moveCalculators.KingCalculator;
import chess.moveCalculators.KnightCalculator;
import chess.moveCalculators.PawnCalculator;
import chess.moveCalculators.QueenCalculator;
import chess.moveCalculators.RookCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {

    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        this.color = color;
        this.type = type;
    }
    public ChessPiece clone() {
        try {
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
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
        return color;
    }
    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public String getSymbol() {
        boolean isWhite = (color == ChessGame.TeamColor.WHITE);
        return switch (type) {
            case KING -> isWhite? "K" : "k";
            case QUEEN -> isWhite? "Q" : "q";
            case ROOK -> isWhite? "R" : "r";
            case KNIGHT -> isWhite? "N" : "n";
            case BISHOP -> isWhite? "B" : "b";
            case PAWN -> isWhite? "P" : "p";
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
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> KingCalculator.pieceMoves(board, myPosition);
            case QUEEN -> QueenCalculator.pieceMoves(board, myPosition);
            case ROOK -> RookCalculator.pieceMoves(board, myPosition);
            case KNIGHT -> KnightCalculator.pieceMoves(board, myPosition);
            case BISHOP -> BishopCalculator.pieceMoves(board, myPosition);
            case PAWN -> PawnCalculator.pieceMoves(board, myPosition);
        };
    }
}
