package org.example;

import org.example.Piece.*;

import java.util.Arrays;

public class Board {

    public static final int CHESSBOARD_ROWS = 8;
    public static final int CHESSBOARD_COLUMNS = 8;


    private Piece[][] boardState = new Piece[8][8];

    private static final double squareSize = 95;

    public Board() {
        createChessboard();
    }


    public void createChessboard() {
        for(int row = 0; row < CHESSBOARD_ROWS; row++) {
            for (int col = 0; col < CHESSBOARD_COLUMNS; col++) {
                Piece piece = createPiece(row, col);
                if (piece != null) {
                    boardState[row][col] = piece;
                }
            }
        }

    }



    public void removePieceFromBoard(int row, int col) {
        boardState[row][col] = null;
    }


    public Piece createPiece(int row, int col) {
        switch (row) {
            case 0:
                switch (col) {
                    case 0, 7 -> {
                        return new Rook(this, row, col, true);
                    }
                    case 1, 6 -> {
                        return new Knight(this, row, col, true);
                    }
                    case 2, 5 -> {
                        return new Bishop(this, row, col, true);
                    }
                    case 3 -> {
                        return new Queen(this, row, col, true);
                    }
                    case 4 -> {
                        return new King(this, row, col, true);
                    }
                }
                break;
            case 1:
                switch (col) {
                    case 0, 1, 2, 3, 4, 5, 6, 7 -> {
                        return new Pawn(this, row, col, true);
                    }
                }
                break;
            case 6:
                switch (col) {
                    case 0, 1, 2, 3, 4, 5, 6, 7 -> {
                        return new Pawn(this, row, col, false);
                    }
                }
                break;
            case 7:
                switch (col) {
                    case 0, 7 -> {
                        return new Rook(this, row, col, false);
                    }
                    case 1, 6 -> {
                        return new Knight(this, row, col, false);
                    }
                    case 2, 5 -> {
                        return new Bishop(this, row, col, false);
                    }
                    case 3 -> {
                        return new Queen(this, row, col, false);
                    }
                    case 4 -> {
                        return new King(this, row, col, false);
                    }
                }
                break;
        }
        return null;
    }




    public Piece[][] getBoardState() {
        return boardState;
    }

    public void setBoardState(Piece[][] boardState) {
        this.boardState = boardState;
    }

    public Piece[][] createBoardStateCopy() {
        int arrayLength = getBoardState().length;
        Piece[][] boardStateCopy = new Piece[arrayLength][arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            boardStateCopy[i] = Arrays.copyOf(getBoardState()[i], arrayLength);
        }
        return boardStateCopy;
    }

    public Piece getKing(boolean isWhite) {
        for (Piece[] pieceRow : getBoardState()) {
            for (Piece piece : pieceRow) {
                if (piece != null) {
                    if (isWhite) {
                        if (piece.getType() == PieceType.KING && piece.getIsWhite()) {
                            return piece;
                        }
                    }
                    else {
                        if (piece.getType() == PieceType.KING && !piece.getIsWhite()) {
                            return piece;
                        }
                    }
                }
            }
        }
        return null;
    }

}

