package org.example.Piece;


import org.example.Board;
import org.example.Move;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    private static final int VALUE = 325;

    public Bishop(Board chessboard, int x, int y, boolean isWhite) {
        super(chessboard, x, y, isWhite, PieceType.BISHOP);
    }


    @Override
    public boolean canMoveTo(int targetRow, int targetCol) {
        int movedRows = Math.abs(targetRow - this.getRow());
        int movedCols = Math.abs(targetCol - this.getCol());
        System.out.println(this.toString() + ": canMoveTo: " + targetRow + ", " + targetCol);
        if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
            return false;
        }
        if (movedRows - movedCols == 0 &&
            !isPieceBlockingDiagonal(targetRow, targetCol))
        {
            return true;
        }

        return false;
    }

    @Override
    public List<Move> generatePossibleMoves() {
        int[][] directions = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
        return super.generateDirectionalMoves(directions);
    }

    @Override
    public int getValue() {
        return VALUE;
    }


    @Override
    public String toString() {
        return "type='" + getType() + '\'' +
                ", isWhite=" + getIsWhite() +
                ", x=" + getRow() +
                ", y=" + getCol() +
                '}';
    }
}
