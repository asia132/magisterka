package grammar_graphs;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.lang.Math;

public class MainPanel extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener {
	static final long serialVersionUID = 42L;
	private int x, y;
	int x1, y1;
	private int x2, y2;
	int screenWidth, screenHeight;
	MainData programData = new MainData();
	boolean ruleAdding;
	boolean newLine = false;
	boolean moveMarker = false;
	public MainPanel(int screenWidth, int screenHeight, boolean ruleAdding) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.ruleAdding = ruleAdding;
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
		if (programData.SHOW_GRID)
			programData.paintGrid(g2d, screenWidth, screenHeight);
		programData.drawLines(g2d);
		if (!programData.temp_shape.isEmpty()){
			programData.drawTempLine(g2d);
		}
		if (programData.checkingRect != null){
			programData.checkingRect.drawRectanle(g2d);
		}
		if (programData.marker != null){
			programData.marker.drawLine(g2d);
			// System.out.println("Marker: " + programData.marker.p.x + " " + programData.marker.p.y + " " + programData.marker.getD() + " " + programData.marker.h);
	
			// for (Line line: programData.lines)
			// 	System.out.println(line.getX_a() + " " + line.getY_a() + " " + line.getX_b() + " " + line.getY_b());
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
						programData.temp_shape.add(line);
					}
				}
				else if (programData.marker != null && programData.marker.isMiddle(x1, y1)){
					this.moveMarker = true;
				}
				else {
					programData.temp_shape.add(new Line(x1, y1, x1, y1));
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
				for (Line line: programData.lines){
					line.move(x2 - x1, y2 - y1);
				}
				if (programData.marker != null)
					programData.marker.move(x2 - x1, y2 - y1);
				this.repaint();
			}
			programData.MIDDLE = false;
		}else{
			if (!programData.RIGHT){
				if (programData.marker != null){
					if (programData.marker.tryToResize(x1, y1, x2, y2) == true)
						newLine = false;
				}
				// if (programData.wasMoved(x1, y1, x2, y2) && !programData.isEmptyModified() && programData.temp_shape.size() > 1){
				// 	for (Line line: programData.getModified()){
				// 		line.move(x2-x1, y2-y1);
				// 	}
				// }
				programData.temp_shape.clear();
				if (newLine == true && programData.wasMoved(x1, y1, x2, y2)){
					programData.lines.add(new Line(x1, y1, x2, y2));
				}
				newLine = false;
				this.moveMarker = false;
			}
			else{
				if (!programData.wasMoved(x1, y1, x2, y2)){
					PopUpMenu menu = new PopUpMenu(this, ruleAdding);
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
			if (this.moveMarker == true){
				this.programData.marker.setXY(x2, y2);
			}
			else if (newLine == true){
				programData.temp_shape.get(0).setXY_b(x2, y2);

			}
			else{
				if (programData.temp_shape.size() == 1){
					Line line = programData.temp_shape.get(0);
					if (programData.distans(line.getX_a(), line.getY_a(), x2, y2) < 
							programData.distans(line.getX_b(), line.getY_b(), x2, y2)){
						line.setXY_a(x2, y2);
					}
					else{
						line.setXY_b(x2, y2);
					}
				}else{
					if (programData.wasMoved(x1, y1, x2, y2) && programData.getModified().size() > 1){
						for (int i = 0; i < programData.getModified().size(); i++){
							// programData.temp_shape.get(i).setXY_a(x2 + programData.initialLines.get(i).getX_a() - x1, y2 + programData.initialLines.get(i).getY_a() - y1);
							// programData.temp_shape.get(i).setXY_b(x2 + programData.initialLines.get(i).getX_b() - x1, y2 + programData.initialLines.get(i).getY_b() - y1);
						
							programData.temp_shape.get(i).move(x2 - x1, y2 - y1);
							programData.temp_shape.get(i).move(x2 - x1, y2 - y1);
						}
					}
				}
			}
		}
		else{
			programData.checkingRect = new Rectangle(x1, y1, x2, y2);
		}
		this.repaint();
	} 
}