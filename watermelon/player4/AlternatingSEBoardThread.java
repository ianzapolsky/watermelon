package watermelon.player4;

import java.util.ArrayList;

import watermelon.sim.seed;

public class AlternatingSEBoardThread implements BoardThread {
	SeedGraph seedgraph;
	Boards boards;

	ArrayList<seed> board;
	double score;

	public AlternatingSEBoardThread(SeedGraph initSeedGraph, Boards initBoards) {
		seedgraph = initSeedGraph;
		boards = initBoards;
	}

	public void run() {
		board = boards.getAlternatingSEBoard();
		score = seedgraph.calculateScore(board);
		System.out.println("SE Alternating Thread finished running.");
	}

	public double getScore() {
		return score;
	}

	public ArrayList<seed> getBoard() {
		return board;
	}

	public void getDetails() {
		System.out.println("Alternating SE Board");
		System.out.println("seedlist size is" + board.size());
		System.out.println("score is " + score);
	}

}
