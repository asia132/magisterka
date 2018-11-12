package grammar_graphs;

import java.util.ArrayList;

import javax.swing.JFrame;

class RigthRulePanel extends MainPanel{
		
	public static final long serialVersionUID = 42L;
	
	ArrayList <Line> leftLines = new ArrayList<Line>();
	LeftRulePanel parent;
	RigthRulePanel(JFrame frameParent, int screenWidth, int screenHeight, LeftRulePanel parent){
		super(frameParent, screenWidth, screenHeight);
		this.parent = parent;
	}
	@Override
	public void moveLines(int x1, int y1, int x2, int y2){
		programData.point0[0] += GridControl.getInstance().toGrid(x2 - x1);
		programData.point0[1] += GridControl.getInstance().toGrid(y2 - y1);
		for (Line line: programData.lines.getLines()){
			line.move(x2 - x1, y2 - y1);
		}
		if (programData.marker != null)
			this.programData.marker.move(x2 - x1, y2 - y1);
		this.parent.moveAllLines(x1, y1, x2, y2);
		this.repaint();
	}
	public void moveAllLines(int x1, int y1, int x2, int y2){
		programData.point0[0] += GridControl.getInstance().toGrid(x2 - x1);
		programData.point0[1] += GridControl.getInstance().toGrid(y2 - y1);
		for (Line line: programData.lines.getLines()){
			line.move(x2 - x1, y2 - y1);
		}
		if (programData.marker != null)
			this.programData.marker.move(x2 - x1, y2 - y1);
		this.repaint();
	}
	public void addLeftLine(int x1, int y1, int x2, int y2){
		Line newLine = Line.createLineAtScreenPoint(x1, y1, x2, y2);
		this.leftLines.add(newLine);
		this.programData.lines.addLine(newLine, false);
		this.repaint();
	}
	@Override
	public void modifyLines(int x1, int y1, int x2, int y2){
		Line line = programData.tempShapeFirstLine();
		if (!this.leftLines.contains(line)){
			if (MainData.distans(line.getX_a(), line.getY_a(), x2, y2) < MainData.distans(line.getX_b(), line.getY_b(), x2, y2)){
				line.setXY_a(x2, y2);
			}
			else{
				line.setXY_b(x2, y2);
			}
		}
	}
	public void pasteLinesFromLetf(int x, int y){
		if (!GrammarControl.getInstance().copiedLines.isEmpty()){
			this.programData.changeModifiedColor(Settings.default_figure_color);
			this.programData.modyfiedLines.clear();
			int [] point = MainData.findCenter(GrammarControl.getInstance().copiedLines);
			for (Line line: GrammarControl.getInstance().copiedLines) {
				line.move(x - point[0], y - point[1]);
				Line newLine = line.copy();
				this.programData.lines.addLine(newLine, false);
				leftLines.add(newLine);
				this.programData.modyfiedLines.add(this.programData.lines.get(this.programData.lines.size() - 1));
				this.programData.modyfiedLines.get(this.programData.modyfiedLines.size() - 1).changeColor(Settings.default_check_color);
			}
		}
		this.repaint();
	}
	@Override
	public void removeSelectedLines(){
		for (Line line: this.programData.getModified()){
			if (!this.leftLines.contains(line)){
				this.programData.lines.removeLine(line);
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
			if (MainData.distans(line.getX_a(), line.getY_a(), x2, y2) < MainData.distans(line.getX_b(), line.getY_b(), x2, y2)){
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
				if (line.equals(lLine)){
					leftLines.remove(line);
					break;
				}
			}
		}
	}
}