package grammar_graphs;

import java.util.ArrayList;
import java.util.StringJoiner;

import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;
import java.lang.Math;

class Level {
	ArrayList <Line> levelLines;
	GeneralPath levelShape = null;
	private Color color;
	double [][] points;

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
		int before = levelLines.size();
		System.out.print("Size before: " + before);
		do{
			before = levelLines.size();
			levelLines = Shape.groupLines(levelLines);
		}while (before != levelLines.size());
		System.out.println("\nSize after: " + before);
		try{
			this.points = generatePoints();
		}catch(NotClosedShape e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	GeneralPath getShape() throws NotClosedShape{
		levelShape = new GeneralPath();
		levelShape.setWindingRule(GeneralPath.WIND_NON_ZERO);

		levelShape.moveTo(points[0][0], points[0][1]);
		for (int k = 1; k < points.length; k++)
			levelShape.lineTo(points[k][0], points[k][1]);
		levelShape.closePath();

		return levelShape;
	}
	double [][] generatePoints() throws NotClosedShape{
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

		Line firstLine = lines.get(0);
		Line lastLine = lines.get(0);

		int sizeControl = 0;

		while(sizeControl != lines.size()){
			sizeControl = lines.size();
			for (int i = 1; i < lines.size(); ++i){
				if (p.equals(lines.get(i).pa)){
					p = lines.get(i).pb;
					points[index][0] = p.x*1.*MainData.grid_size;
					points[index][1] = p.y*1.*MainData.grid_size;
					index++;
					lastLine = lines.get(i);
					lines.remove(i);
					break;
				}else if (p.equals(lines.get(i).pb)){
					p = lines.get(i).pa;
					points[index][0] = p.x*1.*MainData.grid_size;
					points[index][1] = p.y*1.*MainData.grid_size;
					index++;
					lastLine = lines.get(i);
					lines.remove(i);
					break;
				}
			}
		}

		if (points[points.length-1][0] != points[0][0] && points[points.length-1][1] != points[0][1]){
			System.out.println("The points are not connected.");
			System.out.println("Check it there is a chanse to close the shape");
			if (lines.size() == 0){
				System.out.println("No, there isn't");
				throw new NotClosedShape("The points are not connected.");
			}
			System.out.println("Yes, we will check it futher");
			onLine(int x, int y);
		}
		if (lines.size() > 1){
			System.out.println("REMOVE " + lines.size() + " LINES FROM LEVEL");
			levelLines.remove(lines);
		}
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