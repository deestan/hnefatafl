package no.bouvet.hnefatafl.core;

import no.bouvet.hnefatafl.core.minimax.IEvaluator;

import java.util.List;

public class Evaluator implements IEvaluator<Board> {
    private final boolean forBlack;

    public Evaluator(boolean forBlack) {
        this.forBlack = forBlack;
    }

    public int evaluate(Board b) {
        List<Piece> boardPieces = b.getPieces();
        // Based on black's perspective
        int score = 0;

        for (Piece p : boardPieces) {
            if (p.isDead())
                continue;
            if (p.isWhiteKing()) {
                int row = p.getRow();
                int col = p.getCol();
                score -= 100; // white king alive
                if ((row == 0 && col == 0) ||
                        (row == 0 && col == 10) ||
                        (row == 10 && col == 0) ||
                        (row == 10 && col == 10))
                    score -= 100; // white king escaped
            }
            score += p.isBlack() ? 1 : -2; // trades are bad for white
        }

        return score * (forBlack ? 1 : -1);
    }
}
