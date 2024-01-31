package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class KnightCalculator extends MoveCalculator {
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        addMove(board, position, new ChessPosition(row+2, col+1), moves);
        addMove(board, position, new ChessPosition(row+2, col-1), moves);
        addMove(board, position, new ChessPosition(row-2, col+1), moves);
        addMove(board, position, new ChessPosition(row-2, col-1), moves);
        addMove(board, position, new ChessPosition(row+1, col+2), moves);
        addMove(board, position, new ChessPosition(row-1, col+2), moves);
        addMove(board, position, new ChessPosition(row+1, col-2), moves);
        addMove(board, position, new ChessPosition(row-1, col-2), moves);

        return moves;
    }
}
