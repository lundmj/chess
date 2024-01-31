package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class QueenCalculator extends MoveCalculator {
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        moves.addAll(RookCalculator.pieceMoves(board, position));
        moves.addAll(BishopCalculator.pieceMoves(board, position));

        return moves;
    }
}
