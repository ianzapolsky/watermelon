package watermelon.player5;

import java.util.ArrayList;

import watermelon.sim.Pair;
import watermelon.sim.Point;
import watermelon.sim.seed;

public class SeedGraph {

	static double distowall = 1.00;
	static double distotree = 2.00;
	static double distoseed = 2.00;
	static double epsilon = .0000001;

	double width;
	double length;
	double s;
	ArrayList<Pair> treelist;
	ArrayList<seed> seedlist;

	public SeedGraph(ArrayList<Pair> initTreelist, double initWidth, double initLength, double initS) {
		treelist = initTreelist;
		width = initWidth;
		length = initLength;
		s = initS;
	}

	// optimized recolor board to stop if no improvement is seen
	public void recolorBoard(ArrayList<seed> seedlist) {
		double origScore = calculateScore(seedlist);
		double currentScore;
		while (true) {
			currentScore = origScore;
			for (int i = 0; i < seedlist.size(); i++) {
				// change the type to calculate the score
				seedlist.get(i).tetraploid = !seedlist.get(i).tetraploid;
				double diffScore = calculateScore(seedlist);
				// if the original score was better, change it back
				if (currentScore > diffScore)
					seedlist.get(i).tetraploid = !seedlist.get(i).tetraploid;
				else
					currentScore = diffScore;
			}
			if (currentScore <= origScore)
				break;
			else
				origScore = currentScore;
		}
	}

	private void jiggleSeed(ArrayList<seed> seedlist, int seedIndex) {
		seed s = seedlist.get(seedIndex);
		double angleGranularity = 1;
		double locationGranularity = 0.1;
		double origScore = calculateScore(seedlist);
		double bestScore = origScore;
		double origX = s.x;
		double origY = s.y;
		double bestX = s.x;
		double bestY = s.y;

		for (double angle = 0; angle < 360; angle += angleGranularity) {
			s.x = origX;
			s.y = origY;
			double score;
			double x = Math.cos(Math.toRadians(angle)) * locationGranularity;
			double y = Math.sin(Math.toRadians(angle)) * locationGranularity;
			while (true) {
				s.x += x;
				s.y += y;
				if (!validateSeed(s, seedlist))
					break;
				score = calculateScore(seedlist);
				if (score > bestScore) {
					bestScore = score;
					bestX = s.x;
					bestY = s.y;
				}
			}
		}
		s.x = bestX;
		s.y = bestY;
	}

	public void jiggleAllSeeds(ArrayList<seed> seedlist) {
		int i = 0;
		int maxIterations = 50;
		double currentScore = calculateScore(seedlist);
		while (i++ < maxIterations) {
			for (int j = 0; j < seedlist.size(); j++) {
			  jiggleSeed(seedlist, j);
			}
			double diffScore = calculateScore(seedlist);
			if (diffScore > currentScore)
				currentScore = diffScore;
			else
				break;
		}
	}

	public void jiggleSeedTowardTree(ArrayList<seed> seedlist, int seedIndex) {
		seed s = seedlist.get(seedIndex);

		if (treelist.size() == 0)
			return;
		Pair nearestTree = treelist.get(0);
		for (Pair p : treelist) {
			if (distance(p, s) < distance(nearestTree, s))
				nearestTree = p;
		}

		double dist = distance(nearestTree, s);
		double bestDist = dist;
		double angleGranularity = 1;
		double locationGranularity = 0.1;
		double origX = s.x;
		double origY = s.y;
		double bestX = s.x;
		double bestY = s.y;

		for (double angle = 0; angle < 360; angle += angleGranularity) {
			s.x = origX;
			s.y = origY;
			double x = Math.cos(Math.toRadians(angle)) * locationGranularity;
			double y = Math.sin(Math.toRadians(angle)) * locationGranularity;
			while (true) {
				s.x += x;
				s.y += y;
				if (!validateSeed(s, seedlist))
					break;
				double newDist = distance(nearestTree, s);
				if (newDist < bestDist) {
					bestDist = newDist;
					bestX = s.x;
					bestY = s.y;
				}
			}
		}
		s.x = bestX;
		s.y = bestY;
	}

	public void jiggleAllSeedsTowardTree(ArrayList<seed> seedlist) {
		int i = 0;
		int maxIterations = 100;
		while (i++ < maxIterations) {
			for (int j = 0; j < seedlist.size(); j++) {
			  jiggleSeedTowardTree(seedlist, j);
			}
		}
	}

