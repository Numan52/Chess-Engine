package org.example.chess.Piece;


import org.example.chess.Board;
import org.example.chess.Move;

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
            return false;
        }
        if (movedRows - movedCols == 0 && !isPieceBlockingDiagonal(targetRow, targetCol)) {
            return true;
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
                {-20, -10, -10, -5, -5, -10, -10, -20},
                {-10, 0, 0, 0, 0, 0, 0, -10},
                {-10, 0, 5, 5, 5, 5, 0, -10},
                {-5, 0, 5, 5, 5, 5, 0, -5},
                {0, 0, 5, 5, 5, 5, 0, -5},
                {-10, 5, 5, 5, 5, 5, 0, -10},
                {-10, 0, 5, 0, 0, 0, 0, -10},
                {-20, -10, -10, -5, -5, -10, -10, -20}
        };
    }

    @Override
    public int[][] getPieceSquareTable(boolean isEndgame) {
        return getPieceSquareTable();
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
        return "{ type='" + getType() + '\'' +
                ", isWhite=" + getIsWhite() +
                ", x=" + getRow() +
                ", y=" + getCol() +
                '}';
    }
}
