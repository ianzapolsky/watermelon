package watermelon.player4threaded;

import java.util.ArrayList;

import watermelon.sim.Pair;
import watermelon.sim.Point;
import watermelon.sim.seed;

public class SeedGraph {

	static double distowall = 1.00;
	static double distotree = 2.00;
	static double distoseed = 2.00;
	static double epsilon = .000001;

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

	public ArrayList<seed> getMaxBoard(ArrayList<seed> tmplist) {
		ArrayList<ArrayList<seed>> boards = new ArrayList<ArrayList<seed>>();
		ArrayList<Double> scores = new ArrayList<Double>();

		boards.add(tmplist);
		boards.add(shiftRowsAndCols((ArrayList) tmplist.clone()));
		boards.add(jiggleBoard((ArrayList) tmplist.clone()));

		for (ArrayList<seed> b : boards)
			scores.add(calculateScore(b));

		ArrayList<seed> maxBoard = boards.get(0);
		double maxScore = scores.get(0);
		for (int i = 0; i < boards.size(); i++) {
			if (scores.get(i) > maxScore) {
				maxScore = scores.get(i);
				maxBoard = boards.get(i);
			}
		}

		return maxBoard;
	}

	// distance between seed and pair
	public double distance(seed tmp, Pair pair) {
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

	public void recolorBoard(ArrayList<seed> seedlist) {
		boolean scoreIncreasing = true;
		while (scoreIncreasing) {
			int seedChange = 0;
			for (int i = 0; i < seedlist.size(); i++) {
				double origScore = calculateScore(seedlist);
				// change the type to calculate the score
				seedlist.get(i).tetraploid = !seedlist.get(i).tetraploid;
				double diffScore = calculateScore(seedlist);
				// if the original score was better, change it back
				if (origScore > diffScore) {
					seedlist.get(i).tetraploid = !seedlist.get(i).tetraploid;
					seedChange++;
				}
				if (seedChange == 0)
					scoreIncreasing = false;
			}
		}
	}

	public ArrayList<seed> shiftRowsAndCols(ArrayList<seed> seedlist) {
		shiftRows(seedlist);
		return shiftCols(seedlist);
	}

	public ArrayList<seed> shiftRows(ArrayList<seed> seedlist) {
		ArrayList<seed> board = (ArrayList<seed>) seedlist.clone();
		ArrayList<ArrayList<seed>> rows = getRows(board);

		ArrayList<seed> maxBoard = seedlist;
		double maxScore = calculateScore(seedlist);

		// shift up

		for (ArrayList<seed> row : rows) {
			boolean scoreIncreasing = true;
			while (scoreIncreasing) {
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
				if (movedSeedsCount == 0) {
					scoreIncreasing = false;
					continue;
				}

				System.out.println("recoloring board");
				recolorBoard(board);
				System.out.println("done recoloring board");
				double score = calculateScore(board);
				if (score > maxScore) {
					maxScore = score;
					maxBoard = board;
				} else
					scoreIncreasing = false;
			}
		}

		// shift down
		for (ArrayList<seed> row : rows) {
			boolean scoreIncreasing = true;
			while (scoreIncreasing) {
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
				if (movedSeedsCount == 0) {
					scoreIncreasing = false;
					continue;
				}

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

	public ArrayList<seed> shiftCols(ArrayList<seed> seedlist) {
		ArrayList<seed> board = (ArrayList<seed>) seedlist.clone();
		ArrayList<ArrayList<seed>> cols = getCols(board);

		ArrayList<seed> maxBoard = seedlist;
		double maxScore = calculateScore(seedlist);

		for (ArrayList<seed> col : cols) {
			boolean scoreIncreasing = true;
			while (scoreIncreasing) {
				int movedSeedsCount = 0;
				for (seed s : col) {
					s.x -= 0.1;
					if (!validateSeed(seedlist))
						s.x += 0.1;
					else
						movedSeedsCount++;
				}

				if (movedSeedsCount == 0) {
					scoreIncreasing = false;
					continue;
				}

				recolorBoard(board);
				double score = calculateScore(board);
				if (score > maxScore) {
					maxScore = score;
					maxBoard = board;
				} else
					scoreIncreasing = false;
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

				if (movedSeedsCount == 0) {
					scoreIncreasing = false;
					continue;
				}

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

	public ArrayList<seed> jiggleBoard(ArrayList<seed> seedlist) {
		double origScore = calculateScore(seedlist);
		double maxScore = origScore;
		for (seed s : seedlist) {
			// jiggling x in + direction
			boolean increasingScore = true;
			boolean tryOppositeDirection = true;
			while (increasingScore) {
				s.x += 0.1;
				double score = 0;
				if (!validateSeed(seedlist) || (score = calculateScore(seedlist)) < maxScore) {
					s.x -= 0.1;
					if (score < maxScore)
						increasingScore = false;
					continue;
				}
				maxScore = score;
				tryOppositeDirection = false;
			}

			if (tryOppositeDirection) {
				increasingScore = true;
				while (increasingScore) {
					s.x -= 0.1;
					double score = 0;
					if (!validateSeed(seedlist) || (score = calculateScore(seedlist)) < maxScore) {
						s.x += 0.1;
						if (score < maxScore)
							increasingScore = false;
						continue;
					}
					maxScore = score;
				}
			}

			increasingScore = true;
			tryOppositeDirection = true;
			while (increasingScore) {
				s.y -= 0.1;
				double score = 0;
				if (!validateSeed(seedlist) || (score = calculateScore(seedlist)) < maxScore) {
					s.y += 0.1;
					if (score < maxScore)
						increasingScore = false;
					continue;
				}
				maxScore = score;
				tryOppositeDirection = false;
			}

			if (tryOppositeDirection) {
				increasingScore = true;
				while (increasingScore) {
					s.y += 0.1;
					double score = 0;
					if (!validateSeed(seedlist) || (score = calculateScore(seedlist)) < maxScore) {
						s.y -= 0.1;
						if (score < maxScore)
							increasingScore = false;
						continue;
					}
					maxScore = score;
				}
			}

			recolorBoard(seedlist);
		}
		return seedlist;
	}

	public double calculateScore(ArrayList<seed> seedlist) {
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

	// get the hexagonal x offset
	public double getHexagonalOffsetX() {
		return distoseed * Math.sin(Math.PI / 6.0);
	}

	// get the hexagonal y offset
	public double getHexagonalOffsetY() {
		return distoseed * Math.cos(Math.PI / 6.0);
	}

	public boolean validateSeed(seed tmpSeed) {
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

}