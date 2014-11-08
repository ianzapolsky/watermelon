package watermelon.player4threaded;

import java.util.ArrayList;

import watermelon.sim.seed;

public class HexagonalSEBoardThread implements BoardRunnable {
	SeedGraph seedgraph;
	Boards boards;

	ArrayList<seed> board;
	double score;

	public HexagonalSEBoardThread(SeedGraph initSeedGraph, Boards initBoards) {
		seedgraph = initSeedGraph;
		boards = initBoards;
	}

	public void run() {
		System.out.println("SE Hexagonal Thread started running.");
		board = boards.getHexagonalSEBoard();
		score = seedgraph.calculateScore(board);
		System.out.println("SE Hexagonal Thread finished running.");
	}

	public double getScore() {
		return score;
	}

	public ArrayList<seed> getBoard() {
		return board;
	}

	public void getDetails() {
		System.out.println("Hexagonal SE Board");
		System.out.println("seedlist size is " + board.size());
		System.out.println("score is " + score);
	}

}
