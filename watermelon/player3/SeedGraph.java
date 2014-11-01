package watermelon.player3;

import java.util.ArrayList;

import watermelon.sim.Pair;
import watermelon.sim.Point;
import watermelon.sim.seed;

public class SeedGraph {

	static double distowall = 1.00;
	static double distotree = 2.00;
	static double distoseed = 2.00;
	static double epsilon = .000001;

	ArrayList<seed> seedlist;

	public SeedGraph(ArrayList<seed> initSeedlist) {
		seedlist = initSeedlist;
	}

	// distance between seed and pair
	static double distance(seed tmp, Pair pair) {
		return Math.sqrt((tmp.x - pair.x) * (tmp.x - pair.x) + (tmp.y - pair.y)
				* (tmp.y - pair.y));
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
		// if checking adjacent adjacent
		// if(getAdjacentAdjacentSame(seedIndex) >
		// getAdjacentAdjacentDifferent(seedIndex))

		// original, just checking adjacent seeds
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

	// return the number of plants that are adjacent to plant at seedIndex that
	// are also of the same ploidy
	public int getAdjacentAdjacentSame(int seedIndex) {
		int same = 0;
		seed currSeed = seedlist.get(seedIndex);
		for (seed s : getAdjacentAdjacentSeeds(seedIndex))
			if (s.tetraploid == currSeed.tetraploid)
				same += 1;
		return same;
	}

	// return the number of plants that are adjacent to plant at seedIndex that
	// are not of the same ploidy
	public int getAdjacentAdjacentDifferent(int seedIndex) {
		int different = 0;
		seed currSeed = seedlist.get(seedIndex);
		for (seed s : getAdjacentAdjacentSeeds(seedIndex))
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

	// return all the plants that are adjacent to the plant at seedIndex
	public ArrayList<Integer> getAdjacentSeedsIndex(int seedIndex) {
		ArrayList<Integer> adjacentSeedsIndex = new ArrayList<Integer>();
		double minDist = Double.MAX_VALUE;
		for (int i = 0; i < seedlist.size(); i++) {
			if (i == seedIndex) {
				/* do nothing */;
			} else {
				double dist = distance(seedlist.get(seedIndex), seedlist.get(i));
				if (dist < minDist - epsilon) {
					adjacentSeedsIndex.clear();
					adjacentSeedsIndex.add(i);
					minDist = dist;
				} else if (dist < minDist + epsilon) {
					adjacentSeedsIndex.add(i);
				}
			}
		}
		return adjacentSeedsIndex;
	}

	public ArrayList<seed> getAdjacentAdjacentSeeds(int seedIndex) {

		ArrayList<Integer> adjacentSeedsIndex = getAdjacentSeedsIndex(seedIndex);
		ArrayList<seed> adjacentAdjacentSeeds = getAdjacentSeeds(seedIndex);

		for (int i = 0; i < adjacentSeedsIndex.size(); i++) {
			ArrayList<seed> aASeeds = getAdjacentSeeds(adjacentSeedsIndex
					.get(i));

			for (int j = 0; j < aASeeds.size(); j++) {
				if (!adjacentAdjacentSeeds.contains(aASeeds.get(j)))
					adjacentAdjacentSeeds.add(aASeeds.get(j));

			}

		}

		System.out.println("adjacentSeedsIndex.size() = "
				+ adjacentSeedsIndex.size() + ", adjacentAdjacentSeeds = "
				+ adjacentAdjacentSeeds.size());
		/*
		 * // including the first layer of adjacent seeds in the arrayList
		 * ArrayList<seed> adjacentAdjacentSeeds = getAdjacentSeeds(seedIndex);
		 * ArrayList<Integer> adjacentAdjacentSeedsIndex = adjacentSeedsIndex;
		 * 
		 * // adding the second layer of adjacent seeds to the arrayList for
		 * (int i = 0; i < adjacentSeedsIndex.size(); i++) {
		 * 
		 * ArrayList<Integer> nextAdjacentSeedsIndex =
		 * getAdjacentSeedsIndex(adjacentSeedsIndex .get(i)); //
		 * System.out.println("nextAdjacentSeeds.size() = " + //
		 * nextAdjacentSeedsIndex.size());
		 * 
		 * for (int j = 0; j < nextAdjacentSeedsIndex.size(); j++) { int
		 * nextSeedIndex = nextAdjacentSeedsIndex.get(j); // if the seed is not
		 * already in the list of adjacent adjacent // seeds, then add if
		 * (adjacentAdjacentSeedsIndex.indexOf(nextSeedIndex) == -1) { //
		 * System.out.println("in the not in the original index");
		 * adjacentAdjacentSeeds.add(seedlist.get(nextSeedIndex));
		 * adjacentAdjacentSeedsIndex.add(nextSeedIndex); }
		 * 
		 * // System.out.println("after:	adjacentAdjacentSeeds.size() = "+ //
		 * adjacentAdjacentSeeds.size()); } }
		 */
		return adjacentAdjacentSeeds;
	}
}
