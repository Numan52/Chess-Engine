package org.example.Evaluation;

import org.example.Board;
import org.example.Piece.Piece;
import org.example.Piece.PieceType;
import org.example.Utils.ChessUtils;

import java.util.List;

public class ActivityEvaluator implements Evaluator {

    private Board board;

    public ActivityEvaluator(Board board) {
        this.board = board;
    }

    @Override
    public int evaluate() {
        int evaluation = 0;

        evaluation -= evaluateDevelopment(true);
        evaluation += evaluateDevelopment(false);
//        System.out.println("knight and bishop development eval: " + evaluation);
        evaluation -= evaluateCenterControl(true);
        evaluation += evaluateCenterControl(false);
//        System.out.println("CENTER control eval: " + evaluation);
        evaluation -= (int) evaluateMobility(true);
        evaluation += (int) evaluateMobility(false);

        return evaluation;
    }


    public int evaluateDevelopment(boolean isWhite) {
        int penalty = 0;

        penalty += evaluateKnightDevelopment(isWhite);
        penalty += evaluateBishopDevelopment(isWhite);

        return penalty;

    }


    public int evaluateKnightDevelopment(boolean isWhite) {
        int penalty = 0;
        Piece[][] boardState = board.getBoardState();
        int row = isWhite ? 0 : 7;

        boolean isFirstUndeveloped = boardState[row][1] != null && boardState[row][1].getType() == PieceType.KNIGHT &&
                boardState[row][1].getIsWhite() == isWhite;
        boolean isSecondUndeveloped = boardState[row][6] != null && boardState[row][6].getType() == PieceType.KNIGHT &&
                boardState[row][6].getIsWhite() == isWhite;

        if (isFirstUndeveloped) {
            penalty += 30;
        }
        if (isSecondUndeveloped) {
            penalty += 30;
        }

        return penalty;
    }


    public int evaluateBishopDevelopment(boolean isWhite) {
        int penalty = 0;
        Piece[][] boardState = board.getBoardState();
        int row = isWhite ? 0 : 7;
        int firstCol = 2;
        int secondCol = 5;  

        boolean isFirstUndeveloped = isPieceType(row, firstCol, PieceType.BISHOP, isWhite);
        boolean isSecondUndeveloped = isPieceType(row, secondCol, PieceType.BISHOP, isWhite);;

        if (isFirstUndeveloped) {
            penalty += 25;
        }
        if (isSecondUndeveloped) {
            penalty += 25;
        }
        return penalty;
    }


    public int evaluateCenterControl(boolean isWhite) {
        int penalty = 0;

        penalty += evaluatePawnCenterControl(isWhite);

        return penalty;
    }


    public boolean isPieceType(int row, int col, PieceType type, boolean isWhite) {
        return board.getBoardState()[row][col] != null && board.getBoardState()[row][col].getType() == type &&
                board.getBoardState()[row][col].getIsWhite() == isWhite;
    }


    public int evaluatePawnCenterControl(boolean isWhite) {
        int penalty = 0;

        int centerRow = isWhite ? 3 : 4;
        int thirdRow = isWhite ? 2 : 5;
        int cFile = 2;
        int dFile = 3;
        int eFile = 4;
        int fFile = 5;

        boolean dCenterOccupied = isPieceType(centerRow, dFile, PieceType.PAWN, isWhite);
        boolean eCenterOccupied = isPieceType(centerRow, eFile, PieceType.PAWN, isWhite);
        boolean cCenterSupport = isPieceType(centerRow, cFile, PieceType.PAWN, isWhite);
        boolean fCenterSupport = isPieceType(thirdRow, fFile, PieceType.PAWN, isWhite);
        boolean dCenterSupport = isPieceType(thirdRow, dFile, PieceType.PAWN, isWhite);
        boolean eCenterSupport = isPieceType(thirdRow, eFile, PieceType.PAWN, isWhite);

        if (dCenterOccupied) {
            penalty -= 40;
        }
        if (eCenterOccupied) {
            penalty -= 40;
        }
        if (cCenterSupport) {
            penalty -= 10;
        }
        if (fCenterSupport) {
            penalty -= 10;
        }
        if (dCenterSupport) {
            penalty -= 10;
        }
        if (eCenterSupport) {
            penalty -= 10;
        }

        return penalty;
    }


