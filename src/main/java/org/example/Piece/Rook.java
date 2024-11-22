package org.example.Piece;


import org.example.Board;

public class Rook extends Piece{

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
    public String toString() {
        return "type='" + getType() + '\'' +
                ", isWhite=" + getIsWhite() +
                ", x=" + getRow() +
                ", y=" + getCol() +
                '}';
    }
}
