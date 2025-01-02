package org.example.chess.Evaluation;

import org.example.chess.Board;
import org.example.chess.Piece.Pawn;
import org.example.chess.Piece.Piece;
import org.example.chess.Piece.PieceType;

import java.util.ArrayList;
import java.util.List;

public class PawnEvaluater implements Evaluator {
    private Board board;


    public PawnEvaluater(Board board) {
        this.board = board;
    }

    @Override
    public int evaluate() {

        int evaluation = 0;

        evaluation -= evaluateIsolatedPawns(true);
        evaluation += evaluateIsolatedPawns(false);

        evaluation -= evaluateBackwardsPawns(true);
        evaluation += evaluateBackwardsPawns(false);

        return evaluation;
    }





    public int evaluateBackwardsPawns(boolean isWhite) {
        int penalty = 0;

        for (int file = 0; file < 8; file++) {
            List<Pawn> pawns = countPawnsOnFile(file, isWhite);
            for (Pawn pawn : pawns) {
                if (isBackwardPawn(pawn)) {
                    penalty += 40;
                }
            }

        }

        return penalty;
    }



    public boolean isBackwardPawn(Pawn pawn) {
        boolean inFirstFile = pawn.getCol() == 0;
        boolean inLastFile = pawn.getCol() == 7;
        int row = pawn.getRow();
        int col = pawn.getCol();
        int direction = pawn.getIsWhite() ? 1 : -1;

        boolean hasLeftSupport = !inFirstFile && hasSupportingPawnOnFile(row, col - 1, pawn.getIsWhite());
        boolean hasRightSupport = !inLastFile && hasSupportingPawnOnFile(row, col + 1, pawn.getIsWhite());


        if (hasLeftSupport || hasRightSupport) {
            return false;
        }

        if (getAdjacentPawns(pawn) >= squareControlledByPawns(row + direction, col, !pawn.getIsWhite())) {
            return false;
        }

        return true;
    }


    public int getAdjacentPawns(Pawn pawn) {
        int row = pawn.getRow();
        int col = pawn.getCol();
        int adjacentPawns = 0;
        boolean isLeftPiecePawn = false;
        boolean isRightPiecePawn = false;

        if (col > 0) {
            Piece leftPiece = board.getBoardState()[row][col - 1];
            isLeftPiecePawn = leftPiece != null && leftPiece.getType() == PieceType.PAWN && leftPiece.getIsWhite() == pawn.getIsWhite();
        }

        if (col < 7) {
            Piece rightPiece = board.getBoardState()[row][col + 1];
            isRightPiecePawn = rightPiece != null && rightPiece.getType() == PieceType.PAWN && rightPiece.getIsWhite() == pawn.getIsWhite();
        }

        if (isLeftPiecePawn) {
            adjacentPawns++;
        }
        if (isRightPiecePawn) {
            adjacentPawns++;
        }

        return adjacentPawns;
    }


    public boolean hasSupportingPawnOnFile(int row, int file, boolean isWhite) {
        List<Pawn> pawns = countPawnsOnFile(file, isWhite);

        for (Pawn pawn : pawns) {
            if (isWhite && pawn.getRow() < row) {
                return true;
            } else if (!isWhite && pawn.getRow() > row) {
                return true;
            }
        }

        return false;
    }


    public int squareControlledByPawns(int row, int col, boolean isWhite) {
        int controlled = 0;
        int direction = isWhite ? -1 : 1;

        if (row + direction >= 0 && row + direction < 8 && col + 1 < 8 && col - 1 >= 0) {
            if (col > 0) {
                Piece leftDiagonal = board.getBoardState()[row + direction][col - 1];
                if (leftDiagonal != null && leftDiagonal.getType() == PieceType.PAWN && leftDiagonal.getIsWhite() == isWhite) {
                    controlled++;
                }
            }
            if (col < 7) {
                Piece rightDiagonal = board.getBoardState()[row + direction][col + 1];
                if (rightDiagonal != null && rightDiagonal.getType() == PieceType.PAWN && rightDiagonal.getIsWhite() == isWhite) {
                    controlled++;
                }
            }
        }


        return controlled;
    }





    public int evaluateIsolatedPawns(boolean isWhite) {
        int penalty = 0;

        for (int file = 0; file < 8; file++) {
            List<Pawn> pawns = countPawnsOnFile(file, isWhite);

            penalty += calculateIsolationPenalty(file, pawns, isWhite);

        }

        return penalty;
    }


    public List<Pawn> countPawnsOnFile(int col, boolean isWhite) {
        List<Pawn> pawns = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            Piece piece = board.getBoardState()[row][col];
            if (piece != null && piece.getType() == PieceType.PAWN && piece.getIsWhite() == isWhite) {
                pawns.add((Pawn) piece);
            }
        }

        return pawns;
    }


    private int calculateIsolationPenalty(int file, List<Pawn> pawns, boolean isWhite) {
        int penalty = 0;

        boolean isIsolated = isPawnFileIsolated(file, isWhite);
        boolean isSemiOpen = isPawnFileSemiOpen(file, isWhite);


        if (isIsolated) {
            if (pawns.size() >= 2) { // doubled pawns
                penalty += 70;
                if (pawns.size() >= 3) {
                    penalty += 30;
                }
            } else {
                penalty += 20;
            }
        }
        if (isSemiOpen) {
            if (pawns.size() >= 2) {
                penalty += 30;
            }
            penalty += 10;
        }

        return penalty;
    }

        // TODO: FINISH
//    private boolean isPawnGroup(List<Pawn> pawns, int file, boolean isWhite) {
//        int groupSize = 1;
//        int firstPRow = isWhite? pawns.get(0).getRow() : pawns.get(1).getRow();
//        int direction = isWhite? 1 : -1;
//
//        for (int col = Math.max(0, file - 1); col < Math.min(7, file + 1); col++) {
//            for (int row = firstPRow; row < firstPRow + (direction * 2); row++) {
//
//            }
//        }
//
//        if ()
//    }


    private boolean isPawnFileIsolated(int file, boolean isWhite) {
        // todo: check if doubled pawns are group of 3
        boolean leftEmpty = true;
        boolean rightEmpty = true;

        if (file != 0) {
            leftEmpty = countPawnsOnFile(file - 1, isWhite).isEmpty();
        }
        if (file != 7) {
            rightEmpty = countPawnsOnFile(file + 1, isWhite).isEmpty();
        }
        


        return leftEmpty && rightEmpty ;
    }



    private boolean isPawnFileSemiOpen(int file, boolean isWhite) {
        boolean hasOpponentPawn = countPawnsOnFile(file, !isWhite).isEmpty();
        return !hasOpponentPawn;
    }





}
