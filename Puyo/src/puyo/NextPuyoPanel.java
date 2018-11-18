package puyo;

import java.util.ArrayList;

import javax.swing.JPanel;

public class NextPuyoPanel extends JPanel {
	private ArrayList<Puyo> nextPuyoQueue;
	private Field connectField;

	public NextPuyoPanel(Field connectField) {
		this.connectField = connectField;
	}

	Puyo[] pop() {
		Puyo[] popOutPuyos = new Puyo[2];
		popOutPuyos[0] = (nextPuyoQueue.get(0));
		popOutPuyos[1] = (nextPuyoQueue.get(0));
		if(nextPuyoQueue.size() <=4) {
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
