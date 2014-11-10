package watermelon.player4;

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

  // optimized recolor board to stop if no improvement is seen
	public void recolorBoard(ArrayList<seed> seedlist) {
    double origScore = calculateScore(seedlist);
    double currentScore;
		for (int j = 0; j < 5; j++) {
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

  public void jiggleBoard(ArrayList<seed> seedlist) {
    double origScore = calculateScore(seedlist);
    double currentScore; 
    for (int j = 0; j < 5; j++) {
      currentScore = origScore;
      for (int i = 0; i < seedlist.size(); i++) {
        // check for jiggling eligibility
        if (getAdjacentSeeds(seedlist, i).size() < 6) {
          seed s = seedlist.get(i);
          double x = .1;
          double y = .1;
          s.x += x;
          s.y += y;
          double diffScore = calculateScore(seedlist);
          if (!validateSeed(s, seedlist) || currentScore > diffScore) {
            s.x -= x;
            s.y -= y;
          } else {
            System.out.println("jiggle applied to seed " + i);
            currentScore = diffScore;
          }
        }
      }
    }
  }

	//public void jiggleBoard(ArrayList<seed> seedlist) {
	//	for (int j = 0; j < 10; j++) {
	//		for (int i = 0; i < seedlist.size(); i++) {
	//			double origScore = calculateScore(seedlist);
	//			seed origSeed = seedlist.get(i);

	//			double maxScore = origScore;
	//			seed maxSeed = new seed(origSeed.x, origSeed.y, origSeed.tetraploid);

	//			// jiggling x
	//			seed newSeed1 = new seed(maxSeed.x + .1, maxSeed.y, maxSeed.tetraploid);
	//			if (validateSeed(newSeed1, seedlist)) {
	//				seedlist.set(i, newSeed1);
	//				double diffScore1 = calculateScore(seedlist);

	//				if (diffScore1 > maxScore) {
	//					maxSeed = newSeed1;
	//					maxScore = diffScore1;
	//				} else
	//					// set back to original
	//					seedlist.set(i, maxSeed);
	//			}

	//			seed newSeed2 = new seed(maxSeed.x - .1, maxSeed.y, maxSeed.tetraploid);
	//			if (validateSeed(newSeed2, seedlist)) {
	//				seedlist.set(i, newSeed2);
	//				double diffScore2 = calculateScore(seedlist);

	//				if (diffScore2 > maxScore) {
	//					maxSeed = newSeed2;
	//					maxScore = diffScore2;
	//				} else
	//					// set back to original
	//					seedlist.set(i, maxSeed);
	//			}

	//			// jiggling y

	//			seed newSeed3 = new seed(maxSeed.x, maxSeed.y + .1, maxSeed.tetraploid);
	//			if (validateSeed(newSeed3, seedlist)) {
	//				seedlist.set(i, newSeed3);
	//				double diffScore3 = calculateScore(seedlist);

	//				if (diffScore3 > maxScore) {
	//					maxSeed = newSeed3;
	//					maxScore = diffScore3;
	//				} else
	//					// set back to original
	//					seedlist.set(i, maxSeed);
	//			}

	//			seed newSeed4 = new seed(maxSeed.x, maxSeed.y - .1, maxSeed.tetraploid);
	//			if (validateSeed(newSeed4, seedlist)) {
	//				seedlist.set(i, newSeed4);
	//				double diffScore4 = calculateScore(seedlist);

	//				if (diffScore4 > maxScore) {
	//					maxSeed = newSeed4;
	//					maxScore = diffScore4;
	//				} else
	//					// set back to original
	//					seedlist.set(i, maxSeed);
	//			}
	//		}
	//	}
	//}

  // count the number of rows of seeds on our board
  public int countRows(ArrayList<seed> tmplist) {
    int rows = 0;
    for (seed s : tmplist) {
      if (s.x <= 2.00)
        rows++;
    }
    return rows;
  }

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

	public boolean validateSeed(seed tmpSeed, ArrayList<seed> tmplist) {

		for (int i = 0; i < tmplist.size(); i++) {
			if (distance(tmpSeed, tmplist.get(i)) < distoseed) {
				return false;
			}
		}
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

}
