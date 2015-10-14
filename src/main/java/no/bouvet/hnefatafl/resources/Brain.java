package no.bouvet.hnefatafl.resources;

import no.bouvet.hnefatafl.core.*;
import com.codahale.metrics.annotation.Timed;
import no.bouvet.hnefatafl.core.minimax.Minimax;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Random;

@Path("/ponder")
@Produces(MediaType.APPLICATION_JSON)
public class Brain {
	private final int searchDepth;

	public Brain(int searchDepth) {
		this.searchDepth = searchDepth;
	}

    @POST
    @Timed
    public String PonderThis(Board board) {
        Rules rules = new Rules(board);
		BoardStateStack stack = new BoardStateStack(board, rules, searchDepth);
		Minimax minimax = new Minimax(stack, new Evaluator(board.blackTurn()), rules);
		List<Move> moves = minimax.bestMove(searchDepth);
		if (moves.size() == 0) {
            return "null";
        } else {
            Move m = moves.get(new Random().nextInt(moves.size()));
            return
                "{\"pieceIndex\": " + m.pieceIndex + ", " +
                "\"fromRow\": " + board.getPieces().get(m.pieceIndex).getRow() + ", " +
                "\"fromCol\": " + board.getPieces().get(m.pieceIndex).getCol() + ", " +
                "\"row\": " + m.toRow + ", " +
                "\"col\": " + m.toCol + "}";
        }
	}
}
