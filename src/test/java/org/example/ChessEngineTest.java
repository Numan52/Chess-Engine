package org.example;

import org.example.Evaluation.KingSafetyEvaluater;
import org.example.Evaluation.PawnEvaluater;
import org.example.Evaluation.PositionEvaluater;
import org.example.Evaluation.TranspositionTable;
import org.example.Piece.Pawn;
import org.example.Piece.Piece;
import org.example.Piece.PieceType;
import org.example.Piece.Queen;
import org.example.Utils.BoardParser;
import org.example.Utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


public class ChessEngineTest {
    private Board board;
    private KingSafetyEvaluater kingSafetyEvaluater;
    private PawnEvaluater pawnEvaluater;
    private PositionEvaluater positionEvaluater;
    private Searcher searcher;
    private ChessEngine chessEngine;
    private ZobristHash zobristHash;
    private TranspositionTable transpositionTable;

    @Before
    public void setUp() {
        zobristHash = new ZobristHash();
        transpositionTable = new TranspositionTable();
        board = new Board(zobristHash);
        kingSafetyEvaluater = new KingSafetyEvaluater(board);
        pawnEvaluater = new PawnEvaluater(board);
        positionEvaluater = new PositionEvaluater(board, List.of(kingSafetyEvaluater, pawnEvaluater));
        searcher = new Searcher(board, 8, 1000000, positionEvaluater, transpositionTable);
        chessEngine = new ChessEngine(board, searcher, positionEvaluater, zobristHash);
    }


    @Test
    public void testFenToBoardStateProducesCorrectBoardPosition() {
        Piece[][] correctBoardState = new Piece[8][8];

        TestUtils.populateExpectedBoardState(correctBoardState, board);
        Piece[][] calculatedBoardState = BoardParser.fenToBoardState(board, "rnbqkbnr/ppp1pppp/8/8/2PpP3/7P/PP1P1PP1/RNBQKBNR b KQkq c3 0 3");

        for (int row = 0; row < Board.CHESSBOARD_ROWS; row++) {
            for (int col = 0; col < Board.CHESSBOARD_COLUMNS; col++) {
                Piece expectedPiece = correctBoardState[row][col];
                Piece actualPiece = calculatedBoardState[row][col];

                Assert.assertEquals(
                        "Mismatch at position (" + row + ", " + col + ")",
                        TestUtils.PieceToString(expectedPiece),
                        TestUtils.PieceToString(actualPiece)
                );
            }
        }

    }


    @Test
    public void testGenerateAllMovesNormalPosition() {
        chessEngine.updateBoard("rnbqkbnr/pppp1ppp/8/4p3/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 2");

        List<Move> possibleMoves = board.getAllPossibleMoves(0);

        Assert.assertEquals(30, possibleMoves.size());
    }


    @Test
    public void testGenerateAllMovesNormalPosition2() {
        chessEngine.updateBoard("rnbqkbnr/ppp1p1pp/5p2/3pN3/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 1 3");

        List<Move> possibleMoves = board.getAllPossibleMoves(0);
        for (Move move : possibleMoves) {
            System.out.println(move);
        }
        Assert.assertEquals(26, possibleMoves.size());
    }


    @Test
    public void testGenerateAllMovesEnPassantPossible() {
        chessEngine.updateBoard("rnbqkbnr/ppp1pppp/8/8/2PpP3/7P/PP1P1PP1/RNBQKBNR b KQkq c3 0 3");

        List<Move> possibleMoves = board.getAllPossibleMoves(0);

        Assert.assertEquals(30, possibleMoves.size());
    }

    @Test
    public void testGenerateAllMovesEnPassantTwicePossible() {
        chessEngine.updateBoard("rnbqkbn1/pppp1ppr/7p/3PpP2/8/P6N/1PP1P1PP/RNBQKB1R w KQq e6 0 6");
        for (Piece[] piecesRow : board.getBoardState()) {
            for (Piece piece : piecesRow) {
                System.out.println(piece);
            }
        }
        System.out.println("length: " + board.getBoardState()[1].length);
        List<Move> possibleMoves = board.getAllPossibleMoves(0);
        for (Move move : possibleMoves) {
            System.out.println(move);
        }
        Assert.assertEquals(31, possibleMoves.size());
    }


