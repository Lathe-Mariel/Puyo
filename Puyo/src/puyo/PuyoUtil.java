package puyo;

import java.util.ArrayList;
import java.util.Iterator;

public class PuyoUtil {
		private static ArrayList<NextPuyoPanel> nextPuyoPanels;


void addNextPuyoPanel(NextPuyoPanel npp) {
	nextPuyoPanels.add(npp);
}
	/**
	 * This is only method which can create new Puyo.
	 * @param colorNumbers	How many kinds of puyo do you chose from.
	 * @return	Puyo which is created.
	 */
	static void generatePuyos(int colorNumbers) {
		Puyo[] newPuyos = new Puyo[2];
		int color = (int) (Math.random() * colorNumbers);
		newPuyos[0] = new Puyo(color);
		newPuyos[1] = new Puyo(color);
		for(Iterator<NextPuyoPanel> i = nextPuyoPanels.iterator(); i.hasNext(); ) {
			i.next().insertPuyos(newPuyos);
		}
	}
}
