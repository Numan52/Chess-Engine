package org.example.Evaluation;

import org.example.Board;
import org.example.Piece.Piece;
import org.example.Piece.PieceType;

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


        return evaluation;
    }


//    public int evaluateMobility(boolean isWhite) {
//
//    }


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
            penalty -= 30;
        }
        if (eCenterOccupied) {
            penalty -= 30;
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


    public int evaluateBishopMobility(boolean isWhite) {
        int penalty = 0;

        return 0;
    }
}
