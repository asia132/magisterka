package grammar_graphs;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.StringJoiner;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JButton;

import java.lang.Math;


public class MainPanel extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener {
	static final long serialVersionUID = 42L;
	int x1, y1;
	protected int x2, y2;
	int screenWidth, screenHeight;
	MainData programData = new MainData();
	boolean newLine = false;
	boolean moveMarker = false;
	public MainPanel(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		if(!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel))
			MainData.coloringRuleLevels = new ColoringRuleLevels(this);
	}
	public MainPanel() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	public void addLine(int x1, int y1, int x2, int y2){
		programData.addLine(new Line(x1, y1, x2, y2), (!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)));
	}
	public void addMarker(int x, int y){
		if (this.programData.marker == null)
			this.programData.marker = new Marker(x, y);
		else
			this.programData.marker.setXY(x, y);
		this.repaint();
	}
	public void removeMarker(){
		this.programData.marker = null;
		this.repaint();
	}
	public void removeSelectedLines(){
		for (Line line: this.programData.getModified())
			this.programData.removeLine(line);
		this.programData.clearModified();
		this.programData.clearModifiedMarker();
		this.repaint();
	}
	public void copyLines(){
		this.programData.copyModyfied();
		this.programData.clearModified();
		this.programData.clearModifiedMarker();
		this.repaint();
	}
	public void pasteLines(int x, int y){
		this.programData.pasteCopied(x, y);
		this.repaint();
	}
	public void moveMarker(int x2, int y2){
		this.programData.marker.setXY(x2, y2);
	}
	public void paintLines(Graphics2D g2d){
		programData.drawLines(g2d);
	}
	public void moveLines(int x1, int y1, int x2, int y2){
		// programData.point0[0] += MainData.toGrid(x2 - x1);
		// programData.point0[1] += MainData.toGrid(y2 - y1);
		if (programData.marker != null)
			this.programData.marker.move(x2 - x1, y2 - y1);
		programData.moveLines(x1, y1, x2, y2, (!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)));
		
		if (!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)){
			for (Line line: programData.coloringRuleLevels.limitingShape.levelLines){
				line.move(x2 - x1, y2 - y1);
			}
			AffineTransform trans = new AffineTransform();
			trans.translate(x2 - x1, y2 - y1);
			for (ColorRule rule: MainData.getColorRules()) {
				rule.paintCavnas.transform(trans);
			}
		}

		this.repaint();
	}
	public void resizeMarker(int x1, int y1, int x2, int y2){
		if (programData.marker != null){
			if (programData.marker.tryToResize(x1, y1, x2, y2) == true)
				newLine = false;
		}
	}
	public void tempShapeAddLine(Line line){
		programData.tempShapeAddLine(line);
	}
	public void tempShapeClear(){
		programData.tempShapeClear();
	}
	public void modifyLines(int x1, int y1, int x2, int y2){
		Line line = programData.tempShapeFirstLine();
		if (programData.distans(line.getX_a(), line.getY_a(), x2, y2) < programData.distans(line.getX_b(), line.getY_b(), x2, y2)){
			line.setXY_a(x2, y2);
		}
		else{
			line.setXY_b(x2, y2);
		}
	}
	public void moveLinesOfTempShape(int x1, int y1, int x2, int y2){
		if (programData.modified_marker != null && programData.marker != null){
			programData.marker.setXY(programData.modified_marker.getX() + x2 - x1, programData.modified_marker.getY() + y2 - y1);
		}
		for (int i = 0; i < programData.getModified().size(); i++){	
			programData.tempShapeMove(i, x1, y1, x2, y2);
		}
	}
	@Override
	public String toString(){
		StringJoiner info = new StringJoiner("");

		info.add(FileSaver.n).add("\t");
		info.add(programData.coloringRuleLevels.getN() + "\n");
		info.add(programData.levelsToString());
		info.add(programData.limitShapeToString());
		info.add(programData.markerToString());
		info.add(programData.rulesToString());
		info.add(programData.ruleAppListToString());
		info.add(programData.rulePaintingToString());
		
		return info.toString();
	}
	@Override
    public Dimension getPreferredSize() {
        return new Dimension(screenWidth, screenHeight);
    }
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(programData.default_background_color);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (MainData.getColorRules()){
			if (programData.rulePainting.size() > 0)	programData.render(g2d);
			else	MainData.coloringRuleLevels.paintLevels(g2d);
			this.repaint();
		}else{
			if (programData.SHOW_GRID)
				programData.paintGrid(g2d, screenWidth, screenHeight);
			if (programData.LIMITING_SHAPE){
				programData.drawLinesStack(g2d);
			}
			if (!programData.tempShapeIsEmpty()){
				programData.drawTempLine(g2d);
			}
			if (programData.DRAW_LEVELS && !(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)){
				int i = 0;
				for (Level level: MainData.coloringRuleLevels.levels){
					if (i > MainData.coloringRuleLevels.getN() + 1) break;
					// if (i == 2)
					for (Line line: level.levelLines){
						line.changeColor(level.getColor());
						line.drawLine(g2d, programData.point0);
					}

					g2d.setColor(programData.default_background_color);
					g2d.setFont(new Font("Dialog", Font.BOLD, 20)); 
					Rectangle2D rect = g2d.getFontMetrics().getStringBounds("L1000", g2d);
					int x = this.getWidth() - (int)(this.getWidth()*0.25);
					int y = (i + 1)*((int)rect.getHeight());
					g2d.fillRect(x, y - g2d.getFontMetrics().getAscent(), (int) rect.getWidth(), (int) rect.getHeight());

					g2d.setColor(level.getColor());
					g2d.drawString("L" + i, x, y);

					i++;
				}
			}else{
				paintLines(g2d);
			}
			if (programData.checkingRect != null){
				programData.checkingRect.drawRectanle(g2d);
			}
			if (programData.marker != null){
				programData.marker.drawMarker(g2d, programData.point0);
			}
			if (!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)){
				for (Line line: MainData.coloringRuleLevels.limitingShape.levelLines){
					line.drawLine(g2d, programData.point0);
				}
			}
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (!programData.wasMoved(x1, y1, x2, y2)){
			Line line = programData.onLine(x1, y1);
			if (line != null){
				programData.addToModified(line);
				this.repaint();
			}else{
				programData.clearModified();
				programData.clearModifiedMarker();
				this.repaint();
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		this.x1 = e.getX();
		this.y1 = e.getY();
		if (SwingUtilities.isRightMouseButton(e)){
			programData.RIGHT = true;
		}else{
			if(SwingUtilities.isMiddleMouseButton(e)){
				programData.MIDDLE = true;
			}
			else{
				if (!programData.isEmptyModified()){
					for (Line line: programData.getModified()){
						this.tempShapeAddLine(line);
					}
				}
				else if (programData.marker != null && programData.marker.isMiddle(x1, y1)){
					this.moveMarker = true;
				}
				else {
					this.tempShapeAddLine(new Line(x1, y1, x1, y1));
					newLine = true; 
				}
			}
		}		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		this.x2 = e.getX();
		this.y2 = e.getY();
		if (programData.MIDDLE){
			if (programData.wasMoved(x1, y1, x2, y2)){
				moveLines(x1, y1, x2, y2);
			}
			programData.MIDDLE = false;
		}else{
			if (!programData.RIGHT){
				resizeMarker(x1, y1, x2, y2);
				this.tempShapeClear();
				if (newLine == true && programData.wasMoved(x1, y1, x2, y2)){
					this.addLine(x1, y1, x2, y2);
				}
				newLine = false;
				this.moveMarker = false;
			}
			else{
				if (!programData.wasMoved(x1, y1, x2, y2)){
					PopUpMenu menu = new PopUpMenu(this);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}else{
					if (programData.checkingRect != null){
						programData.clearModified();
						programData.clearModifiedMarker();
						programData.findLinesInRect();
						programData.checkingRect = null;
					}
				}
			}
		}
		this.repaint();
		programData.RIGHT = false;
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		if (e.getWheelRotation() < 0){
			programData.allLinesScale(1, screenWidth, screenHeight);
			repaint();
		}
		else
		{
			programData.allLinesScale(-1, screenWidth, screenHeight);
			repaint();
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		this.x2 = e.getX();
		this.y2 = e.getY();
		if (!programData.RIGHT){
			if (this.moveMarker == true)	this.moveMarker(x2, y2);
			else if (newLine == true)	programData.tempShapeFirstLine().setXY_b(x2, y2);
			else if (programData.tempShapeSize() == 1)	modifyLines(x1, y1, x2, y2);
			else if (programData.wasMoved(x1, y1, x2, y2) && programData.getModified().size() > 1)	moveLinesOfTempShape(x1, y1, x2, y2);
		}
		else	programData.checkingRect = new Rectangle(x1, y1, x2, y2);
		this.repaint();
	} 
}