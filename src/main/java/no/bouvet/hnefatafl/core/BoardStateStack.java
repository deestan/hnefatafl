package no.bouvet.hnefatafl.core;

import no.bouvet.hnefatafl.core.minimax.IStateStack;

import java.util.List;

/* Optimized to avoid garbage collection */
public class BoardStateStack implements IStateStack<Board> {
    private class ReversibleMove {
        public int pieceIndex;
        public int fromRow;
        public int fromCol;
        // Trying to see if this "fake" array is faster than a proper
        // array due to being eligible for immediate recycling.
        public int captured0Index;
        public int captured1Index;
        public int captured2Index;
    }

    public final Board board;
    public final List<Piece> boardPieces;
    private final ReversibleMove[] moveStack;
    private int moveStackPos;
    private final Rules rules;

    public BoardStateStack(Board board, Rules rules, int maxDepth) {
        this.board = board;
        this.rules = rules;
        this.boardPieces = board.getPieces();
        this.moveStack = new ReversibleMove[maxDepth];
        for (int i = 0; i < maxDepth; i++)
            this.moveStack[i] = new ReversibleMove();
        this.moveStackPos = -1;
    }

    public Board currentState() {
        return board;
    }

    public void pushMove(Move move) {
        Piece p = boardPieces.get(move.pieceIndex);
        newMove(move);
        // Apply captures
        int captured;
        captured = rules.capturedNeighbor(p, -1, 0);
        if (captured != -1) capture(captured);
        captured = rules.capturedNeighbor(p, 1, 0);
        if (captured != -1) capture(captured);
        captured = rules.capturedNeighbor(p, 0, -1);
        if (captured != -1) capture(captured);
        captured = rules.capturedNeighbor(p, 0, 1);
        if (captured != -1) capture(captured);
    }

    private void newMove(Move move) {
        this.moveStackPos += 1;
        this.board.setTurn(this.board.getTurn() + 1);
        ReversibleMove m = this.moveStack[this.moveStackPos];
        Piece p = this.boardPieces.get(move.pieceIndex);
        m.pieceIndex = move.pieceIndex;
        m.fromRow = p.getRow();
        m.fromCol = p.getCol();
        m.captured0Index = -1;
        m.captured1Index = -1;
        m.captured2Index = -1;
        p.setRow(move.toRow);
        p.setCol(move.toCol);
    }

    // Applies to last registered move.
    private void capture(int pieceIndex) {
        ReversibleMove m = this.moveStack[this.moveStackPos];
        if (m.captured0Index == -1)
            m.captured0Index = pieceIndex;
        else if (m.captured1Index == -1)
            m.captured1Index = pieceIndex;
        else
            m.captured2Index = pieceIndex;
        this.boardPieces.get(pieceIndex).setDead(true);
    }

    public void popMove() {
        ReversibleMove m = this.moveStack[this.moveStackPos];
        Piece p = this.boardPieces.get(m.pieceIndex);
        p.setRow(m.fromRow);
        p.setCol(m.fromCol);
        if (m.captured0Index != -1)
            this.boardPieces.get(m.captured0Index).setDead(false);
        if (m.captured1Index != -1)
            this.boardPieces.get(m.captured1Index).setDead(false);
        if (m.captured2Index != -1)
            this.boardPieces.get(m.captured2Index).setDead(false);
        this.moveStackPos -= 1;
        this.board.setTurn(this.board.getTurn() - 1);
    }
}
