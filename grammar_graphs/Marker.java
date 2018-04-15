package grammar_graphs;

import java.awt.Color;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.Graphics2D;

import java.lang.Math;

class Marker{
	Point p;
	int r;
	Direct dir;
	Color color;
	static int toGrid(int x){
		int modX = x%MainData.grid_size;
		if (modX <= (MainData.grid_size/2)*1.){
			return (int)Math.floor((x/MainData.grid_size)*1);
		}
		return (int)Math.ceil((x/MainData.grid_size)*1);
	}
	Marker(int x, int y){
		this.p = new Point(toGrid(x), toGrid(y));
		this.r = 2;
		this.dir = Direct.N;
		this.color = MainData.default_marker_color;
	}
	Marker(Marker marker){
		this.p = new Point(marker.p.x, marker.p.y);
		this.r = marker.r;
		this.dir = marker.dir;
		this.color = MainData.default_marker_color;
	}
	Marker(Point p){
		this.p = p;
		this.r = 2;
		this.dir = Direct.N;
		this.color = MainData.default_marker_color;
	}
	boolean isMiddle(int x, int y){
		if (Math.abs(getX() - x) < MainData.grid_size * 0.5 && Math.abs(getY() - y) < MainData.grid_size * 0.5){
			return true;
		}
		return false;
	}
	void drawLine(Graphics2D g2d){
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(MainData.default_rect_color);
		g2d.drawOval(getX(), getY(), 1, 1);

		if (MainData.showDist == true){
			g2d.drawString(getD(), getX()+ 2, getY() - 2);
			g2d.drawString("A", this.getAx(), this.getAy());
			g2d.drawString("B", this.getBx(), this.getBy());
			g2d.drawString("C", this.getCx(), this.getCy());
			g2d.drawString("D", this.getDx(), this.getDy());
		}
		g2d.setColor(color);

		int d = getR() * 2;
		int x = getX()-getR(), y = getY()-getR();
		int startAngle = 0;
		int bigAngle = 180, shortAngle = 90;

		switch (dir){
			case N:{
					g2d.drawArc(x, y, d, d, 0, bigAngle);
					g2d.drawArc(x - getR(), y + getR(), d, d, 0, shortAngle);
					g2d.drawArc(x + getR(), y + getR(), d, d, 90, shortAngle);
					break;
				}
			case W:{
					g2d.drawArc(x, y, d, d, 90, bigAngle);
					g2d.drawArc(x + getR(), y - getR(), d, d, 180, shortAngle);
					g2d.drawArc(x + getR(), y + getR(), d, d, 90, shortAngle);
					break;
				}
			case S:{
					g2d.drawArc(x, y, d, d, 180, bigAngle);
					g2d.drawArc(x + getR(), y - getR(), d, d, 180, shortAngle);
					g2d.drawArc(x - getR(), y - getR(), d, d, 270, shortAngle);
					break;
				}
			case E:{
					g2d.drawArc(x, y, d, d, 270, bigAngle);
					g2d.drawArc(x - getR(), y + getR(), d, d, 0, shortAngle);
					g2d.drawArc(x - getR(), y - getR(), d, d, 270, shortAngle);
					break;
				}
		}
	}
	void setXY(int x, int y){
		this.p = new Point(toGrid(x), toGrid(y));
	}
	int getX(){
		return p.x*MainData.grid_size;
	}
	int getY(){
		return p.y*MainData.grid_size;
	}
	int getR(){
		return r*MainData.grid_size;
	}
	int [] calcX(){
		int [] x = {getAx(), getBx(), getX(), getCx()};
		return x;
	}
	int [] calcY(){
		int [] y = {getAy(), getBy(), getY(), getCy()};
		return y;
	}
	void scale(int i){
		r += i;
	}
	void move(int xt, int yt){
		this.p.x += toGrid(xt);
		this.p.y += toGrid(yt);
	}
// ABC
	int getAx(){
		switch (dir){
			case N: return getX();
			case W:	return getX() - getR();
			case S: return getX();
			case E:	return getX() + getR();
		}
		return 0;
	}
	int getBx(){
		switch (dir){
			case N:	return getX() - getR();
			case W:	return getX();
			case S:	return getX() + getR();
			case E:	return getX();
		}
		return 0;
	}
	int getCx(){
		switch (dir){
			case N:	return getX();
			case W:	return getX() + getR();
			case S:	return getX();
			case E:	return getX() - getR();
		}
		return 0;
	}
	int getDx(){
		switch (dir){
			case N:	return getX() + getR();
			case W:	return getX();
			case S:	return getX() - getR();
			case E:	return getX();
		}
		return 0;
	}
	int getAy(){
		switch (dir){
			case N:	return getY() - getR();
			case W:	return getY();
			case S:	return getY() + getR();
			case E:	return getY();
		}
		return 0;
	}
	int getBy(){
		switch (dir){
			case N:	return getY();
			case W:	return getY() + getR();
			case S:	return getY();
			case E:	return getY() - getR();
		}
		return 0;
	}
	int getCy(){
		switch (dir){
			case N:	return getY() + getR();
			case W:	return getY();
			case S:	return getY() - getR();
			case E:	return getY();
		}
		return 0;
	}
	int getDy(){
		switch (dir){
			case N:	return getY();
			case W:	return getY() - getR();
			case S:	return getY();
			case E:	return getY() + getR();
		}
		return 0;
	}
// abc
	int getax(){
		switch (dir){
			case N: return p.x;
			case W:	return p.x - r;
			case S: return p.x;
			case E:	return p.x + r;
		}
		return 0;
	}
	int getbx(){
		switch (dir){
			case N:	return p.x - r;
			case W:	return p.x;
			case S:	return p.x + r;
			case E:	return p.x;
		}
		return 0;
	}
	int getcx(){
		switch (dir){
			case N:	return p.x;
			case W:	return p.x + r;
			case S:	return p.x;
			case E:	return p.x - r;
		}
		return 0;
	}
	int getdx(){
		switch (dir){
			case N:	return p.x + r;
			case W:	return p.x;
			case S:	return p.x - r;
			case E:	return p.x;
		}
		return 0;
	}
	int getay(){
		switch (dir){
			case N:	return p.y - r;
			case W:	return p.y;
			case S:	return p.y + r;
			case E:	return p.y;
		}
		return 0;
	}
	int getby(){
		switch (dir){
			case N:	return p.y;
			case W:	return p.y + r;
			case S:	return p.y;
			case E:	return p.y - r;
		}
		return 0;
	}
	int getcy(){
		switch (dir){
			case N:	return p.y + r;
			case W:	return p.y;
			case S:	return p.y - r;
			case E:	return p.y;
		}
		return 0;
	}
	int getdy(){
		switch (dir){
			case N:	return p.y;
			case W:	return p.y - r;
			case S:	return p.y;
			case E:	return p.y + r;
		}
		return 0;
	}
//
	void setColor(Color color){
		this.color = color;
	}
	Marker copy(){
		return new Marker(this);
	}
// check ABD
	boolean checkA(int x, int y){
		if (Math.abs(this.getAx() - x) < (int)(MainData.grid_size*0.25) && Math.abs(this.getAy() - y) < (int)(MainData.grid_size*0.25)){
			return true;
		}
		return false;
	}
	boolean checkB(int x, int y){
		if (Math.abs(this.getBx() - x) < (int)(MainData.grid_size*0.25) && Math.abs(this.getBy() - y) < (int)(MainData.grid_size*0.25))
			return true;
		return false;
	}
	boolean checkC(int x, int y){
		if (Math.abs(this.getCx() - x) < (int)(MainData.grid_size*0.25) && Math.abs(this.getCy() - y) < (int)(MainData.grid_size*0.25))
			return true;
		return false;
	}
	boolean checkD(int x, int y){
		if (Math.abs(this.getDx() - x) < (int)(MainData.grid_size*0.25) && Math.abs(this.getDy() - y) < (int)(MainData.grid_size*0.25))
			return true;
		return false;
	}
//
	boolean tryToResize(int x1, int y1, int x2, int y2){
		int precision = (int)(MainData.grid_size*0.5);
		if (this.checkA(x1, y1) == true){
			if (this.dir == Direct.N){
				if (x1 - x2 > precision) rotateL();
				if (x2 - x1 > precision) rotateR();
				if (y1 - y2 > precision) r++;
				if (y2 - y1 > precision && r > 1) r--;
			}
			else if (this.dir == Direct.S){
				if (x1 - x2 > precision) rotateR();
				if (x2 - x1 > precision) rotateL();
				if (y1 - y2 > precision && r > 1) r--;
				if (y2 - y1 > precision) r++;
			}
			else if (this.dir == Direct.E){
				if (y1 - y2 > precision) rotateL();
				if (y2 - y1 > precision) rotateR();
				if (x1 - x2 > precision && r > 1) r--;
				if (x2 - x1 > precision) r++;
			}
			else if (this.dir == Direct.W){
				if (y1 - y2 > precision) rotateR();
				if (y2 - y1 > precision) rotateL();
				if (x1 - x2 > precision) r++;
				if (x2 - x1 > precision && r > 1) r--;
			}
			return true;
		}
		else if (this.checkB(x1, y1) == true){
			if (this.dir == Direct.N){
				if (x1 - x2 > precision) r++;
				if (x2 - x1 > precision && r > 1) r--;
				if (y1 - y2 > precision) rotateR();
				if (y2 - y1 > precision) rotateL();
			}
			else if (this.dir == Direct.S){
				if (x1 - x2 > precision && r > 1) r--;
				if (x2 - x1 > precision) r++;
				if (y1 - y2 > precision) rotateL();
				if (y2 - y1 > precision) rotateR();
			}
			else if (this.dir == Direct.E){
				if (y1 - y2 > precision) r++;
				if (y2 - y1 > precision && r > 1) r--;
				if (x1 - x2 > precision) rotateL();
				if (x2 - x1 > precision) rotateR();
			}
			else if (this.dir == Direct.W){
				if (y1 - y2 > precision && r > 1) r--;
				if (y2 - y1 > precision) r++;
				if (x1 - x2 > precision) rotateR();
				if (x2 - x1 > precision) rotateL();
			}
			return true;
		}
		else if (this.checkC(x1, y1) == true){
			if (this.dir == Direct.N){
				if (x1 - x2 > precision) rotateR();
				if (x2 - x1 > precision) rotateL();
				if (y1 - y2 > precision && r > 1) r--;
				if (y2 - y1 > precision) r++;
			}
			else if (this.dir == Direct.S){
				if (x1 - x2 > precision) rotateL();
				if (x2 - x1 > precision) rotateR();
				if (y1 - y2 > precision) r++;
				if (y2 - y1 > precision && r > 1) r--;
			}
			else if (this.dir == Direct.E){
				if (y1 - y2 > precision) rotateR();
				if (y2 - y1 > precision) rotateL();
				if (x1 - x2 > precision) r++;
				if (x2 - x1 > precision && r > 1) r--;
			}
			else if (this.dir == Direct.W){
				if (y1 - y2 > precision) rotateL();
				if (y2 - y1 > precision) rotateR();
				if (x1 - x2 > precision && r > 1) r--;
				if (x2 - x1 > precision) r++;
			}
			return true;
		}
		else if (this.checkD(x1, y1) == true){
			if (this.dir == Direct.N){
				if (x1 - x2 > precision && r > 1) r--;
				if (x2 - x1 > precision) r++;
				if (y1 - y2 > precision) rotateL();
				if (y2 - y1 > precision) rotateR();
			}
			else if (this.dir == Direct.S){
				if (x1 - x2 > precision) r++;
				if (x2 - x1 > precision && r > 1) r--;
				if (y1 - y2 > precision) rotateR();
				if (y2 - y1 > precision) rotateL();
			}
			else if (this.dir == Direct.E){
				if (y1 - y2 > precision && r > 1) r--;
				if (y2 - y1 > precision) r++;
				if (x1 - x2 > precision) rotateR();
				if (x2 - x1 > precision) rotateL();
			}
			else if (this.dir == Direct.W){
				if (y1 - y2 > precision) r++;
				if (y2 - y1 > precision && r > 1) r--;
				if (x1 - x2 > precision) rotateL();
				if (x2 - x1 > precision) rotateR();
			}
			return true;
		}
		return false;
	}
// rotate
	void rotateR(){
		if 		(this.dir == Direct.N)	this.dir = Direct.E;
		else if (this.dir == Direct.E) 	this.dir = Direct.S;
		else if (this.dir == Direct.S) 	this.dir = Direct.W;
		else if (this.dir == Direct.W) 	this.dir = Direct.N;
	}
	void rotateL(){
		if 		(this.dir == Direct.N)	this.dir = Direct.W;
		else if (this.dir == Direct.E) 	this.dir = Direct.N;
		else if (this.dir == Direct.S) 	this.dir = Direct.E;
		else if (this.dir == Direct.W) 	this.dir = Direct.S;
	}
//
	String getD(){
		if 		(dir == Direct.N)	return "N";
		else if (dir == Direct.E) 	return "E";
		else if (dir == Direct.S) 	return "S";
		else if (dir == Direct.W) 	return "W";
		return "";
	}
	double length(){
		return MainData.distans(this.getX(), this.getY(), this.getAx(), this.getAy());
	}
	double [] getNormalized(){
		double [] norm = new double [4];
		norm[0] = this.p.x * 1.;
		norm[1] = this.p.y * 1.;
		norm[2] = this.getax() * 1.;
		norm[3] = this.getay() * 1.;
		return norm;
	}
	double calcRotation(Direct otherDir){
		int v = this.dir.value() - otherDir.value();
		if (v == -1 || v == 3){	
			return Math.PI * 3 / 2;
		}
		if (v == -2 || v == 2){	
			return Math.PI;
		}
		if (v == -3 || v == 1){	
			return Math.PI / 2;
		}
		return 0;
	}
}
enum Direct{
	N, E, S, W;
	private int value;
	static {
		N.value = 1;
		E.value = 2;
		W.value = 4;
		S.value = 3;
	}
	public int value(){
		return value;
	}
};

