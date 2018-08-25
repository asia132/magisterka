package grammar_graphs;

import java.awt.Graphics2D;
import java.util.ArrayList;

class RigthRulePanel extends MainPanel{
	ArrayList <Line> leftLines = new ArrayList<Line>();
	RigthRulePanel(int screenWidth, int screenHeight){
		super(screenWidth, screenHeight);
	}
	RigthRulePanel(){
		super();
	}
	@Override
	public void moveLines(int x1, int y1, int x2, int y2){
		for (Line line: programData.getLines()){
			if (!this.leftLines.contains(line))		line.move(x2 - x1, y2 - y1);
		}
		if (programData.marker != null)
			this.programData.marker.move(x2 - x1, y2 - y1);
		this.repaint();
	}
	public void moveAllLines(int x1, int y1, int x2, int y2){
		for (Line line: programData.getLines()){
			line.move(x2 - x1, y2 - y1);
		}
		if (programData.marker != null)
			this.programData.marker.move(x2 - x1, y2 - y1);
		this.repaint();
	}
	public void addLeftLine(int x1, int y1, int x2, int y2){
		Line newLine = new Line(x1, y1, x2, y2);
		this.leftLines.add(newLine);
		this.programData.addLine(newLine, false);
		this.repaint();
	}
	@Override
	public void modifyLines(int x1, int y1, int x2, int y2){
		Line line = programData.tempShapeFirstLine();
		if (!this.leftLines.contains(line)){
			if (programData.distans(line.getX_a(), line.getY_a(), x2, y2) < programData.distans(line.getX_b(), line.getY_b(), x2, y2)){
				line.setXY_a(x2, y2);
			}
			else{
				line.setXY_b(x2, y2);
			}
		}
	}
	public void pasteLinesFromLetf(int x, int y){
		if (!this.programData.copiedLines.isEmpty()){
			this.programData.changeModifiedColor(this.programData.default_figure_color);
			this.programData.modyfiedLines.clear();
			int [] point = this.programData.findCenter(this.programData.copiedLines);
			for (Line line: this.programData.copiedLines) {
				line.move(x - point[0], y - point[1]);
				Line newLine = line.copy();
				this.programData.addLine(newLine, false);
				leftLines.add(newLine);
				this.programData.modyfiedLines.add(this.programData.getLine(this.programData.getLinesSize() - 1));
				this.programData.modyfiedLines.get(this.programData.modyfiedLines.size() - 1).changeColor(this.programData.default_check_color);
			}
		}
		this.repaint();
	}
	@Override
	public void removeSelectedLines(){
		for (Line line: this.programData.getModified()){
			if (!this.leftLines.contains(line)){
				this.programData.removeLine(line);
				this.leftLines.remove(line);
			}
		}
		this.programData.clearModified();
		this.programData.clearModifiedMarker();
		this.repaint();
	}
	@Override
	public void moveLinesOfTempShape(int x1, int y1, int x2, int y2){
		for (int i = 0; i < programData.getModified().size(); i++){
			if (!this.leftLines.contains(programData.tempShapeGetLine(i)))	
				programData.tempShapeMove(i, x1, y1, x2, y2);
		}
		this.repaint();
	}
	public void modifyLines(int i ,int x1, int y1, int x2, int y2){
		if (i >= 0){
			Line line = leftLines.get(i);
			if (programData.distans(line.getX_a(), line.getY_a(), x2, y2) < programData.distans(line.getX_b(), line.getY_b(), x2, y2)){
				line.setXY_a(x2, y2);
			}
			else{
				line.setXY_b(x2, y2);
			}
		}
	}
	void makeLineRemovable(ArrayList <Line> linesRemovedFromLeftSide){
		for (Line lLine: linesRemovedFromLeftSide){
			for (Line line: leftLines){
				if (line.isTheSameLine(lLine)){
					leftLines.remove(line);
					break;
				}
			}
		}
	}
}