    @Test
    public void testUpdateBoardCorrectlySetsBoardState() {
        chessEngine.updateBoard("rnbqkbnr/pppp1Bp1/7p/4p3/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3");
        List<Move> possibleMoves = board.getAllPossibleMoves(0);
        System.out.println(board.toString());
        Assert.assertEquals(2, possibleMoves.size());
    }


    @Test
    public void testMakeAndUndoMove() {
        chessEngine.updateBoard("rnbqkbnr/ppp1p2p/5pp1/4N3/4p3/5Q2/PPPP1PPP/RNB1KB1R w KQkq - 0 5");
        Queen queen = board.getQueen(true);
        Pawn capturedPawn = (Pawn) board.getBoardState()[3][4];

        Assert.assertEquals(2, queen.getRow());
        Assert.assertEquals(5, queen.getCol());
        Assert.assertEquals(queen, board.getBoardState()[2][5]);


        Move move = new Move(2, 5, 3, 4, queen, capturedPawn, false, false, null );
        board.makeMove(move);

        Assert.assertEquals(3, queen.getRow());
        Assert.assertEquals(4, queen.getCol());
        Assert.assertEquals(queen, board.getBoardState()[3][4]);

        board.undoMove(move);

        Assert.assertEquals(2, queen.getRow());
        Assert.assertEquals(5, queen.getCol());
        Assert.assertEquals(queen, board.getBoardState()[2][5]);

        Assert.assertEquals(3, capturedPawn.getRow());
        Assert.assertEquals(4, capturedPawn.getCol());
        Assert.assertEquals(capturedPawn, board.getBoardState()[3][4]);
        Assert.assertEquals(capturedPawn, board.getBoardState()[3][4]);
    }


    @Test
    public void testSearcherCalculatesCorrectCapture() {
        chessEngine.updateBoard("rnbqkbnr/ppp1p1pp/5p2/3pN3/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 1 3");

        MoveScore moveScore = chessEngine.calculateBestMove();
        System.out.println("Move: " + moveScore.move + " ,Score: " + moveScore.score);
        System.out.println();
        System.out.println("Path: " + moveScore.movePath);

        for (Move move : moveScore.movePath) {
            System.out.println(move);
        }

        Assert.assertEquals(PieceType.KNIGHT, moveScore.move.getCapturedPiece().getType());
        Assert.assertEquals(4, moveScore.move.getTargetRow());
        Assert.assertEquals(4, moveScore.move.getTargetCol());
    }


//    @Test
//    public void testSearcherCalculatesCorrectMoveInQuietPosition() {
//        chessEngine.updateBoard("r1bqk2r/ppp1bppp/2np1n2/4p3/2B1P3/2NP1N2/PPP2PPP/R1BQK2R w KQkq - 1 6");
//
//        Move move = chessEngine.calculateBestMove().move;
//
//        System.out.println(move);
//    }

    // TODO: WHAT IS WRONG WITH THE BOARD STATE
    @Test
    public void testSearcherCalculatesCorrectCapture2() {
        chessEngine.updateBoard("r2qr1k1/1bp2pp1/p2b1n1p/2pP4/2B1N2B/P7/1PPQ1PPP/3RR1K1 b - - 2 18");

        Move move = chessEngine.calculateBestMove().move;

        System.out.println(move);
        Assert.assertEquals(PieceType.ROOK, move.getMovedPiece().getType());
        Assert.assertEquals(PieceType.KNIGHT, move.getCapturedPiece().getType());
        Assert.assertEquals(3, move.getTargetRow());
        Assert.assertEquals(4, move.getTargetCol());
    }

    @Test
    public void testSearcherCalculatesCorrectCapture3() {
        chessEngine.updateBoard("rn1qkb1r/ppp2ppp/1n1p4/6Bb/2P5/3P1N1P/PP3PP1/RN1QKB1R b KQkq - 2 8");

        Move move = chessEngine.calculateBestMove().move;

        System.out.println(move);
        Assert.assertEquals(PieceType.BISHOP, move.getMovedPiece().getType());
        Assert.assertEquals(PieceType.KNIGHT, move.getCapturedPiece().getType());
        Assert.assertEquals(2, move.getTargetRow());
        Assert.assertEquals(5, move.getTargetCol());
    }



