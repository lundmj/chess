package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class RookCalculator extends MoveCalculator {
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        for (int i = 1; addMove(board, position, new ChessPosition(row, col+i), moves); i++);
        for (int i = 1; addMove(board, position, new ChessPosition(row, col-i), moves); i++);
        for (int i = 1; addMove(board, position, new ChessPosition(row+i, col), moves); i++);
        for (int i = 1; addMove(board, position, new ChessPosition(row-i, col), moves); i++);

        return moves;
    }
}
