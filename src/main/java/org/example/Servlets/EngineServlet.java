package org.example.Servlets;



import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.*;
import org.example.Evaluation.*;
import org.example.Utils.ChessUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class EngineServlet extends HttpServlet {



    // fen should have the following form: rnb1kbnr/ppp2ppp/8/3Np1q1/8/5N2/PPPPPPPP/R1BQKB1R w KQkq e6 1 4
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ZobristHash zobristHash = new ZobristHash();
        Board board = new Board(zobristHash);
        KingSafetyEvaluater kingSafetyEvaluater = new KingSafetyEvaluater(board);
        ActivityEvaluator activityEvaluator = new ActivityEvaluator(board);
        PawnEvaluater pawnEvaluater = new PawnEvaluater(board);
        PositionEvaluater positionEvaluater = new PositionEvaluater(board, List.of(pawnEvaluater, kingSafetyEvaluater, activityEvaluator));
        TranspositionTable transpositionTable = new TranspositionTable();
        Searcher searcher = new Searcher(board, 12, 30000, positionEvaluater, transpositionTable);
        ChessEngine chessEngine = new ChessEngine(board, searcher, positionEvaluater, zobristHash);


        System.out.println("yoooooooooooOYOOO");
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


       chessEngine.updateBoard(fen);



        MoveScore bestMove = chessEngine.calculateBestMove();
        System.out.println("Best Move: " + bestMove.move);
        System.out.println("Score: " + bestMove.score);
        System.out.println("Sequence: " + bestMove.movePath);
        System.out.println(board.toString());

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("from", ChessUtils.toAlgebraicNotation(bestMove.move.getStartRow(), bestMove.move.getStartCol()));
        jsonResponse.put("to", ChessUtils.toAlgebraicNotation(bestMove.move.getTargetRow(), bestMove.move.getTargetCol()));
        response.setContentType("application/json");
        response.setStatus(200);
        response.getOutputStream().println(jsonResponse.toString());

    }
}