    @Test
    public void testPieceSquareTableAffectsEvaluation() {
        chessEngine.updateBoard("r1b1k2r/ppp2ppp/4pn2/2q1b3/P2n3N/2NP3P/1PPQ1PP1/R1B1KB1R b KQkq - 0 10");

        int evaluation = positionEvaluater.evaluatePosition(0);
        System.out.println(evaluation);

        Assert.assertTrue(evaluation < -0.5);

    }


    @Test
    public void testMate() {
        chessEngine.updateBoard("N7/5kpp/b3r3/5p2/PB1P4/8/2P2P1P/RR4K1 b - - 0 24");

        MoveScore moveScore = chessEngine.calculateBestMove();

        Piece movedPiece = moveScore.move.getMovedPiece();
        Assert.assertEquals(PieceType.ROOK, movedPiece.getType());
        Assert.assertEquals(6, moveScore.move.getTargetCol());

    }

    @Test
    public void testMateInTwo() {
        chessEngine.updateBoard("1r3rk1/2q2p1p/6p1/1pp5/3B1bPR/1BPQ3P/1P6/6K1 w - - 0 25");

        MoveScore moveScore = chessEngine.calculateBestMove();
        Piece movedPiece = moveScore.move.getMovedPiece();
        Assert.assertEquals(PieceType.QUEEN, movedPiece.getType());
        Assert.assertEquals(5, moveScore.move.getTargetRow());
        Assert.assertEquals(6, moveScore.move.getTargetCol());

    }



    @Test
    public void testIdenticalPositionsProduceSameHash() {
        chessEngine.updateBoard("r7/2N2kpp/b3r3/5p2/PB1P4/8/2P2P1P/RR4K1 w - - 1 24");
        long hash1 = board.getPositionHash();

        chessEngine.updateBoard("r7/2N2kpp/b3r3/5p2/PB1P4/8/2P2P1P/RR4K1 w - - 1 24");
        long hash2 = board.getPositionHash();

        Assert.assertEquals(hash1, hash2);
    }


    @Test
    public void testDifferentlPositionsProduceDifferentHash() {
        chessEngine.updateBoard("r7/2N2kpp/b3r3/5p2/PB1P4/8/2P2P1P/RR4K1 w - - 1 24");
        long hash1 = board.getPositionHash();

        chessEngine.updateBoard("r7/2N2kpp/b3r3/5p2/PB1P4/8/2P2P1P/RR4K1 b - - 1 24");
        long hash2 = board.getPositionHash();

        Assert.assertNotEquals(hash1, hash2);
    }


    @Test
    public void testPositionHashIsCorrectlyUpdated() {
        chessEngine.updateBoard("r7/2N2kpp/b3r3/5p2/PB1P4/8/2P2P1P/RR4K1 w - - 1 24");
        long originalHash = board.getPositionHash();

        Piece movedPiece = board.getBoardState()[6][2];
        Piece capturedPiece = board.getBoardState()[7][0];
        Move move1 = new Move(6, 2, 7, 0, movedPiece, capturedPiece, false, false, null);
        board.makeMove(move1);
        long hash2 = board.getPositionHash();

        Piece movedPiece2 = board.getBoardState()[5][4];

        Move move2 = new Move(5, 4, 5, 6, movedPiece2, null, false, false, null);
        board.makeMove(move2);
        long hash3 = board.getPositionHash();

        Assert.assertNotEquals(hash2, hash3);

        board.undoMove(move2);
        board.undoMove(move1);



        Assert.assertEquals(originalHash, board.getPositionHash());
    }

    // TODO MAKE THIS WORK
    @Test
    public void testAvoidsMateInOne() {
        chessEngine.updateBoard("rnbqkbnr/1ppppppp/8/8/p1B1P3/5Q2/PPPP1PPP/RNB1K1NR b KQkq - 1 3");


        MoveScore moveScore = chessEngine.calculateBestMove();
        System.out.println(moveScore.move);
        System.out.println(moveScore.score);

        Piece movedPiece = moveScore.move.getMovedPiece();



    }




}


