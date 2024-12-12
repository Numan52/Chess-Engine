package org.example;

import org.example.Piece.Piece;
import org.example.Piece.PieceType;

import java.util.Objects;

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
    private int previousCastlingRights; // 4 bits - qkQK

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

    public int getPreviousCastlingRights() {
        return previousCastlingRights;
    }

    public void setPreviousCastlingRights(int previousCastlingRights) {
        this.previousCastlingRights = previousCastlingRights;
    }

    @Override
    public String toString() {
        return "Move{" +
                "capturedPiece=" + capturedPiece +
                ", movedPiece=" + movedPiece +
                ", targetCol=" + targetCol +
                ", targetRow=" + targetRow +
                ", startCol=" + startCol +
                ", startRow=" + startRow +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return startRow == move.startRow && startCol == move.startCol && targetRow == move.targetRow && targetCol == move.targetCol && isCastling == move.isCastling && isEnPassant == move.isEnPassant && Objects.equals(movedPiece, move.movedPiece) && Objects.equals(capturedPiece, move.capturedPiece) && promotionPiece == move.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startRow, startCol, targetRow, targetCol, movedPiece, capturedPiece, isCastling, isEnPassant, promotionPiece, previousCastlingRights);
    }
}
