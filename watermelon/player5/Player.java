package watermelon.player5;

import java.util.ArrayList;

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

		ArrayList<ArrayList<seed>> seedBoards = new ArrayList<ArrayList<seed>>();
		seedBoards.add(boards.getHexagonalNWBoard());
		// seedBoards.add(boards.getHexagonalNEBoard());
		// seedBoards.add(boards.getHexagonalSWBoard());
		// seedBoards.add(boards.getHexagonalSEBoard());
		// seedBoards.add(boards.getAlternatingNWBoard());
		// seedBoards.add(boards.getAlternatingNEBoard());
		// seedBoards.add(boards.getAlternatingSWBoard());
		// seedBoards.add(boards.getAlternatingSEBoard());

		ArrayList<Double> scores = new ArrayList<Double>();
		for (ArrayList<seed> b : seedBoards)
			scores.add(seedgraph.calculateScore(b));

		seedlist = seedBoards.get(0);
		double maxScore = scores.get(0);

		for (int i = 0; i < scores.size(); i++) {
			if (scores.get(i) > maxScore) {
				maxScore = scores.get(i);
				seedlist = seedBoards.get(i);
			}
		}

		System.out.println("maxScore = " + maxScore);
		System.out.printf("seedlist size is %d\n", seedlist.size());
		System.out.printf("score is %f\n", maxScore);
		return seedlist;
	}

}
