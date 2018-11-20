package puyo;

import java.util.ArrayList;
import java.util.Iterator;

public class PuyoUtil {
		private static ArrayList<NextPuyoPanel> nextPuyoPanels = new ArrayList<NextPuyoPanel>();

static void addNextPuyoPanel(NextPuyoPanel npp) {
	nextPuyoPanels.add(npp);
}
	/**
	 * This is only method which can create new Puyo.
	 * @param colorNumbers	How many kinds of puyo do you chose from.
	 * @return	Puyo which is created.
	 */
	static void generatePuyos(int colorNumbers) {
		Puyo[] newPuyos = new Puyo[2];
		int color0 = (int) (Math.random() * colorNumbers);
		int color1 = (int) (Math.random() * colorNumbers);
		newPuyos[0] = new Puyo(color0);
		newPuyos[0].setFrameX(3);
		newPuyos[0].setFrameY(1);
		newPuyos[1] = new Puyo(color1);
		newPuyos[1].setFrameX(3);
		newPuyos[1].setFrameY(0);
		for(Iterator<NextPuyoPanel> i = nextPuyoPanels.iterator(); i.hasNext(); ) {
			i.next().insertPuyos(newPuyos);
		}
	}
}
