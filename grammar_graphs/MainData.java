package grammar_graphs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.ArrayList;
import java.util.StringJoiner;

import java.lang.Math;

class MainData {

	Rectangle checkingRect = null;
	Marker marker = null;
	Marker modified_marker = null;

	PanelLines lines = new PanelLines();

	ArrayList<Line> modyfiedLines = new ArrayList<Line>();
	ArrayList<Line> initialLines = new ArrayList<Line>();
	private ArrayList<Line> temp_shape = new ArrayList<Line>();
	int[] point0 = { 0, 0 };

	// limit shape
	void drawLinesStack(Graphics2D g2d) {
		for (Line s : lines.linesStack) {
			s.drawLine(g2d, point0);
		}
	}

	static void updateLimitShapeColor() {
		for (Line line : GrammarControl.getInstance().getLimitShapeLines()) {
			line.changeColor(Settings.default_check_marker_color);
		}
	}

	void endDefininingLimitShape() {
		this.lines.clear();
		for (Line line : lines.linesStack) {
			this.lines.add(line);
		}
		lines.linesStack.clear();
		Settings.default_figure_color = Color.BLACK;
		updateLimitShapeColor();
	}

	void startDefininingLimitShape() {
		lines.linesStack = lines.copy();
		this.lines.clear();
		Settings.default_figure_color = Color.MAGENTA;
		for (Line line : GrammarControl.getInstance().getLimitShapeLines()) {
			this.lines.add(line);
		}
	}

	// random things
	static ArrayList<Line> RelativeComplement(ArrayList<Line> setA, ArrayList<Line> setB) {
		ArrayList<Line> resultSet = new ArrayList<>();
		for (Line lineA : setA) {
			boolean isInB = false;
			for (Line lineB : setB) {
				if (lineA.equals(lineB) == true) {
					isInB = true;
					break;
				}
			}
			if (isInB == false)
				resultSet.add(lineA);
		}
		return resultSet;
	}

	void clear() {
		this.marker = null;
		this.modified_marker = null;

		GrammarControl.getInstance().ruleList.clear();
		GrammarControl.getInstance().ruleAppList.clear();

		this.modyfiedLines.clear();
		this.lines.clear();
		this.tempShapeClear();
		GrammarControl.getInstance().copiedLines.clear();
	}

	boolean inRuleList(String name) {
		for (Rule rule : GrammarControl.getInstance().ruleList) {
			if (name.equals(rule.getName()))
				return true;
		}
		return false;
	}

	// temp shape
	int tempShapeSize() {
		return temp_shape.size();
	}

	Line tempShapeFirstLine() {
		return temp_shape.get(0);
	}

	Line tempShapeGetLine(int i) {
		return temp_shape.get(i);
	}

	void tempShapeClear() {
		temp_shape.clear();
		initialLines.clear();
	}

	void tempShapeAddLine(Line line) {
		temp_shape.add(line);
		initialLines.add(line.copy());
	}

	boolean tempShapeIsEmpty() {
		return temp_shape.isEmpty();
	}

	void tempShapeMove(int i, int x1, int y1, int x2, int y2) {
		temp_shape.get(i).setXY_a(initialLines.get(i).getX_a() + x2 - x1, initialLines.get(i).getY_a() + y2 - y1);
		temp_shape.get(i).setXY_b(initialLines.get(i).getX_b() + x2 - x1, initialLines.get(i).getY_b() + y2 - y1);
	}

	// to string
	String rulePaintingToString() {
		if (!GrammarControl.getInstance().rulePainting.isEmpty()) {
			StringJoiner info = new StringJoiner("\n");
			for (PaintingRule rule : GrammarControl.getInstance().rulePainting) {
				info.add(rule.toString());
			}
			return info.add("").toString();
		} else {
			return "";
		}
	}

	String ruleAppListToString() {
		StringJoiner info = new StringJoiner("");
		for (Rule rule : GrammarControl.getInstance().ruleAppList) {
			info.add(FileSaverTags.RULELIST.toString()).add("\t").add(rule.getName()).add("\n");
		}
		return info.toString();
	}

	String markerToString() {
		StringJoiner info = new StringJoiner("\t");
		if (marker != null) {
			return info.add(FileSaverTags.INPUTTAG.toString()).add(FileSaverTags.ISIDETAG.toString())
					.add(marker.toString() + "\n").toString();
		}
		return "";
	}

	// modified markers
	void addMarkerToModified() {
		modified_marker = marker.copy();
	}

	void clearModifiedMarker() {
		modified_marker = null;
	}

	// modified lines
	void clearModified() {
		if (Settings.LIMITING_SHAPE)
			changeModifiedColor(Settings.default_check_marker_color);
		else
			changeModifiedColor(Settings.default_figure_color);
		modyfiedLines.clear();
	}

	boolean isEmptyModified() {
		return modyfiedLines.isEmpty();
	}

	ArrayList<Line> getModified() {
		return modyfiedLines;
	}

