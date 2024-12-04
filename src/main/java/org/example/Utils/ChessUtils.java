package org.example.Utils;

import org.example.Board;
import org.example.Piece.*;

import java.util.List;

public class ChessUtils {
    public static List<PieceType> getPromotionOptions() {
        return List.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT);
    }


}
