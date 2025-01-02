package org.example.chess.Evaluation;

import org.example.chess.Board;
import org.example.chess.Piece.King;
import org.example.chess.Piece.Piece;
import org.example.chess.Piece.PieceType;

public class KingSafetyEvaluater implements Evaluator {
    private Board board;

    // TODO: ENDGAME HANDLING
    public KingSafetyEvaluater(Board board) {
        this.board = board;
    }


    @Override
    public int evaluate() {
        int evaluation = 0;
        King whiteKing = board.getWhiteKing();
        King blackKing = board.getBlackKing();

        if (!board.isEndgamePhase()) {
            evaluation -= evaluatePawnShield(whiteKing);
            evaluation += evaluatePawnShield(blackKing);

            evaluation -= evaluateFileSafety(whiteKing);
            evaluation += evaluateFileSafety(blackKing);

//            evaluation -= evaluateKingsRow(whiteKing);
//            evaluation += evaluateKingsRow(blackKing);

            evaluation -= evaluateKingsCol(whiteKing);
            evaluation += evaluateKingsCol(blackKing);

            evaluation -= evaluateKingCastling(whiteKing);
            evaluation += evaluateKingCastling(blackKing);
        }


        return evaluation;
    }



    public int evaluatePawnShield(King king) {
        int penalty = 0;

        int kingRow = king.getRow();
        int kingCol = king.getCol();
        int pawnShieldRow = king.getIsWhite() ? kingRow + 1 : kingRow - 1; // pawn shield only matters if king is on first 3 rows


        // check the 3 protecting pawns in front of king
        if (checkPawnshieldApplies(king)) {
            for (int col = Math.max(0, kingCol - 1); col <= Math.min(7, kingCol + 1); col++) {
                Piece piece = board.getBoardState()[pawnShieldRow][col];
                if (piece != null && piece.getType() == PieceType.PAWN && piece.getIsWhite() == king.getIsWhite()) {
                    penalty -= 30;  // minus == bonus
                }
            }
        }


        return penalty;
    }

    public boolean checkPawnshieldApplies(Piece king) {
        int[] validRows = king.getIsWhite() ? new int[]{0, 1, 2} : new int[]{7, 6, 5};
        return !board.isEndgamePhase() && king.getCol() != 3 && king.getCol() != 4 && king.getRow() ==  validRows[0] && king.getRow() == validRows[1] && king.getRow() == validRows[2];
    }


    public int evaluateFileSafety(King king) {
        int penalty = 0;
        int kingCol = king.getCol();
        boolean hasOppPawn = false;
        boolean hasFriendlyPawn = false;


        for (int col = Math.max(0, kingCol - 1); col <= Math.min(7, kingCol + 1); col++) {
            for (int row = 1; row < board.getBoardState().length - 1; row++) {
                Piece piece = board.getBoardState()[row][col];
                if (piece != null && piece.getType() == PieceType.PAWN ) {
                    if (piece.getIsWhite() == king.getIsWhite()) {
                        hasFriendlyPawn = true;
                    }
                    else {
                        hasOppPawn = true;
                    }
                }

            }
            if (!hasOppPawn && !hasFriendlyPawn) {
                penalty += 50;
            } else if (hasOppPawn && !hasFriendlyPawn) {
                penalty += 25;
            } else if (!hasOppPawn && hasFriendlyPawn) {
                penalty += 20;
            }
        }


        return penalty;

    }


    public int evaluateKingsRow(King king) {
        int kingRow = king.getRow();
        int penalty = 0;

        int[] penalties = new int[]{0, 30, 50, 80, 90, 90, 90, 90}; // for each row

        if (king.getIsWhite()) {
            penalty += penalties[kingRow];
        } else {
            penalty += penalties[7 - kingRow];
        }

        return penalty;
    }


    public int evaluateKingsCol(King king) {
        int kingRow = king.getRow();
        int kingCol = king.getCol();
        int penalty = 0;

        if (kingRow != 0 && kingRow != 7) {
            return 0;
        }


        if (!king.getHasKingsideCastlingRights() && !king.getHasQueensideCastlingRights() ) {
            if (isKingBlockingRookIn(king)) {
                penalty += 80;
            }
        }

        return penalty;
    }


    public int evaluateKingCastling(King king) {
        int penalty = 0;

        if (!king.getHasCastled()) {
            penalty += 50;
        }

        return penalty;
    }


    public boolean isKingBlockingRookIn(King king) {
        int kingRow = king.getRow();
        int kingCol = king.getCol();

        if (kingCol <= 3) {
            for (int col = 0; col < kingCol; col++) {
                Piece piece = board.getBoardState()[kingRow][col];
                if (piece != null && piece.getType() == PieceType.ROOK &&
                        piece.getIsWhite() == king.getIsWhite()) {
                    return true;
                }
            }
        }

        if (kingCol >= 4) {
            for (int col = Math.min(7, kingCol + 1); col < 8; col++) {
                Piece piece = board.getBoardState()[kingRow][col];
                if (piece != null && piece.getType() == PieceType.ROOK &&
                        piece.getIsWhite() == king.getIsWhite()) {
                    return true;
                }
            }
        }

        return false;

    }



}
