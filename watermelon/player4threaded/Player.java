package watermelon.player4threaded;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

		ArrayList<BoardRunnable> boardRunnables = new ArrayList<BoardRunnable>();
		ArrayList<Thread> boardThreads = new ArrayList<Thread>();

		boardRunnables.add(new HexagonalNWBoardThread(seedgraph, boards));
		boardRunnables.add(new HexagonalNEBoardThread(seedgraph, boards));
		boardRunnables.add(new HexagonalSWBoardThread(seedgraph, boards));
		boardRunnables.add(new HexagonalSEBoardThread(seedgraph, boards));
		boardRunnables.add(new AlternatingNWBoardThread(seedgraph, boards));
		boardRunnables.add(new AlternatingNEBoardThread(seedgraph, boards));
		boardRunnables.add(new AlternatingSWBoardThread(seedgraph, boards));
		boardRunnables.add(new AlternatingSEBoardThread(seedgraph, boards));

		ExecutorService threadPoolExecutor = new ThreadPoolExecutor(boardRunnables.size(), boardRunnables.size(), 600,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		for (BoardRunnable r : boardRunnables)
			threadPoolExecutor.execute(r);

		while (!threadPoolExecutor.isTerminated())
			; // stay until threads terminate

		BoardRunnable maxRunnable = boardRunnables.get(0);
		for (BoardRunnable r : boardRunnables)
			if (r.getScore() > maxRunnable.getScore())
				maxRunnable = r;

		maxRunnable.getDetails();
		return maxRunnable.getBoard();
	}

}