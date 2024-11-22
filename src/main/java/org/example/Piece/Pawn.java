package org.example.Piece;


import org.example.Board;

public class Pawn extends Piece {

    public Pawn(Board chessboard, int x, int y, boolean isWhite) {
        super(chessboard, x, y, isWhite, PieceType.PAWN);
    }



    @Override
    public boolean canMoveTo(int targetRow, int targetCol) {
        int movedRows = Math.abs(targetRow - this.getRow());
        int movedCols = Math.abs(targetCol - this.getCol());

        if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
            return false;
        }

        Piece pieceAtTargetLocation = this.getChessboard().getBoardState()[targetRow][targetCol];

        if ((targetRow <= this.getRow() && getIsWhite()) ||
            (targetRow >= this.getRow() && !getIsWhite()) ||
            (targetCol != this.getCol() && targetRow == this.getRow()))
        {
            return false;
        }

        // 1 square forward
        if (movedRows == 1 && movedCols == 0 && pieceAtTargetLocation == null) {
            return true;
        }
        // 2 squares forward
        if (movedRows == 2 && !getHasMoved() && pieceAtTargetLocation == null && !isPieceBlockingLine(targetRow, targetCol)) {
            return true;
        }
        if (pieceAtTargetLocation != null && movedRows == 1 && movedCols == 1 ) {
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
