package grammar_graphs;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.StringJoiner;

final class PanelLines {
	ArrayList<Line> lines = new ArrayList<Line>();
	ArrayList <Line> linesStack = null;
	
	void clear() {
		lines.clear();
	}
	
	void add(Line line) {
		lines.add(line);
	}
	
	void fillPaintingRuleLevelsWithInput() {
		for (Line line : lines) {
			GrammarControl.getInstance().paintingRuleLevels.updateLevel0(line);
		}
	}

	void setLines(ArrayList<Line> newLines) {
		this.lines = newLines;
		GrammarControl.getInstance().paintingRuleLevels.updateLevel0(newLines);
	}

	ArrayList<Line> copy() {
		ArrayList<Line> copy = new ArrayList<Line>();
		for (Line line : this.lines)
			copy.add(line.copy());
		return copy;
	}

	void addLinesByRule(ArrayList<Line> newlines) {
		this.lines.addAll(newlines);
	}

	void addLine(Line line, boolean mainPanel) {
		if (Settings.LIMITING_SHAPE) {
			line.changeColor(Settings.default_check_marker_color);
			GrammarControl.getInstance().paintingRuleLevels.limitingShape.levelLines.add(line);
		}
		this.lines.add(line);

		if (mainPanel && !Settings.LIMITING_SHAPE) {
			GrammarControl.getInstance().paintingRuleLevels.updateLevel0(line);
		}
	}

	void addLine(Line line, int level_i) {
		this.lines.add(line);
		PaintingRuleLevels.levels[level_i].levelLines.add(line);
	}

	void removeLine(Line line) {
		this.lines.remove(line);
		PaintingRuleLevels.levels[0].levelLines.remove(line);
		if (Settings.LIMITING_SHAPE) {
			GrammarControl.getInstance().getLimitShapeLines().remove(line);
		}
	}

	void moveLines(int x1, int y1, int x2, int y2, boolean mainPanel) {
		for (Line line : this.lines) {
			line.move(x2 - x1, y2 - y1);
		}
		if (Settings.LIMITING_SHAPE) {
			for (Line line : linesStack) {
				line.move(x2 - x1, y2 - y1);
			}
		}
	}

	ArrayList<Line> getLines() {
		return this.lines;
	}

	Line get(int i) {
		return this.lines.get(i);
	}

	int size() {
		return this.lines.size();
	}
	
	String linesToString() {
		StringJoiner info = new StringJoiner("");
		for (Line line : lines) {
			info.add(FileSaverTags.INPUTTAG.toString()).add("\t").add(FileSaverTags.ISIDETAG.toString()).add("\t")
					.add(line.toString()).add("\n");
		}
		return info.toString();
	}
	
	void drawLines(Graphics2D g2d, int [] point0) {
		for (Line line : lines) {
			line.drawLine(g2d, point0);
		}
	}
	
	Line isPoint_a(int x, int y) {
		for (Line line : lines) {
			if (line.isPoint_a(x, y, GridControl.getInstance().grid_size))
				return line;
		}
		return null;
	}

	Line isPoint_b(int x, int y) {
		for (Line line : lines) {
			if (line.isPoint_b(x, y, GridControl.getInstance().grid_size))
				return line;
		}
		return null;
	}
	
	Line onLine(int x, int y) {
		for (Line line : lines) {
			if (line.onLine(x, y, GridControl.getInstance().grid_size))
				return line;
		}
		return null;
	}
}
