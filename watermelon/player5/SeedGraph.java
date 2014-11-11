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
    double locationGranularity = 0.01;
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
		int maxIterations = 100;
    double currentScore = calculateScore(seedlist);
    while (i++ < maxIterations) {
			for (int j = 0; j < seedlist.size(); j++) {
        if (getAdjacentSeeds(seedlist, j).size() < 6)
				  jiggleSeed(seedlist, j);
      }
      double diffScore = calculateScore(seedlist);
      if (diffScore > currentScore)
        currentScore = diffScore;
      else
        break;
    }
	}

  // scan the board with a small granularity, check if a seed can be inserted
  public void scanAndInsert(ArrayList<seed> tmplist) {
    for (double i = distowall; i <= width - distowall; i += .01) {
      for (double j = distowall; j <= length - distowall; j += .01) {
        seed s = new seed(i, j, true);
        if (validateSeed(s, tmplist))
          tmplist.add(s); 
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

		for (seed s : tmplist) {
			if (s != tmpSeed && distance(tmpSeed, s) < distoseed) {
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

}
