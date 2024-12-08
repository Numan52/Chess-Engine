package org.example.Evaluation;

import org.example.Move;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    private Map<Long, TranspositionEntry> table = new HashMap<>();

    public void store(long key, int depth, int score, int flag, Move bestMove) {
        table.put(key, new TranspositionEntry(depth, score, flag, bestMove));
    }

    public TranspositionEntry lookup(long key) {
        return table.get(key);
    }

    public static class TranspositionEntry {
        public int depth;
        public int score;
        public int flag; // 0 = exact, -1 = lower bound, 1 = upper bound
        public Move bestMove;

        public TranspositionEntry(int depth, int score, int flag, Move bestMove) {
            this.depth = depth;
            this.score = score;
            this.flag = flag;
            this.bestMove = bestMove;
        }
    }
}
