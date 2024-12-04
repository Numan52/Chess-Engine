package org.example;

import org.example.Piece.Piece;
import org.example.Piece.PieceType;
import org.example.Utils.BoardParser;
import org.example.Utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


public class ChessEngineTest {
    private Board board;
    private PositionEvaluater positionEvaluater;
    private Searcher searcher;
    private ChessEngine chessEngine;


    @Before
    public void setUp() {
        board = new Board();
        positionEvaluater = new PositionEvaluater(board);
        searcher = new Searcher(board, 6, 15000, positionEvaluater);
        chessEngine = new ChessEngine(board, searcher, positionEvaluater);
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

        List<Move> possibleMoves = board.getAllPossibleMoves();

        Assert.assertEquals(30, possibleMoves.size());
    }


    @Test
    public void testGenerateAllMovesNormalPosition2() {
        chessEngine.updateBoard("rnbqkbnr/ppp1p1pp/5p2/3pN3/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 1 3");

        List<Move> possibleMoves = board.getAllPossibleMoves();
        for (Move move : possibleMoves) {
            System.out.println(move);
        }
        Assert.assertEquals(26, possibleMoves.size());
    }


    @Test
    public void testGenerateAllMovesEnPassantPossible() {
        chessEngine.updateBoard("rnbqkbnr/ppp1pppp/8/8/2PpP3/7P/PP1P1PP1/RNBQKBNR b KQkq c3 0 3");

        List<Move> possibleMoves = board.getAllPossibleMoves();

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
        List<Move> possibleMoves = board.getAllPossibleMoves();
        for (Move move : possibleMoves) {
            System.out.println(move);
        }
        Assert.assertEquals(31, possibleMoves.size());
    }


    @Test
    public void testUpdateBoardCorrectlySetsBoardState() {
        chessEngine.updateBoard("rnbqkbnr/pppp1Bp1/7p/4p3/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3");
        List<Move> possibleMoves = board.getAllPossibleMoves();
        Assert.assertEquals(2, possibleMoves.size());
    }


    @Test
    public void testSearcherCalculatesCorrectCapture() {
        chessEngine.updateBoard("rnbqkbnr/ppp1p1pp/5p2/3pN3/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 1 3");

        Move move = chessEngine.calculateBestMove().move;

        Assert.assertEquals(PieceType.KNIGHT, move.getCapturedPiece().getType());
        Assert.assertEquals(4, move.getTargetRow());
        Assert.assertEquals(4, move.getTargetCol());
    }


    @Test
    public void testSearcherCalculatesCorrectMoveInQuietPosition() {
        chessEngine.updateBoard("r1bqk2r/ppp1bppp/2np1n2/4p3/2B1P3/2NP1N2/PPP2PPP/R1BQK2R w KQkq - 1 6");

        Move move = chessEngine.calculateBestMove().move;

        System.out.println(move);
    }

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



}


