package puyo;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;

public class AbsoluteLayout implements LayoutManager {

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	private ArrayList<Component> componentMap;
	private int column, row;

	public AbsoluteLayout(int column, int row) {
		super();
		this.column = column;
		this.row = row;
		componentMap = new ArrayList<Component>();
	}

	@Override
	public void layoutContainer(Container target) {
		// TODO 自動生成されたメソッド・スタブ
		synchronized (target.getTreeLock()) {
			Insets i = target.getInsets();

			int nmembers = target.getComponentCount();
			if (nmembers <= 0) {
				componentMap.clear();
				return;
			}

			for (int k = 0; k < nmembers; k++) {
				if (!componentMap.contains(target.getComponent(k))) {
					componentMap.add(target.getComponent(k));
				}
			}

			//			int validX = target.getWidth() - i.left - i.right;
			//			int validY = target.getHeight() - i.top - i.bottom;
			//
			//			int rateX = validX / column;
			//			int rateY = validY / row;
			for (int j = 0; j < nmembers; j++) {
				Component m = target.getComponent(j);
				if (m.isVisible()) {
					//m.setSize(d.width, d.height);

					//					int componentOrder = componentMap.indexOf(m);
					//					//System.out.println(componentOrder);
					//					int x = (componentOrder % column) * rateX;
					//					int y = (componentOrder / column) * rateY;

					int x = m.getX();
					int y = m.getY();
					m.setLocation(x, y);
				}
			}
		}
	}
}
