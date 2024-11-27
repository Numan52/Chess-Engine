package org.example;

import org.example.Piece.Piece;
import org.example.Piece.PieceType;

public class PositionEvaluater {
    private Board board;
    private int evaluation;

    public PositionEvaluater(Board board) {
        this.board = board;
    }

    public int evaluatePosition() {
        for (Piece[] piecesRow : board.getBoardState()) {
            for (Piece piece : piecesRow) {
                if (piece != null && piece.getType() != PieceType.KING) {
                    if (piece.getIsWhite()) {
                        evaluation += piece.getValue();
                    }
                    else {
                        evaluation -= piece.getValue();
                    }
                }
            }
        }
        return 0;
    }
}
