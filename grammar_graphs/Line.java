package grammar_graphs;

import java.awt.Point;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics2D;

import java.lang.Math;

import java.util.StringJoiner;
import java.util.ArrayList;

class Line{
	Point pa;
	Point pb;

	private ArrayList <Line> childs = new ArrayList<>();

	private Color color = MainData.default_figure_color;

	Line(int x_a, int y_a, int x_b, int y_b){	
		// System.out.println("CREATE LINE + " + this);
		this.pa = new Point(toGrid(x_a), toGrid(y_a));
		this.pb = new Point(toGrid(x_b), toGrid(y_b));
	}
	Line(Point a, Point b){
		// System.out.println("CREATE LINE + " + this);
		this.pa = a;
		this.pb = b;
	}
	void mirrorX(int x){
		pa.x = 2*x - pa.x;
		pb.x = 2*x - pb.x;
	}
	void mirrorY(int y){
		pa.y = 2*y - pa.y;
		pb.y = 2*y - pb.y;
	}
	int round(double v, int x, double s){
		double xx = (double)(x);
		if (s > 1){
			if (v < xx)	return (int)Math.ceil(v);
			else		return (int)Math.floor(v);
		}
		else{
			if (v < xx)	return (int)Math.floor(v);
			else		return (int)Math.ceil(v);
		}
	}
	void addChild(Line line){
		this.childs.add(line);
		System.out.println(this + " add child: " + line);
	}
	void scale(int x, int y, double s){
		// pa.x = round((pa.x - x) * s + x, x, s);
		// pb.x = round((pb.x - x) * s + x, x, s);
		// pa.y = round((pa.y - y) * s + y, y, s);
		// pb.y = round((pb.y - y) * s + y, y, s);
		pa.x = (int)Math.round((pa.x - x) * s + x);
		pb.x = (int)Math.round((pb.x - x) * s + x);
		pa.y = (int)Math.round((pa.y - y) * s + y);
		pb.y = (int)Math.round((pb.y - y) * s + y);
	}
	void rotate(int x, int y, double alpha){
		int pax = pa.x, pbx = pb.x, pay = pa.y, pby = pb.y;

		pa.x = (int)Math.round(Math.cos(alpha) * pax - (Math.sin(alpha) * pay) + (x * (1 - Math.cos(alpha))) + (y * Math.sin(alpha)));
		pb.x = (int)Math.round(Math.cos(alpha) * pbx - (Math.sin(alpha) * pby) + (x * (1 - Math.cos(alpha))) + (y * Math.sin(alpha)));
		pa.y = (int)Math.round(Math.sin(alpha) * pax + (Math.cos(alpha) * pay) + (y * (1 - Math.cos(alpha))) - (x * Math.sin(alpha)));
		pb.y = (int)Math.round(Math.sin(alpha) * pbx + (Math.cos(alpha) * pby) + (y * (1 - Math.cos(alpha))) - (x * Math.sin(alpha)));
	}
	boolean sameA(Line line){
		return (line.pa.x == this.pa.x && line.pa.y == this.pa.y) || (line.pb.x == this.pa.x && line.pb.y == this.pa.y);
	}
	boolean sameB(Line line){
		return (line.pa.x == this.pb.x && line.pa.y == this.pb.y) || (line.pb.x == this.pb.x && line.pb.y == this.pb.y);
	}
	static int toGrid(int x){
		int gs = MainData.grid_size;
		int modX = x%gs;
		if (modX <= (gs*0.5)*1.){
			return (int)Math.floor((x/gs)*1);
		}
		return (int)Math.ceil((x/gs)*1);
	}
	double length(){
		return MainData.distans(this.getX_a(), this.getY_a(), this.getX_b(), this.getY_b());
	}
	double small_length(){
		return MainData.distans(this.pa.x, this.pa.y, this.pb.x, this.pb.y);
	}
	double [] getDoubleCoordinates(){
		double [] norm = new double [4];
		norm[0] = this.pa.x * 1.;
		norm[1] = this.pa.y * 1.;
		norm[2] = this.pb.x * 1.;
		norm[3] = this.pb.y * 1.;
		return norm;
	}
	void drawLine(Graphics2D g2d, int [] point0){
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(3));

		g2d.drawLine(this.getX_a(), this.getY_a(), this.getX_b(), this.getY_b());
		