	void changeFiguresColor() {
		for (Line line : lines.getLines()) {
			if (modyfiedLines.indexOf(line) == -1) {
				line.changeColor(Settings.default_figure_color);
			}
		}
	}

	void changeModifiedColor(Color color) {
		for (Line line : modyfiedLines) {
			line.changeColor(color);
		}
	}

	void addToModified(Line line) {
		if (modyfiedLines.indexOf(line) == -1) {
			modyfiedLines.add(line);
			line.changeColor(Settings.default_check_color);
		} else {
			if (Settings.LIMITING_SHAPE)
				changeModifiedColor(Settings.default_check_marker_color);
			else
				line.changeColor(Settings.default_figure_color);
			modyfiedLines.remove(line);
		}
	}

	// distanses
	static double distans(double x1, double y1, double x2, double y2) {
		double x = (x1 - x2) * (x1 - x2);
		double y = (y1 - y2) * (y1 - y2);
		return Math.sqrt(x + y);
	}

	static double distans(int x1, int y1, int x2, int y2) {
		double x = (x1 - x2) * (x1 - x2);
		double y = (y1 - y2) * (y1 - y2);
		return Math.sqrt(x + y);
	}

	static double distans(int[] p1, int[] p2) {
		double x = (p1[0] - p2[0]) * (p1[0] - p2[0]);
		double y = (p1[1] - p2[1]) * (p1[1] - p2[1]);
		return Math.sqrt(x + y);
	}

	// copied lines
	static int[] findCenter(ArrayList<Line> linesList) {
		if (linesList.size() > 0) {
			int min_x = linesList.get(0).getX_a();
			int max_x = linesList.get(0).getX_a();
			int min_y = linesList.get(0).getY_a();
			int max_y = linesList.get(0).getY_a();
			for (Line line : linesList) {
				min_x = min_x < line.getX_a() ? min_x : line.getX_a();
				min_x = min_x < line.getX_b() ? min_x : line.getX_b();

				max_x = max_x > line.getX_a() ? max_x : line.getX_a();
				max_x = max_x > line.getX_b() ? max_x : line.getX_b();

				min_y = min_y < line.getY_a() ? min_y : line.getY_a();
				min_y = min_y < line.getY_b() ? min_y : line.getY_b();

				max_y = min_y > line.getY_a() ? max_y : line.getY_a();
				max_y = min_y > line.getY_b() ? max_y : line.getY_b();
			}

			int[] point = { (int) ((min_x + max_x) * 0.5), (int) ((min_y + max_y) * 0.5) };
			return point;
		}
		int[] point = { 0, 0 };
		return point;
	}

	void pasteCopied(int x, int y) {
		if (!GrammarControl.getInstance().copiedLines.isEmpty()) {
			if (Settings.LIMITING_SHAPE)
				changeModifiedColor(Settings.default_check_marker_color);
			else
				changeModifiedColor(Settings.default_figure_color);
			modyfiedLines.clear();
			int[] point = findCenter(GrammarControl.getInstance().copiedLines);
			for (Line line : GrammarControl.getInstance().copiedLines) {
				line.move(x - point[0], y - point[1]);
				lines.add(line.copy());
				modyfiedLines.add(lines.get(lines.size() - 1));
				modyfiedLines.get(modyfiedLines.size() - 1).changeColor(Settings.default_check_color);
			}
		}
	}

	void copyModyfied() {
		if (!GrammarControl.getInstance().copiedLines.isEmpty())
			GrammarControl.getInstance().copiedLines.clear();
		for (Line line : modyfiedLines) {
			GrammarControl.getInstance().copiedLines.add(line.copy());
		}
		if (Settings.LIMITING_SHAPE)
			changeModifiedColor(Settings.default_check_marker_color);
		else
			changeModifiedColor(Settings.default_figure_color);
		modyfiedLines.clear();
	}

	// where it was clicked
	boolean wasMoved(int x1, int y1, int x2, int y2) {
		if (Math.abs(x1 - x2) >= (int) (GridControl.getInstance().grid_size * 0.5)
				|| Math.abs(y1 - y2) >= (int) (GridControl.getInstance().grid_size * 0.5))
			return true;
		return false;
	}

	void findLinesInRect() {
		for (Line line : lines.getLines()) {
			if (checkingRect.insideRect(line)) {
				addToModified(line);
			}
		}
		if (marker != null && checkingRect.insideRect(marker)) {
			addMarkerToModified();
		}
	}

	// drawing
	void render(Graphics2D g2d) {

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		for (PaintingRule rule : GrammarControl.getInstance().rulePainting) {
			if (rule.isAplicable) {
				g2d.setPaint(rule.getColor());
				g2d.fill(rule.paintCavnas);
			}
		}
		g2d.dispose();
	}

	void drawTempLine(Graphics2D g2d) {
		for (Line line : temp_shape) {
			line.drawLine(g2d, point0);
		}
	}
}