package org.example;

import java.util.List;

public class Searcher {
    private Board board;
    private int maxDepth;
    private PositionEvaluater positionEvaluater;
    private int maxSearchTime;
    private boolean isTimeUp;

    public Searcher(Board board, int maxDepth, int maxSearchTime, PositionEvaluater positionEvaluater) {
        this.board = board;
        this.maxDepth = maxDepth;
        this.maxSearchTime = maxSearchTime;
        this.positionEvaluater = positionEvaluater;
        this.isTimeUp = false;
    }

    // includes alpha beta pruning
    public MoveScore minimax(Board board, int depth, int alpha, int beta, boolean isWhitesTurn, long startTime) {
        if (isTimeUp || System.currentTimeMillis() - startTime >= maxSearchTime) {
            this.isTimeUp = true;
            return new MoveScore(null, 0);
        }
        if (depth == 0 || board.isGameOver()) {
            return new MoveScore(null, positionEvaluater.evaluatePosition());
        }

        Move bestMove = null;

        if (isWhitesTurn) {
            List<Move> possibleMoves = board.getAllPossibleMoves();
            int maxEval = Integer.MIN_VALUE;
            for (Move move : possibleMoves) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, false, startTime).score;
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
            List<Move> possibleMoves = board.getAllPossibleMoves();

            int minEval = Integer.MAX_VALUE;
            for (Move move : possibleMoves) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, true, startTime).score;
                board.undoMove(move);

                if (eval < minEval) {
                    bestMove = move;
                    minEval = eval;
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
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        long startTime = System.currentTimeMillis();


        for (int depth = 1; depth <= maxDepth; depth++) {
            if (isTimeUp || System.currentTimeMillis() - startTime >= maxSearchTime) {
                break;
            }

            MoveScore moveScore = minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.getIsWhitesTurn() , startTime);

            if (!isTimeUp) {
                bestMove = moveScore.move;
                bestScore = moveScore.score;
            } else {
                System.out.println("times up: search cancelled at depth: " + depth );
            }

        }

        return new MoveScore(bestMove, bestScore);
    }
}
