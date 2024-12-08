package org.example;

import org.example.Piece.*;
import org.example.Utils.BoardParser;

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
    private boolean isWhitesTurn;
    private Move lastMove;
    private String enPassantField;
    private int castlingRights; // 4 bits - qkQK


    public Board() {
        createChessboard();
        this.lastMove = null;
        this.isGameOver = false;
        this.isWhitesTurn = true;
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


    // TODO: HANDLE MOVES THAT LEAD TO CHECKMATE

    public List<Move> getAllPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        for (Piece[] pieceRow : getBoardState()) {
            for (Piece piece : pieceRow) {
                if (piece != null && piece.getIsWhite() == isWhitesTurn) {
                    moves.addAll(piece.generatePossibleMoves());

                }
            }
        }

        List<Move> movesToRemove = new ArrayList<>();
        for (Move move : moves) {
            boolean isWhitesMove = move.getMovedPiece().getIsWhite();
            King king = getKing(isWhitesMove);
            this.makeMove(move);

            if (isKingInCheck(king)){
                movesToRemove.add(move);
            }

            this.undoMove(move);
        }

        moves.removeAll(movesToRemove);

        return moves;
    }


    public void makeMove(Move move) {
        this.boardState[move.getStartRow()][move.getStartCol()] = null;
        this.boardState[move.getTargetRow()][move.getTargetCol()] = move.getMovedPiece();

        move.getMovedPiece().setMoveCount(move.getMovedPiece().getMoveCount() + 1);
        move.getMovedPiece().setRow(move.getTargetRow());
        move.getMovedPiece().setCol(move.getTargetCol());

        // handle en passant
        if (move.isEnPassant()) {
            int direction = move.getMovedPiece().getIsWhite() ? -1 : 1;
            this.boardState[move.getTargetRow() + direction][move.getTargetCol()] = null;
        }

        // Handle castling
        if (move.isCastling()) {
            int rookStartCol = (move.getTargetCol() == 2) ? 0 : 7;
            int rookEndCol = (move.getTargetCol() == 2) ? 3 : 5;

            Piece rook = this.boardState[move.getTargetRow()][rookStartCol];
            this.boardState[move.getTargetRow()][rookStartCol] = null;
            this.boardState[move.getTargetRow()][rookEndCol] = rook;

            rook.setCol(rookEndCol);
//            rook.setMoveCount(rook.getMoveCount() + 1);
        }

        // handle promotion
        if (move.getPromotionPiece() != null) {
            char pieceColor = move.getMovedPiece().getIsWhite() ? 'w' : 'b';
            char type = move.getPromotionPiece() == PieceType.KNIGHT ?
                    'N' : Character.toUpperCase(move.getMovedPiece().getType().toString().charAt(0));

            Piece promotionPiece = BoardParser.createPiece(this, move.getTargetRow(), move.getTargetCol(), pieceColor, type);
            this.boardState[move.getTargetRow()][move.getTargetCol()] = promotionPiece;
            promotionPiece.setRow(move.getTargetRow());
            promotionPiece.setCol(move.getTargetCol());
            promotionPiece.setMoveCount(1); // Promotion counts as the first move
        }

        this.moveHistory.push(move);
        this.lastMove = move;
        updateCastlingRights(move);
        setIsWhitesTurn(!getIsWhitesTurn());
    }


    public void undoMove(Move move) {
        this.boardState[move.getStartRow()][move.getStartCol()] = move.getMovedPiece();
        this.boardState[move.getTargetRow()][move.getTargetCol()] = move.getCapturedPiece();

        move.getMovedPiece().setRow(move.getStartRow());
        move.getMovedPiece().setCol(move.getStartCol());
        move.getMovedPiece().setMoveCount(move.getMovedPiece().getMoveCount() - 1);

        // Restore en passant capture
        if (move.isEnPassant()) {
            int direction = move.getMovedPiece().getIsWhite() ? -1 : 1;
            this.boardState[move.getTargetRow() + direction][move.getTargetCol()] = move.getCapturedPiece();
        }

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

        // Restore promotion
        if (move.getPromotionPiece() != null) {
            // Replace the promoted piece with the original pawn
            Piece originalPawn = move.getMovedPiece();
            this.boardState[move.getTargetRow()][move.getTargetCol()] = null;
            this.boardState[move.getStartRow()][move.getStartCol()] = originalPawn;

        }

        if (!this.moveHistory.isEmpty()) {
            this.moveHistory.pop();
        }

        if (!this.moveHistory.isEmpty()) {
            this.lastMove = this.moveHistory.peek();
        } else {
            this.lastMove = null;
        }

        castlingRights = move.getPreviousCastlingRights();
        setIsWhitesTurn(!getIsWhitesTurn());
    }


    // TODO: NO NEED TO CALL THIS FOR THE SIDE WHO ALREADY LOST CASTLING RIGHTS
    public void updateCastlingRights(Move move) {
        move.setPreviousCastlingRights(this.castlingRights);

        if (move.getMovedPiece().getType() == PieceType.KING) {
            if (move.getMovedPiece().getIsWhite()) {
                this.castlingRights &= ~0b0011; // bitwise AND Operation
            } else {
                this.castlingRights &= ~0b1100;
            }
        }

        if (move.getMovedPiece().getType() == PieceType.ROOK) {
            if (move.getStartRow() == 0 && move.getStartCol() == 0) {
                this.castlingRights &= ~0b0010;
            } else if (move.getStartRow() == 0 && move.getStartCol() == 7 ) {
                this.castlingRights &= ~0b0001;
            } else if (move.getStartRow() == 7 && move.getStartCol() == 0) {
                this.castlingRights &= ~0b1000;
            } else if (move.getStartRow() == 7 && move.getStartCol() == 7 ) {
                this.castlingRights &= ~0b0100;
            }
        }

        if (move.getCapturedPiece() != null && move.getCapturedPiece().getType() == PieceType.ROOK) {
            if (move.getCapturedPiece().getRow() == 0 && move.getCapturedPiece().getCol() == 0 ) {
                this.castlingRights &= ~0b0010;
            } else if (move.getCapturedPiece().getRow() == 0 && move.getCapturedPiece().getCol() == 7) {
                this.castlingRights &= ~0b0001;
            } else if (move.getCapturedPiece().getRow() == 7 && move.getCapturedPiece().getCol() == 0) {
                this.castlingRights &= ~0b1000;
            } else if (move.getCapturedPiece().getRow() == 7 && move.getCapturedPiece().getCol() == 7) {
                this.castlingRights &= ~0b0100;
            }
        }

        bitsToCastlingRights();
    }

    private void bitsToCastlingRights() {
        if ((castlingRights & 0b0001) != 0b0001 ) {
            getKing(true).setHasKingsideCastlingRight(false);
        }
        if ((castlingRights & 0b0010) != 0b0010 ) {
            getKing(true).setHasQueensideCastlingRight(false);
        }
        if ((castlingRights & 0b0100) != 0b0100 ) {
            getKing(false).setHasKingsideCastlingRight(false);
        }
        if ((castlingRights & 0b1000) != 0b1000 ) {
            getKing(false).setHasQueensideCastlingRight(false);
        }
    }


    public boolean isKingInCheck(Piece king) {
        for (Piece[] pieceRow : this.boardState) {
            for (Piece piece : pieceRow) {
                if (piece != null && piece.getIsWhite() != king.getIsWhite()) {
                    if (piece.canMoveTo(king.getRow(), king.getCol())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public King getKing(boolean isWhite) {
        for (Piece[] pieceRow : getBoardState()) {
            for (Piece piece : pieceRow) {
                if (piece != null) {
                    if (isWhite) {
                        if (piece.getType() == PieceType.KING && piece.getIsWhite()) {
                            return (King) piece;
                        }
                    }
                    else {
                        if (piece.getType() == PieceType.KING && !piece.getIsWhite()) {
                            return (King) piece;
                        }
                    }
                }
            }
        }
        return null;
    }


    public Queen getQueen(boolean isWhite) {
        for (Piece[] pieceRow : getBoardState()) {
            for (Piece piece : pieceRow) {
                if (piece != null) {
                    if (isWhite) {
                        if (piece.getType() == PieceType.QUEEN && piece.getIsWhite()) {
                            return (Queen) piece;
                        }
                    }
                    else {
                        if (piece.getType() == PieceType.QUEEN && !piece.getIsWhite()) {
                            return (Queen) piece;
                        }
                    }
                }
            }
        }
        return null;
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



    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }

    public boolean getIsWhitesTurn() {
        return isWhitesTurn;
    }

    public void setIsWhitesTurn(boolean whitesTurn) {
        isWhitesTurn = whitesTurn;
    }


    public String getEnPassantField() {
        return enPassantField;
    }

    public void setEnPassantField(String enPassantField) {
        this.enPassantField = enPassantField;
    }
}

