package grammar_graphs;

import java.util.ArrayList;
import java.util.List;
import java.awt.geom.GeneralPath;
import java.awt.Color;
import java.awt.Point;
import java.lang.Math;
import java.awt.geom.Area;

class Level {
	List <Line> levelLines;
	private Color color;
	double [][] points;
	Area area;
	double precision = 1e-12;
	String name;

	void setColor(Color color){
		this.color = color;
	}
	void randColor(){
		this.color = new Color((int)(Math.random() * 0x1000000)).darker();
	}
	Color getColor(){
		return this.color;
	}
	void closeLevel(){
		if (Settings.CLOSED_SHAPES){
			System.out.print("---------------CLOSE-LEVEL-" + this.name);
			int before = levelLines.size();
			do{
				before = levelLines.size();
				levelLines = Shape.groupLines(levelLines);
			}while (before != levelLines.size());
			System.out.print(" - size = " + levelLines.size());
			if (levelLines.size() > 2){
				try{
					this.points = generatePoints();
					getShape();
					System.out.println(" WITH SUCCESS. Check area: " + area.isEmpty());
				}catch(PaintingRuleLevels.NotClosedShape e){
					System.out.println(" WITHOUT SUCCESS--------------");
					Settings.setColorRules();
					if (!this.name.equals("Limit Shape"))
						Settings.CLOSED_SHAPES = false;
					System.out.println(e.getMessage());
				}
			}
		}
	}
	Area getShape() throws PaintingRuleLevels.NotClosedShape{
		if (Settings.CLOSED_SHAPES){
			GeneralPath levelShape = new GeneralPath();
			levelShape.setWindingRule(GeneralPath.WIND_NON_ZERO);

			if (points == null || points.length < 0){
				System.out.println(name + ": Get shape: Points are null. The shapes are not closed.");
				throw new PaintingRuleLevels.NotClosedShape("Get shape: Points are null. The shapes are not closed.");
			}
			levelShape.moveTo(points[0][0], points[0][1]);
			for (int k = 1; k < points.length; k++)
				levelShape.lineTo(points[k][0], points[k][1]);
			levelShape.closePath();

			area = new Area(levelShape);
			if (GrammarControl.getInstance().paintingRuleLevels.limitingShape.area == null){
				GrammarControl.getInstance().paintingRuleLevels.limitingShape.closeLevel();
			}
			if (GrammarControl.getInstance().paintingRuleLevels.limitingShape.area != null)
				area.intersect(GrammarControl.getInstance().paintingRuleLevels.limitingShape.area);
			return area;
		}else{
			throw new PaintingRuleLevels.NotClosedShape("Get shape: one of shape is not closed. The shapes are not closed.");
		}
	}
	int moveToBackCounter = 0;
	void moveToBack(Line line) throws PaintingRuleLevels.NotClosedShape{
		moveToBackCounter++;
		if (moveToBackCounter == levelLines.size() - 1){
			System.out.println("The points are not connected. Regression count = " + moveToBackCounter);
			throw new PaintingRuleLevels.NotClosedShape("The points are not connected. Regression count = " + moveToBackCounter);
		}
		levelLines.remove(line);
		levelLines.add(line);
	}
	double [][] generatePoints() throws PaintingRuleLevels.NotClosedShape{
		List <Double []> points = new ArrayList<>();
		List <Line> linesToCheck = new ArrayList<>();
		
		for (Line line: levelLines){
			linesToCheck.add(line);
		}

		points.add(new Double [2]);
		points.add(new Double [2]);

		points.get(0)[0] = linesToCheck.get(0).pa.x*1.;
		points.get(0)[1] = linesToCheck.get(0).pa.y*1.;

		Point p = linesToCheck.get(0).pb;
		points.get(1)[0] = p.x*1.;
		points.get(1)[1] = p.y*1.;

		// linesToCheck.remove(0);

		int sizeControl = 0;

		while(sizeControl != linesToCheck.size()){
			sizeControl = linesToCheck.size();
			for (int i = 1; i < linesToCheck.size(); ++i){
				if (p.equals(linesToCheck.get(i).pa)){
					p = linesToCheck.get(i).pb;
					points.add(new Double []{p.x*1., p.y*1.});
					linesToCheck.remove(i);
					break;
				}else if (p.equals(linesToCheck.get(i).pb)){
					p = linesToCheck.get(i).pa;
					points.add(new Double[]{p.x*1., p.y*1.});
					linesToCheck.remove(i);
					break;
				}
			}
		}
		if (Math.abs(points.get(points.size()-1)[0] - points.get(0)[0]) > precision || Math.abs(points.get(points.size()-1)[1] - points.get(0)[1]) > precision){
			if (linesToCheck.size() == 0){
				this.moveToBack(levelLines.get(0));
				return generatePoints();
			}
			for (Line line: linesToCheck.subList(1, linesToCheck.size())) {
				if (line.onLine(points.get(0)[0].intValue(), points.get(0)[1].intValue()) && line.onLine(points.get(points.size()-1)[0].intValue(), points.get(points.size()-1)[1].intValue())){
					return arrayConverter(points.toArray(new Double[points.size()][2]));
				}
			}
			this.moveToBack(levelLines.get(0));
			return generatePoints();
			}
		return arrayConverter(points.toArray(new Double[points.size()][2]));
	}
	static double [][] arrayConverter(Double [][] input){
		double[][] output = new double [input.length][input[0].length];
		for (int i = 0; i < input.length; ++i) {
			for (int j = 0; j < input[0].length; ++j) {
				output[i][j] = input[i][j].doubleValue()*GridControl.getInstance().grid_size;
			}
		}
		return output;
	}
	Level(List <Line> levelLines, String name){
		this.levelLines = levelLines;
		this.randColor();
		this.name = name;
	}
	Level(String name){
		this.levelLines = new ArrayList <Line>();
		this.randColor();
		this.name = name;
	}
	void update(List <Line> newLevelLines){
		for (Line newline: newLevelLines){
			this.levelLines.add(newline);
		}
	}
	void addLine(Line newline){
		int controlSize = this.levelLines.size();
		boolean shouldBeAdded = true;
		for (int i = 0; i < controlSize; ++i){
			if (this.levelLines.get(i).equals(newline)){
				shouldBeAdded = false;
				break;
			}
		}
		if (shouldBeAdded)	this.levelLines.add(newline);
	}
}