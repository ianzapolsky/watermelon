package watermelon.player4threaded;

import java.util.ArrayList;

import watermelon.sim.seed;

public interface BoardRunnable extends Runnable {

	public double getScore();

	public ArrayList<seed> getBoard();

	public void getDetails();

}
