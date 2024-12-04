package org.example.Utils;

import org.example.Board;
import org.example.Piece.*;

import java.util.Arrays;

public class BoardParser {


//    public static String moveToString(Move move) {
//        StringBuilder moveString = new StringBuilder();
//        moveString.append(columnMap.get(move.getStartCol())).append(move.getStartRow() + 1);
//        moveString.append("-");
//        moveString.append(columnMap.get(move.getTargetCol())).append(move.getTargetRow() + 1);
//
//        return moveString.toString();
//    }


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



    public static String getEnPassantField(String fen) {
        return fen.split(" ")[3];
    }


    public static boolean parseIsWhitesTurn(String fen) {
        String[] fenPieces = fen.split(" ");
        return fenPieces[1].equals("w");
    }


    public static boolean canWhiteCastleKingside(String fen) {
        return getCastlingRights(fen).contains("K");
    }

    public static boolean canWhiteCastleQueenside(String fen) {
        return getCastlingRights(fen).contains("Q");
    }

    public static boolean canBlackCastleKingside(String fen) {
        return getCastlingRights(fen).contains("k");
    }

    public static boolean canBlackCastleQueenside(String fen) {
        return getCastlingRights(fen).contains("q");
    }

    private static String getCastlingRights(String fen) {
        String[] fenPieces = fen.split(" ");
        return fenPieces.length > 2 ? fenPieces[2] : "";
    }





//    private static String toAlgebraicNotation(int row, int col) {
//        char colChar = (char) ('a' + col);
//        int rowNum = row + 1;
//        String position = String.valueOf(colChar) + rowNum;
//        return position;
//    }




    public static Piece createPiece(Board board, int row, int col, char color, char type) {
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
