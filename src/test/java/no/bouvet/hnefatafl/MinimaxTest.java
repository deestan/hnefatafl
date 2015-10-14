package no.bouvet.hnefatafl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import no.bouvet.hnefatafl.core.*;
import no.bouvet.hnefatafl.core.minimax.IEvaluator;
import no.bouvet.hnefatafl.core.minimax.Minimax;

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
        Board b = kingSoloTop();
        Rules r = new Rules(b);
        new Minimax<Board>(new BoardStateStack(b, r, 0), new Evaluator(false), r);
    }

    public void testPrioritizeKingEscape() {
        Board b = kingSoloTop();
        Rules r = new Rules(b);
        Minimax<Board> mm = new Minimax<>(new BoardStateStack(b, r, 1), new EscapeTopLeftEvaluator(), r);
        List<Move> ms = mm.bestMove(1);
        assertEquals(1, ms.size());
        Move m = ms.get(0);
        assertEquals(0, m.pieceIndex);
        assertEquals(0, m.toRow);
        assertEquals(0, m.toCol);
    }

    public void testKingPlansEscape() {
        Board b = kingSoloMiddleBlockedLeft();
        Rules r = new Rules(b);
        Minimax<Board> mm = new Minimax<Board>(new BoardStateStack(b, r, 3), new EscapeTopLeftEvaluator(), r);
        Move m = mm.bestMove(3).get(0);
        assertEquals(0, m.pieceIndex);
        assertEquals(0, m.toRow);
        assertEquals(5, m.toCol);
    }

    public void testNoMoves() {
        Board b = kingSoloTop();
        Rules r = new Rules(b);
        Minimax mm = new Minimax(new BoardStateStack(b, r, 1), new EscapeTopLeftEvaluator(), r);
        b.setTurn(1);
        List<Move> m = mm.bestMove(1);
        assertEquals(0, m.size());
    }

    /* ---- Evaluators ---- */

    private class EscapeTopLeftEvaluator implements IEvaluator<Board> {
        public int evaluate(Board b) {
            for (Piece p : b.getPieces())
                if (p.isWhiteKing() && p.getRow() == 0 && p.getCol() == 0)
                    return 1;
            return 0;
        }
    }

    /* ---- Board state generators ---- */

    private Board kingSoloMiddleBlockedLeft() {
        Board b = new Board();
        b.setTurn(0);
        List<Piece> pieces = new ArrayList<Piece>(2);
        pieces.add(new Piece.Builder().row(5).col(5).isWhiteKing().piece);
        pieces.add(new Piece.Builder().row(5).col(0).isBlack().piece);
        b.setPieces(pieces);
        return b;
    }

    private Board kingSoloTop() {
        Board b = new Board();
        b.setTurn(0);
        List<Piece> pieces = new ArrayList<Piece>(1);
        pieces.add(new Piece.Builder().row(0).col(1).isWhiteKing().piece);
        b.setPieces(pieces);
        return b;
    }
}
