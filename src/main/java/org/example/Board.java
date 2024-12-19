package org.example;

import org.example.Piece.*;
import org.example.Utils.BoardParser;
import org.example.Utils.ChessUtils;

import java.util.*;

public class Board {

    public static final int CHESSBOARD_ROWS = 8;
    public static final int CHESSBOARD_COLUMNS = 8;

    private boolean isGameOver;
    private boolean isCheckmate;
    private Stack<Move> moveHistory = new Stack<>();
    private Piece[][] boardState = new Piece[8][8];
    private boolean isWhitesTurn;
    private Move lastMove;
    private String enPassantField;
    private int castlingRights; // 4 bits - qkQK
    private Searcher searcher;
    private King whiteKing;
    private King blackKing;

    private ZobristHash zobristHasher;
    private long positionHash;


    public Board(ZobristHash zobristHasher) {
        this.lastMove = null;
        this.isGameOver = false;
        this.isWhitesTurn = true;
        this.isCheckmate = false;
        this.whiteKing = null;
        this.blackKing = null;
        this.positionHash = 0;
        this.zobristHasher = zobristHasher;

    }

    // TODO: HANDLE MOVES THAT LEAD TO CHECKMATE
    // TODO: OPTIMIZE (filtering illegal moves)
    // depth parameter is only needed for killer moves
    public List<Move> getAllPossibleMoves(int depth) {
        List<Move> allMoves = new ArrayList<>();
        for (Piece[] pieceRow : getBoardState()) {
            for (Piece piece : pieceRow) {
                if (piece != null && piece.getIsWhite() == isWhitesTurn) {
                    allMoves.addAll(piece.generatePossibleMoves());
                }
            }
        }

        ChessUtils.removeIllegalMoves(this, allMoves);

        List<Move> captures = new ArrayList<>();
        List<Move> quietMoves = new ArrayList<>();

        for (Move move : allMoves) {
            if (move.getCapturedPiece() != null) {
                captures.add(move);
            } else {
                quietMoves.add(move);
            }

        }

        ChessUtils.sortCaptures(captures);
        ChessUtils.sortMovesByKillers(searcher, quietMoves, depth); // TODO: CHECK IF KILLER HEURISTIC WORKS CORRECTLY

        allMoves.clear();
        allMoves.addAll(captures);
        allMoves.addAll(quietMoves);

        return allMoves;
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

            King king = (King) move.getMovedPiece();
            king.setHasCastled(true);

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

//        System.out.println(this.toString());

        move.setPreviousCastlingRights(this.castlingRights);
        if (getIsWhitesTurn()) {
            if (whiteKing.getHasKingsideCastlingRights() || whiteKing.getHasQueensideCastlingRights()) {
                updateCastlingRights(move);
            }
        } else {
            if (blackKing.getHasKingsideCastlingRights() || blackKing.getHasQueensideCastlingRights()) {
                updateCastlingRights(move);
            }
        }

        setIsWhitesTurn(!getIsWhitesTurn());

        positionHash = zobristHasher.updatePositionHash(positionHash, move);
    }

    // TODO: EN PASSANT, CASTLING OR PROMOTION CAUSES INCONSISTENCIES IN BOARD STATE
    public void undoMove(Move move) {
        if (isCheckmate) {
            isCheckmate = false;
        }

        this.boardState[move.getStartRow()][move.getStartCol()] = move.getMovedPiece();
        if (!move.isEnPassant()) {
            this.boardState[move.getTargetRow()][move.getTargetCol()] = move.getCapturedPiece();
        } else {
            this.boardState[move.getTargetRow()][move.getTargetCol()] = null;
        }


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

            King king = (King) move.getMovedPiece();
            king.setHasCastled(false);

            Piece rook = this.boardState[move.getTargetRow()][rookEndCol];

            this.boardState[move.getTargetRow()][rookEndCol] = null;
            this.boardState[move.getTargetRow()][rookStartCol] = rook;

            rook.setCol(rookStartCol);
            rook.setMoveCount(rook.getMoveCount() - 1);
        }

        // TODO: THIS SHOULD NOT BE NEEDED, BUT REMOVING THIS CAUSES INDEX ERROR
//        if (move.getPromotionPiece() != null) {
//            // Replace the promoted piece with the original pawn
//            Piece originalPawn = move.getMovedPiece();
//            this.boardState[move.getTargetRow()][move.getTargetCol()] = null;
//            this.boardState[move.getStartRow()][move.getStartCol()] = originalPawn;
//        }

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

        positionHash = zobristHasher.updatePositionHash(positionHash, move);
    }



    public void updateCastlingRights(Move move) {
        if (move.getMovedPiece().getType() == PieceType.KING) {
            if (move.getMovedPiece().getIsWhite()) {
                this.castlingRights &= ~0b0011; //  NAND Operation
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

    // TODO: OPTIMIZE
    public boolean isCheckmate() {
        King king = getKing(isWhitesTurn);
        if (!isKingInCheck(king)) {
            return false;
        }

        boolean canEscape = false;

        // check if king can escape
        List<Move> kingMoves = king.generatePossibleMoves();
        for (Move move : kingMoves) {
            this.makeMove(move);
            if (!isKingInCheck(king)) {
                canEscape = true;
            }
            this.undoMove(move);

            if (canEscape) {
                return false;
            }
        }

        // check if pieces can block check or capture checking piece
        List<Move> allMoves = this.getAllPossibleMoves(0);
        for (Move move : allMoves) {
            if (move.getMovedPiece().getType() == PieceType.KING) {
                break;
            }
            this.makeMove(move);
            if (!isKingInCheck(king)) {
                canEscape = true;
            }
            this.undoMove(move);

            if (canEscape) {
                return false;
            }
        }

        return true;
    }

    // TODO: CHECK IF THIS WORKS
    public King getKing(boolean isWhite) {
        King king = isWhite ? this.whiteKing : this.blackKing;
        if (king != null) {
            return king;
        }

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




    public King getWhiteKing() {
        return whiteKing;
    }

    public void setWhiteKing(King whiteKing) {
        this.whiteKing = whiteKing;
    }

    public King getBlackKing() {
        return blackKing;
    }

    public void setBlackKing(King blackKing) {
        this.blackKing = blackKing;
    }

    public Piece[][] getBoardState() {
        return boardState;
    }

    public boolean getIsCheckmate() {
        return isCheckmate;
    }

    public void setIsCheckmate(boolean isCheckmate) {
        this.isCheckmate = isCheckmate;
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

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public long getPositionHash() {
        return positionHash;
    }

    public void setPositionHash(long positionHash) {
        this.positionHash = positionHash;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < CHESSBOARD_ROWS; row++) {
            for (int col = 0; col < CHESSBOARD_COLUMNS; col++) {
                Piece piece = boardState[7 - row][col];
                if (piece == null) {
                    sb.append(". "); // Use "." to represent empty squares
                } else {
                    char pieceChar = piece.getType() == PieceType.KNIGHT ? 'N' : piece.getType().toString().charAt(0);

                    if (piece.getIsWhite()) {
                        sb.append(Character.toUpperCase(pieceChar)).append(" ");
                    } else {
                        sb.append(Character.toLowerCase(pieceChar)).append(" ");
                    }
                }
            }
            sb.append("\n"); // Add a newline after each row
        }
        return sb.toString();
    }


}

