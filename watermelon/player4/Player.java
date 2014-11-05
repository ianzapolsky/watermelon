package watermelon.player4;

import java.util.ArrayList;
import java.util.Random;

import watermelon.sim.Pair;
import watermelon.sim.Point;
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

	public Player() {
		init();
	}

	public void init() {
		seedlist = new ArrayList<seed>();
		random = new Random();
	}

	// distance between seed and pair
	static double distance(seed tmp, Pair pair) {
		return Math.sqrt((tmp.x - pair.x) * (tmp.x - pair.x) + (tmp.y - pair.y) * (tmp.y - pair.y));
	}

	// distance between seed and point
	static double distance(seed a, Point b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	// distance between seed and seed
	static double distance(seed a, seed b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	// get the hexagonal x offset
	static double getHexagonalOffsetX() {
		return distoseed * Math.sin(Math.PI / 6.0);
	}

	// get the hexagonal y offset
	static double getHexagonalOffsetY() {
		return distoseed * Math.cos(Math.PI / 6.0);
	}

	@Override
	public ArrayList<seed> move(ArrayList<Pair> initTreelist, double initWidth, double initLength, double initS) {
		treelist = initTreelist;
		width = initWidth;
		length = initLength;
		s = initS;

		ArrayList<seed> hexAlternatingNW = getHexagonalNWAlternatingBoard();
		ArrayList<seed> hexAlternatingNE = getHexagonalNEAlternatingBoard();
		ArrayList<seed> hexAlternatingSW = getHexagonalSWAlternatingBoard();
		ArrayList<seed> hexAlternatingSE = getHexagonalSEAlternatingBoard();
		ArrayList<seed> gridAlternatingNW = getNWAlternatingBoard();
		ArrayList<seed> gridAlternatingNE = getNEAlternatingBoard();
		ArrayList<seed> gridAlternatingSW = getSWAlternatingBoard();
		ArrayList<seed> gridAlternatingSE = getSEAlternatingBoard();

		if (calculateScore(hexAlternatingNW) > calculateScore(gridAlternatingNW))
			seedlist = hexAlternatingNW;
		else
			seedlist = gridAlternatingNW;

		if (calculateScore(hexAlternatingNE) > calculateScore(seedlist))
			seedlist = hexAlternatingNE;

		if (calculateScore(hexAlternatingSW) > calculateScore(seedlist))
			seedlist = hexAlternatingSW;

		if (calculateScore(hexAlternatingSE) > calculateScore(seedlist))
			seedlist = hexAlternatingSE;

		if (calculateScore(gridAlternatingNE) > calculateScore(seedlist))
			seedlist = gridAlternatingNE;

		if (calculateScore(gridAlternatingSW) > calculateScore(seedlist))
			seedlist = gridAlternatingSW;

		if (calculateScore(gridAlternatingSE) > calculateScore(seedlist))
			seedlist = gridAlternatingSE;

		System.out.printf("seedlist size is %d\n", seedlist.size());
		System.out.printf("score is %f\n", calculateScore(seedlist));
		return seedlist;
	}

	private void recolorBoard(ArrayList<seed> seedlist) {
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < seedlist.size(); i++) {
				double origScore = calculateScore(seedlist);
				// change the type to calculate the score
				seedlist.get(i).tetraploid = !seedlist.get(i).tetraploid;
				double diffScore = calculateScore(seedlist);
				// if the original score was better, change it back
				if (origScore > diffScore)
					seedlist.get(i).tetraploid = !seedlist.get(i).tetraploid;
			}
		}
	}

	// North West
	private ArrayList<seed> getHexagonalNWAlternatingBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = distowall; j <= length - distowall; j = j + getHexagonalOffsetY() + epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = distowall; i <= width - distowall; i = i + distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x += getHexagonalOffsetX();
				}
				if (seedType == 1) {
					tmp.tetraploid = true;
				}
				seedType *= -1;
				if (validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
			shift = !shift;
		}
		recolorBoard(tmplist);
		return tmplist;
	}

	// North East
	private ArrayList<seed> getHexagonalNEAlternatingBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = distowall; j <= length - distowall; j = j + getHexagonalOffsetY() + epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = width - distowall; i >= distowall; i = i - distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x -= getHexagonalOffsetX();
				}
				if (seedType == 1) {
					tmp.tetraploid = true;
				}
				seedType *= -1;
				if (validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
			shift = !shift;
		}
		recolorBoard(tmplist);
		return tmplist;
	}

	// South West
	private ArrayList<seed> getHexagonalSWAlternatingBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = length - distowall; j >= distowall; j = j - getHexagonalOffsetY() - epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = distowall; i <= width - distowall; i = i + distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x += getHexagonalOffsetX();
				}
				if (seedType == 1) {
					tmp.tetraploid = true;
				}
				seedType *= -1;
				if (validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
			shift = !shift;
		}
		recolorBoard(tmplist);
		return tmplist;
	}

	// South East
	private ArrayList<seed> getHexagonalSEAlternatingBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = length - distowall; j >= distowall; j = j - getHexagonalOffsetY() - epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = width - distowall; i >= distowall; i = i - distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x -= getHexagonalOffsetX();
				}
				if (seedType == 1) {
					tmp.tetraploid = true;
				}
				seedType *= -1;
				if (validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
			shift = !shift;
		}
		recolorBoard(tmplist);
		return tmplist;
	}

	// North West
	private ArrayList<seed> getNWAlternatingBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		for (double i = distowall; i <= width - distowall; i = i + distoseed) {

			// alternate initial seed type per row
			if (alternateRow)
				seedType = seedType * -1;
			alternateRow = !alternateRow;
			for (double j = distowall; j <= length - distowall; j = j + distoseed) {
				seed tmp;
				// alternate seed type
				if (seedType == 1) {
					tmp = new seed(i, j, false);
					seedType *= -1;
				} else {
					tmp = new seed(i, j, true);
					seedType *= -1;
				}
				if (validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
		}
		return tmplist;
	}

	// North East
	private ArrayList<seed> getNEAlternatingBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		for (double i = width - distowall; i >= distowall; i = i - distoseed) {

			// alternate initial seed type per row
			if (alternateRow)
				seedType = seedType * -1;
			alternateRow = !alternateRow;
			for (double j = distowall; j <= length - distowall; j = j + distoseed) {
				seed tmp;
				// alternate seed type
				if (seedType == 1) {
					tmp = new seed(i, j, false);
					seedType *= -1;
				} else {
					tmp = new seed(i, j, true);
					seedType *= -1;
				}
				if (validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
		}
		return tmplist;
	}

	// South West
	private ArrayList<seed> getSWAlternatingBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		for (double i = distowall; i <= width - distowall; i = i + distoseed) {

			// alternate initial seed type per row
			if (alternateRow)
				seedType = seedType * -1;
			alternateRow = !alternateRow;
			for (double j = length - distowall; j >= distowall; j = j - distoseed) {
				seed tmp;
				// alternate seed type
				if (seedType == 1) {
					tmp = new seed(i, j, false);
					seedType *= -1;
				} else {
					tmp = new seed(i, j, true);
					seedType *= -1;
				}
				if (validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
		}
		return tmplist;
	}

	// South East
	private ArrayList<seed> getSEAlternatingBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		for (double i = width - distowall; i >= distowall; i = i - distoseed) {

			// alternate initial seed type per row
			if (alternateRow)
				seedType = seedType * -1;
			alternateRow = !alternateRow;
			for (double j = length - distowall; j >= distowall; j = j - distoseed) {
				seed tmp;
				// alternate seed type
				if (seedType == 1) {
					tmp = new seed(i, j, false);
					seedType *= -1;
				} else {
					tmp = new seed(i, j, true);
					seedType *= -1;
				}
				if (validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
		}
		return tmplist;
	}

	private ArrayList<seed> getRandomBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		for (double i = distowall; i < width - distowall; i = i + distoseed) {
			for (double j = distowall; j < length - distowall; j = j + distoseed) {
				seed tmp;
				if (random.nextInt(2) == 0)
					tmp = new seed(i, j, false);
				else
					tmp = new seed(i, j, true);
				if (validateSeed(tmp))
					tmplist.add(tmp);
			}
		}
		return tmplist;
	}

	private boolean validateSeed(seed tmpSeed) {
		for (Pair p : treelist) {
			if (distance(tmpSeed, p) < distotree) {
				return false;
			}
			if (tmpSeed.x + 1.00 > width || tmpSeed.x - 1.00 < 0) {
				return false;
			}
			if (tmpSeed.y + 1.00 > length || tmpSeed.y - 1.00 < 0) {
				return false;
			}
		}
		return true;
	}

	private double calculateScore(ArrayList<seed> seedlist) {
		double total = 0.0;
		for (int i = 0; i < seedlist.size(); i++) {
			double score = 0.0;
			double chance = 0.0;
			double totaldis = 0.0;
			double difdis = 0.0;
			for (int j = 0; j < seedlist.size(); j++) {
				if (j != i) {
					totaldis = totaldis + Math.pow(distance(seedlist.get(i), seedlist.get(j)), -2);
				}
			}
			for (int j = 0; j < seedlist.size(); j++) {
				if (j != i
						&& ((seedlist.get(i).tetraploid && !seedlist.get(j).tetraploid) || (!seedlist.get(i).tetraploid && seedlist
								.get(j).tetraploid))) {
					difdis = difdis + Math.pow(distance(seedlist.get(i), seedlist.get(j)), -2);
				}
			}
			chance = difdis / totaldis;
			score = chance + (1 - chance) * s;
			total = total + score;
		}
		return total;
	}

}