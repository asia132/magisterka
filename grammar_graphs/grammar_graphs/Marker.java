package grammar_graphs;

import java.awt.Color;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.Graphics2D;

import java.lang.Math;
import java.lang.IllegalArgumentException;

import java.util.StringJoiner;

class Marker{
	Point p;
	int r;
	Direct dir;
	Color color;
	// Based on Point
	Marker(Point p){
		this.p = p;
		this.r = 2;
		this.dir = Direct.N;
		this.color = Settings.default_marker_color;
	}
	// based on coordinators
	static Marker createMarkerAtScreenPoint(int x, int y){
		return new Marker(new Point(GridControl.getInstance().toGrid(x), GridControl.getInstance().toGrid(y)));
	}
	void print(){
		System.out.println(dir.toString() + ", " + r + " [" + p.x + ", " + p.y + "]");
	}
	void mirrorX(int x){
		p.x = 2*x - p.x;
	}
	void mirrorY(int y){
		p.y = 2*y - p.y;
	}
	void mirrorDir(){
		if (this.dir == Direct.N)	this.dir = Direct.S;
		else if (this.dir == Direct.S)	this.dir = Direct.N;
		else if (this.dir == Direct.E)	this.dir = Direct.W;
		else if (this.dir == Direct.W)	this.dir = Direct.E;
	}
//
	@Override
	public String toString(){
		StringJoiner info = new StringJoiner("\t");
		return info.add("#M").add(dir.toString()).add(Integer.toString(r)).add("" + p.x).add(p.y + " ").toString();
	}
	boolean isMiddle(int x, int y){
		if (Math.abs(getX() - x) < GridControl.getInstance().grid_size * 0.5 && Math.abs(getY() - y) < GridControl.getInstance().grid_size * 0.5){
			return true;
		}
		return false;
	}
	void drawMarker(Graphics2D g2d, int [] point0){
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(3));
		g2d.drawOval(getX(), getY(), 1, 1);

		int d = getR() * 2;
		int x = getX()-getR(), y = getY()-getR();
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
		
