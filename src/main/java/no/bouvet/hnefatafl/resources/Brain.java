package no.bouvet.hnefatafl.resources;

import com.google.common.base.Optional;
import no.bouvet.hnefatafl.core.*;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
		BoardStateStack stack = new BoardStateStack(board, searchDepth);
		Minimax minimax = new Minimax(stack, new BlackEvaluator());
		Optional<Move> move = minimax.bestMove(searchDepth);
		if (move.isPresent()) {
            Move m = move.get();
            return
                "{\"pieceIndex\": " + m.pieceIndex + ", " +
                "\"fromRow\": " + board.getPieces().get(m.pieceIndex).getRow() + ", " +
                "\"fromCol\": " + board.getPieces().get(m.pieceIndex).getCol() + ", " +
                "\"row\": " + m.toRow + ", " +
                "\"col\": " + m.toCol + "}";
        } else {
            return "null";
        }
	}
}
