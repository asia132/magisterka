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
import java.util.StringJoiner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainPanel extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener {
	static final long serialVersionUID = 42L;
	int x1, y1;
	protected int x2, y2;
	boolean RIGHT = false;
	boolean MIDDLE = false;

	MainData programData;
	boolean newLine = false;
	boolean moveMarker = false;

	public MainPanel(JFrame parent, int screenWidth, int screenHeight) {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		if (!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel))
			GrammarControl.getInstance().paintingRuleLevels = new PaintingRuleLevels(this);
		this.programData = new MainData();
	}

	public void addLine(int x1, int y1, int x2, int y2) {
		programData.lines.addLine(Line.createLineAtScreenPoint(x1, y1, x2, y2),
				(!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)));
	}

	public void addMarker(int x, int y) {
		if (this.programData.marker == null)
			this.programData.marker = Marker.createMarkerAtScreenPoint(x, y);
		else
			this.programData.marker.setXY(x, y);
		this.repaint();
	}

	public void removeMarker() {
		this.programData.marker = null;
		this.repaint();
	}

	public void removeSelectedLines() {
		for (Line line : this.programData.getModified())
			this.programData.lines.removeLine(line);
		this.programData.clearModified();
		this.programData.clearModifiedMarker();
		this.repaint();
	}

	public void copyLines() {
		this.programData.copyModyfied();
		this.programData.clearModified();
		this.programData.clearModifiedMarker();
		this.repaint();
	}

	public void pasteLines(int x, int y) {
		this.programData.pasteCopied(x, y);
		this.repaint();
	}

	public void moveMarker(int x2, int y2) {
		this.programData.marker.setXY(x2, y2);
	}

	public void paintLines(Graphics2D g2d) {
		programData.lines.drawLines(g2d, programData.point0);
	}

	public void moveLines(int x1, int y1, int x2, int y2) {
		programData.point0[0] += GridControl.getInstance().toGrid(x2 - x1);
		programData.point0[1] += GridControl.getInstance().toGrid(y2 - y1);
		if (programData.marker != null)
			this.programData.marker.move(x2 - x1, y2 - y1);
		programData.lines.moveLines(x1, y1, x2, y2,
				(!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)));

		if (!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)) {
			AffineTransform trans = new AffineTransform();
			trans.translate(x2 - x1, y2 - y1);
			for (PaintingRule rule : GrammarControl.getInstance().rulePainting) {
				rule.paintCavnas.transform(trans);
			}
		}

		this.repaint();
	}

	public void resizeMarker(int x1, int y1, int x2, int y2) {
		if (programData.marker != null) {
			if (programData.marker.tryToResize(x1, y1, x2, y2) == true)
				newLine = false;
		}
	}

	public void tempShapeAddLine(Line line) {
		programData.tempShapeAddLine(line);
	}

	public void tempShapeClear() {
		programData.tempShapeClear();
	}

	public void modifyLines(int x1, int y1, int x2, int y2) {
		Line line = programData.tempShapeFirstLine();
		if (MainData.distans(line.getX_a(), line.getY_a(), x2, y2) < MainData.distans(line.getX_b(), line.getY_b(), x2,
				y2)) {
			line.setXY_a(x2, y2);
		} else {
			line.setXY_b(x2, y2);
		}
	}

	public void moveLinesOfTempShape(int x1, int y1, int x2, int y2) {
		if (programData.modified_marker != null && programData.marker != null) {
			programData.marker.setXY(programData.modified_marker.getX() + x2 - x1,
					programData.modified_marker.getY() + y2 - y1);
		}
		for (int i = 0; i < programData.tempShapeSize(); i++) {
			programData.tempShapeMove(i, x1, y1, x2, y2);
		}
	}

	@Override
	public String toString() {
		StringJoiner info = new StringJoiner("");

		info.add(FileSaverTags.N.toString()).add("\t");
		info.add(GrammarControl.getInstance().paintingRuleLevels.getN() + "\n");
		info.add(GrammarControl.getInstance().levelsToString());
		info.add(GrammarControl.getInstance().limitShapeToString());
		info.add(programData.markerToString());
		info.add(GrammarControl.getInstance().rulesToString());
		info.add(programData.rulePaintingToString());
		info.add(programData.ruleAppListToString());

		return info.toString();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Settings.default_background_color);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (Settings.COLOR_RULES && Settings.CLOSED_SHAPES) {
			if (GrammarControl.getInstance().rulePainting.size() > 0)
				programData.render(g2d);
			else
				GrammarControl.getInstance().paintingRuleLevels.paintLevels(g2d);
			this.repaint();
		} else {
			if (Settings.SHOW_GRID)
				GridControl.getInstance().paintGrid(g2d, this);
			if (Settings.LIMITING_SHAPE) {
				programData.drawLinesStack(g2d);
			}
			if (!programData.tempShapeIsEmpty()) {
				programData.drawTempLine(g2d);
			}
			if (Settings.DRAW_LEVELS && !(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)) {
				int i = 0;
				int x = 0;
				int y = 0;
				for (Level level : PaintingRuleLevels.levels) {
					if (i > GrammarControl.getInstance().paintingRuleLevels.getN() + 1)
						break;
					// if (i == 0)
					for (Line line : level.levelLines) {
						line.changeColor(level.getColor());
						line.drawLine(g2d, programData.point0);
					}

					g2d.setColor(Settings.default_background_color);
					g2d.setFont(new Font("Dialog", Font.BOLD, 20));
					Rectangle2D rect = g2d.getFontMetrics().getStringBounds("L1000", g2d);
					x = this.getWidth() - (int) (this.getWidth() * 0.25);
					y = (i + 1) * ((int) rect.getHeight());
					g2d.fillRect(x, y - g2d.getFontMetrics().getAscent(), (int) rect.getWidth(),
							(int) rect.getHeight());

					g2d.setColor(level.getColor());
					g2d.drawString("L" + i, x, y);

					i++;
				}
				Rectangle2D rect = g2d.getFontMetrics().getStringBounds("L1000", g2d);
				x = this.getWidth() - (int) (this.getWidth() * 0.25);
				y = (i + 1) * ((int) rect.getHeight());
				g2d.fillRect(x, y - g2d.getFontMetrics().getAscent(), (int) rect.getWidth(), (int) rect.getHeight());
				g2d.setColor(Settings.default_figure_color);
				g2d.drawString("n = " + GrammarControl.getInstance().paintingRuleLevels.getN(), x, y);
			} else {
				paintLines(g2d);
			}
			if (programData.checkingRect != null) {
				programData.checkingRect.drawRectanle(g2d);
			}
			if (programData.marker != null) {
				programData.marker.drawMarker(g2d, programData.point0);
			}
			if (!(this instanceof LeftRulePanel) && !(this instanceof RigthRulePanel)) {
				for (Line line : GrammarControl.getInstance().paintingRuleLevels.limitingShape.levelLines) {
					line.drawLine(g2d, programData.point0);
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!programData.wasMoved(x1, y1, x2, y2)) {
			Line line = programData.lines.onLine(x1, y1);
			if (line != null) {
				programData.addToModified(line);
				this.repaint();
			} else {
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
		if (SwingUtilities.isRightMouseButton(e)) {
			RIGHT = true;
		} else {
			if (SwingUtilities.isMiddleMouseButton(e)) {
				MIDDLE = true;
			} else {
				if (!programData.isEmptyModified()) {
					for (Line line : programData.getModified()) {
						this.tempShapeAddLine(line);
					}
				} else if (programData.marker != null && programData.marker.isMiddle(x1, y1)) {
					this.moveMarker = true;
				} else {
					this.tempShapeAddLine(Line.createLineAtScreenPoint(x1, y1, x1, y1));
					newLine = true;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.x2 = e.getX();
		this.y2 = e.getY();
		if (MIDDLE) {
			if (programData.wasMoved(x1, y1, x2, y2)) {
				moveLines(x1, y1, x2, y2);
			}
			MIDDLE = false;
		} else {
			if (!RIGHT) {
				resizeMarker(x1, y1, x2, y2);
				this.tempShapeClear();
				if (newLine == true && programData.wasMoved(x1, y1, x2, y2)) {
					this.addLine(x1, y1, x2, y2);
				}
				newLine = false;
				this.moveMarker = false;
			} else {
				if (!programData.wasMoved(x1, y1, x2, y2)) {
					PopUpMenu menu = new PopUpMenu(this);
					menu.show(e.getComponent(), e.getX(), e.getY());
				} else {
					if (programData.checkingRect != null) {
						programData.clearModified();
						programData.clearModifiedMarker();
						programData.findLinesInRect();
						programData.checkingRect = null;
					}
				}
			}
		}
		this.repaint();
		RIGHT = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			GridControl.getInstance().allLinesScale(1, this.getSize());
			GrammarControl.repaintAll();
		} else {
			GridControl.getInstance().allLinesScale(-1, this.getSize());
			GrammarControl.repaintAll();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.x2 = e.getX();
		this.y2 = e.getY();
		if (!RIGHT) {
			if (this.moveMarker == true)
				this.moveMarker(x2, y2);
			else if (newLine == true)
				programData.tempShapeFirstLine().setXY_b(x2, y2);
			else if (programData.tempShapeSize() == 1)
				modifyLines(x1, y1, x2, y2);
			else if (programData.wasMoved(x1, y1, x2, y2) && programData.getModified().size() > 1)
				moveLinesOfTempShape(x1, y1, x2, y2);
		} else
			programData.checkingRect = new Rectangle(x1, y1, x2, y2);
		this.repaint();
	}
}