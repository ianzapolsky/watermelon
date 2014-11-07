package watermelon.player4;

import java.util.ArrayList;
import java.util.Random;

import watermelon.sim.Pair;
import watermelon.sim.seed;

public class Player extends watermelon.sim.Player {

	static double distowall = 1.00;
	static double distotree = 2.00;
	static double distoseed = 2.00;
	static double epsilon = .0000001;

	double width;
	double length;
	double s;
	ArrayList<Pair> treelist;
	ArrayList<seed> seedlist;
	Random random;
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

		ArrayList<BoardThread> boardThreads = new ArrayList<BoardThread>();

		HexagonalNWBoardThread t1 = new HexagonalNWBoardThread(seedgraph, boards);
		HexagonalNWBoardThread t2 = new HexagonalNWBoardThread(seedgraph, boards);
		HexagonalNWBoardThread t3 = new HexagonalNWBoardThread(seedgraph, boards);
		HexagonalNWBoardThread t4 = new HexagonalNWBoardThread(seedgraph, boards);
		HexagonalNWBoardThread t5 = new HexagonalNWBoardThread(seedgraph, boards);
		HexagonalNWBoardThread t6 = new HexagonalNWBoardThread(seedgraph, boards);
		HexagonalNWBoardThread t7 = new HexagonalNWBoardThread(seedgraph, boards);
		HexagonalNWBoardThread t8 = new HexagonalNWBoardThread(seedgraph, boards);

		boardThreads.add(t1);
		boardThreads.add(t2);
		boardThreads.add(t3);
		boardThreads.add(t4);
		boardThreads.add(t5);
		boardThreads.add(t6);
		boardThreads.add(t7);
		boardThreads.add(t8);

		for (BoardThread t : boardThreads)
			t.run();

		BoardThread maxThread = boardThreads.get(0);
		for (BoardThread t : boardThreads)
			if (t.getScore() > maxThread.getScore())
				maxThread = t;

		maxThread.getDetails();
		return maxThread.getBoard();
	}

}