package org.example;

import java.util.ArrayList;
import java.util.List;

public class MoveScore {
    public Move move; // The best move at the current depth
    public int score; // The score of the position after this move
    public List<Move> movePath; // The sequence of moves leading to this score

    public MoveScore(Move move, int score) {
        this.move = move;
        this.score = score;
        this.movePath = new ArrayList<>();
    }

    public MoveScore(Move move, int score, List<Move> movePath) {
        this.move = move;
        this.score = score;
        this.movePath = new ArrayList<>(movePath); // Copy the existing path
    }
}