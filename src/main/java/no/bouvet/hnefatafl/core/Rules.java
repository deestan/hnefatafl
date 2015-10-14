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
            for (int toRow = 0; toRow < 11; toRow++)
                addMoveIfValid(moves, pieceIndex, toRow, piece.getCol());
            for (int toCol = 0; toCol < 11; toCol++)
                addMoveIfValid(moves, pieceIndex, piece.getRow(), toCol);
        }
        return moves;
    }

    public int capturedNeighbor(Piece moved, int dRow, int dCol) {
        cachedPieces = board.getPieces();
        int victimRow = moved.getRow() + dRow;
        int victimCol = moved.getCol() + dCol;
        int helperRow = victimRow + dRow;
        int helperCol = victimCol + dCol;
        int helperPerp1Row = victimRow + dCol;
        int helperPerp1Col = victimCol + dRow;
        int helperPerp2Row = victimRow - dCol;
        int helperPerp2Col = victimCol - dRow;
        int victimIndex = -1;
        Piece victim = null;
        Piece helper = null;
        Piece helperPerp1 = null;
        Piece helperPerp2 = null;
        boolean helpByHostileSquare =
                (helperRow == 0 && helperCol == 0) ||
                        (helperRow == 10 && helperCol == 0) ||
                        (helperRow == 0 && helperCol == 10) ||
                        (helperRow == 10 && helperCol == 10) ||
                        (helperRow == 5 && helperCol == 5);
        for (int pIndex = 0; pIndex < cachedPieces.size(); pIndex++) {
            Piece p = cachedPieces.get(pIndex);
            if (p.isDead())
                continue;
            int row = p.getRow();
            int col = p.getCol();
            if (row == victimRow && col == victimCol) {
                victim = p;
                victimIndex = pIndex;
            } else if (row == helperRow && col == helperCol)
                helper = p;
            else if (row == helperPerp1Row && col == helperPerp1Col)
                helperPerp1 = p;
            else if (row == helperPerp2Row && col == helperPerp2Col)
                helperPerp2 = p;
        }

        if (victim == null)
            return -1;

        // test for victim different from attacker
        if (victim.isBlack() == moved.isBlack())
            return -1;

        // test for valid helper
        boolean validHelper = ((helper != null && helper.isBlack() == moved.isBlack()) ||
                helpByHostileSquare);
        if (!validHelper)
            return -1;

        // king need to be surrounded
        if (victim.isWhiteKing())
            if (helper == null || helperPerp1 == null || helperPerp2 == null || !helperPerp1.isBlack() || !helperPerp2.isBlack())
                return -1;

        return victimIndex;
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
