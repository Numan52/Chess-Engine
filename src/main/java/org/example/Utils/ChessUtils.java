package org.example.Utils;

import org.example.Board;
import org.example.Piece.*;

import java.util.List;

public class ChessUtils {
    public static List<PieceType> getPromotionOptions() {
        return List.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT);
    }


    public static int enPassantFieldToCol(Board board) {
        if (board.getEnPassantField().equals("-")) {
            return -1;
        }
        return board.getEnPassantField().charAt(0) - (int) 'a';
    }

}
