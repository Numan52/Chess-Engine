package org.example;

import org.example.Piece.PieceType;

import java.util.List;

public class ChessUtils {
    public static List<PieceType> getPromotionOptions() {
        return List.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT);
    }
}