		if (MainData.SHOW_DIST == true){
			int x = (int)((getX_a() + getX_b()) * 0.5);
			int y = (int)((getY_a() + getY_b()) * 0.5) - (int)(MainData.grid_size*0.5);
			String text = Integer.toString((int)Math.floor(disx()))  + ", " + Integer.toString((int)Math.floor(disy()));
			g2d.setColor(MainData.default_rect_color);
			g2d.drawString(text, x + 1, y + 1);
		}
		if (MainData.SHOW_POINTS == true){
			g2d.setColor(MainData.default_point_color);
			String text = "[" + (pa.x - point0[0]) + ", " + (pa.y - point0[1]) + "]";
			g2d.drawString(text, getX_a() + 1, getY_a() - (int)(MainData.grid_size*0.5));
			
			text = "[" + (pb.x - point0[0]) + ", " + (pb.y - point0[1]) + "]";
			g2d.drawString(text, getX_b() + 1, getY_b() - (int)(MainData.grid_size*0.5));
		}
	}
	void changeColor(Color new_color){
		this.color = new_color;
	}
// gets
	int [] getA(){
		int [] a = {this.getX_a(), this.getY_a()};
		return a;
	}
	int [] getB(){
		int [] b = {this.getX_b(), this.getY_b()};
		return b;
	}
	int getX_a(){
		return pa.x*MainData.grid_size;
	}
	int getY_a(){
		return pa.y*MainData.grid_size;
	}
	int getX_b(){
		return pb.x*MainData.grid_size;
	}
	int getY_b(){
		return pb.y*MainData.grid_size;
	}
	int [] getSortedAB(){
		int [] ab = new int [4];
		if (this.pa.x < this.pb.x || (this.pa.x == this.pb.x && this.pa.y <= this.pb.y)){
			ab[0] = this.pa.x;
			ab[1] = this.pa.y;
			ab[2] = this.pb.x;
			ab[3] = this.pb.y;
		}else{
			ab[0] = this.pb.x;
			ab[1] = this.pb.y;
			ab[2] = this.pa.x;
			ab[3] = this.pa.y;
		}
		return ab;
	}
//
	boolean isPartOfLinearFun(){
		return this.pa.x != this.pb.x;
	}
	double [] getFunctionParams() throws NotALinearFunction{
		if (!this.isPartOfLinearFun()){
			throw new NotALinearFunction(this.pa.x);
		}
		else{
			double a = (this.getY_b()*1. - this.getY_a()*1.)/(this.getX_b()*1. - this.getX_a()*1.);
			double b = this.getY_a() - a * this.getX_a();
			double [] result = {a, b};
			return result;
		}
	}
	double [] getFunctionParamsOnGrid() throws NotALinearFunction{
		if (!this.isPartOfLinearFun()){
			throw new NotALinearFunction(this.pa.x);
		}
		else{
			double a = (this.pb.y*1. - this.pa.y*1.)/(this.pb.x*1. - this.pa.x*1.);
			double b = this.pa.y - a * this.pa.x;
			double [] result = {a, b};
			return result;
		}
	}
