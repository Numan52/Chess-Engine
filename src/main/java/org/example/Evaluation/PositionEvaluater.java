package org.example.Evaluation;

import org.example.Board;
import org.example.Piece.Piece;
import org.example.Piece.PieceType;

public class PositionEvaluater {
    private Board board;


    public PositionEvaluater(Board board) {
        this.board = board;
    }

    public int evaluatePosition() {
        int evaluation = 0;
        for (Piece[] piecesRow : board.getBoardState()) {
            for (Piece piece : piecesRow) {
                if (piece != null ) {
                    int pieceValue = piece.getValue();
                    int squareValue = applyPieceSquareTable(piece);

                    if (piece.getIsWhite() ) {
                        evaluation += pieceValue + squareValue;
                    }
                    else  {
                        evaluation -= pieceValue + squareValue;
                    }
                }
            }
        }
        return evaluation;
    }

    public int applyPieceSquareTable(Piece piece) {
        if (piece.getIsWhite()) {
            return piece.getPieceSquareTable()[7 - piece.getRow()][piece.getCol()];
        } else {
            return piece.getPieceSquareTable()[piece.getRow()][piece.getCol()];
        }
    }
}
