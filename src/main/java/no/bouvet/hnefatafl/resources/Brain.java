package no.bouvet.hnefatafl.resources;

import com.codahale.metrics.annotation.Timed;
import no.bouvet.hnefatafl.core.*;
import no.bouvet.hnefatafl.core.minimax.Minimax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;

@Path("/ponder")
@Produces(MediaType.APPLICATION_JSON)
public class Brain {
    private final int searchDepth;
    private int currentTaskCount = 0;
    private final int maxCurrentTaskCount;
    private final static Logger logger = LoggerFactory.getLogger(Brain.class);

    public Brain(int searchDepth, int maxCurrentTaskCount) {
        this.searchDepth = searchDepth;
        this.maxCurrentTaskCount = maxCurrentTaskCount;
    }

    private synchronized void startTask() {
        if (currentTaskCount >= maxCurrentTaskCount)
            throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
        currentTaskCount++;
    }

    private synchronized void endTask() {
        currentTaskCount--;
    }

    @POST
    @Timed
    public String PonderThis(Board board) {
        startTask();
        try {
            logger.info("Pondering a board...");
            // 503 Service Unavailable - overload

            Rules rules = new Rules(board);
            BoardStateStack stack = new BoardStateStack(board, rules, searchDepth);
            Minimax<Board> minimax = new Minimax<>(stack, new Evaluator(board.blackTurn()), rules);
            List<Move> moves = minimax.bestMove(searchDepth);

            logger.info(String.format("Found %d best moves.", moves.size()));

            if (moves.size() == 0)
                return "null";

            int randomMoveIndex = new Random().nextInt(moves.size());
            return moves.get(randomMoveIndex).toString();
        } finally {
            endTask();
        }
    }
}