// sets
	void setXY_a(int x_a, int y_a){
		System.out.println("SET XY A");
		this.pa.x = toGrid(x_a);
		this.pa.y = toGrid(y_a);
		if (!childs.isEmpty()){
			for (Line child: childs){
				System.out.println("\tSET XY A CHILD");
				child.setXY_a(x_a, y_a);
			}
		}
	}
	void setXY_b(int x_b, int y_b){
		System.out.println("SET XY B");
		this.pb.x = toGrid(x_b);
		this.pb.y = toGrid(y_b);
		if (!childs.isEmpty()){
			for (Line child: childs){
				System.out.println("\tSET XY B CHILD");
				child.setXY_b(x_b, y_b);
			}
		}
	}
	void setA(int [] points){
		System.out.println("SET A");
		this.pa.x = points[0];
		this.pa.y = points[1];
		if (!childs.isEmpty()){
			for (Line child: childs){
				System.out.println("\tSET V CHILD");
				child.setA(points);
			}
		}
	}
	void setB(int [] points){
		System.out.println("SET B");
		this.pb.x = points[0];
		this.pb.y = points[1];
		if (!childs.isEmpty()){
			for (Line child: childs){
				System.out.println("\tSET B CHILD");
				child.setB(points);
			}
		}
	}
	void move(int x, int y){
		System.out.println("MOVE " + childs);
		this.pa.x += toGrid(x); 
		this.pa.y += toGrid(y);
		this.pb.x += toGrid(x);
		this.pb.y += toGrid(y);
		if (!childs.isEmpty()){
			for (Line child: childs){
				System.out.println("\tMOVE CHILD");
				child.move(x, y);
			}
		}
	}
	// check if the other line has the same a nad b params as the line
	boolean compareABParams(Line otherLine){
		return this.onLine(otherLine.getX_a(), otherLine.getY_a(), MainData.grid_size) || this.onLine(otherLine.getX_b(), otherLine.getY_b(), MainData.grid_size);
	}
	// check if the point of x and y coordinates are in the line
	boolean onLine(int x, int y, int threshold){
		try{
			int x_1, x_2;
			if (this.getX_a() < this.getX_b()){
				x_1 = this.getX_a();
				x_2 = this.getX_b();
			}
			else{
				x_1 = this.getX_b();
				x_2 = this.getX_a();
			}
			double [] ab = getFunctionParams();
			int ceil = (int)Math.ceil(ab[0] * x + ab[1]);
			int floor = (int)Math.floor(ab[0] * x + ab[1]);
			if (floor - threshold <= y && ceil + threshold >= y && x_1 <= x && x_2 >= x){
				return true;
			}
		}
		catch(NotALinearFunction error){
			int y_1, y_2;
			if (this.getY_a() < this.getY_b()){
				y_1 = this.getY_a();
				y_2 = this.getY_b();
			}
			else{
				y_1 = this.getY_b();
				y_2 = this.getY_a();
			}
			if (y_1 <= y && y_2 >= y && toGrid(x)*threshold == this.getX_a())
				return true;
		}
		return false;
	}
	// check if the point of x and y coordinates are in the line
	boolean onLine(int x, int y){
		try{
			int x_1, x_2;
			if (this.pa.x < this.pb.x){
				x_1 = this.pa.x;
				x_2 = this.pb.x;
			}
			else{
				x_1 = this.pb.x;
				x_2 = this.pa.x;
			}
			double [] ab = getFunctionParamsOnGrid();
			if (y == (int)Math.round(ab[0] * x + ab[1]) && x_1 <= x && x <= x_2)
				return true;			
		}
		catch(NotALinearFunction error){
			int y_1, y_2;
			if (this.pa.y < this.pb.y){
				y_1 = this.pa.y;
				y_2 = this.pb.y;
			}
			else{
				y_1 = this.pb.y;
				y_2 = this.pa.y;
			}
			if (x == this.getX_a() && y_1 <= y && y <= y_2)
				return true;
		}
		return false;
	}
	boolean isPoint_a(int x, int y, int grid_size){
		if (Math.abs(this.getX_a() - x) < grid_size && Math.abs(this.getY_a() - y) < grid_size){
			return true;
		}
		return false;
	}
	boolean isPoint_b(int x, int y, int grid_size){
		if (Math.abs(this.getX_b() - x) < grid_size && Math.abs(this.getY_b() - y) < grid_size){
			return true;
		}
		return false;
	}
	Line copy(){
		return new Line(this.getX_a(), this.getY_a(), this.getX_b(), this.getY_b());
	}
	double disX(){
		return Math.abs(this.getX_a() - this.getX_b());
	}
	double disY(){
		return Math.abs(this.getY_a() - this.getY_b());
	}

	double disx(){
		return Math.abs(this.pa.x - this.pb.x);
	}
	double disy(){
		return Math.abs(this.pa.y - this.pb.y);
	}
	void print(){
		System.out.println("Line: A("+pa.x+", "+pa.y+") - B("+pb.x+", "+pb.y+")");
	}
	// @Override
	// public String toString(){
	// 	StringJoiner info = new StringJoiner("\t");
	// 	return info.add(FileSaver.lineTag).add("" + pa.x).add("" + pa.y).add("" + pb.x).add(pb.y + "").toString();
	// }
	boolean isTheSameLine(Line other){
		if (this.pa.x != other.pa.x) return false;
		if (this.pa.y != other.pa.y) return false;
		if (this.pb.x != other.pb.x) return false;
		if (this.pb.y != other.pb.y) return false;
		return true;
	}
	class NotALinearFunction extends Exception{
		private int x;
		NotALinearFunction(String message, int x){
			super(message);
			this.x = x;
		}
		NotALinearFunction(int x){
			super("Cannot find params a and b.\nThe function is not unique for y coordinate, but it can be defined as X = " + x);
			this.x = x;
		}
		int getX(){
			return x;
		}
	}
}
