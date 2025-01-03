package org.example.chess.Piece;


import org.example.chess.Board;
import org.example.chess.Move;

import java.util.List;

public class Rook extends Piece{
    private static final int VALUE = 500;


    public Rook(Board chessboard, int x, int y, boolean isWhite) {
        super(chessboard, x, y, isWhite, PieceType.ROOK);
    }



    @Override
    public boolean canMoveTo(int targetRow, int targetCol) {
        if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
            return false;
        }

        if ((targetRow == getRow() || targetCol == getCol()) &&
            !isPieceBlockingLine(targetRow, targetCol))
        {
            return true;
        }

        return false;
    }

    @Override
    public int[][] getPieceSquareTable() {
        return new int[][] {
                {0, 0, 0, 5, 5, 0, 0, 0},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {5, 10, 10, 10, 10, 10, 10, 5},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    @Override
    public int[][] getPieceSquareTable(boolean isEndgame) {
        return getPieceSquareTable();
    }

    @Override
    public List<Move> generatePossibleMoves() {
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        return super.generateDirectionalMoves(directions);
    }


    @Override
    public int getValue() {
        return VALUE;
    }

    @Override
    public String toString() {
        return "{ type='" + getType() + '\'' +
                ", isWhite=" + getIsWhite() +
                ", x=" + getRow() +
                ", y=" + getCol() +
                '}';
    }
}
