package puyo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LinkedPuyos {
	ArrayList<Puyo> puyos;
	static List<LinkedPuyos> master = Collections.synchronizedList(new ArrayList<LinkedPuyos>());

	LinkedPuyos(Puyo puyo) {
		puyos = new ArrayList<Puyo>();
		puyos.add(puyo);
		master.add(this);
		System.out.println("add new Linked Puyos");
	}

	LinkedPuyos add(Puyo newPuyo) {
		System.out.println("add:  " + newPuyo.getFrameX() + " : " + newPuyo.getFrameY());
		if (!puyos.contains(newPuyo))
			puyos.add(newPuyo);
		return this;
	}

	boolean isDisappearable() {
		System.out.println("isDisappearable:  " + puyos.get(0).getFrameX() + " : " + puyos.get(0).getFrameY() + "     size: " + puyos.size() + "  color: " + puyos.get(0).getColorNumber());
		if(puyos.size() > 3)return true;
		return false;
	}

	void remove(Puyo puyo) {

	}

	Iterator<Puyo> iterator() {
		return puyos.iterator();
	}
}
