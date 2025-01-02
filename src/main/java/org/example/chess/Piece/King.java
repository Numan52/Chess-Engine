package org.example.chess.Piece;


import org.example.chess.Board;
import org.example.chess.Move;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private static final int VALUE = 0;
    private boolean hasKingsideCastlingRight = true;
    private boolean hasQueensideCastlingRight = true;
    private boolean hasCastled = false;

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
        if (canCastleKingside(targetRow, targetCol)) {
            return true;
        }
        if (canCastleQueenside(targetRow, targetCol)) {
            return true;
        }

        return false;

    }

    @Override
    public int[][] getPieceSquareTable() {
        return new int[][] {
                {-100, -100, -100, -100, -100, -100, -100, -100},
                {-100, -100, -100, -100, -100, -100, -100, -100},
                {-100, -100, -100, -100, -100, -100, -100, -100},
                {-100, -100, -100, -100, -100, -100, -100, -100},
                {-70, -70, -70, -70, -70, -70, -70, -70},
                {-60, -60, -60, -60, -60, -60, -60, -60},
                {-20, -30, -30, -50, -50, -30, -30, 20},
                {20, 30, -10, -30, -30, -10, 30, 20}
        };
    }


    @Override
    public int[][] getPieceSquareTable(boolean isEndgame) {
        if (isEndgame) {
            return new int[][] {
                    {-50, -30, -30, -30, -30, -30, -30, -50},
                    {-30, -10, 0, 0, 0, 0, -10, -30},
                    {-30, 0, 10, 15, 15, 10, 0, -30},
                    {-30, 0, 15, 20, 20, 15, 0, -30},
                    {-30, 0, 15, 20, 20, 15, 0, -30},
                    {-30, 0, 10, 15, 15, 10, 0, -30},
                    {-30, -10, 0, 0, 0, 0, -10, -30},
                    {-50, -30, -30, -30, -30, -30, -30, -50}
            };
        } else {
            return getPieceSquareTable();
        }

    }

    // needed as replacement for canMoveTo() in areSquaresUnderAttack()
    public boolean canAttack(int targetRow, int targetCol) {
        if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
            return false;
        }

        if (Math.abs(targetRow - this.getRow()) <= 1 && Math.abs(targetCol - this.getCol()) <= 1) {
            return true;
        }

        return false;
    }


    private boolean canCastleKingside(int targetRow, int targetCol) {
        if (targetCol != 6 || targetRow != this.getRow() || !this.hasKingsideCastlingRight) {
            return false;
        }

        // Check that the path to the right rook is clear
        if (isPieceBlockingLine(this.getRow(), 7)) {
            return false;
        }

        // Validate the rook
        Piece rook = this.getChessboard().getBoardState()[this.getRow()][7];
        if (rook == null || rook.getType() != PieceType.ROOK) {
            return false;
        }

        // Ensure no attacking piece threatens the king or the squares it passes through
        return !areSquaresUnderAttack(new int[][] {
                {this.getRow(), 4}, {this.getRow(), 5}, {this.getRow(), 6}
        });
    }



    private boolean canCastleQueenside(int targetRow, int targetCol) {
        if (targetCol != 2 || targetRow != this.getRow() || !this.hasQueensideCastlingRight) {
            return false;
        }

        // Check that the path to the left rook is clear
        if (isPieceBlockingLine(this.getRow(), 0)) {
            return false;
        }

        // Validate the rook
        Piece rook = this.getChessboard().getBoardState()[this.getRow()][0];
        if (rook == null || rook.getType() != PieceType.ROOK) {
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
            List<Piece> activePieces = this.getIsWhite() ? getChessboard().getBlackPieces() : getChessboard().getWhitePieces();

            for (Piece piece : activePieces) {
                if (piece.getType() == PieceType.KING) {
                    // For Kings, use canAttack to avoid cyclic dependency between areSquaresUnderAttack() and canMoveTo()
                    if (((King) piece).canAttack(row, col)) {
                        return true;
                    }
                } else {
                    if (piece.canMoveTo(row, col)) {
                        return true;
                    }

                }
            }
        }

        return false;
    }



    @Override
    public List<Move> generatePossibleMoves() {
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, -1}, {-1, 1},
                {1, 0}, {0, 1}, {-1, 0}, {0, -1}
        };

        List<Move> possibleMoves = new ArrayList<>(super.generateFixedMoves(directions));

        if (canCastleQueenside(this.getRow(), 2)) {
            possibleMoves.add(new Move(this.getRow(), this.getCol(), this.getRow(), 2, this, null, true, false, null));
        }
        if (canCastleKingside(this.getRow(), 6)) {
            possibleMoves.add(new Move(this.getRow(), this.getCol(), this.getRow(), 6, this, null, true, false, null));
        }

        return possibleMoves;
    }


    public boolean getHasKingsideCastlingRights() {
        return hasKingsideCastlingRight;
    }

    public void setHasKingsideCastlingRight(boolean hasKingsideCastlingRight) {
        this.hasKingsideCastlingRight = hasKingsideCastlingRight;
    }

    public boolean getHasQueensideCastlingRights() {
        return hasQueensideCastlingRight;
    }

    public void setHasQueensideCastlingRight(boolean hasQueensideCastlingRight) {
        this.hasQueensideCastlingRight = hasQueensideCastlingRight;
    }




    @Override
    public int getValue() {
        return VALUE;
    }


    public boolean getHasCastled() {
        return hasCastled;
    }

    public void setHasCastled(boolean hasCastled) {
        this.hasCastled = hasCastled;
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
