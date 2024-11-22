package org.example.Piece;


import org.example.Board;

public class King extends Piece {
    private boolean hasCastled = false;

    public King(Board chessboard, int x, int y, boolean isWhite) {
        super(chessboard, x, y, isWhite, PieceType.KING);
    }


    @Override
    public boolean canMoveTo(int targetRow, int targetCol) {
        Piece pieceAtTargetLocation = this.getChessboard().getBoardState()[targetRow][targetCol];
        if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
            return false;
        }
        // Right CASTLING
        if (Math.abs(targetRow - this.getRow()) == 0 &&
            targetCol == 6 &&
            !isPieceBlockingLine(targetRow, targetCol)
            && canCastle())
        {
            for (Piece[] piecesRow : getChessboard().getBoardState()) {
                for (Piece piece : piecesRow) {
                    if (piece != null) {
                        System.out.println("------ king castle right  -------");
                        if (piece.getIsWhite() != this.getIsWhite() &&
                            (piece.canMoveTo(targetRow, 5) || piece.canMoveTo(targetRow, 6))) {
                            return false;
                        }
                    }
                }
            }

            Piece rook = getChessboard().getBoardState()[targetRow][7];
            if (rook != null &&
                rook.getCol() == this.getCol() + 3 &&
                !rook.getHasMoved())
            {
                System.out.println(targetRow);
                System.out.println("right castled");
                return true;
            }
        }

        // LEFT CASTLING
        if (Math.abs(targetRow - this.getRow()) == 0 &&
                targetCol == 2 &&
                !isPieceBlockingLine(targetRow, 0)
                && canCastle())
        {
            for (Piece[] piecesRow : getChessboard().getBoardState()) {
                for (Piece piece : piecesRow) {
                    if (piece != null) {

                        if (piece.getIsWhite() != this.getIsWhite() &&
                            (piece.canMoveTo(targetRow, 3) || piece.canMoveTo(targetRow, 2) || piece.canMoveTo(targetRow, 1)))
                        {
                            return false;
                        }
                    }

                }
            }

            Piece rook = getChessboard().getBoardState()[targetRow][0];
            if (rook != null &&
                rook.getCol() == this.getCol() - 4 &&
                !rook.getHasMoved())
            {
                System.out.println(targetRow);
                System.out.println("right castled");
                return true;
            }
        }

        if (Math.abs(targetRow - this.getRow()) + Math.abs(targetCol - this.getCol()) == 1 ||
            Math.abs(targetRow - this.getRow()) * Math.abs(targetCol - this.getCol()) == 1)
        {
            System.out.println(this + ": can move to: " +  targetRow + ", " + targetCol);
            return true;
        }
        return false;
    }

    public boolean canCastle() {
        if (hasCastled || getHasMoved()) {
            return false;
        }
        return true;

    }

    @Override
    public String toString() {
        return "type='" + getType() + '\'' +
                ", isWhite=" + getIsWhite() +
                ", x=" + getRow() +
                ", y=" + getCol() +
                '}';
    }

    public void setHasCastled(boolean hasCastled) {
        this.hasCastled = hasCastled;
    }

    public boolean isHasCastled() {
        return hasCastled;
    }
}
