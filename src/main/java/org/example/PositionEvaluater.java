package org.example;

public class PositionEvaluater {
    private static final int PAWN_VALUE = 100;
    private static final int ROOK_VALUE = 500;
    private static final int KNIGHT_VALUE = 300;
    private static final int BISHOP_VALUE = 300;
    private static final int QUEEN_VALUE = 900;
    private Board board;

    public PositionEvaluater(Board board) {
        this.board = board;
    }

    public int evaluatePosition() {
        return 0;
    }
}
