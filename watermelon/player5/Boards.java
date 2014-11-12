package watermelon.player5;

import java.util.ArrayList;

import watermelon.sim.seed;

public class Boards {
	static double distowall = 1.00;
	static double distotree = 2.00;
	static double distoseed = 2.00;
	static double epsilon = .0000001;

	SeedGraph seedgraph;

	public Boards(SeedGraph initSeedGraph) {
		seedgraph = initSeedGraph;
	}

	// get the hexagonal x offset
	public double getHexagonalOffsetX() {
		return distoseed * Math.sin(Math.PI / 6.0);
	}

	// get the hexagonal y offset
	public double getHexagonalOffsetY() {
		return distoseed * Math.cos(Math.PI / 6.0);
	}

	// recolor and jiggle the board, stopping when no improvement is seen
	public void recolorJiggleSmart(ArrayList<seed> tmplist) {
		double currentScore = seedgraph.calculateScore(tmplist);

		// try to add a new seed
		seedgraph.moveAllSeedsToSidesAndInsert(tmplist);
		seedgraph.jiggleAllSeedsTowardTree(tmplist);
		seedgraph.scanAndInsert(tmplist);

		while (true) {
			// now try to increase the board score
			seedgraph.shiftRowsAndCols(tmplist);
			seedgraph.jiggleAllSeeds(tmplist);
			seedgraph.recolorBoard(tmplist);

			double improvedScore = seedgraph.calculateScore(tmplist);
			if (improvedScore <= currentScore)
				break;
			else {
				System.out.println("Old score: " + currentScore + ", New score: " + improvedScore);
				currentScore = improvedScore;
			}
		}
	}

	// North West
	public ArrayList<seed> getHexagonalNWBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = distowall; j <= seedgraph.length - distowall; j = j + getHexagonalOffsetY() + epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = distowall; i <= seedgraph.width - distowall; i = i + distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x += getHexagonalOffsetX();
				}
				if (seedType == 1) {
					tmp.tetraploid = true;
				}
				seedType *= -1;
				if (seedgraph.validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
			shift = !shift;
		}
		recolorJiggleSmart(tmplist);
		System.out.println("Finished Building NW HEX with score " + seedgraph.calculateScore(tmplist) + " and "
				+ tmplist.size() + "seeds");
		return tmplist;
	}

	// North East
	public ArrayList<seed> getHexagonalNEBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = distowall; j <= seedgraph.length - distowall; j = j + getHexagonalOffsetY() + epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = seedgraph.width - distowall; i >= distowall; i = i - distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x -= getHexagonalOffsetX();
				}
				if (seedType == 1) {
					tmp.tetraploid = true;
				}
				seedType *= -1;
				if (seedgraph.validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
			shift = !shift;
		}
		recolorJiggleSmart(tmplist);
		System.out.println("Finished Building NE HEX with score " + seedgraph.calculateScore(tmplist) + " and "
				+ tmplist.size() + "seeds");
		return tmplist;
	}

	// South West
	public ArrayList<seed> getHexagonalSWBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = seedgraph.length - distowall; j >= distowall; j = j - getHexagonalOffsetY() - epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = distowall; i <= seedgraph.width - distowall; i = i + distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x += getHexagonalOffsetX();
				}
				if (seedType == 1) {
					tmp.tetraploid = true;
				}
				seedType *= -1;
				if (seedgraph.validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
			shift = !shift;
		}
		recolorJiggleSmart(tmplist);
		System.out.println("Finished Building SW HEX with score " + seedgraph.calculateScore(tmplist) + " and "
				+ tmplist.size() + "seeds");
		return tmplist;
	}

	// South East
	public ArrayList<seed> getHexagonalSEBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = seedgraph.length - distowall; j >= distowall; j = j - getHexagonalOffsetY() - epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = seedgraph.width - distowall; i >= distowall; i = i - distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x -= getHexagonalOffsetX();
				}
				if (seedType == 1) {
					tmp.tetraploid = true;
				}
				seedType *= -1;
				if (seedgraph.validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
			shift = !shift;
		}
		recolorJiggleSmart(tmplist);
		System.out.println("Finished Building SE HEX with score " + seedgraph.calculateScore(tmplist) + " and "
				+ tmplist.size() + "seeds");
		return tmplist;
	}

	// North West
	public ArrayList<seed> getAlternatingNWBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		for (double i = distowall; i <= seedgraph.width - distowall; i = i + distoseed) {

			// alternate initial seed type per row
			if (alternateRow)
				seedType = seedType * -1;
			alternateRow = !alternateRow;
			for (double j = distowall; j <= seedgraph.length - distowall; j = j + distoseed) {
				seed tmp;
				// alternate seed type
				if (seedType == 1) {
					tmp = new seed(i, j, false);
					seedType *= -1;
				} else {
					tmp = new seed(i, j, true);
					seedType *= -1;
				}
				if (seedgraph.validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
		}
		System.out.println("Finished Building NW ALT with score " + seedgraph.calculateScore(tmplist) + " and "
				+ tmplist.size() + "seeds");
		return tmplist;
	}

	// North East
	public ArrayList<seed> getAlternatingNEBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		for (double i = seedgraph.width - distowall; i >= distowall; i = i - distoseed) {

			// alternate initial seed type per row
			if (alternateRow)
				seedType = seedType * -1;
			alternateRow = !alternateRow;
			for (double j = distowall; j <= seedgraph.length - distowall; j = j + distoseed) {
				seed tmp;
				// alternate seed type
				if (seedType == 1) {
					tmp = new seed(i, j, false);
					seedType *= -1;
				} else {
					tmp = new seed(i, j, true);
					seedType *= -1;
				}
				if (seedgraph.validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
		}
		System.out.println("Finished Building NE ALT with score " + seedgraph.calculateScore(tmplist) + " and "
				+ tmplist.size() + "seeds");
		return tmplist;
	}

	// South West
	public ArrayList<seed> getAlternatingSWBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		for (double i = distowall; i <= seedgraph.width - distowall; i = i + distoseed) {

			// alternate initial seed type per row
			if (alternateRow)
				seedType = seedType * -1;
			alternateRow = !alternateRow;
			for (double j = seedgraph.length - distowall; j >= distowall; j = j - distoseed) {
				seed tmp;
				// alternate seed type
				if (seedType == 1) {
					tmp = new seed(i, j, false);
					seedType *= -1;
				} else {
					tmp = new seed(i, j, true);
					seedType *= -1;
				}
				if (seedgraph.validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
		}
		System.out.println("Finished Building SW ALT with score " + seedgraph.calculateScore(tmplist) + " and "
				+ tmplist.size() + "seeds");
		return tmplist;
	}

	// South East
	public ArrayList<seed> getAlternatingSEBoard() {
		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean alternateRow = true;
		for (double i = seedgraph.width - distowall; i >= distowall; i = i - distoseed) {

			// alternate initial seed type per row
			if (alternateRow)
				seedType = seedType * -1;
			alternateRow = !alternateRow;
			for (double j = seedgraph.length - distowall; j >= distowall; j = j - distoseed) {
				seed tmp;
				// alternate seed type
				if (seedType == 1) {
					tmp = new seed(i, j, false);
					seedType *= -1;
				} else {
					tmp = new seed(i, j, true);
					seedType *= -1;
				}
				if (seedgraph.validateSeed(tmp))
					tmplist.add(tmp);
			}
			seedType = 1;
		}
		System.out.println("Finished Building SE ALT with score " + seedgraph.calculateScore(tmplist) + " and "
				+ tmplist.size() + "seeds");
		return tmplist;
	}

}
