package org.example;

import org.example.Piece.Piece;
import org.example.Piece.PieceType;

public class Move {
    private final int startRow;
    private final int startCol;
    private final int targetRow;
    private final int targetCol;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private final boolean isCastling;
    private final boolean isEnPassant;
    private final PieceType promotionPiece;


    public Move(int startRow, int startCol, int targetRow, int targetCol, Piece movedPiece, Piece capturedPiece,
                boolean isCastling, boolean isEnPassant, PieceType promotionPieceType) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.isCastling = isCastling;
        this.isEnPassant = isEnPassant;
        this.promotionPiece = promotionPieceType;
    }

    public Move(int startRow, int startCol, int targetRow, int targetCol, Piece movedPiece) {
        this(startRow, startCol, targetRow, targetCol, movedPiece, null, false, false, null);
    }


    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public boolean isCastling() {
        return isCastling;
    }

    public boolean isEnPassant() {
        return isEnPassant;
    }

    public PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
