package grammar_graphs;

import java.util.ArrayList;
import java.util.StringJoiner;

import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;
import java.lang.Math;
import java.awt.geom.Area;

class Level {
	ArrayList <Line> levelLines;
	private Color color;
	double [][] points;
	Area area;
	double precision = 1e-12;

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
		System.out.println("---------------CLOSE-LEVEL---------------");
		int before = levelLines.size();
		do{
			before = levelLines.size();
			levelLines = Shape.groupLines(levelLines);
		}while (before != levelLines.size());
		if (levelLines.size() > 2){
			try{
				this.points = generatePoints();
			}catch(NotClosedShape e){
				System.out.println(e.getMessage());
				MainData.setColorRules();
				// e.printStackTrace();
			}
		}
	}
	Area getShape() throws NotClosedShape{
		GeneralPath levelShape = new GeneralPath();
		levelShape.setWindingRule(GeneralPath.WIND_NON_ZERO);

		if (points == null || points.length < 0){
			throw new NotClosedShape("Points are null. The shapes are not closed.");
		}
		levelShape.moveTo(points[0][0], points[0][1]);
		for (int k = 1; k < points.length; k++)
			levelShape.lineTo(points[k][0], points[k][1]);
		levelShape.closePath();

		area = new Area(levelShape);
		area.intersect(MainData.coloringRuleLevels.limitingShape.area);
		return area;
	}
	int moveToBackCounter = 0;
	void moveToBack(Line line) throws NotClosedShape{
		moveToBackCounter++;
		if (moveToBackCounter == levelLines.size() - 1){
			System.out.println("The points are not connected. Regression count = " + moveToBackCounter);
			throw new NotClosedShape("The points are not connected. Regression count = " + moveToBackCounter);
		}
		levelLines.remove(line);
		levelLines.add(line);
	}
	double [][] generatePoints() throws NotClosedShape{


		ArrayList <Double []> points = new ArrayList<>();

		ArrayList <Line> linesToCheck = new ArrayList<>();
		ArrayList <Line> linesAlreadyChecked = new ArrayList<>();
		
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

		int sizeControl = 0;

		while(sizeControl != linesToCheck.size()){
			sizeControl = linesToCheck.size();
			for (int i = 1; i < linesToCheck.size(); ++i){
				if (p.equals(linesToCheck.get(i).pa)){
					p = linesToCheck.get(i).pb;
					points.add(new Double []{p.x*1., p.y*1.});
					linesAlreadyChecked.add(linesToCheck.get(i));
					linesToCheck.remove(i);
					break;
				}else if (p.equals(linesToCheck.get(i).pb)){
					p = linesToCheck.get(i).pa;
					points.add(new Double[]{p.x*1., p.y*1.});
					linesAlreadyChecked.add(linesToCheck.get(i));
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
			for (Line line: linesToCheck) {
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
				output[i][j] = input[i][j].doubleValue()*MainData.grid_size;
			}
		}
		return output;
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
		for (Line newline: newLevelLines){
			this.levelLines.add(newline);
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