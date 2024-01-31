package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import jdk.jshell.spi.ExecutionControl;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;

public abstract class MoveCalculator {
    protected static boolean inBoard(ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8
                && pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }
    protected static boolean addMove(ChessBoard board, ChessPosition start, ChessPosition dest, ArrayList<ChessMove> moves) {
        if (!inBoard(dest)) return false;
        if (board.getPiece(dest) == null) {
            moves.add(new ChessMove(start, dest, null));
            return true;
        }
        if (board.getPiece(dest).getTeamColor() != board.getPiece(start).getTeamColor()) {
            moves.add(new ChessMove(start, dest, null));
        }
        return false;
    }
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }
}
