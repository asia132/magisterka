package grammar_graphs;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.lang.Math;

class Rectangle{
	int x, y;
	int a, b;

	Color color;

	Rectangle(int x_a, int y_a, int x_b, int y_b){
		this.x = x_a < x_b ? x_a : x_b;
		this.y = y_a < y_b ? y_a : y_b;

		this.a = Math.abs(x_a - x_b);
		this.b = Math.abs(y_a - y_b);

		this.color = MainData.default_rect_color;
	}
	void drawRectanle(Graphics2D g2d){
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(1));
		g2d.drawRect(x, y, a, b);
	}
	void changeColor(Color new_color){
		this.color = new_color;
	}
	int getX(){
		return x;
	}
	int getY(){
		return y;
	}
	int getA(){
		return a;
	}
	int getB(){
		return b;
	}
	void setXY(int x, int y){
		this.x = x;
		this.y = y;
	}
	void setAB(int a, int b){
		this.a = a;
		this.b = b;
	}
	boolean insideRect(Line line){
		for (int i = this.x; i < this.x + this.a; i += MainData.grid_size)
			for (int j = this.y; j < this.y + this.b; j += MainData.grid_size)
				if (line.onLine(i, j, MainData.grid_size))
					return true;
		if (this.x < line.getX_a() && line.getX_a() < this.x + this.a
			&& this.y < line.getY_a() && line.getY_a() < this.y + this.b)
			return true;
		if (this.x < line.getX_b() && line.getX_b() < this.x + this.a
			&& this.y < line.getY_b() && line.getY_b() < this.y + this.b)
			return true;
		return false;
	}
	boolean insideRect(Marker marker){
		if (((this.x < marker.getX() && marker.getX() < this.x + this.a)
			|| (this.x > marker.getX() && marker.getX() > this.x - this.a))
		  && ((this.y < marker.getY() && marker.getY() < this.y + this.b)
			|| (this.y > marker.getY() && marker.getY() > this.y - this.b)))
				return true;
		return false;
	}
}
