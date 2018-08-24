package grammar_graphs;

import java.util.ArrayList;
import java.util.StringJoiner;

import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;
import java.lang.Math;

class ColoringRule {
	static ArrayList <Level> levels;
	private int n;
	private MainPanel panel;

	ColoringRule(MainPanel panel){
		this.levels = new ArrayList<>();
		levels.add(new Level());
		n = 1;
		this.panel = panel;
		print();
	}
	void updateLevel0(Line newLine){
		levels.get(0).levelLines.add(newLine);
	}
	void updateWithRule(Rule rule){
		if (rule.getCategory() == Category.A){
			if (levels.size() == n)
				// levels.add(n, new Level(rule.getInitialLines()));
				levels.add(n, new Level(rule.initialshape.setInPlace(panel.programData.marker, rule.initialshape.marker) ));
			else
				levels.get(n).update(rule.initialshape.setInPlace(panel.programData.marker, rule.initialshape.marker ));
		}else if (rule.getCategory() == Category.B){
			if (levels.size() == n)
				levels.add(n, new Level(rule.initialshape.setInPlace(panel.programData.marker, rule.initialshape.marker) ));
			else
				levels.get(n).update(rule.initialshape.setInPlace(panel.programData.marker, rule.initialshape.marker) );
			levels.add(++n, new Level(rule.initialshape.setInPlace(panel.programData.marker, rule.initialshape.marker) ));
		}else if (rule.getCategory() == Category.C){
			if (levels.size() == n)
				levels.add(n, new Level(rule.finalshape.setInPlace(panel.programData.marker, rule.initialshape.marker) ));
			else
				levels.add(++n, new Level(rule.finalshape.setInPlace(panel.programData.marker, rule.initialshape.marker) ));
		}
		print();
	}
	void print(){
		int i = 0;
		for (Level level: levels){
			System.out.println("Level " + i++);
			level.print();
		}
	}
	void paintLevels(Graphics2D g2d){
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		for (Level level: levels) {
			if (level.levelLines.size() <= 2) continue;
			try{
				g2d.setPaint(level.getColor());
				// g2d.translate(25, 5);

				double points[][] = level.getPoints();

				GeneralPath star = new GeneralPath();
				star.setWindingRule(GeneralPath.WIND_NON_ZERO);

				star.moveTo(points[0][0], points[0][1]);
				for (int k = 1; k < points.length; k++)
					star.lineTo(points[k][0], points[k][1]);
				star.closePath();
				g2d.fill(star);        
			}catch (NotClosedShape e) {
				MainData.COLOR_RULES = false;
				new MessageFrame(e.getMessage());
			}
		}
		g2d.dispose();
	}
}
class Level {
	ArrayList <Line> levelLines;
	private Color color;

	void setColor(Color color){
		this.color = color;
	}
	void randColor(){
		this.color = new Color((int)(Math.random() * 0x1000000));
	}
	Color getColor(){
		return this.color;
	}
	double [][] getPoints() throws NotClosedShape{
		double [][] points = new double [levelLines.size() + 1][2];
		ArrayList <Line> lines = new ArrayList<>();
		
		for (Line line: levelLines)
			lines.add(line);

		points[0][0] = lines.get(0).pa.x*1.*MainData.grid_size;
		points[0][1] = lines.get(0).pa.y*1.*MainData.grid_size;
		Point p = lines.get(0).pb;
		points[1][0] = p.x*1.*MainData.grid_size;
		points[1][1] = p.y*1.*MainData.grid_size;
		int index = 2;

		int externalSizeControl = 0;

		while (lines.size() > 1 && externalSizeControl != lines.size()){
			int sizeControl = 0;
			externalSizeControl = lines.size();

			while(sizeControl != lines.size()){
				sizeControl = lines.size();
				for (int i = 1; i < lines.size(); ++i){
					if (p.equals(lines.get(i).pa)){
						p = lines.get(i).pb;
						points[index][0] = p.x*1.*MainData.grid_size;
						points[index][1] = p.y*1.*MainData.grid_size;
						index++;
						lines.remove(i);
						break;
					}else if (p.equals(lines.get(i).pb)){
						p = lines.get(i).pa;
						points[index][0] = p.x*1.*MainData.grid_size;
						points[index][1] = p.y*1.*MainData.grid_size;
						index++;
						lines.remove(i);
						break;
					}
				}
			}
			
		}

		// if (lines.size() > 1){
		// 	throw new NotClosedShape();
		// }
		// if (points[points.length-1][0] != points[0][0] && points[points.length-1][1] != points[0][1])
		// 	throw new NotClosedShape();
		return points;
	}
	Level(ArrayList <Line> levelLines){
		this.levelLines = levelLines;
		this.randColor();
	}
	Level(){
		this.levelLines = new ArrayList <Line>();
		this.randColor();
	}
	void update(ArrayList <Line> levelLines){
		this.levelLines.addAll(levelLines);
	}
	void print(){
		System.out.println(this.color.toString());
		for (Line line: this.levelLines) {
			line.print();
		}
	}
}
class NotClosedShape extends Exception {
	NotClosedShape(){
		super("The shape is not closed");
	}
}