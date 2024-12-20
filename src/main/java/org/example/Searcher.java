package org.example;

import org.example.Evaluation.PositionEvaluater;
import org.example.Evaluation.TranspositionTable;

import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private Board board;
    private int maxDepth;
    private PositionEvaluater positionEvaluater;
    private TranspositionTable transpositionTable;
    private int maxSearchTime;
    private boolean isTimeUp;
    private Move[][] killerMoves;



    public Searcher(Board board, int maxDepth, int maxSearchTime, PositionEvaluater positionEvaluater, TranspositionTable transpositionTable) {
        this.board = board;
        this.maxDepth = maxDepth;
        this.maxSearchTime = maxSearchTime;
        this.positionEvaluater = positionEvaluater;
        this.transpositionTable = transpositionTable;
        this.isTimeUp = false;
        this.killerMoves = new Move[maxDepth + 1][2]; // 2 Killer moves per depth
        board.setSearcher(this);
    }


    public MoveScore minimax(Board board, int depth, int alpha, int beta, boolean isWhitesTurn, long startTime) {
        if (isTimeUp || System.currentTimeMillis() - startTime >= maxSearchTime) {
            this.isTimeUp = true;
            return new MoveScore(null, 0, new ArrayList<>());
        }

        // TODO: CHECK FOR DRAW (INSUFFICIENT MATERIAL, STALEMATE, 50-MOVE RULE)

        if (board.isCheckmate()) {
            board.setIsCheckmate(true);

        }

        if (depth == 0 || board.getIsCheckmate()) {
            return new MoveScore(null, positionEvaluater.evaluatePosition(depth), new ArrayList<>());

        }
        // TODO: random PIECES ARE SPAWNING ON THE BOARD
        TranspositionTable.TranspositionEntry entry = transpositionTable.lookup(board.getPositionHash());
        if (entry != null && entry.depth >= depth) {
            if (entry.evalType == 0) {
                return new MoveScore(entry.bestMove, entry.score, new ArrayList<>());
            }
            if (entry.evalType == 1) {
                beta = entry.score;
            }
            if (entry.evalType == -1) {
                alpha = entry.score;
            }

            if (beta <= alpha) {
                return new MoveScore(entry.bestMove, entry.score, new ArrayList<>());
            }
        }

        Move bestMove = null;
        List<Move> bestPath = new ArrayList<>();
        int evalType = 0;

        if (isWhitesTurn) {
            List<Move> possibleMoves = board.getAllPossibleMoves(depth);
            int maxEval = Integer.MIN_VALUE;

            for (Move move : possibleMoves) {
                if (isTimeUp) {
                    break;
                }
                //TODO: WHY CHECK THE OTHER MOVES IF CHECKMATE WAS FOUND
                board.makeMove(move);
                MoveScore childScore = minimax(board, depth - 1, alpha, beta, false, startTime);
                boolean isCheckmate = board.getIsCheckmate();
                board.undoMove(move);


                if (childScore.score > maxEval) {
                    bestMove = move;
                    maxEval = childScore.score;
                    bestPath = new ArrayList<>(childScore.movePath);
                    bestPath.add(0, move); // Add the current move to the path
                }


                alpha = Math.max(alpha, childScore.score);
                if (beta <= alpha) {
                    if (move.getCapturedPiece() == null) {
                        addKillerMove(move, depth);
                    }
                    evalType = -1;
                    break;
                }

                if (isCheckmate) {
                    break;
                }
            }
            transpositionTable.store(board.getPositionHash(), depth, maxEval, false, evalType, bestMove);
            return new MoveScore(bestMove, maxEval, bestPath);

        } else {
            List<Move> possibleMoves = board.getAllPossibleMoves(depth);

            int minEval = Integer.MAX_VALUE;
            for (Move move : possibleMoves) {
                if (isTimeUp) {
                    break;
                }
                board.makeMove(move);
                MoveScore childScore = minimax(board, depth - 1, alpha, beta, true, startTime);
                boolean isCheckmate = board.getIsCheckmate();
                board.undoMove(move);

                if (childScore.score < minEval) {
                    bestMove = move;
                    minEval = childScore.score;
                    bestPath = new ArrayList<>(childScore.movePath); // Copy the child's path
                    bestPath.add(0, move); // Add the current move to the path
                }

                beta = Math.min(beta, childScore.score);
                if (beta <= alpha) {
                    if (move.getCapturedPiece() == null) {
                        addKillerMove(move, depth);
                    }
                    evalType = 1;
                    break;
                }

                if (isCheckmate) {
                    break;
                }
            }

            transpositionTable.store(board.getPositionHash(), depth, minEval, false, evalType, bestMove);
            return new MoveScore(bestMove, minEval, bestPath);
        }

    }


    private void addKillerMove(Move move, int depth) {
        if (killerMoves[depth][0] == null || !killerMoves[depth][0].equals(move)) {
            killerMoves[depth][1] = killerMoves[depth][0];
            killerMoves[depth][0] = move;
        }
    }

    public boolean isKillerMove(Move move, int depth) {
        return (killerMoves[depth][0] != null && killerMoves[depth][0].equals(move)) ||
                (killerMoves[depth][1] != null && killerMoves[depth][1].equals(move));
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


    public Move[][] getKillerMoves() {
        return killerMoves;
    }


    public TranspositionTable getTranspositionTable() {
        return transpositionTable;
    }
}
