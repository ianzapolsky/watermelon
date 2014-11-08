package watermelon.player4threaded;

import java.util.ArrayList;

import watermelon.sim.seed;

public class HexagonalNWBoardThread implements BoardRunnable {
	SeedGraph seedgraph;
	Boards boards;

	ArrayList<seed> board;
	double score;

	public HexagonalNWBoardThread(SeedGraph initSeedGraph, Boards initBoards) {
		seedgraph = initSeedGraph;
		boards = initBoards;
	}

	public void run() {
		System.out.println("NW Hexagonal Thread started running.");
		board = boards.getHexagonalNWBoard();
		score = seedgraph.calculateScore(board);
		System.out.println("NW Hexagonal Thread finished running.");
	}

	public double getScore() {
		return score;
	}

	public ArrayList<seed> getBoard() {
		return board;
	}

	public void getDetails() {
		System.out.println("Hexagonal NW Board");
		System.out.println("seedlist size is" + board.size());
		System.out.println("score is " + score);
	}

}