    public double evaluateMobility(boolean isWhite) {
        List<Piece> activePieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        double mobility = 0;
        int penalty = 0;
        int maxKnightMobility = 8;
        int maxBishopMobility = 13;
        int maxRookMobility = 14;

        for (Piece piece : activePieces) {
            int[][] bishopDirections = {{1, -1}, {1, 1}, {-1, 1}, {-1, -1}};

            if (piece.getType() == PieceType.KNIGHT) {
                mobility += evaluateKnightMobility(piece);
                penalty += calculatePenalty(mobility, maxKnightMobility, piece);
            }

            if (piece.getType() == PieceType.BISHOP) {
                mobility += evaluateSlidingPieceMobility(piece, bishopDirections);
                penalty += calculatePenalty(mobility, maxBishopMobility, piece);
            }

            int[][] rookDirections = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
            if (piece.getType() == PieceType.ROOK) {
                mobility += evaluateSlidingPieceMobility(piece, rookDirections);
                penalty += calculatePenalty(mobility, maxRookMobility, piece);
            }

            int[][] queenDirections = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, -1}, {1, 1}, {-1, 1}, {-1, -1}};
            if (piece.getType() == PieceType.QUEEN) {
                mobility += evaluateSlidingPieceMobility(piece, queenDirections);
            }
        }
        return penalty;
    }



    private int evaluateKnightMobility(Piece knight) {
        int[][] squares = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
        int mobility = 0;

        for (int[] square : squares) {
            int targetRow = knight.getRow() + square[0];
            int targetCol = knight.getCol() + square[1];

            if (knight.canMoveTo(targetRow, targetCol)) {
                mobility++;
            }
        }

        return mobility;
    }



    private double evaluateSlidingPieceMobility(Piece piece, int[][] directions) {
        double mobility = 0;

        for (int[] direction : directions) {
            int currentRow = piece.getRow();
            int currentCol = piece.getCol();

            while (true) {
                currentRow += direction[0];
                currentCol += direction[1];

                if (!ChessUtils.isWithinBoard(currentRow, currentCol)) {
                    break;
                }

                Piece blockingPiece = board.getBoardState()[currentRow][currentCol];

                if (blockingPiece == null) {
                    mobility++;
                    if (piece.getType() == PieceType.ROOK) {
                        if (direction[0] == 1 || direction[0] == -1) {
                            mobility++;
                        }
                    }

                    if (piece.getType() == PieceType.BISHOP) {
                        if (direction[0] == 1) {
                            mobility++;
                        }
                    }
                } else if (blockingPiece.getIsWhite() != piece.getIsWhite()) {
                    mobility++;
                    break;

                } else if (blockingPiece.getIsWhite() == piece.getIsWhite()) {
                    mobility+= 0.5;
                    break;
                } else {
                    break;
                }
            }
        }

        return mobility;
    }


    public int calculatePenalty(double mobility, int maxMobility, Piece piece) {
        int penalty = 0;
        int normalizedMobility = (int) ((mobility * 100) / maxMobility); // value between 0 and 100
        double multiplier = 1;

        switch (piece.getType()) {
            case KNIGHT, BISHOP -> multiplier = board.isOpeningPhase() ? 1.5 : 1;
            case ROOK, QUEEN -> multiplier = board.isEndgamePhase() ? 1.5 : 1;
        }


        if (normalizedMobility == 0) {
            penalty += 40;
        } else if (normalizedMobility < 30) {
            penalty += 25;
        } else if (normalizedMobility < 60) {
            penalty += 15;
        }

        penalty *= multiplier;

        return penalty;
    }

}
