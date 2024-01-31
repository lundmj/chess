package chess.moveCalculators;

import chess.*;

import java.util.ArrayList;

public class PawnCalculator extends MoveCalculator {
    public static ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        boolean isWhite = (color == ChessGame.TeamColor.WHITE);
        int rowStep = isWhite? 1 : -1; // white goes up, black goes down
        int promotionRow = isWhite? 8 : 1;
        int startRow = isWhite? 2 : 7;

        ChessPosition left = new ChessPosition(row+rowStep, col-1);
        ChessPosition right = new ChessPosition(row+rowStep, col+1);
        if (inBoard(left) && board.getPiece(left) != null && board.getPiece(left).getTeamColor() != color) {
            addMove(board, position, left, moves, promotionRow);
        }
        if (inBoard(right) && board.getPiece(right) != null && board.getPiece(right).getTeamColor() != color) {
            addMove(board, position, right, moves, promotionRow);
        }

        ChessPosition adv = new ChessPosition(row+rowStep, col);
        if (inBoard(adv) && board.getPiece(adv) == null) {
            addMove(board, position, adv, moves, promotionRow);

            ChessPosition doubleAdv = new ChessPosition(row+rowStep+rowStep, col);
            if (position.getRow() == startRow && board.getPiece(doubleAdv) == null) {
                addMove(board, position, doubleAdv, moves, promotionRow);
            }
        }
        return moves;
    }
    private static void addMove(ChessBoard board, ChessPosition start, ChessPosition dest, ArrayList<ChessMove> moves, int promotionRow) {
        if (dest.getRow() != promotionRow) {
            moves.add(new ChessMove(start, dest, null));
        } else {
            moves.add(new ChessMove(start, dest, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, dest, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, dest, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, dest, ChessPiece.PieceType.KNIGHT));
        }
    }

}
