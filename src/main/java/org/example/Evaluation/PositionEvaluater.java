package org.example.Evaluation;

import org.example.Board;
import org.example.Piece.King;
import org.example.Piece.Piece;
import org.example.Piece.PieceType;

import java.util.ArrayList;
import java.util.List;

public class PositionEvaluater {
    private Board board;
    private List<PieceEvaluater> pieceEvaluaters;


    public PositionEvaluater(Board board, List<PieceEvaluater> pieceEvaluaters) {
        this.board = board;
        this.pieceEvaluaters = pieceEvaluaters;
    }

    public int evaluatePosition(int depth) {
        if (board.getIsCheckmate()) {
            return evaluateCheckmate(depth);
        }

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

        for (PieceEvaluater pieceEvaluater : pieceEvaluaters) {
            evaluation += pieceEvaluater.evaluate();
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


    public int evaluateCheckmate(int depth) {
        int offset = 1000;
        // check if side whose turn it is, is checkmated
        if (board.getIsWhitesTurn()) {
            return Integer.MIN_VALUE + offset + depth;
        } else {
            return Integer.MAX_VALUE - offset - depth;
        }

    }


}
