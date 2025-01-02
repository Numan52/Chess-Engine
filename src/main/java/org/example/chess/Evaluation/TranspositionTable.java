package org.example.chess.Evaluation;

import org.example.chess.Move;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    private Map<Long, TranspositionEntry> table = new HashMap<>();

    public void store(long zobristKey, int depth, int score, boolean replaceFlag, int evalType, Move bestMove) {
        table.put(zobristKey, new TranspositionEntry(depth, score, replaceFlag, evalType, bestMove));
    }

    public TranspositionEntry lookup(long zobristKey) {
        return table.get(zobristKey);
    }

    public static class TranspositionEntry {
        public int depth;
        public int score;
        // TODO: SET FLAG TO INDICATE IF ENTRY SHOULD BE REPLACED (if its useless)
        public boolean replaceFlag = false;
        public int evalType; // 0 = exact, -1 = lower bound, 1 = upper bound
        public Move bestMove;

        public TranspositionEntry(int depth, int score, boolean replaceFlag, int evalType, Move bestMove) {
            this.depth = depth;
            this.score = score;
            this.replaceFlag = replaceFlag;
            this.evalType = evalType;
            this.bestMove = bestMove;
        }
    }

    public Map<Long, TranspositionEntry> getTable() {
        return table;
    }
}
