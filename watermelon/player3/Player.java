package watermelon.player3;

import java.util.ArrayList;
import java.util.Random;

import watermelon.sim.Pair;
import watermelon.sim.Point;
import watermelon.sim.seed;

public class Player extends watermelon.sim.Player {

	public static double distowall = 1.00;
	public static double distotree = 2.00;
	public static double distoseed = 2.00;
	public static double epsilon = .0000001;

	public static double width;
	public static double length;
	public static double s;
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

		// ArrayList<seed> hexNWAlternating = getHexagonalNWAlternatingBoard();
		// ArrayList<seed> hexNEAlternating = getHexagonalNEAlternatingBoard();
		// ArrayList<seed> gridAlternating = getAlternatingBoard();
		ArrayList<seed> gridSpread = getSpreadBoard2();

		/*
		 * if (calculateScore(hexNWAlternating) >
		 * calculateScore(hexNEAlternating)) seedlist = hexNWAlternating; else
		 * seedlist = hexNEAlternating;
		 */
		seedlist = gridSpread;
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

	private seed getSeed(ArrayList<seed> tmplist, boolean type) {

		ArrayList<seed> tmp1list = tmplist;
		System.out.println("tmp1list.size()=" + tmp1list.size());
		seed tmp1 = new seed(distowall, distowall, type);
		tmp1list.add(tmp1);
		double score = calculateScore(tmp1list);

		for (double i = distowall; i <= width - distowall; i = i + distoseed) {
			for (double j = distowall; j <= length - distowall; j = j + distoseed) {

				seed tmp2 = new seed(i, j, type);
				ArrayList<seed> tmp2list = tmp1list;
				tmp2list.remove(tmplist.size() - 1);
				tmp2list.add(tmp2);
				System.out.println("tmp1list.size()=" + tmp1list.size());
				double tmp2score = calculateScore(tmp2list);

				if (tmp2score > score) {
					tmp1 = tmp2;
					tmp1list = tmp2list;
					score = tmp2score;
				}
			}
		}

		System.out.println("RETURNED FROM GETSEED: tmp1list.size()=" + tmp1list.size());
		return tmp1;
	}

	private ArrayList<seed> getSpreadBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		boolean added = false;

		seed tmp1 = new seed(distowall, distowall, false);
		seed tmp2 = new seed(distowall, distowall + distoseed, true);

		if (validateSeed(tmp1) && validateSeed(tmp2)) {
			tmplist.add(tmp1);
			tmplist.add(tmp2);
			System.out.println("tmplist.size()=" + tmplist.size());
		}

		while (tmplist.size() < 50) {
			System.out.println("tmplist.size()=" + tmplist.size());
			seed tmp3 = getSeed(tmplist, false);
			seed tmp4 = new seed(tmp3.x, tmp3.y + distoseed, true);
			System.out.println("validateSeed(tmp3)=" + validateSeed(tmp3) + ", tmplist.contains(tmp3)="
					+ tmplist.contains(tmp3));
			if (validateSeed(tmp3) && tmplist.contains(tmp3) == false) {
				System.out.println("in first if statement");
				tmplist.add(tmp3);
				System.out.println("from first if: tmplist.size()=" + tmplist.size());
			}
			if (validateSeed(tmp4) && tmplist.contains(tmp4) == false) {
				System.out.println("in second if statement");
				tmplist.add(tmp4);
				System.out.println("from second if: tmplist.size()=" + tmplist.size());
			}
		}

		System.out.println("FINAL: tmplist.size()=" + tmplist.size());
		return tmplist;
	}

	private ArrayList<seed> getSpreadBoard2() {
		ArrayList<seed> tmplist = getHexagonalNWAlternatingBoard();
		double score = calculateScore(tmplist);

		for (int i = 0; i < tmplist.size(); i++) {
			ArrayList<seed> tmp2list = tmplist;
			tmp2list.remove(tmp2list.size() - 1);
			recolorBoard(tmp2list);
			double tmp2score = calculateScore(tmp2list);
			if (tmp2score > score) {
				tmplist = tmp2list;
				score = tmp2score;
			}
		}
		return tmplist;
	}

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

	private ArrayList<seed> getHexagonalNEAlternatingBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = length - distowall; j > distowall; j = j - getHexagonalOffsetY() + epsilon) {
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

	private ArrayList<seed> getAlternatingBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		for (double i = distowall; i <= width - distowall; i = i + distoseed) {

			// alternate initial seed type per row
			if (alternateRow)
				seedType = seedType * -1;
			alternateRow = !alternateRow;
			System.out.println("i = " + i);
			for (double j = distowall; j <= length - distowall; j = j + distoseed) {
				System.out.println("j = " + j);
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

		recolorBoard(tmplist);
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
