package org.example;

public class ChessEngine {
    private Board board;
    private Searcher searcher;
    private PositionEvaluater positionEvaluater;

    public ChessEngine(Board board, Searcher searcher, PositionEvaluater positionEvaluater) {
        this.board = board;
        this.searcher = searcher;
        this.positionEvaluater = positionEvaluater;
    }


    public Move calculateBestMove(boolean isWhitesTurn) {
        return searcher.getBestMove(isWhitesTurn);
    }

    public void applyBestMove(boolean isWhitesTurn) {
        Move move = calculateBestMove(isWhitesTurn);
        board.makeMove(move);
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }


}
