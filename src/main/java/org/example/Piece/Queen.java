package org.example.Piece;


import org.example.Board;
import org.example.Move;

import java.util.List;

public class Queen extends Piece {
    private static final int VALUE = 900;

    public Queen(Board chessboard, int x, int y, boolean isWhite) {
        super(chessboard, x, y, isWhite, PieceType.QUEEN);
    }



    @Override
    public boolean canMoveTo(int targetRow, int targetCol) {
        int movedRows = Math.abs(targetRow - this.getRow());
        int movedCols = Math.abs(targetCol - this.getCol());

        if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
            System.out.println(this.toString() + ": cannot move: isFriendlyPiece");
            return false;
        }
        if (movedRows - movedCols == 0 && !isPieceBlockingDiagonal(targetRow, targetCol)) {
            System.out.println(this.toString() + ": can move: diagonal");
            return true;
        }
        if ((targetRow == getRow() || targetCol == getCol()) &&
            !isPieceBlockingLine(targetRow, targetCol))
        {
            System.out.println(this.toString() + ": can move: line");
            return true;
        }
        System.out.println(this.toString() + ": cannot move to " + targetRow + ", " + targetCol );
        return false;
    }

    @Override
    public List<Move> generatePossibleMoves() {
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, -1}, {-1, 1},
                {1, 0}, {0, 1}, {-1, 0}, {0, -1}
        };
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
