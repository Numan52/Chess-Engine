package org.example;

import org.example.Evaluation.PositionEvaluater;

import java.util.ArrayList;
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
            return new MoveScore(null, 0, new ArrayList<>());
        }

        if (depth == 0 || board.isGameOver()) {
            return new MoveScore(null, positionEvaluater.evaluatePosition(), new ArrayList<>());
        }

        Move bestMove = null;
        List<Move> bestPath = new ArrayList<>();

        if (isWhitesTurn) {
            List<Move> possibleMoves = board.getAllPossibleMoves();
            int maxEval = Integer.MIN_VALUE;
            for (Move move : possibleMoves) {
                board.makeMove(move);
                MoveScore childScore = minimax(board, depth - 1, alpha, beta, false, startTime);
                board.undoMove(move);

                if (childScore.score > maxEval) {
                    bestMove = move;
                    maxEval = childScore.score;
                    bestPath = new ArrayList<>(childScore.movePath);
                    bestPath.add(0, move); // Add the current move to the path
                }

                alpha = Math.max(alpha, childScore.score);
                if (beta <= alpha) {
                    break;
                }
            }

            return new MoveScore(bestMove, maxEval, bestPath);

        } else {
            List<Move> possibleMoves = board.getAllPossibleMoves();

            int minEval = Integer.MAX_VALUE;
            for (Move move : possibleMoves) {
                board.makeMove(move);
                MoveScore childScore = minimax(board, depth - 1, alpha, beta, true, startTime);
                board.undoMove(move);

                if (childScore.score < minEval) {
                    bestMove = move;
                    minEval = childScore.score;
                    bestPath = new ArrayList<>(childScore.movePath); // Copy the child's path
                    bestPath.add(0, move); // Add the current move to the path
                }

                beta = Math.min(beta, childScore.score);
                if (beta <= alpha) {
                    break;
                }

            }

            return new MoveScore(bestMove, minEval, bestPath);
        }

    }

    public MoveScore getBestMove() {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        List<Move> bestPath = new ArrayList<>();

        long startTime = System.currentTimeMillis();


        for (int depth = 1; depth <= maxDepth; depth++) {
            System.out.println("depth:" + depth);
            if (isTimeUp || System.currentTimeMillis() - startTime >= maxSearchTime) {
                break;
            }

            MoveScore moveScore = minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, board.getIsWhitesTurn() , startTime);

            if (!isTimeUp) {
                bestMove = moveScore.move;
                bestScore = moveScore.score;
                bestPath = moveScore.movePath;
            } else {
                System.out.println("times up: search cancelled at depth: " + depth );
            }

        }

        return new MoveScore(bestMove, bestScore, bestPath);
    }


}
