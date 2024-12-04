package org.example;

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
    public MoveScore minimax(Board board, int depth, int alpha, int beta, boolean isWhitesTurn) {
        if (depth == 0 || board.isGameOver()) {
            return new MoveScore(null, positionEvaluater.evaluatePosition());
        }

        Move bestMove = null;

        if (isWhitesTurn) {
            List<Move> possibleMoves = board.getAllPossibleMoves();
            int maxEval = Integer.MIN_VALUE;
            for (Move move : possibleMoves) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, false).score;
                board.undoMove(move);

                if (eval > maxEval) {
                    bestMove = move;
                    maxEval = eval;
                }

                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return new MoveScore(bestMove, maxEval);

        } else {
            System.out.print("Board State: ");
            List<Move> possibleMoves = board.getAllPossibleMoves();

            int minEval = Integer.MAX_VALUE;
            for (Move move : possibleMoves) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, true).score;
                board.undoMove(move);

                if (eval < minEval) {
                    minEval = Math.min(minEval, eval);
                    bestMove = move;
                }

                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }

            }
            return new MoveScore(bestMove, minEval);
        }

    }

    public MoveScore getBestMove() {
        return minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.getIsWhitesTurn());
    }
}