		g2d.setColor(Settings.default_rect_color);
		if (Settings.SHOW_DIST == true){
			g2d.drawString(getD(), getX()+ 2, getY() - 2);
			g2d.drawString("A", this.getAx(), this.getAy());
			g2d.drawString("B", this.getBx(), this.getBy());
			g2d.drawString("C", this.getCx(), this.getCy());
			g2d.drawString("D", this.getDx(), this.getDy());
		}
		if (Settings.SHOW_POINTS == true){
			g2d.setColor(Settings.default_point_color);
			g2d.drawString("[" + (p.x - point0[0]) + ", " + (p.y - point0[1]) + "]", getX() - 4, getY() + (int)(GridControl.getInstance().grid_size*0.5)); // S
			g2d.drawString("[" + (getax() - point0[0]) + ", " + (getay() - point0[1]) + "]", getAx() - 4, getAy() + (int)(GridControl.getInstance().grid_size*0.5)); // A
			g2d.drawString("[" + (getbx() - point0[0]) + ", " + (getby() - point0[1]) + "]", getBx() - 4, getBy() + (int)(GridControl.getInstance().grid_size*0.5)); // B
			g2d.drawString("[" + (getcx() - point0[0]) + ", " + (getcy() - point0[1]) + "]", getCx() - 4, getCy() + (int)(GridControl.getInstance().grid_size*0.5)); // C
			g2d.drawString("[" + (getdx() - point0[0]) + ", " + (getdy() - point0[1]) + "]", getDx() - 4, getDy() + (int)(GridControl.getInstance().grid_size*0.5)); // D
		}
	}
	void setXY(int x, int y){
		this.p = new Point(GridControl.getInstance().toGrid(x), GridControl.getInstance().toGrid(y));
	}
	int getX(){
		return p.x*GridControl.getInstance().grid_size;
	}
	int getY(){
		return p.y*GridControl.getInstance().grid_size;
	}
	int getR(){
		return r*GridControl.getInstance().grid_size;
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
	void scale(double k) throws ToSmallRException{
		double newR = r * k;
		if (newR >= 1)
			r = (int)newR;
		else
			throw new ToSmallRException("The marker radius cannot be smaller than 1.");
	}
	void move(int xt, int yt, double angle){
		int x = (int)Math.round(Math.cos(angle) * xt*1. - (Math.sin(angle) * yt*1.));
		int y = (int)Math.round(Math.sin(angle) * xt*1. + (Math.cos(angle) * yt*1.));
		this.p.x += GridControl.getInstance().toGrid(x);
		this.p.y += GridControl.getInstance().toGrid(y);
	}
	void move(int xt, int yt){
		this.p.x += GridControl.getInstance().toGrid(xt);
		this.p.y += GridControl.getInstance().toGrid(yt);
	}
	void move(Point a, Point b){
		this.p.x += b.x - a.x;
		this.p.y += b.y - a.y;
	}
// ABC
	int getAx(){
		return getax() * GridControl.getInstance().grid_size;
	}
	int getBx(){
		return getbx() * GridControl.getInstance().grid_size;
	}
	int getCx(){
		return getcx() * GridControl.getInstance().grid_size;
	}
	int getDx(){
		return getdx() * GridControl.getInstance().grid_size;
	}
	int getAy(){
		return getay() * GridControl.getInstance().grid_size;
	}
	int getBy(){
		return getby() * GridControl.getInstance().grid_size;
	}
	int getCy(){
		return getcy() * GridControl.getInstance().grid_size;
	}
	int getDy(){
		return getdy() * GridControl.getInstance().grid_size;
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
		Marker newMarker = new Marker(new Point(this.p.x, this.p.y));
		newMarker.r = this.r;
		newMarker.dir = this.dir;
		newMarker.setColor(Settings.default_marker_color);
		return newMarker;
	}
// check ABD
	boolean checkA(int x, int y){
		if (Math.abs(this.getAx() - x) < (int)(GridControl.getInstance().grid_size*0.25) && Math.abs(this.getAy() - y) < (int)(GridControl.getInstance().grid_size*0.25)){
			return true;
		}
		return false;
	}
	boolean checkB(int x, int y){
		if (Math.abs(this.getBx() - x) < (int)(GridControl.getInstance().grid_size*0.25) && Math.abs(this.getBy() - y) < (int)(GridControl.getInstance().grid_size*0.25))
			return true;
		return false;
	}
	boolean checkC(int x, int y){
		if (Math.abs(this.getCx() - x) < (int)(GridControl.getInstance().grid_size*0.25) && Math.abs(this.getCy() - y) < (int)(GridControl.getInstance().grid_size*0.25))
			return true;
		return false;
	}
	boolean checkD(int x, int y){
		if (Math.abs(this.getDx() - x) < (int)(GridControl.getInstance().grid_size*0.25) && Math.abs(this.getDy() - y) < (int)(GridControl.getInstance().grid_size*0.25))
			return true;
		return false;
	}
//
	void increaseR(int i){
		this.r += GridControl.getInstance().toGrid(i);
	}
	void decreaseR(int i){
		this.r -= GridControl.getInstance().toGrid(i);
	}
	boolean checkR(int i){
		return r > GridControl.getInstance().toGrid(i);
	}
	boolean tryToResize(int x1, int y1, int x2, int y2){
		int precision = (int)(GridControl.getInstance().grid_size*0.5);
		if (this.checkA(x1, y1) == true){
			if (this.dir == Direct.N){
				if (x1 - x2 > precision) rotateL();
				if (x2 - x1 > precision) rotateR();
				if (y1 - y2 > precision) increaseR(y1 - y2);
				if (y2 - y1 > precision && checkR(y2 - y1)) decreaseR(y2 - y1);
			}
			else if (this.dir == Direct.S){
				if (x1 - x2 > precision) rotateR();
				if (x2 - x1 > precision) rotateL();
				if (y1 - y2 > precision && checkR(y1 - y2)) decreaseR(y1 - y2);
				if (y2 - y1 > precision) increaseR(y2 - y1);
			}
			else if (this.dir == Direct.E){
				if (y1 - y2 > precision) rotateL();
				if (y2 - y1 > precision) rotateR();
				if (x1 - x2 > precision && checkR(x1 - x2)) decreaseR(x1 - x2);
				if (x2 - x1 > precision) increaseR(x2 - x1);
			}
			else if (this.dir == Direct.W){
				if (y1 - y2 > precision) rotateR();
				if (y2 - y1 > precision) rotateL();
				if (x1 - x2 > precision) increaseR(x1 - x2);
				if (x2 - x1 > precision && checkR(x2 - x1)) decreaseR(x2 - x1);
			}
			return true;
		}
		else if (this.checkB(x1, y1) == true){
			if (this.dir == Direct.N){
				if (x1 - x2 > precision) increaseR(x1 - x2);
				if (x2 - x1 > precision && checkR(x2 - x1)) decreaseR(x2 - x1);
				if (y1 - y2 > precision) rotateR();
				if (y2 - y1 > precision) rotateL();
			}
			else if (this.dir == Direct.S){
				if (x1 - x2 > precision && checkR(x1 - x2)) decreaseR(x1 - x2);
				if (x2 - x1 > precision) increaseR(x2 - x1);
				if (y1 - y2 > precision) rotateL();
				if (y2 - y1 > precision) rotateR();
			}
			else if (this.dir == Direct.E){
				if (y1 - y2 > precision) increaseR(y1 - y2);
				if (y2 - y1 > precision && checkR(y2 - y1)) decreaseR(y2 - y1);
				if (x1 - x2 > precision) rotateL();
				if (x2 - x1 > precision) rotateR();
			}
			else if (this.dir == Direct.W){
				if (y1 - y2 > precision && checkR(y1 - y2)) decreaseR(y1 - y2);
				if (y2 - y1 > precision) increaseR(y2 - y1);
				if (x1 - x2 > precision) rotateR();
				if (x2 - x1 > precision) rotateL();
			}
			return true;
		}
		else if (this.checkC(x1, y1) == true){
			if (this.dir == Direct.N){
				if (x1 - x2 > precision) rotateR();
				if (x2 - x1 > precision) rotateL();
				if (y1 - y2 > precision && checkR(y1 - y2)) decreaseR(y1 - y2);
				if (y2 - y1 > precision) increaseR(y2 - y1);
			}
			else if (this.dir == Direct.S){
				if (x1 - x2 > precision) rotateL();
				if (x2 - x1 > precision) rotateR();
				if (y1 - y2 > precision) increaseR(y1 - y2);
				if (y2 - y1 > precision && checkR(y2 - y1)) decreaseR(y2 - y1);
			}
			else if (this.dir == Direct.E){
				if (y1 - y2 > precision) rotateR();
				if (y2 - y1 > precision) rotateL();
				if (x1 - x2 > precision) increaseR(x1 - x2);
				if (x2 - x1 > precision && checkR(x2 - x1)) decreaseR(x2 - x1);
			}
			else if (this.dir == Direct.W){
				if (y1 - y2 > precision) rotateL();
				if (y2 - y1 > precision) rotateR();
				if (x1 - x2 > precision && checkR(x1 - x2)) decreaseR(x1 - x2);
				if (x2 - x1 > precision) increaseR(x2 - x1);
			}
			return true;
		}
		else if (this.checkD(x1, y1) == true){
			if (this.dir == Direct.N){
				if (x1 - x2 > precision && checkR(x1 - x2)) decreaseR(x1 - x2);
				if (x2 - x1 > precision) increaseR(x2 - x1);
				if (y1 - y2 > precision) rotateL();
				if (y2 - y1 > precision) rotateR();
			}
			else if (this.dir == Direct.S){
				if (x1 - x2 > precision) increaseR(x1 - x2);
				if (x2 - x1 > precision && checkR(x2 - x1)) decreaseR(x2 - x1);
				if (y1 - y2 > precision) rotateR();
				if (y2 - y1 > precision) rotateL();
			}
			else if (this.dir == Direct.E){
				if (y1 - y2 > precision && checkR(y1 - y2)) decreaseR(y1 - y2);
				if (y2 - y1 > precision) increaseR(y2 - y1);
				if (x1 - x2 > precision) rotateR();
				if (x2 - x1 > precision) rotateL();
			}
			else if (this.dir == Direct.W){
				if (y1 - y2 > precision) increaseR(y1 - y2);
				if (y2 - y1 > precision && checkR(y2 - y1)) decreaseR(y2 - y1);
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
	void rotateBasedOnDirSub(Direct inDir, Direct finDir, boolean mirror){
		int qty = 0;
		try{
			qty = inDir.rotationRQty(finDir);
			if (mirror){
				while (qty > 0){
					qty--;
					this.rotateL();
				}

			}else{
				while (qty > 0){
					qty--;
					this.rotateR();
				}
			}
			// if (mirror)	this.mirrorDir();
		}catch (Exception e1) {
			try{
				qty = inDir.rotationLQty(finDir);
				if (mirror){
					while (qty > 0){
						qty--;
						this.rotateR();
					}
				}else{
					while (qty > 0){
						qty--;
						this.rotateL();
					}
				}
				
				// if (mirror)	this.mirrorDir();
			}catch (Exception e2) {;
				// System.out.println("Cannot find rotation. " + e1.getMessage() + " " + e2.getMessage());
			}
		}
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
		int v = otherDir.getNum() - this.dir.getNum();
		if (v == -1 || v == 3){
			// System.out.println((Math.PI / 2) + " (90) " + v + ". Input " + this.dir.value() + ". Initial " + otherDir.value());
			return Math.PI / 2;
		}
		if (v == -2 || v == 2){
			// System.out.println(Math.PI + " (180) " + v + ". Input " + this.dir.value() + ". Initial " + otherDir.value());
			return Math.PI;
		}
		if (v == -3 || v == 1){
			// System.out.println((Math.PI * 3 / 2) + " (270) " + v + ". Input " + this.dir.value() + ". Initial " + otherDir.value());
			return Math.PI * 3 / 2;
		}
		return 0;
	}
	enum Direct{
		N('N', 1), E('E', 2), S('W', 3), W('S', 4);
		private char value;
		private int num;
		Direct(char value, int num){
			this.value = value;
			this.num = num;
		}
		public char value(){
			return value;
		}
		@Override
		public String toString(){
			return "" + value;
		}
		public boolean equals(Direct dir){
			if (dir.value == this.value)	return true;
			return false;
		}
		public int getNum(){
			return num;
		}
		public static Direct parseValue(char value) throws IllegalArgumentException{
			if (value == 'N')   return N;
			if (value == 'E')   return E;
			if (value == 'W')   return W;
			if (value == 'S')   return S;
			throw new IllegalArgumentException("Wrong value " + value);
		}
		public static Direct parseValue(String value) throws IllegalArgumentException{
			if (value.equals("N"))   return N;
			if (value.equals("E"))   return E;
			if (value.equals("W"))   return W;
			if (value.equals("S"))   return S;
			throw new IllegalArgumentException("Wrong value " + value);
		}
		public int rotationRQty(Direct otherDir) throws Exception{
			if (otherDir.getNum() - this.num > 0)
				return otherDir.getNum() - this.num;
			throw new Exception("It is beter to use left rotation in this case.");
		}
		public int rotationLQty(Direct otherDir) throws Exception{
			if (this.num - otherDir.getNum() > 0)
				return this.num - otherDir.getNum();
			throw new Exception("It is beter to use right rotation in this case.");
		}
	}
	class ToSmallRException extends Exception{
		public static final long serialVersionUID = 42L;
		ToSmallRException(){
			super("The marker radius cannot be smaller than 1.");
		}
		ToSmallRException(String mess){
			super(mess + ".\tThe marker radius cannot be smaller than 1.");
		}
	}
}



