package org.example;

import com.google.gson.Gson;
import org.example.Piece.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MoveParser {
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


    public static Piece[][] fenToBoardState(Board board, String fen) {
        String[] fenPieces = fen.split(" ");
        String[] rows = fenPieces[0].split("/");
        Piece[][] boardState = new Piece[8][8];
        System.out.println(Arrays.toString(rows));

        for (int i = 0; i < rows.length; i++) {
            int col = 0;
            for (int j = 0; j < rows[i].length(); j++) {
                if (Character.isDigit(rows[i].charAt(j))) {
                    int digit = Character.getNumericValue(rows[i].charAt(j));
                    col += digit;
                    continue;
                }
                char color = Character.isUpperCase(rows[i].charAt(j)) ? 'w' : 'b';
                char type = Character.toUpperCase(rows[i].charAt(j));
                Piece piece = createPiece(board, 7 - i, col, color, type);
                boardState[7 - i][col] = piece;
                col++;
            }
        }
        return boardState;
    }


    public static void main(String[] args) {
        System.out.println(Arrays.deepToString(MoveParser.fenToBoardState(new Board(), "rnb1kbnr/ppp2ppp/8/3Np1q1/8/5N2/PPPPPPPP/R1BQKB1R w KQkq - 1 4\n")));
//        Piece[][] boardState = MoveParser.fenToBoardState(new Board(), "rnb1kbnr/ppp2ppp/8/3Np1q1/8/5N2/PPPPPPPP/R1BQKB1R w KQkq - 1 4\n");
//        System.out.println(boardState[4][4]);
    }

//    public static String boardStateToJson(Board board) {
//        Gson gson = new Gson();
//        Map<String, String> boardStateMap = new HashMap<>();
//        Piece[][] boardState = board.getBoardState();
//
//        for (Piece [] row : boardState) {
//            for (Piece piece : row) {
//                if (piece == null) continue;
//                String position = toAlgebraicNotation(piece.getRow(), piece.getCol());
//                String pieceColorCode = (piece.getIsWhite() ? "w" : "b");
//                String pieceTypeCode = piece.getType() == PieceType.KNIGHT ? "N" : String.valueOf(piece.getType().toString().toUpperCase().charAt(0));
//                boardStateMap.put(position, pieceColorCode + pieceTypeCode);
//            }
//        }
//        return gson.toJson(boardStateMap);
//    }
//
//
//    private static String toAlgebraicNotation(int row, int col) {
//        char colChar = (char) ('a' + col);
//        int rowNum = row + 1;
//        String position = String.valueOf(colChar) + rowNum;
//        return position;
//    }




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
