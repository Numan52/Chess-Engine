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

    // includes alpha beta pruning
    public int minimax(Board board, int depth, int alpha, int beta, boolean isWhitesTurn) {
        if (depth == 0 || board.isGameOver()) {
            return positionEvaluater.evaluatePosition();
        }

        if (isWhitesTurn) {
            List<Move> possibleMoves = board.getAllPossibleMoves(true);
            int maxEval = Integer.MIN_VALUE;
            for (Move move : possibleMoves) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
                board.undoMove(move);
            }
            return maxEval;

        } else {
            List<Move> possibleMoves = board.getAllPossibleMoves(false);
            int minEval = Integer.MAX_VALUE;
            for (Move move : possibleMoves) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
                board.undoMove(move);
            }
            return minEval;
        }

    }
}
