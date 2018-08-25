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
	static Level levels[];
	private int n;
	int max_n_allowed;
	private boolean n_change = false;

	ColoringRule(MainPanel panel){
		this.n = 0;
		this.max_n_allowed = 10;
		this.levels = new Level [100];

		for (int i = 0; i < this.max_n_allowed; ++i)
			this.levels[i] = new Level();
		this.print();
	}
	void updateLevel0(Line newLine){
		this.levels[0].levelLines.add(newLine);
	}
	void updateWithRule(Category ruleCat, ArrayList <Line> ruleInitialLines, ArrayList <Line> ruleFinalLines){
		if (ruleCat == Category.A){ // left side => n 
			levels[n].update(ruleInitialLines);
		}else if (ruleCat == Category.B){ // left side => n | right side => n+1
			levels[n].update(ruleInitialLines);
			if (this.n_change){
				n++;
				this.n_change = false;
			}
			if (n+1 < max_n_allowed)	levels[n+1].update(ruleInitialLines);
		}else if (ruleCat == Category.C){ // right side - left side => n+1
			if (n+1 < max_n_allowed)	levels[n+1].update(ruleFinalLines);
			this.n_change = true;
		}
		System.out.println("KONTROLA N: n = " + n + ", ilosc leveli: " + levels.length);
		print();
	}
	void print(){
		
		for (int i = 0; i < max_n_allowed; ++i){
			System.out.println("Level " + i);
			levels[i].print();
		}
	}
	void paintLevels(Graphics2D g2d){
		this.print();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		for (int i = 0; i < max_n_allowed; ++i) {
			Level level = levels[i];
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
		// int break_index = -1;

		// int externalSizeControl = 0;

		// while (lines.size() > 1 && externalSizeControl != lines.size()){
			int sizeControl = 0;
		// 	externalSizeControl = lines.size();

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
		// 	break_index = index;
		// }

		if (lines.size() > 1){
			throw new NotClosedShape();
		}
		if (points[points.length-1][0] != points[0][0] && points[points.length-1][1] != points[0][1])
			throw new NotClosedShape();
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
	void update(ArrayList <Line> newLevelLines){
		int controlSize = this.levelLines.size();
		for (Line newline: newLevelLines){
			boolean shouldBeAdded = true;
			for (int i = 0; i < controlSize; ++i){
				if (this.levelLines.get(i).isTheSameLine(newline)){
					shouldBeAdded = false;
					break;
				}
			}
			if (shouldBeAdded)	this.levelLines.add(newline);
		}
	}
	void addLine(Line newline){
		int controlSize = this.levelLines.size();
		boolean shouldBeAdded = true;
		for (int i = 0; i < controlSize; ++i){
			if (this.levelLines.get(i).isTheSameLine(newline)){
				shouldBeAdded = false;
				break;
			}
		}
		if (shouldBeAdded)	this.levelLines.add(newline);
	}
	void print(){
		System.out.println(this.color.toString());
		int i = 1;
		for (Line line: this.levelLines) {
			System.out.print(i++ + ". ");
			line.print();
		}
	}
}
class NotClosedShape extends Exception {
	NotClosedShape(){
		super("The shape is not closed");
	}
}