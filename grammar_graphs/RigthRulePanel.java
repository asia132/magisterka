package grammar_graphs;

import java.awt.Graphics2D;
import java.util.ArrayList;

class RigthRulePanel extends MainPanel{
	ArrayList <Line> leftLines = new ArrayList<Line>();
	RigthRulePanel(int screenWidth, int screenHeight){
		super(screenWidth, screenHeight);
	}
	public void addLeftLine(int x1, int y1, int x2, int y2){
		Line newLine = new Line(x1, y1, x2, y2);
		this.leftLines.add(newLine);
		this.programData.lines.add(newLine);
		this.repaint();
	}
	@Override
	public void pasteLines(int x, int y){
		if (!this.programData.copiedLines.isEmpty()){
			this.programData.changeModifiedColor(this.programData.default_figure_color);
			this.programData.modyfiedLines.clear();
			int [] point = this.programData.findCenter(this.programData.copiedLines);
			for (Line line: this.programData.copiedLines) {
				line.move(x - point[0], y - point[1]);
				Line newLine = line.copy();
				this.programData.lines.add(newLine);
				leftLines.add(newLine);
				this.programData.modyfiedLines.add(this.programData.lines.get(this.programData.lines.size() - 1));
				this.programData.modyfiedLines.get(this.programData.modyfiedLines.size() - 1).changeColor(this.programData.default_check_color);
			}
		}
		this.repaint();
	}
	@Override
	public void removeSelectedLines(){
		for (Line line: this.programData.getModified()){
			if (!this.leftLines.contains(line)){
				this.programData.lines.remove(line);
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