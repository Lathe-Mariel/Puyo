package puyo;

import java.util.ArrayList;

import javax.swing.JPanel;

public class NextPuyoPanel extends JPanel {
	private ArrayList<Puyo> nextPuyoQueue;
	private Field connectField;

	public NextPuyoPanel(Field connectField) {
		this.connectField = connectField;
		nextPuyoQueue = new ArrayList<Puyo>();
	}

	Puyo[] pop() {
		Puyo[] popOutPuyos = new Puyo[2];
		popOutPuyos[0] = (nextPuyoQueue.get(0));
		nextPuyoQueue.remove(0);
		popOutPuyos[1] = (nextPuyoQueue.get(0));
		nextPuyoQueue.remove(0);
		while(nextPuyoQueue.size() <=4) {
			System.out.println("nextPuyoQueue.size(): " + nextPuyoQueue.size());
			PuyoUtil.generatePuyos(5);
		}
		return popOutPuyos;
	}

	void insertPuyos(Puyo[] newPuyo) {
		nextPuyoQueue.add(newPuyo[0]);
		nextPuyoQueue.add(newPuyo[1]);
		newPuyo[0].setContainer(connectField);
		newPuyo[1].setContainer(connectField);
	}
}
