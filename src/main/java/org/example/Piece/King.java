package org.example.Piece;


import org.example.Board;
import org.example.Move;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {


    public King(Board chessboard, int x, int y, boolean isWhite) {
        super(chessboard, x, y, isWhite, PieceType.KING);
    }


    @Override
    public boolean canMoveTo(int targetRow, int targetCol) {
        if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
            return false;
        }

        if (areSquaresUnderAttack(new int[][] {{targetRow, targetCol}})) {
            return false;
        }

        if (Math.abs(targetRow - this.getRow()) <= 1 && Math.abs(targetCol - this.getCol()) <= 1) {
            return true;
        }

        // Handle castling
        if (canCastleRight(targetRow, targetCol)) {
            return true;
        }
        if (canCastleLeft(targetRow, targetCol)) {
            return true;
        }

        return false;

    }


    private boolean canCastleRight(int targetRow, int targetCol) {
        if (this.getMoveCount() > 0 || targetCol != 6 || targetRow != this.getRow()) {
            return false;
        }

        // Check that the path to the right rook is clear
        if (isPieceBlockingLine(this.getRow(), 7)) {
            return false;
        }

        // Validate the rook
        Piece rook = this.getChessboard().getBoardState()[this.getRow()][7];
        if (rook == null || rook.getType() != PieceType.ROOK || rook.getMoveCount() > 0) {
            return false;
        }

        // Ensure no attacking piece threatens the king or the squares it passes through
        return !areSquaresUnderAttack(new int[][] {
                {this.getRow(), 4}, {this.getRow(), 5}, {this.getRow(), 6}
        });
    }

    private boolean canCastleLeft(int targetRow, int targetCol) {
        if (this.getMoveCount() > 0 || targetCol != 2 || targetRow != this.getRow()) {
            return false;
        }

        // Check that the path to the left rook is clear
        if (isPieceBlockingLine(this.getRow(), 0)) {
            return false;
        }

        // Validate the rook
        Piece rook = this.getChessboard().getBoardState()[this.getRow()][0];
        if (rook == null || rook.getType() != PieceType.ROOK || rook.getMoveCount() > 0) {
            return false;
        }

        // Ensure no attacking piece threatens the king or the squares it passes through
        return !areSquaresUnderAttack(new int[][] {
                {this.getRow(), 4}, {this.getRow(), 3}, {this.getRow(), 2}
        });
    }

    private boolean areSquaresUnderAttack(int[][] squares) {
        for (int[] square : squares) {
            int row = square[0];
            int col = square[1];
            for (Piece[] rowPieces : getChessboard().getBoardState()) {
                for (Piece piece : rowPieces) {
                    if (piece != null && piece.getIsWhite() != this.getIsWhite() && piece.canMoveTo(row, col)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    @Override
    public List<Move> generatePossibleMoves() {
        int[][] squares = {
                {1, 1}, {1, -1}, {-1, -1}, {-1, 1},
                {1, 0}, {0, 1}, {-1, 0}, {0, -1}
        };

        List<Move> possibleMoves = new ArrayList<>(super.generateFixedMoves(squares));

        if (canCastleLeft(this.getRow(), 2)) {
            possibleMoves.add(new Move(this.getRow(), this.getCol(), this.getRow(), 2, this, null, true, false, null));
        }
        if (canCastleRight(this.getRow(), 6)) {
            possibleMoves.add(new Move(this.getRow(), this.getCol(), this.getRow(), 6, this, null, true, false, null));
        }

        return possibleMoves;
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
