package org.example;

import org.example.Piece.King;
import org.example.Piece.Piece;
import org.example.Piece.PieceType;
import org.example.Utils.BoardParser;

public class ChessEngine {
    private Board board;
    private Searcher searcher;
    private PositionEvaluater positionEvaluater;

    public ChessEngine(Board board, Searcher searcher, PositionEvaluater positionEvaluater) {
        this.board = board;
        this.searcher = searcher;
        this.positionEvaluater = positionEvaluater;
    }


    public void updateBoard(String fen) {
        Piece[][] boardState = BoardParser.fenToBoardState(board, fen);
        boolean isWhitesTurn = BoardParser.parseIsWhitesTurn(fen);
        String enPassantField = BoardParser.getEnPassantField(fen);

        board.setBoardState(boardState);
        board.setIsWhitesTurn(isWhitesTurn);
        board.setEnPassantField(enPassantField); // TODO: NEED TO HANDLE MANUALLY DURING SEARCH


        updateCastlingRights(fen);

    }

    // TODO: NEED TO HANDLE MANUALLY DURING SEARCH
    public void updateCastlingRights(String fen) {
        for (Piece[] piecesRow : board.getBoardState()) {
            for (Piece piece : piecesRow) {
                if (piece != null && piece.getType() == PieceType.KING ) {
                    if (piece.getIsWhite()) {
                        ((King) piece).setHasKingsideCastlingRight(BoardParser.canWhiteCastleKingside(fen));
                        ((King) piece).setHasQueensideCastlingRight(BoardParser.canWhiteCastleQueenside(fen));
                    } else {
                        ((King) piece).setHasKingsideCastlingRight(BoardParser.canBlackCastleKingside(fen));
                        ((King) piece).setHasQueensideCastlingRight(BoardParser.canBlackCastleQueenside(fen));
                    }

                }
            }
        }
    }


    public MoveScore calculateBestMove() {
        long start = System.currentTimeMillis();
        MoveScore moveScore = searcher.getBestMove();
        long finish = System.currentTimeMillis();

        double timeElapsed = (double) (finish - start) / 1000;
        System.out.println("Search time: " + timeElapsed);

        return moveScore;
    }

    public void applyBestMove() {
        MoveScore moveScore = calculateBestMove();
        board.makeMove(moveScore.move);

        System.out.println();
        System.out.println("Best move: " + moveScore.move + ", " + moveScore.score);
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }


}
