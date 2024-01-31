package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class KingCalculator extends MoveCalculator {
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        addMove(board, position, new ChessPosition(row+1, col-1), moves);
        addMove(board, position, new ChessPosition(row+1, col), moves);
        addMove(board, position, new ChessPosition(row+1, col+1), moves);

        addMove(board, position, new ChessPosition(row, col-1), moves);
        addMove(board, position, new ChessPosition(row, col+1), moves);

        addMove(board, position, new ChessPosition(row-1, col-1), moves);
        addMove(board, position, new ChessPosition(row-1, col), moves);
        addMove(board, position, new ChessPosition(row-1, col+1), moves);

        return moves;
    }
}
