package org.example;

import org.example.Piece.Piece;

import javax.swing.text.Position;
import java.util.List;

public class Searcher {
    private Board board;
    private int depth;
    private PositionEvaluater positionEvaluater;

    public Searcher(Board board, int depth, PositionEvaluater positionEvaluater) {
        this.board = board;
        this.depth = depth;
        this.positionEvaluater = positionEvaluater;
    }

    public int minimax(Board board, int depth, int alpha, int beta, boolean isWhitesTurn) {
        if (depth == 0 || board.isGameOver()) {
            return positionEvaluater.evaluatePosition();
        }

        List<Move> possibleMoves = board.getAllPossibleMoves(isWhitesTurn);

        if (isWhitesTurn) {
            int maxEval = Integer.MIN_VALUE;


        } else {
            int minEval = Integer.MAX_VALUE;
        }

    }
}