	public void moveAllSeedsToSidesAndInsert(ArrayList<seed> seedlist) {
		int i = 0;
		int maxIterations = 2;
		while (i++ < maxIterations) {
			moveAllSeedsN(seedlist);
			scanAndInsert(seedlist);
			moveAllSeedsE(seedlist);
			scanAndInsert(seedlist);
			moveAllSeedsS(seedlist);
			scanAndInsert(seedlist);
			moveAllSeedsW(seedlist);
		}
	}

	public void moveAllSeedsN(ArrayList<seed> seedlist) {
		ArrayList<ArrayList<seed>> rows = getRows(seedlist);
		// shift up
		for (ArrayList<seed> row : rows) {
			boolean movedRow = false;
			while (true) {
				boolean movedSeed = false;
				for (seed s : row) {
					s.y -= 0.1;
					while (validateSeed(seedlist)) {
						movedRow = true;
						movedSeed = true;
						s.y -= 0.1;
					}
					s.y += 0.1;
				}
				// if no seeds in this row can move, stop moving this row
				if (movedSeed == false)
					break;
			}
			// if the last row hasn't moved, then the next row won't be
			// able to either
			if (movedRow == false)
				break;
		}
	}

	public void moveAllSeedsS(ArrayList<seed> seedlist) {
		ArrayList<ArrayList<seed>> rows = getRows(seedlist);
		// shift down
		for (ArrayList<seed> row : rows) {
			boolean movedRow = false;
			while (true) {
				boolean movedSeed = false;
				for (seed s : row) {
					s.y += 0.1;
					while (validateSeed(seedlist)) {
						movedRow = true;
						movedSeed = true;
						s.y += 0.1;
					}
					s.y -= 0.1;
				}
				// if no seeds in this row can move, stop moving this row
				if (movedSeed == false)
					break;
			}
			// if the last row hasn't moved, then the next row won't be
			// able to either
			if (movedRow == false)
				break;
		}
	}

	public void moveAllSeedsE(ArrayList<seed> seedlist) {
		ArrayList<ArrayList<seed>> cols = getCols(seedlist);
		// shift down
		for (ArrayList<seed> col : cols) {
			boolean movedCol = false;
			while (true) {
				boolean movedSeed = false;
				for (seed s : col) {
					s.x += 0.1;
					while (validateSeed(seedlist)) {
						movedCol = true;
						movedSeed = true;
						s.x += 0.1;
					}
					s.x -= 0.1;
				}
				// if no seeds in this row can move, stop moving this row
				if (movedSeed == false)
					break;
			}
			// if the last row hasn't moved, then the next row won't be
			// able to either
			if (movedCol == false)
				break;
		}
	}

	public void moveAllSeedsW(ArrayList<seed> seedlist) {
		ArrayList<ArrayList<seed>> cols = getCols(seedlist);
		// shift down
		for (ArrayList<seed> col : cols) {
			boolean movedCol = false;
			while (true) {
				boolean movedSeed = false;
				for (seed s : col) {
					s.x -= 0.1;
					while (validateSeed(seedlist)) {
						movedCol = true;
						movedSeed = true;
						s.x -= 0.1;
					}
					s.x += 0.1;
				}
				// if no seeds in this row can move, stop moving this row
				if (movedSeed == false)
					break;
			}
			// if the last row hasn't moved, then the next row won't be
			// able to either
			if (movedCol == false)
				break;
		}
	}

	// scan the board with a small granularity, check if a seed can be inserted
	public void scanAndInsert(ArrayList<seed> tmplist) {
		for (double i = distowall; i <= width - distowall; i += .01) {
			for (double j = distowall; j <= length - distowall; j += .01) {
				seed s = new seed(i, j, true);
				if (validateSeed(s, tmplist)) {
					tmplist.add(s);
				}
			}
		}
	}

	// THIS METHOD CONTAINS A BUG. DO NOT USE.
	// move the bottom or top row to the edge of the field
	public void spaceEdgeRow(ArrayList<seed> tmplist) {
		// determine spacing direction
		if (tmplist.get(0).y > 1.00) {
			int i = tmplist.size() - 1;
			double rowY = tmplist.get(i).y;
			do {
				seed s = tmplist.get(i--);
				double tempY = s.y;
				s.y = 1.00;
				if (!validateSeed(s))
					s.y = tempY;
			} while (tmplist.get(i).y == rowY);
		} else {
			int i = tmplist.size() - 1;
			double rowY = tmplist.get(i).y;
			do {
				seed s = tmplist.get(i--);
				double tempY = s.y;
				s.y = length - 1.00;
				if (!validateSeed(s))
					s.y = tempY;
			} while (tmplist.get(i).y == rowY);
		}
	}

