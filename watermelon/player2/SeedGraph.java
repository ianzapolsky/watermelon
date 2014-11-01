package watermelon.player2;

import java.util.*;

import watermelon.sim.Pair;
import watermelon.sim.Point;
import watermelon.sim.seed;

public class SeedGraph {

	static double distowall = 1.00;
	static double distotree = 2.00;
	static double distoseed = 2.00;
  static double epsilon   = .000001;

  ArrayList<seed> seedlist;

	public SeedGraph(ArrayList<seed> initSeedlist) {
    seedlist = initSeedlist;
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

  // identify low producers by checking if a seed has more similar plants
  // around it than different
  public boolean isLowProducer(int seedIndex) {
    if (getAdjacentSame(seedIndex) > getAdjacentDifferent(seedIndex))
      return true;
    return false;
  }  

  // return the number of plants that are adjacent to plant at seedIndex that
  // are also of the same ploidy
  public int getAdjacentSame(int seedIndex) {
    int same = 0;
    seed currSeed = seedlist.get(seedIndex);
    for (seed s : getAdjacentSeeds(seedIndex))
      if (s.tetraploid == currSeed.tetraploid)
        same += 1;
    return same;
  }

  // return the number of plants that are adjacent to plant at seedIndex that
  // are not of the same ploidy
  public int getAdjacentDifferent(int seedIndex) {
    int different = 0;
    seed currSeed = seedlist.get(seedIndex);
    for (seed s : getAdjacentSeeds(seedIndex))
      if (s.tetraploid != currSeed.tetraploid)
        different += 1;
    return different;
  }

  // return all the plants that are adjacent to the plant at seedIndex
  public ArrayList<seed> getAdjacentSeeds(int seedIndex) {
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
}
