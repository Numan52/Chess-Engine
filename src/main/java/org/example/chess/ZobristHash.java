package org.example.chess;

import org.example.chess.Piece.Piece;
import org.example.chess.Utils.ChessUtils;

import java.util.Random;

public class ZobristHash {
    private static final int SQUARES_NUM = 64;
    private static final int PIECE_TYPES = 12; // 6 white, 6 black pieces
    private static final int CASTLING_RIGHTS = 4; // KQkq
    private static final int EN_PASSANT = 8; // Files a-h

    private long[][] pieceHashes;
    private long[] castlingRightsHashes;
    private long[] enPassantHashes;
    private long sideToMoveHash;

    public ZobristHash() {
        Random random = new Random();
        pieceHashes = new long[SQUARES_NUM][PIECE_TYPES];
        castlingRightsHashes = new long[CASTLING_RIGHTS];
        enPassantHashes = new long[EN_PASSANT];
        sideToMoveHash = random.nextLong();

        for (int i = 0; i < SQUARES_NUM; i++) {
            for (int j = 0; j < PIECE_TYPES; j++) {
                pieceHashes[i][j] = random.nextLong();
            }
        }

        for (int i = 0; i < CASTLING_RIGHTS; i++) {
            castlingRightsHashes[i] = random.nextLong();
        }

        for (int i = 0; i < EN_PASSANT; i++) {
            enPassantHashes[i] = random.nextLong();
        }
    }


    public long calculatePositionHash(Board board) {
        long hash = 0;

        for (Piece[] piecesRow : board.getBoardState()) {
            for (Piece piece : piecesRow) {
                if (piece != null) {
                    int pieceTypeIndex = getPieceTypeIndex(piece);
                    hash ^= getPieceHash(piece.getRow(), piece.getCol(), pieceTypeIndex);
                }
            }
        }

        hash ^= board.getWhiteKing().getHasKingsideCastlingRights() ? getCastlingRightsHash(0) : 0;
        hash ^= board.getWhiteKing().getHasQueensideCastlingRights() ? getCastlingRightsHash(1) : 0;
        hash ^= board.getBlackKing().getHasKingsideCastlingRights() ? getCastlingRightsHash(2) : 0;
        hash ^= board.getBlackKing().getHasQueensideCastlingRights() ? getCastlingRightsHash(3) : 0;

        int enPassantCol = ChessUtils.getEnPassantFile(board);
        hash ^= enPassantCol == -1 ? 0 : getEnPassantHash(enPassantCol);

        hash ^= board.getIsWhitesTurn() ? sideToMoveHash : 0;

        return hash;
    }


    public long updatePositionHash(long hash, Move move) {
        hash ^= getPieceHash(move.getStartRow(), move.getStartCol(),  getPieceTypeIndex(move.getMovedPiece()));
        hash ^= getPieceHash(move.getTargetRow(), move.getTargetCol(),  getPieceTypeIndex(move.getMovedPiece()));

        if (move.getCapturedPiece() != null) {
            hash ^= getPieceHash(move.getCapturedPiece().getRow(), move.getCapturedPiece().getCol(),  getPieceTypeIndex(move.getCapturedPiece()));
        }

        if (move.isEnPassant()) {
            hash ^= getEnPassantHash(move.getTargetCol());
        }

        if (move.isCastling()) {
            if (move.getMovedPiece().getIsWhite()) {
                if (move.getTargetCol() == 6) {
                    hash ^= getCastlingRightsHash(0); // kingside castling
                } else {
                    hash ^= getCastlingRightsHash(1); // queenside castling
                }
            } else {
                if (move.getTargetCol() == 6) {
                    hash ^= getCastlingRightsHash(2);
                } else {
                    hash ^= getCastlingRightsHash(3);
                }
            }
        }

        hash ^= sideToMoveHash;

        return hash;

    }


    public long getPieceHash(int row, int col, int pieceTypeIndex) {
        int square = row * 8 + col;
        return pieceHashes[square][pieceTypeIndex];
    }


    public long getCastlingRightsHash(int index) {
        return castlingRightsHashes[index];
    }


    public int getPieceTypeIndex(Piece piece) {
        int index = piece.getIsWhite() ? 0 : 6;
        int typeOrdinal = piece.getType().ordinal();
        return index + typeOrdinal;
    }


    public long getEnPassantHash(int index) {
        return enPassantHashes[index];
    }



}