	public ArrayList<seed> shiftRowsAndCols(ArrayList<seed> seedlist) {
		shiftRows(seedlist);
		shiftCols(seedlist);
		return seedlist;
	}

	public ArrayList<seed> shiftRows(ArrayList<seed> seedlist) {
		ArrayList<seed> board = (ArrayList<seed>) seedlist.clone();
		ArrayList<ArrayList<seed>> rows = getRows(board);
		ArrayList<seed> maxBoard = seedlist;
		double maxScore = calculateScore(seedlist);

		// shift up
		for (ArrayList<seed> row : rows) {
			while (true) {
				int movedSeedsCount = 0;
				for (seed s : row) {
					s.y -= .1;
					if (!validateSeed(seedlist))
						s.y += .1;
					else
						movedSeedsCount++;
				}
				// makes sure that seeds have actually been moved before we
				// recolor and calculate the score
				if (movedSeedsCount == 0)
					break;
				recolorBoard(board);
				double score = calculateScore(board);
				if (score > maxScore) {
					maxScore = score;
					maxBoard = board;
				} else
					break;
			}
		}

		// shift down
		for (ArrayList<seed> row : rows) {
			while (true) {
				int movedSeedsCount = 0;
				for (seed s : row) {
					s.y += .1;
					if (!validateSeed(seedlist))
						s.y -= .1;
					else
						movedSeedsCount++;
				}
				// makes sure that seeds have actually been moved before we
				// recolor and calculate the score
				if (movedSeedsCount == 0)
					break;
				recolorBoard(board);
				double score = calculateScore(board);
				if (score > maxScore) {
					maxScore = score;
					maxBoard = board;
				} else
					break;
			}
		}

		seedlist = maxBoard;
		return maxBoard;
	}

	public ArrayList<seed> shiftCols(ArrayList<seed> seedlist) {
		ArrayList<seed> board = (ArrayList<seed>) seedlist.clone();
		ArrayList<ArrayList<seed>> cols = getCols(board);

		ArrayList<seed> maxBoard = seedlist;
		double maxScore = calculateScore(seedlist);

		for (ArrayList<seed> col : cols) {
			while (true) {
				int movedSeedsCount = 0;
				for (seed s : col) {
					s.x -= 0.1;
					if (!validateSeed(seedlist))
						s.x += 0.1;
					else
						movedSeedsCount++;
				}

				if (movedSeedsCount == 0)
					break;

				recolorBoard(board);
				double score = calculateScore(board);
				if (score > maxScore) {
					maxScore = score;
					maxBoard = board;
				} else
					break;
			}
		}

		for (ArrayList<seed> col : cols) {
			boolean scoreIncreasing = true;
			while (scoreIncreasing) {
				int movedSeedsCount = 0;
				for (seed s : col) {
					s.x += 0.1;
					if (!validateSeed(seedlist))
						s.x -= 0.1;
					else
						movedSeedsCount++;
				}

				if (movedSeedsCount == 0)
					break;

				recolorBoard(board);
				double score = calculateScore(board);
				if (score > maxScore) {
					maxScore = score;
					maxBoard = board;
				} else
					scoreIncreasing = false;
			}
		}
		seedlist = maxBoard;
		return maxBoard;
	}

	public ArrayList<ArrayList<seed>> getRows(ArrayList<seed> seedlist) {
		ArrayList<ArrayList<seed>> rows = new ArrayList<ArrayList<seed>>();
		rows.add(getRow(seedlist, seedlist.get(0)));
		for (seed s : seedlist) {
			boolean hasRow = false;
			for (int j = 0; j < rows.size(); j++) {
				if (Math.abs(rows.get(j).get(0).y - s.y) < distoseed / 2) {
					hasRow = true;
					rows.get(j).add(s);
					continue;
				}
			}
			if (hasRow == false)
				rows.add(getRow(seedlist, s));
		}
		return rows;
	}

	public ArrayList<ArrayList<seed>> getCols(ArrayList<seed> seedlist) {
		ArrayList<ArrayList<seed>> cols = new ArrayList<ArrayList<seed>>();
		cols.add(getCol(seedlist, seedlist.get(0)));
		for (seed s : seedlist) {
			boolean hasCol = false;
			for (int j = 0; j < cols.size(); j++) {
				if (Math.abs(cols.get(j).get(0).y - s.y) < distoseed / 2) {
					hasCol = true;
					cols.get(j).add(s);
					continue;
				}
			}
			if (hasCol == false)
				cols.add(getRow(seedlist, s));
		}
		return cols;
	}

