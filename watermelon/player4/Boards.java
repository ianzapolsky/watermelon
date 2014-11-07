package watermelon.player4;

import java.util.ArrayList;

import watermelon.sim.seed;

public class Boards extends Thread {
	static double distowall = 1.00;
	static double distotree = 2.00;
	static double distoseed = 2.00;
	static double epsilon = .0000001;

	SeedGraph seedgraph;

	public Boards(SeedGraph initSeedGraph) {
		seedgraph = initSeedGraph;
	}

	// North West
	public ArrayList<seed> getHexagonalNWBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = distowall; j <= seedgraph.length - distowall; j = j + seedgraph.getHexagonalOffsetY() + epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = distowall; i <= seedgraph.width - distowall; i = i + distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x += seedgraph.getHexagonalOffsetX();
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
		for (int k = 0; k < 5; k++) {
			seedgraph.recolorBoard(tmplist);
			seedgraph.jiggleBoard(tmplist);
		}
		return tmplist;
	}

	// North East
	public ArrayList<seed> getHexagonalNEBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = distowall; j <= seedgraph.length - distowall; j = j + seedgraph.getHexagonalOffsetY() + epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = seedgraph.width - distowall; i >= distowall; i = i - distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x -= seedgraph.getHexagonalOffsetX();
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
		for (int k = 0; k < 5; k++) {
			seedgraph.recolorBoard(tmplist);
			seedgraph.jiggleBoard(tmplist);
		}
		return tmplist;
	}

	// South West
	public ArrayList<seed> getHexagonalSWBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = seedgraph.length - distowall; j >= distowall; j = j - seedgraph.getHexagonalOffsetY() - epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = distowall; i <= seedgraph.width - distowall; i = i + distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x += seedgraph.getHexagonalOffsetX();
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
		for (int k = 0; k < 5; k++) {
			seedgraph.recolorBoard(tmplist);
			seedgraph.jiggleBoard(tmplist);
		}
		return tmplist;
	}

	// South East
	public ArrayList<seed> getHexagonalSEBoard() {

		ArrayList<seed> tmplist = new ArrayList<seed>();
		int seedType = 1;
		boolean shift = false;
		boolean alternateRow = true;
		for (double j = seedgraph.length - distowall; j >= distowall; j = j - seedgraph.getHexagonalOffsetY() - epsilon) {
			if (!alternateRow)
				seedType *= -1;
			alternateRow = !alternateRow;
			for (double i = seedgraph.width - distowall; i >= distowall; i = i - distoseed) {
				seed tmp = new seed(i, j, false);
				if (shift) {
					tmp.x -= seedgraph.getHexagonalOffsetX();
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
		for (int k = 0; k < 5; k++) {
			seedgraph.recolorBoard(tmplist);
			seedgraph.jiggleBoard(tmplist);
		}
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
		return tmplist;
	}

}
