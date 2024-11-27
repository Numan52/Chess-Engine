package org.example;

import com.google.gson.Gson;
import org.example.Piece.*;

import java.util.HashMap;
import java.util.Map;

public class MoveTranslator {
    private static final Map<Integer, Character> columnMap = new HashMap<>();

    static {
        for (int i = 0; i < 8; i++) {
            columnMap.put(i, (char) ('a' + i));
        }
    }

    public static String moveToString(Move move) {
        StringBuilder moveString = new StringBuilder();
        moveString.append(columnMap.get(move.getStartCol())).append(move.getStartRow() + 1);
        moveString.append("-");
        moveString.append(columnMap.get(move.getTargetCol())).append(move.getTargetRow() + 1);

        return moveString.toString();
    }


    public static void updateBoard(String boardStateJson, Board board) {
        Gson gson = new Gson();
        Piece[][] newBoardState = new Piece[8][8];
        Map<String, String> boardState = gson.fromJson(boardStateJson, Map.class);

        for (Map.Entry<String, String> entry : boardState.entrySet()) {
            String position = entry.getKey();
            String pieceCode = entry.getValue();

            int col = position.charAt(0) - 'a';
            int row = Integer.parseInt(position.substring(1)) - 1;

            char color = pieceCode.charAt(0); // 'b' for black, 'w' for white
            char type = pieceCode.charAt(1); // 'P', 'R', 'N', 'B', 'Q', 'K'

            Piece piece = createPiece(board, row, col, color, type);
            board.setBoardState(newBoardState);
            board.getBoardState()[row][col] = piece;
        }

    }

    private static Piece createPiece(Board board, int row, int col, char color, char type) {
        boolean isWhite = color == 'w';

        return switch (type) {
            case 'P' -> new Pawn(board, row, col, isWhite);
            case 'R' -> new Rook(board, row, col, isWhite);
            case 'N' -> new Knight(board, row, col, isWhite);
            case 'B' -> new Bishop(board, row, col, isWhite);
            case 'Q' -> new Queen(board, row, col, isWhite);
            case 'K' -> new King(board, row, col, isWhite);
            default -> throw new IllegalArgumentException("Invalid piece type: " + type);
        };
    }
}
