package org.example;

import org.example.Piece.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Board {

    public static final int CHESSBOARD_ROWS = 8;
    public static final int CHESSBOARD_COLUMNS = 8;
    private boolean isGameOver;
    private Stack<Move> moveHistory = new Stack<>();
    private Piece[][] boardState = new Piece[8][8];
    private Move lastMove;


    public Board() {
        createChessboard();
        this.lastMove = null;
        this.isGameOver = false;
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


    public List<Move> getAllPossibleMoves(boolean isWhitesTurn) {
        List<Move> moves = new ArrayList<>();
        for (Piece[] pieceRow : getBoardState()) {
            for (Piece piece : pieceRow) {
                if (piece != null && piece.getIsWhite() == isWhitesTurn) {
                    moves.addAll(piece.generatePossibleMoves());
                }
            }
        }
        return moves;
    }


    public void makeMove(Move move) {
        this.boardState[move.getStartRow()][move.getStartCol()] = null;
        this.boardState[move.getTargetCol()][move.getTargetCol()] = move.getMovedPiece();

        move.getMovedPiece().setMoveCount(move.getMovedPiece().getMoveCount() + 1);
        move.getMovedPiece().setRow(move.getTargetRow());
        move.getMovedPiece().setCol(move.getTargetCol());

        this.moveHistory.push(move);
        this.lastMove = move;
    }

    //TODO: Fix
    public void undoMove(Move move) {
        this.boardState[move.getStartRow()][move.getStartCol()] = move.getMovedPiece();
        this.boardState[move.getTargetRow()][move.getTargetCol()] = move.getCapturedPiece();

        move.getMovedPiece().setRow(move.getStartRow());
        move.getMovedPiece().setCol(move.getStartCol());
        move.getMovedPiece().setMoveCount(move.getMovedPiece().getMoveCount() - 1);

        // Handle castling
        if (move.isCastling()) {
            int rookStartCol = (move.getTargetCol() == 2) ? 0 : 7;
            int rookEndCol = (move.getTargetCol() == 2) ? 3 : 5;

            Piece rook = this.boardState[move.getTargetRow()][rookEndCol];

            this.boardState[move.getTargetRow()][rookEndCol] = null;
            this.boardState[move.getTargetRow()][rookStartCol] = rook;

            rook.setCol(rookStartCol);
            rook.setMoveCount(rook.getMoveCount() - 1);
        }

        if (!this.moveHistory.isEmpty()) {
            this.moveHistory.pop();
        }

        if (!this.moveHistory.isEmpty()) {
            this.lastMove = this.moveHistory.peek();
        } else {
            this.lastMove = null;
        }
    }


    public Piece[][] getBoardState() {
        return boardState;
    }


    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
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

    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }
}

