package no.bouvet.hnefatafl.health;

import com.codahale.metrics.health.HealthCheck;
import no.bouvet.hnefatafl.core.Board;
import no.bouvet.hnefatafl.core.Piece;
import no.bouvet.hnefatafl.resources.Brain;

import java.util.ArrayList;

public class BrainHealthCheck extends HealthCheck {
    private final Brain brain;

    public BrainHealthCheck(Brain brain) {
        this.brain = brain;
    }

    @Override
    protected Result check() throws Exception {
        Board emptyBoard = new Board();
        emptyBoard.setPieces(new ArrayList<Piece>());
        String outputJson = brain.PonderThis(emptyBoard);

        if (outputJson != "\"null\"") {
            return Result.unhealthy("Brain does not yield output.");
        }
        return Result.healthy();
    }
}
