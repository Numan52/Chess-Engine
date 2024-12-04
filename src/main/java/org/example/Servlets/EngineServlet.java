package org.example.Servlets;



import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class EngineServlet extends HttpServlet {
    private Board board = new Board();
    private PositionEvaluater positionEvaluater = new PositionEvaluater(board);
    private Searcher searcher = new Searcher(board, 5, positionEvaluater);
    private ChessEngine chessEngine = new ChessEngine(board, searcher, positionEvaluater);


    // fen should have the following form: rnb1kbnr/ppp2ppp/8/3Np1q1/8/5N2/PPPPPPPP/R1BQKB1R w KQkq e6 1 4
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        StringBuilder jsonBody = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
        }
        catch (IOException e) {
            System.out.println("error while reading request body");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Failed to update the board: " + e.getMessage());
        }

        String requestBody = jsonBody.toString();
        String fen = null;
        try {
            JSONObject json = new JSONObject(requestBody);
            fen = json.getString("fen");
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid JSON format or missing 'fen' attribute.");
            return;
        }

        System.out.println("fen: " + fen);

       chessEngine.updateBoard(fen);


        List<Move> moves = board.getAllPossibleMoves();

//        for (Move move : moves) {
//            System.out.println(move.toString());
//        }

        chessEngine.applyBestMove();

//        System.out.println(Arrays.deepToString(board.getBoardState()));
//        for (Piece[] piecesRow : board.getBoardState()) {
//            for (Piece piece : piecesRow) {
//                if (piece != null) {
//                    System.out.println(piece.toString());
//                }
//            }
//        }

        response.setStatus(200);
        response.getOutputStream().println("lakaka");

    }
}
