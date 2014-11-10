package watermelon.player4;

import java.util.ArrayList;

import watermelon.player4.Boards;
import watermelon.player4.SeedGraph;
import watermelon.sim.Pair;
import watermelon.sim.seed;

public class Player extends watermelon.sim.Player {

	static double distowall = 1.00;
	static double distotree = 2.00;
	static double distoseed = 2.00;

	double width;
	double length;
	double s;
	ArrayList<Pair> treelist;
	ArrayList<seed> seedlist;

	SeedGraph seedgraph;
	Boards boards;

	public Player() {
		init();
	}

	public void init() {
		seedlist = new ArrayList<seed>();
	}

	@Override
	public ArrayList<seed> move(ArrayList<Pair> initTreelist, double initWidth, double initLength, double initS) {
		treelist = initTreelist;
		width = initWidth;
		length = initLength;
		s = initS;
		seedgraph = new SeedGraph(initTreelist, initWidth, initLength, initS);
		boards = new Boards(seedgraph);

		ArrayList<seed> hexAlternatingNW  = boards.getHexagonalNWBoard();
		ArrayList<seed> hexAlternatingNE  = boards.getHexagonalNEBoard();
		ArrayList<seed> hexAlternatingSW  = boards.getHexagonalSWBoard();
		ArrayList<seed> hexAlternatingSE  = boards.getHexagonalSEBoard();
		ArrayList<seed> gridAlternatingNW = boards.getAlternatingNWBoard();
		ArrayList<seed> gridAlternatingNE = boards.getAlternatingNEBoard();
		ArrayList<seed> gridAlternatingSW = boards.getAlternatingSWBoard();
		ArrayList<seed> gridAlternatingSE = boards.getAlternatingSEBoard();

		double scoreHexAlternatingNW  = seedgraph.calculateScore(hexAlternatingNW);
		double scoreHexAlternatingNE  = seedgraph.calculateScore(hexAlternatingNE);
		double scoreHexAlternatingSW  = seedgraph.calculateScore(hexAlternatingSW);
		double scoreHexAlternatingSE  = seedgraph.calculateScore(hexAlternatingSE);
		double scoreGridAlternatingNW = seedgraph.calculateScore(gridAlternatingNW);
		double scoreGridAlternatingNE = seedgraph.calculateScore(gridAlternatingNE);
		double scoreGridAlternatingSW = seedgraph.calculateScore(gridAlternatingSW);
		double scoreGridAlternatingSE = seedgraph.calculateScore(gridAlternatingSE);

		double maxScore = Double.MIN_VALUE;

    // check alternating scores
		if (scoreHexAlternatingNW > maxScore) {
			seedlist = hexAlternatingNW;
			maxScore = scoreHexAlternatingNW;
		} 
		if (scoreHexAlternatingNE > maxScore) {
			seedlist = hexAlternatingNE;
			maxScore = scoreHexAlternatingNE;
		}
		if (scoreHexAlternatingSW > maxScore) {
			seedlist = hexAlternatingSW;
			maxScore = scoreHexAlternatingSW;
		}
		if (scoreHexAlternatingSE > maxScore) {
			seedlist = hexAlternatingSE;
			maxScore = scoreHexAlternatingSE;
		}

    // check grid scores
    if (scoreGridAlternatingNW > maxScore) {
			seedlist = gridAlternatingNW;
			maxScore = scoreGridAlternatingNW;
		}
		if (scoreGridAlternatingNE > maxScore) {
			seedlist = gridAlternatingNE;
			maxScore = scoreGridAlternatingNE;
		}
		if (scoreGridAlternatingSW > maxScore) {
			seedlist = gridAlternatingSW;
			maxScore = scoreGridAlternatingSW;
		}
		if (scoreGridAlternatingSE > maxScore) {
			seedlist = gridAlternatingSE;
			maxScore = scoreGridAlternatingSE;
		}

		System.out.println("maxScore = " + maxScore);
		System.out.printf("seedlist size is %d\n", seedlist.size());
		System.out.printf("score is %f\n", maxScore);
		return seedlist;
	}

}
