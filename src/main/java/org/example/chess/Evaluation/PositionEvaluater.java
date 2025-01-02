package org.example.chess.Evaluation;

import org.example.chess.Board;
import org.example.chess.Piece.Piece;

import java.util.List;

public class PositionEvaluater {
    private Board board;
    private List<Evaluator> evaluators;


    public PositionEvaluater(Board board, List<Evaluator> evaluators) {
        this.board = board;
        this.evaluators = evaluators;
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

        for (Evaluator evaluator : evaluators) {
            evaluation += evaluator.evaluate();
        }

        return evaluation;
    }

    public int applyPieceSquareTable(Piece piece) {
        boolean isEndgame = board.isEndgamePhase();

        if (piece.getIsWhite()) {
            return piece.getPieceSquareTable(isEndgame)[7 - piece.getRow()][piece.getCol()];
        } else {
            return piece.getPieceSquareTable(isEndgame)[piece.getRow()][piece.getCol()];
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