	public ArrayList<seed> getRow(ArrayList<seed> seedlist, seed seedInit) {
		ArrayList<seed> row = new ArrayList<seed>();
		for (seed s : seedlist)
			if (Math.abs(seedInit.y - s.y) < distoseed / 2)
				row.add(s);
		return row;
	}

	public ArrayList<seed> getCol(ArrayList<seed> seedlist, seed seedInit) {
		ArrayList<seed> row = new ArrayList<seed>();
		for (seed s : seedlist)
			if (Math.abs(seedInit.x - s.x) < distoseed / 2)
				row.add(s);
		return row;
	}

	// return all the plants that are adjacent to the plant at seedIndex
	public ArrayList<seed> getAdjacentSeeds(ArrayList<seed> seedlist, int seedIndex) {
		ArrayList<seed> adjacentSeeds = new ArrayList<seed>();
		double minDist = Double.MAX_VALUE;
		for (int i = 0; i < seedlist.size(); i++) {
			if (i == seedIndex) {
				/* do nothing */;
			} else {
				double dist = distance(seedlist.get(seedIndex), seedlist.get(i));
				if (dist < minDist - epsilon) {
					adjacentSeeds.clear();
					adjacentSeeds.add(seedlist.get(i));
					minDist = dist;
				} else if (dist < minDist + epsilon) {
					adjacentSeeds.add(seedlist.get(i));
				}
			}
		}
		return adjacentSeeds;
	}

	public double calculateScore(ArrayList<seed> seedlist) {
		if (seedlist.size() == 1)
			return s;
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

	public boolean validateSeed(seed tmpSeed) {

		if (tmpSeed.x + 1.00 > width || tmpSeed.x - 1.00 < 0) {
			return false;
		}
		if (tmpSeed.y + 1.00 > length || tmpSeed.y - 1.00 < 0) {
			return false;
		}

		for (Pair p : treelist) {
			if (distance(tmpSeed, p) < distotree) {
				return false;
			}
		}
		return true;
	}

	public boolean validateSeed(seed tmpSeed, ArrayList<seed> tmplist) {

		if (tmpSeed.x + 1.00 > width || tmpSeed.x - 1.00 < 0) {
			return false;
		}
		if (tmpSeed.y + 1.00 > length || tmpSeed.y - 1.00 < 0) {
			return false;
		}

		for (seed s : tmplist) {
			if (s != tmpSeed && distance(tmpSeed, s) < distoseed) {
				return false;
			}
		}
		for (Pair p : treelist) {
			if (distance(tmpSeed, p) < distotree) {
				return false;
			}
		}
		return true;
	}

	public boolean validateSeed(ArrayList<seed> seedlist) {
		int nseeds = seedlist.size();

		for (int i = 0; i < nseeds; i++) {
			for (int j = i + 1; j < nseeds; j++) {
				if (distance(seedlist.get(i), seedlist.get(j)) < distoseed) {
					return false;
				}
			}
		}
		for (int i = 0; i < nseeds; i++) {
			if (seedlist.get(i).x < 0 || seedlist.get(i).x > width || seedlist.get(i).y < 0
					|| seedlist.get(i).y > length) {
				return false;
			}
			if (seedlist.get(i).x < distowall || width - seedlist.get(i).x < distowall || seedlist.get(i).y < distowall
					|| length - seedlist.get(i).y < distowall) {
				return false;
			}
		}
		for (int i = 0; i < treelist.size(); i++) {
			for (int j = 0; j < nseeds; j++) {
				if (distance(seedlist.get(j), treelist.get(i)) < distotree) {
					return false;
				}
			}
		}
		return true;
	}

	// distance between seed and pair
	public double distance(seed tmp, Pair pair) {
		return Math.sqrt((tmp.x - pair.x) * (tmp.x - pair.x) + (tmp.y - pair.y) * (tmp.y - pair.y));
	}

	public double distance(Pair pair, seed tmp) {
		return Math.sqrt((tmp.x - pair.x) * (tmp.x - pair.x) + (tmp.y - pair.y) * (tmp.y - pair.y));
	}

	// distance between seed and point
	public double distance(seed a, Point b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	// distance between seed and seed
	public double distance(seed a, seed b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

}
