package org.example.chess.Utils;

import org.example.chess.Board;
import org.example.chess.Piece.*;

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
        if (fenPieces.length != 6) {
            throw new IllegalArgumentException("incorrect fen format");
        }
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

                initializeReferences(board, piece);

                boardState[7 - i][col] = piece;
                col++;
            }
        }
        return boardState;
    }


    public static void initializeReferences(Board board, Piece piece) {
        if (piece.getIsWhite()) {
            if (piece.getType() == PieceType.KING) {
                board.setWhiteKing((King) piece);
            }
            board.getWhitePieces().add(piece);
        } else {
            if (piece.getType() == PieceType.KING) {
                board.setBlackKing((King) piece);
            }
            board.getBlackPieces().add(piece);

        }
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
