package org.example.Piece;


import org.example.Board;
import org.example.Move;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    private static final int VALUE = 300;

    public Knight(Board chessboard, int x, int y, boolean isWhite) {
        super(chessboard, x, y, isWhite, PieceType.KNIGHT);
    }


    @Override
    public boolean canMoveTo(int targetRow, int targetCol) {
        int movedRows = Math.abs(targetRow - this.getRow());
        int movedCols = Math.abs(targetCol - this.getCol());

        if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
            return false;
        }

        if ((movedRows == 2 && movedCols == 1) ||
            (movedRows == 1 && movedCols == 2))
        {
            return true;
        }
        return false;
    }

    @Override
    public int[][] getPieceSquareTable() {
        return new int[][] {
                {-50, -40, -30, -30, -30, -30, -40, -50},
                {-40, -20, 0, 0, 0, 0, -20, -40},
                {-30, 0, 10, 15, 15, 10, 0, -30},
                {-30, 5, 15, 20, 20, 15, 5, -30},
                {-30, 5, 15, 20, 20, 15, 5, -30},
                {-30, 0, 10, 15, 15, 10, 0, -30},
                {-40, -20, 0, 5, 5, 0, -20, -40},
                {-50, -40, -30, -30, -30, -30, -40, -50}
        };
    }

    @Override
    public int[][] getPieceSquareTable(boolean isEndgame) {
        return getPieceSquareTable();
    }

    @Override
    public List<Move> generatePossibleMoves() {
        int[][] squares = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
        return super.generateFixedMoves(squares);
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
