package org.example.Utils;

import org.example.Board;
import org.example.Piece.*;

public class TestUtils {

    public static void populateExpectedBoardState(Piece[][] boardState, Board board) {
        boardState[0][0] = new Rook(board, 0, 0, true);
        boardState[0][1] = new Knight(board, 0, 1, true);
        boardState[0][2] = new Bishop(board, 0, 2, true);
        boardState[0][3] = new Queen(board, 0, 3, true);
        boardState[0][4] = new King(board, 0, 4, true);
        boardState[0][5] = new Bishop(board, 0, 5, true);
        boardState[0][6] = new Knight(board, 0, 6, true);
        boardState[0][7] = new Rook(board, 0, 7, true);


        boardState[1][0] = new Pawn(board, 1, 0, true);
        boardState[1][1] = new Pawn(board, 1, 1, true);
        boardState[1][2] = null;
        boardState[1][3] = new Pawn(board, 1, 3, true);
        boardState[1][4] = null;
        boardState[1][5] = new Pawn(board, 1, 5, true);
        boardState[1][6] = new Pawn(board, 1, 6, true);
        boardState[1][7] = null;


        boardState[2][0] = null;
        boardState[2][1] = null;
        boardState[2][2] = null;
        boardState[2][3] = null;
        boardState[2][4] = null;
        boardState[2][5] = null;
        boardState[2][6] = null;
        boardState[2][7] = new Pawn(board, 2, 7, true);


        boardState[3][0] = null;
        boardState[3][1] = null;
        boardState[3][2] = new Pawn(board, 3, 2, true);
        boardState[3][3] = new Pawn(board, 3, 3, false);
        boardState[3][4] = new Pawn(board, 3, 4, true);
        boardState[3][5] = null;
        boardState[3][6] = null;
        boardState[3][7] = null;


        boardState[4][0] = null;
        boardState[4][1] = null;
        boardState[4][2] = null;
        boardState[4][3] = null;
        boardState[4][4] = null;
        boardState[4][5] = null;
        boardState[4][6] = null;
        boardState[4][7] = null;


        boardState[5][0] = null;
        boardState[5][1] = null;
        boardState[5][2] = null;
        boardState[5][3] = null;
        boardState[5][4] = null;
        boardState[5][5] = null;
        boardState[5][6] = null;
        boardState[5][7] = null;


        boardState[6][0] = new Pawn(board, 6, 0, false);
        boardState[6][1] = new Pawn(board, 6, 1, false);
        boardState[6][2] = new Pawn(board, 6, 2, false);
        boardState[6][3] = null;
        boardState[6][4] = new Pawn(board, 6, 4, false);
        boardState[6][5] = new Pawn(board, 6, 5, false);
        boardState[6][6] = new Pawn(board, 6, 6, false);
        boardState[6][7] = new Pawn(board, 6, 7, false);


        boardState[7][0] = new Rook(board, 7, 0, false);
        boardState[7][1] = new Knight(board, 7, 1, false);
        boardState[7][2] = new Bishop(board, 7, 2, false);
        boardState[7][3] = new Queen(board, 7, 3, false);
        boardState[7][4] = new King(board, 7, 4, false);
        boardState[7][5] = new Bishop(board, 7, 5, false);
        boardState[7][6] = new Knight(board, 7, 6, false);
        boardState[7][7] = new Rook(board, 7, 7, false);
    }

    public static String PieceToString(Piece piece) {
        return piece == null ? "null" : piece.toString();
    }
}
