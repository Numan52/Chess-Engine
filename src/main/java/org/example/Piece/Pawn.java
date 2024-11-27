package org.example.Piece;


import org.example.Board;
import org.example.ChessUtils;
import org.example.Move;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    private static final int VALUE = 100;

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
        if (movedRows == 2 && getMoveCount() == 0 && pieceAtTargetLocation == null && !isPieceBlockingLine(targetRow, targetCol)) {
            return true;
        }
        if (pieceAtTargetLocation != null && movedRows == 1 && movedCols == 1 ) {
            return true;
        }

        return false;
    }

    private boolean isEnPassantPossible() {
        Move lastMove = this.getChessboard().getLastMove();
        return lastMove != null && lastMove.getMovedPiece().getType() == PieceType.PAWN
                && Math.abs(lastMove.getStartRow() - lastMove.getTargetRow()) == 2
                && lastMove.getTargetRow() == this.getRow()
                && Math.abs(lastMove.getTargetCol() - this.getCol()) == 1;
    }


    @Override
    public List<Move> generatePossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();


        return List.of();
    }


    public List<Move> generateForwardMoves() {
        List<Move> possibleForwardMoves = new ArrayList<>();
        int direction = this.getIsWhite() ? 1 : -1; // white pawns move to higher rows, black pawns to lower ones
        int oneMoveForward = direction;
        int twoMovesForward = 2 * direction;

        int targetRow = this.getRow() + oneMoveForward;
        // one move forward
        if (this.canMoveTo(targetRow, this.getCol())) {
            if (isPromotionRow(targetRow)) {
                for (PieceType pieceType : ChessUtils.getPromotionOptions()) {
                    possibleForwardMoves.add(
                            new Move(this.getRow(), this.getCol(), targetRow, this.getCol(), this,
                                    null, false, false, pieceType ));
                }
            } else {
                possibleForwardMoves.add(new Move(
                        this.getRow(), this.getCol(), targetRow, this.getCol(), this
                ));
            }
        }

        // two moves forward
        targetRow = this.getRow() + twoMovesForward;
        if (this.canMoveTo(targetRow, this.getCol())) {
            possibleForwardMoves.add(new Move(
                    this.getRow(), this.getCol(), targetRow, this.getCol(), this
            ));
        }

        return possibleForwardMoves;
    }


    public List<Move> generateCaptures() {
        List<Move> possibleCaptures = new ArrayList<>();
        int direction = this.getIsWhite() ? 1 : -1;

        // Diagonal capture to the left
        generateCapture(possibleCaptures, this.getRow() + direction, this.getCol() - 1);
        // Diagonal capture to the right
        generateCapture(possibleCaptures, this.getRow() + direction, this.getCol() + 1);

        return possibleCaptures;
    }


    public void generateCapture(List<Move> possibleCaptures, int targetRow, int targetCol) {
        if (this.canMoveTo(targetRow, targetCol)) {
            Piece capturedPiece = this.getChessboard().getBoardState()[targetRow][targetCol];
            if (isPromotionRow(targetRow)) {
                for (PieceType pieceType : ChessUtils.getPromotionOptions()) {
                    possibleCaptures.add(
                            new Move(this.getRow(), this.getCol(), targetRow, this.getCol(), this,
                                    capturedPiece, false, false, pieceType ));
                }
            } else {
                possibleCaptures.add(new Move(
                        this.getRow(), this.getCol(), targetRow, targetCol, this, capturedPiece,
                        false, false, null
                ));
            }
        }
    }


    public List<Move> generateEnPassantMoves() {
        List<Move> possibleEnPassantMoves = new ArrayList<>();
        int direction = this.getIsWhite() ? 1 : -1;
        Move lastMove = this.getChessboard().getLastMove();
        int enPassantCol = lastMove.getTargetCol();

        if (isEnPassantPossible()) {
            Piece capturedPiece = this.getChessboard().getBoardState()[this.getRow()][enPassantCol];
            possibleEnPassantMoves.add(new Move(
                    this.getRow(), this.getCol(), this.getRow() + direction, this.getCol() - 1, this, capturedPiece,
                    false, true, null
            ));
        }

        return possibleEnPassantMoves;
    }



    private boolean isPromotionRow(int row) {
        return (row == 7 && getIsWhite()) || (row == 0 && !getIsWhite());
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
