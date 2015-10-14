package no.bouvet.hnefatafl.core;

import java.util.List;
import java.util.Vector;

public class Rules {
    private Board board;
    private List<Piece> cachedPieces;

    public Rules(Board board) {
        this.board = board;
    }

    public List<Move> getValidMoves() {
        cachedPieces = board.getPieces();

        boolean blackTurn = (board.getTurn() % 2 == 1);
        List<Move> moves = new Vector<Move>();
        for (int pieceIndex = 0; pieceIndex < cachedPieces.size(); pieceIndex++) {
            Piece piece = cachedPieces.get(pieceIndex);
            if (piece.isDead())
                continue;
            if (piece.isBlack() != blackTurn)
                continue;
            for (int toRow = 0; toRow < 11; toRow ++)
                addMoveIfValid(moves, pieceIndex, toRow, piece.getCol());
            for (int toCol = 0; toCol < 11; toCol ++)
                addMoveIfValid(moves, pieceIndex, piece.getRow(), toCol);
        }
        return moves;
    }

    private void addMoveIfValid(List<Move> list, int pieceIndex, int toRow, int toCol) {
        Piece p = cachedPieces.get(pieceIndex);
        int fromRow = p.getRow();
        int fromCol = p.getCol();

        // Assume correct side is moving.

        // Actually move.
        if (fromRow == toRow && fromCol == toCol)
            return;

        // Peons cannot enter escape squares.
        if (!p.isWhiteKing())
            if ((toRow == 0 && toCol == 0) ||
                    (toRow == 0 && toCol == 10) ||
                    (toRow == 10 && toCol == 0) ||
                    (toRow == 10 && toCol == 10))
                return;

        // Assume diagonal moves not attempted.

        // Do not move through or onto other pieces.
        int dRow = 0;
        int dCol = 0;
        if (fromRow < toRow) dRow = 1;
        if (fromRow > toRow) dRow = -1;
        if (fromCol < toCol) dCol = 1;
        if (fromCol > toCol) dCol = -1;
        int testRow = fromRow;
        int testCol = fromCol;
        while (testRow != toRow || testCol != toCol) {
            testRow += dRow;
            testCol += dCol;
            for (Piece otherPiece : cachedPieces)
                if (!otherPiece.isDead() &&
                        otherPiece.getRow() == testRow &&
                        otherPiece.getCol() == testCol)
                    return;
        }

        // All move tests passed.
        list.add(new Move(pieceIndex, toRow, toCol));
    }
}
