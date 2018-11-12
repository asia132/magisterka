package grammar_graphs;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public final class GridControl {
	private static final GridControl INSTANCE = new GridControl();

	int grid_size = 20;
	int grid_section = 12;

	private GridControl() {
		if (INSTANCE != null)
			throw new AssertionError();
	}

	static GridControl getInstance() {
		return INSTANCE;
	}

	int toGrid(int x) {
		int modX = x % grid_size;
		if (modX <= (grid_size / 2) * 1.) {
			return (int) Math.floor((x / grid_size) * 1);
		}
		return (int) Math.ceil((x / grid_size) * 1);
	}

	boolean isOnGrid(double x, double y) {
		return x % grid_size == 0.0 && y % grid_size == 0.0;
	}

	boolean isOnGrid(double value) {
		return value % grid_size == 0.0;
	}

	int getGridSize() {
		return grid_size;
	}

	void setGridSize(int i, int screenWidth, int screenHeight) {
		int max_grid = screenWidth < screenHeight ? screenWidth : screenHeight;
		if (i + grid_size > 1 && grid_size + i < max_grid) {

			AffineTransform trans = new AffineTransform();
			trans.scale((grid_size * 1. + i) / (grid_size * 1.), (grid_size * 1. + i) / (grid_size * 1.));
			System.out.println(trans.getScaleX());
			for (PaintingRule rule : GrammarControl.getInstance().rulePainting) {
				rule.paintCavnas.transform(trans);
			}

			grid_size += i;
		}
	}

	void allLinesScale(int i, Dimension dim) {
		if (i == 1 || grid_size > 5) {
			setGridSize(i, dim.width, dim.height);
		}
	}

	void paintGrid(Graphics2D g2d, MainPanel panel) {
		Dimension dim = panel.getSize();
		g2d.setColor(Settings.default_grid_color);
		for (int i = panel.programData.point0[0] * grid_size, j = 0; i > 0; i -= grid_size, --j) {
			if (j % grid_section == 0)
				g2d.setStroke(new BasicStroke(2));
			else
				g2d.setStroke(new BasicStroke(1));
			g2d.drawLine(i, 0, i, dim.height);
		}
		for (int i = panel.programData.point0[1] * grid_size, j = 0; i > 0; i -= grid_size, j--) {
			if (j % grid_section == 0)
				g2d.setStroke(new BasicStroke(2));
			else
				g2d.setStroke(new BasicStroke(1));
			g2d.drawLine(0, i, dim.width, i);
		}
		for (int i = panel.programData.point0[0] * grid_size, j = 0; i < dim.width; i += grid_size, j++) {
			if (j % grid_section == 0)
				g2d.setStroke(new BasicStroke(2));
			else
				g2d.setStroke(new BasicStroke(1));
			g2d.drawLine(i, 0, i, dim.height);
		}
		for (int i = panel.programData.point0[1] * grid_size, j = 0; i < dim.height; i += grid_size, j++) {
			if (j % grid_section == 0)
				g2d.setStroke(new BasicStroke(2));
			else
				g2d.setStroke(new BasicStroke(1));
			g2d.drawLine(0, i, dim.width, i);
		}
	}
}
