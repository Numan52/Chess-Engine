package org.example.Utils;

import org.example.Board;
import org.example.Move;
import org.example.Piece.*;
import org.example.Searcher;

import java.util.ArrayList;
import java.util.List;

public class ChessUtils {
    public static List<PieceType> getPromotionOptions() {
        return List.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT);
    }


    public static int getEnPassantFile(Board board) {
        int col = -1;
        Move lastMove = board.getLastMove();

        if (lastMove == null) {
            col = ChessUtils.enPassantFieldToCol(board);
            return col;
        }

        if (lastMove.getMovedPiece().getType() != PieceType.PAWN) {
            return -1;
        }

        int startRow = lastMove.getStartRow();
        int endRow = lastMove.getTargetRow();

        boolean isTwoSquareMove = Math.abs(endRow - startRow) == 2;
        if (!isTwoSquareMove) {
            return -1;
        }

        return lastMove.getTargetCol();

    }


    public static int enPassantFieldToCol(Board board) {
        if (board.getEnPassantField().equals("-")) {
            return -1;
        }
        return board.getEnPassantField().charAt(0) - (int) 'a';
    }





    public static void sortMovesByKillers(Searcher searcher, List<Move> quietMoves, int depth) {
        quietMoves.sort((move1, move2) -> {
            if (searcher.isKillerMove(move1, depth) && !searcher.isKillerMove(move2, depth)) {
                return -1;
            } else if (searcher.isKillerMove(move2, depth) && !searcher.isKillerMove(move1, depth)) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    // MVV-LVA
    public static void sortCaptures(List<Move> moves) {
        moves.sort((move1, move2) -> {
            int moveOneValueDiff = move1.getCapturedPiece().getValue() - move1.getMovedPiece().getValue();
            int moveTwoValueDiff = move2.getCapturedPiece().getValue() - move2.getMovedPiece().getValue();


            return Integer.compare(moveTwoValueDiff, moveOneValueDiff);
        });

    }


    public static void removeIllegalMoves(Board board, List<Move> moves) {
        List<Move> movesToRemove = new ArrayList<>();
        for (Move move : moves) {
            boolean isWhitesMove = move.getMovedPiece().getIsWhite();
            King king = board.getKing(isWhitesMove);
            board.makeMove(move);
//            System.out.println("move: " + move);

            if (board.isKingInCheck(king)){
                movesToRemove.add(move);
            }

            board.undoMove(move);
        }

        moves.removeAll(movesToRemove);
    }


}
