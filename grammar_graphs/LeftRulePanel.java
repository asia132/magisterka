package grammar_graphs;

import javax.swing.JFrame;

class LeftRulePanel extends MainPanel {
	
	public static final long serialVersionUID = 42L;
	
	RigthRulePanel rigthRulePanel;
	LeftRulePanel(JFrame parent, int screenWidth, int screenHeight){
		super(parent, screenWidth, screenHeight);
	}
	public void copyMarker(){
		rigthRulePanel.programData.marker = this.programData.marker.copy();
	}
	@Override
	public void addLine(int x1, int y1, int x2, int y2){
		programData.lines.addLine(Line.createLineAtScreenPoint(x1, y1, x2, y2), false);
		rigthRulePanel.addLeftLine(x1, y1, x2, y2);
	}
	@Override
	public void pasteLines(int x, int y){
		this.programData.pasteCopied(x, y);
		this.repaint();
		this.rigthRulePanel.pasteLinesFromLetf(x, y);
	}
	@Override
	public void removeSelectedLines(){
		this.rigthRulePanel.makeLineRemovable(this.programData.getModified());
		for (Line line: this.programData.getModified())
			this.programData.lines.removeLine(line);
		this.programData.clearModified();
		this.programData.clearModifiedMarker();
		this.repaint();
	}
	@Override
	public void moveLinesOfTempShape(int x1, int y1, int x2, int y2){
		for (int i = 0; i < programData.getModified().size(); i++){
			Line line = programData.getModified().get(i).copy();
			programData.tempShapeMove(i, x1, y1, x2, y2);
			for (Line rline: rigthRulePanel.leftLines){
				if (rline.equals(line)){
					rline.setXY_a(programData.getModified().get(i).getX_a(), programData.getModified().get(i).getY_a());
					rline.setXY_b(programData.getModified().get(i).getX_b(), programData.getModified().get(i).getY_b());
				}
			}
		}
		rigthRulePanel.repaint();
	}
	@Override
	public void moveLines(int x1, int y1, int x2, int y2){
		programData.point0.x += GridControl.getInstance().toGrid(x2 - x1);
		programData.point0.y += GridControl.getInstance().toGrid(y2 - y1);
		programData.lines.moveLines(x1, y1, x2, y2, false);
		if (programData.marker != null)
			this.programData.marker.move(x2 - x1, y2 - y1);
		this.repaint();
		this.rigthRulePanel.moveAllLines(x1, y1, x2, y2);
	}
	public void moveAllLines(int x1, int y1, int x2, int y2){
		programData.point0.x += GridControl.getInstance().toGrid(x2 - x1);
		programData.point0.y += GridControl.getInstance().toGrid(y2 - y1);
		programData.lines.moveLines(x1, y1, x2, y2, false);
		if (programData.marker != null)
			this.programData.marker.move(x2 - x1, y2 - y1);
		this.repaint();
	}
	@Override
	public void modifyLines(int x1, int y1, int x2, int y2){
		Line line = programData.tempShapeFirstLine();
		if (MainData.distans(line.getX_a(), line.getY_a(), x2, y2) < MainData.distans(line.getX_b(), line.getY_b(), x2, y2)){
			line.setXY_a(x2, y2);
		}
		else{
			line.setXY_b(x2, y2);
		}

		int i = programData.lines.getLines().indexOf(line);
		rigthRulePanel.modifyLines(i, x1, y1, x2, y2);

		rigthRulePanel.repaint();
	}
}