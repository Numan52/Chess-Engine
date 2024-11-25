package org.example.Piece;

import org.example.Board;
import org.example.Move;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    private PieceType type;
    private boolean isWhite;
    private int row;
    private int col;
    private boolean hasMoved;
    private Board chessboard;


    Piece(Board chessboard, int row, int y, boolean isWhite, PieceType type) {
        this.row = row;
        this.col = y;
        this.isWhite = isWhite;
        this.type = type;
        this.chessboard = chessboard;

    }

    public abstract boolean canMoveTo(int row, int col);

    public abstract List<Move> generatePossibleMoves();

    public boolean isWithinBoard(int targetRow, int targetCol) {
        return targetRow <= 7 && targetRow >= 0 && targetCol <= 7 && targetCol >= 0;
    }


    public boolean isFriendlyPiece(int targetRow, int targetCol) {
        Piece pieceAtTargetLocation = this.getChessboard().getBoardState()[targetRow][targetCol];
        return ((pieceAtTargetLocation != null) && (pieceAtTargetLocation.getIsWhite()) == this.getIsWhite());
    }


    public boolean isPieceBlockingDiagonal(int targetRow, int targetCol) {
        int rowDirection = targetRow > row ? 1 : -1;
        int colDirection = targetCol > col ? 1 : -1;

        int row = this.row + rowDirection;
        int col = this.col + colDirection;

        while(row != targetRow && col != targetCol) {
            if (chessboard.getBoardState()[row][col] != null){
                return true;
            }
            row += rowDirection;
            col += colDirection;
        }
        return false;
    }


    public boolean isPieceBlockingLine(int targetRow, int targetCol) {
        int rowDirection = 0;
        int colDirection = 0;

        // find out movement direction
        if (targetCol == col && targetRow != row) {
            if (targetRow > row) {
                rowDirection = 1;
            } else {
                rowDirection = -1;
            }
        } else if (targetCol != col && targetRow == row) {
            if (targetCol > col) {
                colDirection = 1;
            } else {
                colDirection = -1;
            }
        }

        int row = this.row + rowDirection;
        int col = this.col + colDirection;

        while (row != targetRow || col != targetCol) {
            if (chessboard.getBoardState()[row][col] != null) {
                return true;
            }
            row += rowDirection;
            col += colDirection;
        }

        return false;
    }


    // for knight and king
    public List<Move> generateFixedMoves(int[][] squares) {
        List<Move> possibleMoves = new ArrayList<>();

        for (int[] square : squares) {
            int targetRow = this.getRow() + square[0];
            int targetCol = this.getCol() + square[1];

            if (this.canMoveTo(targetRow, targetCol)) {
                Piece capturedPiece = this.getChessboard().getBoardState()[targetRow][targetCol];
                possibleMoves.add(new Move(
                        this.getRow(), this.getCol(), targetRow, targetCol, this,
                        capturedPiece, false, false, null
                ));
            }
        }
        return possibleMoves;
    }

    // for rook, bishop and queen
    protected List<Move> generateDirectionalMoves(int[][] directions) {
        List<Move> possibleMoves = new ArrayList<>();

        for (int[] direction : directions) {
            int targetRow = this.getRow();
            int targetCol = this.getCol();

            while (true) {
                targetRow += direction[0];
                targetCol += direction[1];

                if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
                    break;
                }

                Piece capturedPiece = this.getChessboard().getBoardState()[targetRow][targetCol];
                possibleMoves.add(new Move(
                        this.getRow(), this.getCol(), targetRow, targetCol, this,
                        capturedPiece, false, false, null
                ));

                if (capturedPiece != null) {
                    break;
                }

            }
        }
        return possibleMoves;
    }


    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public PieceType getType() {
        return type;
    }



    public boolean getIsWhite() {
        return isWhite;
    }

    public Board getChessboard() {
        return chessboard;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }


}