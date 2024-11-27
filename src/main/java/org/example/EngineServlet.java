package org.example;



import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Piece.Piece;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class EngineServlet extends HttpServlet {
    private Board board = new Board();
    private PositionEvaluater positionEvaluater = new PositionEvaluater(board);
    private Searcher searcher = new Searcher(board, 15, positionEvaluater);
    private ChessEngine chessEngine = new ChessEngine(board, searcher, positionEvaluater);



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder jsonBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
                System.out.println(line);
            }
        }
        catch (IOException e) {
            System.out.println("error while reading request body");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Failed to update the board: " + e.getMessage());
        }

        System.out.println(jsonBody.toString());


        MoveTranslator.updateBoard(jsonBody.toString(), board);
        chessEngine.applyBestMove(false);

        System.out.println(Arrays.deepToString(board.getBoardState()));
        for (Piece[] piecesRow : board.getBoardState()) {
            for (Piece piece : piecesRow) {
                if (piece != null) {
                    System.out.println(piece.toString());
                }
            }
        }

        response.setStatus(200);
        response.getOutputStream().println("lakaka");

    }
}
