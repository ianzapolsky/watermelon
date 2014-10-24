package watermelon.player1;

import java.util.*;

import watermelon.sim.Pair;
import watermelon.sim.Point;
import watermelon.sim.seed;

public class Player extends watermelon.sim.Player {
	static double distowall = 2.00;
	static double distotree = 2.00;
	static double distoseed = 1.00;

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

	@Override
	public ArrayList<seed> move(ArrayList<Pair> initTreelist, double initWidth, double initLength, double initS) {
		// TODO Auto-generated method stub
    
    treelist = initTreelist;
    width = initWidth;
    length = initLength;
    s = initS;
    double maxScore = Double.MIN_VALUE;

    seedlist = getAlternatingBoard();

		System.out.printf("seedlist size is %d\n", seedlist.size());
    System.out.printf("score is %f\n", calculateScore(seedlist));
		return seedlist;
	}

  private ArrayList<seed> getAlternatingBoard() {
    int seedType = 1;
    ArrayList<seed> tmplist = new ArrayList<seed>();
		for (double i = distowall; i < width - distowall; i = i + distoseed) {
      seedType = seedType * -1;
			for (double j = distowall; j < length - distowall; j = j + distoseed) {
				seed tmp;


        if (seedType == 1) {
          tmp = new seed(i, j, false);
          seedType *= -1;
        } else {
          tmp = new seed(i, j, true);
          seedType *= -1;
        }

				boolean add = true;
				for (int f = 0; f < treelist.size(); f++) {
					if (distance(tmp, treelist.get(f)) < distotree) {
						add = false;
						break;
					}
				}
				if (add) {
					tmplist.add(tmp);
				}
      }
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
				boolean add = true;
				for (int f = 0; f < treelist.size(); f++) {
					if (distance(tmp, treelist.get(f)) < distotree) {
						add = false;
						break;
					}
				}
				if (add) {
					tmplist.add(tmp);
				}
      }
    }
    return tmplist;
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
					totaldis = totaldis
							+ Math.pow(
									distance(seedlist.get(i),
											seedlist.get(j)), -2);
				}
			}
			for (int j = 0; j < seedlist.size(); j++) {
				if (j != i
						&& ((seedlist.get(i).tetraploid && !seedlist.get(j).tetraploid) || (!seedlist
								.get(i).tetraploid && seedlist.get(j).tetraploid))) {
					difdis = difdis
							+ Math.pow(
									distance(seedlist.get(i),
											seedlist.get(j)), -2);
				}
			}
			chance = difdis / totaldis;
			score = chance + (1 - chance) * s;
			total = total + score;
		}
		return total;
	}

}
