package no.bouvet.hnefatafl;

import com.google.common.base.Optional;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import no.bouvet.hnefatafl.core.*;

import java.util.ArrayList;
import java.util.List;

public class MinimaxTest extends TestCase {
    public MinimaxTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(MinimaxTest.class);
    }

    public void testConstructor() {
		new Minimax(kingSoloTop(), new BlackEvaluator());
    }

    public void testKingEscapes() {
        Minimax mm = new Minimax(kingSoloTop(), new EscapeTopLeftEvaluator());
        List<Move> ms = mm.bestMove(1);
        assertEquals(1, ms.size());
        Move m = ms.get(0);
        assertEquals(0, m.pieceIndex);
        assertEquals(0, m.toRow);
        assertEquals(0, m.toCol);
    }

    public void testKingPlansEscape() {
        Minimax mm = new Minimax(kingSoloMiddleBlockedLeft(), new EscapeTopLeftEvaluator());
        Move m = mm.bestMove(3).get(0);
        assertEquals(0, m.pieceIndex);
        assertEquals(0, m.toRow);
        assertEquals(5, m.toCol);
    }

    public void testNoMoves() {
        BoardStateStack bss = kingSoloTop();
        bss.board.setTurn(1);
        List<Move> m = new Minimax(bss, new BlackEvaluator()).bestMove(1);
        assertEquals(0, m.size());
    }

    /* ---- Evaluators ---- */
    private class EscapeTopLeftEvaluator implements IBlackEvaluator {
        public int evaluate(List<Piece> boardPieces) {
            for (Piece p : boardPieces)
                if (p.isWhiteKing() && p.getRow() == 0 && p.getCol() == 0)
                    return -1;
            return 0;
        }
    }

    /* ---- Board state generators ---- */

    private BoardStateStack kingSoloMiddleBlockedLeft() {
        Board b = new Board();
        b.setTurn(0);
        List<Piece> pieces = new ArrayList<Piece>(2);
        pieces.add(new Piece.Builder().row(5).col(5).isWhiteKing().piece);
        pieces.add(new Piece.Builder().row(5).col(0).isBlack().piece);
        b.setPieces(pieces);
        return new BoardStateStack(b, 10);
    }

    private BoardStateStack kingSoloTop() {
        Board b = new Board();
        b.setTurn(0);
        List<Piece> pieces = new ArrayList<Piece>(1);
        pieces.add(new Piece.Builder().row(0).col(1).isWhiteKing().piece);
        b.setPieces(pieces);
        return new BoardStateStack(b, 10);
    }
}
