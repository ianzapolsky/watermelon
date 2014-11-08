package watermelon.player4threaded;

import java.util.ArrayList;

import watermelon.sim.seed;

public interface BoardThread extends Runnable {

	public double getScore();

	public ArrayList<seed> getBoard();

	public void getDetails();

}
