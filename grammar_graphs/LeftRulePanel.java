package grammar_graphs;

import java.awt.event.MouseEvent;

class LeftRulePanel extends MainPanel {
	RigthRulePanel rigthRulePanel;
	LeftRulePanel(int screenWidth, int screenHeight){
		super(screenWidth, screenHeight);
	}
	@Override
	public void addLine(int x1, int y1, int x2, int y2){
		programData.lines.add(new Line(x1, y1, x2, y2));
		rigthRulePanel.addLeftLine(x1, y1, x2, y2);
	}
	@Override
	public void pasteLines(int x, int y){
		this.programData.pasteCopied(x, y);
		this.repaint();
		this.rigthRulePanel.pasteLines(x, y);
	}
	@Override
	public void removeSelectedLines(){
		this.rigthRulePanel.makeLineRemovable(this.programData.getModified());
		for (Line line: this.programData.getModified())
			this.programData.lines.remove(line);
		this.programData.clearModified();
		this.programData.clearModifiedMarker();
		this.repaint();
	}
	@Override
	public void moveLinesOfTempShape(int x1, int y1, int x2, int y2){
		for (Line line: programData.getModified()){
			for (Line rline: rigthRulePanel.leftLines){
				if (rline.isTheSameLine(line)){
					rline.setXY_a(line.getX_a() + x2 - x1, line.getY_a() + y2 - y1);
					rline.setXY_b(line.getX_b() + x2 - x1, line.getY_b() + y2 - y1);
				}
			}
		}
		for (int i = 0; i < programData.getModified().size(); i++){	
			programData.tempShapeMove(i, x1, y1, x2, y2);
		}
		rigthRulePanel.repaint();
	}
	@Override
	public void moveLines(int x1, int y1, int x2, int y2){
		for (Line line: programData.lines){
			line.move(x2 - x1, y2 - y1);
		}
		if (programData.marker != null)
			this.programData.marker.move(x2 - x1, y2 - y1);
		this.repaint();
		this.rigthRulePanel.moveLines(x1, y1, x2, y2);
	}
}