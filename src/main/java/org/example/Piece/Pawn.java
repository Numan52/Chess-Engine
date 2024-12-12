package org.example.Piece;


import org.example.Board;
import org.example.Utils.ChessUtils;
import org.example.Move;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    private static final int VALUE = 100;

    public Pawn(Board chessboard, int x, int y, boolean isWhite) {
        super(chessboard, x, y, isWhite, PieceType.PAWN);
    }


    @Override
    public boolean canMoveTo(int targetRow, int targetCol) {
        int movedRows = Math.abs(targetRow - this.getRow());
        int movedCols = Math.abs(targetCol - this.getCol());
        int startingRow = this.getIsWhite() ? 1 : 6;

        if (!isWithinBoard(targetRow, targetCol) || isFriendlyPiece(targetRow, targetCol)) {
            return false;
        }

        // check en passant
        if (!getChessboard().getEnPassantField().equals("-")) {
            int enPassantRow = calculateEnPassantRow();
            int enPassantCol = calculateEnPassantCol();
            System.out.println(enPassantRow + " " + enPassantCol);

        }


        if ((targetRow <= this.getRow() && getIsWhite()) ||
            (targetRow >= this.getRow() && !getIsWhite()) ||
            (targetCol != this.getCol() && targetRow == this.getRow()))
        {
            return false;
        }

        Piece pieceAtTargetLocation = this.getChessboard().getBoardState()[targetRow][targetCol];

        // 1 square forward
        if (movedRows == 1 && movedCols == 0 && pieceAtTargetLocation == null) {
            return true;
        }
        // 2 squares forward
        if (movedRows == 2 && this.getRow() == startingRow && pieceAtTargetLocation == null && !isPieceBlockingLine(targetRow, targetCol)) {
            return true;
        }
        if (pieceAtTargetLocation != null && movedRows == 1 && movedCols == 1 ) {
            return true;
        }

        return false;
    }




    @Override
    public List<Move> generatePossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(generateForwardMoves());
        possibleMoves.addAll(generateCaptures());
        possibleMoves.addAll(generateEnPassantMove());

        return possibleMoves;
    }


    public List<Move> generateForwardMoves() {
        List<Move> possibleForwardMoves = new ArrayList<>();
        int direction = this.getIsWhite() ? 1 : -1; // white pawns move to higher rows, black pawns to lower ones
        int oneMoveForward = direction;
        int twoMovesForward = 2 * direction;

        int targetRow = this.getRow() + oneMoveForward;
        // one move forward
        if (this.canMoveTo(targetRow, this.getCol())) {
            if (isPromotionRow(targetRow)) {
                for (PieceType pieceType : ChessUtils.getPromotionOptions()) {
                    possibleForwardMoves.add(
                            new Move(this.getRow(), this.getCol(), targetRow, this.getCol(), this,
                                    null, false, false, pieceType ));
                }
            } else {
                possibleForwardMoves.add(new Move(
                        this.getRow(), this.getCol(), targetRow, this.getCol(), this
                ));
            }
        }

        // two moves forward
        targetRow = this.getRow() + twoMovesForward;
        if (this.canMoveTo(targetRow, this.getCol())) {
            possibleForwardMoves.add(new Move(
                    this.getRow(), this.getCol(), targetRow, this.getCol(), this
            ));
        }

        return possibleForwardMoves;
    }


    public List<Move> generateCaptures() {
        List<Move> possibleCaptures = new ArrayList<>();
        int direction = this.getIsWhite() ? 1 : -1;

        // Diagonal capture to the left
        generateCapture(possibleCaptures, this.getRow() + direction, this.getCol() - 1);
        // Diagonal capture to the right
        generateCapture(possibleCaptures, this.getRow() + direction, this.getCol() + 1);

        return possibleCaptures;
    }


    public void generateCapture(List<Move> possibleCaptures, int targetRow, int targetCol) {
        if (this.canMoveTo(targetRow, targetCol)) {
            Piece capturedPiece = this.getChessboard().getBoardState()[targetRow][targetCol];
            if (isPromotionRow(targetRow)) {
                for (PieceType pieceType : ChessUtils.getPromotionOptions()) {
                    possibleCaptures.add(
                            new Move(this.getRow(), this.getCol(), targetRow, this.getCol(), this,
                                    capturedPiece, false, false, pieceType ));
                }
            } else {
                possibleCaptures.add(new Move(
                        this.getRow(), this.getCol(), targetRow, targetCol, this, capturedPiece,
                        false, false, null
                ));
            }
        }
    }

    //TODO: meine augen tun weh
    // enPassantField is the field that the pawn will land in if after performing en passant (eg. f3)
    public List<Move> generateEnPassantMove() {
        List<Move> possibleEnPassantMoves = new ArrayList<>();



        if (this.getChessboard().getLastMove() == null) {
            if (this.getChessboard().getEnPassantField().equals("-")) {
                return List.of();
            } else {
                int enPassantRow = calculateEnPassantRow();
                int enPassantCol = calculateEnPassantCol();
                int direction = this.getIsWhite() ? -1 : 1;
                int opponentPawnRow = enPassantRow + direction;


                if (this.getRow() == opponentPawnRow &&
                        (this.getCol() == enPassantCol - 1 || this.getCol() == enPassantCol + 1)) {
                    Piece capturedPiece = this.getChessboard().getBoardState()[this.getRow()][enPassantCol];
                    possibleEnPassantMoves.add(new Move(
                                    this.getRow(), this.getCol(), enPassantRow, enPassantCol, this, capturedPiece,
                                    false, true, null
                            )
                    );
                }
                return possibleEnPassantMoves;
            }
        }

        if (this.getChessboard().getLastMove().getMovedPiece().getType() != PieceType.PAWN) {
            return List.of();
        }

        int startRow = this.getChessboard().getLastMove().getStartRow();
        int endRow = this.getChessboard().getLastMove().getTargetRow();
        int col  = this.getChessboard().getLastMove().getTargetCol();
        int direction = this.getIsWhite() ? 1 : -1;

        boolean isTwoSquareMove = Math.abs(endRow - startRow) == 2;
        if (!isTwoSquareMove) {
            return List.of();
        }

        if (this.getRow() == endRow &&
                (this.getCol() == col - 1 || this.getCol() == col + 1)) {
            Piece capturedPiece = this.getChessboard().getBoardState()[this.getRow()][col];
            possibleEnPassantMoves.add(new Move(
                            this.getRow(), this.getCol(), this.getRow() + direction, col, this, capturedPiece,
                            false, true, null
                    )
            );
        }

        return possibleEnPassantMoves;
    }




    private int calculateEnPassantRow() {
        return Character.getNumericValue(this.getChessboard().getEnPassantField().charAt(1)) - 1;
    }

    private int calculateEnPassantCol() {
        return this.getChessboard().getEnPassantField().charAt(0) - (int) 'a';
    }



    private boolean isPromotionRow(int row) {
        return (row == 7 && getIsWhite()) || (row == 0 && !getIsWhite());
    }


    @Override
    public int[][] getPieceSquareTable() {
        return new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {50, 50, 50, 50, 50, 50, 50, 50},
                {10, 10, 20, 30, 30, 20, 10, 10},
                {5, 5, 10, 25, 25, 10, 5, 5},
                {0, 0, 0, 20, 20, 0, 0, 0},
                {5, -5, -10, 0, 0, -10, -5, 5},
                {5, 10, 10, -20, -20, 10, 10, 5},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };

    }

    @Override
    public int[][] getPieceSquareTable(boolean isEndgame) {
        return getPieceSquareTable();
    }


    @Override
    public int getValue() {
        return VALUE;
    }

    @Override
    public String toString() {
        return "{ type='" + getType() + '\'' +
                ", isWhite=" + getIsWhite() +
                ", x=" + getRow() +
                ", y=" + getCol() +
                '}';
    }
}
