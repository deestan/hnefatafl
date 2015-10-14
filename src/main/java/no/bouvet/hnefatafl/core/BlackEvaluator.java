package no.bouvet.hnefatafl.core;

import java.util.List;

public class BlackEvaluator implements IBlackEvaluator {
    public int evaluate(List<Piece> boardPieces) {
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
            score += p.isBlack() ? 1 : -2; // trades are good for black
        }

        return score;
    }
}
