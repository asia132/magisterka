package grammar_graphs;

import java.awt.Point;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.lang.Math;

class Line{
	Point pa;
	Point pb;

	Color color = MainData.default_figure_color;
	boolean wasAdded = false;

	Line(int x_a, int y_a, int x_b, int y_b){	
		this.pa = new Point(toGrid(x_a), toGrid(y_a));
		this.pb = new Point(toGrid(x_b), toGrid(y_b));
	}
	Line(Point a, Point b){	
		this.pa = a;
		this.pb = b;
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
	void scale(int x, int y, double s){
		pa.x = round((pa.x - x) * s + x, x, s);
		pb.x = round((pb.x - x) * s + x, x, s);
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
	double [] getNormalized(){
		double [] norm = new double [4];
		norm[0] = this.pa.x * 1.;
		norm[1] = this.pa.y * 1.;
		norm[2] = this.pb.x * 1.;
		norm[3] = this.pb.y * 1.;
		return norm;
	}
	void drawLine(Graphics2D g2d){
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(3));

		g2d.drawLine(this.getX_a(), this.getY_a(), this.getX_b(), this.getY_b());
		
		if (MainData.showDist == true){
			int x = (int)((getX_a() + getX_b()) * 0.5);
			int y = (int)((getY_a() + getY_b()) * 0.5);
			String text = Integer.toString((int)Math.floor(disx()))  + ", " + Integer.toString((int)Math.floor(disy()));
			g2d.setColor(MainData.default_rect_color);
			g2d.drawString(text, x + 1, y + 1);
		}
	}
	void changeColor(Color new_color){
		this.color = new_color;
	}
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
	double [] getFunctionParams(){
		if (this.getX_a() == this.getX_b()){
			double [] result = {0, 0};
			return result;
		}
		else{
			double a = (this.getY_b()*1. - this.getY_a()*1.)/(this.getX_b()*1. - this.getX_a()*1.);
			double b = this.getY_a() - a * this.getX_a();
			double [] result = {a, b};
			return result;
		}
	}
	double [] changeDistFromA(double new_dist){
		double [] funParams = this.getFunctionParams();
		double dist = MainData.distans(this.pa.x, this.pa.y, this.pb.x, this.pb.y);

		double newAx1 = this.getX_a() - dist / Math.sqrt(1 + funParams[0] * funParams[0]);
		double newAx2 = this.getX_a() + dist / Math.sqrt(1 + funParams[0] * funParams[0]);

		double newAy1 = newAx1 * funParams[0] + funParams[1];
		double newAy2 = newAx2 * funParams[0] + funParams[1];

		return new double [4];
	}
	void setXY_a(int x_a, int y_a){
		this.pa.x = toGrid(x_a);
		this.pa.y = toGrid(y_a);
	}
	void setXY_b(int x_b, int y_b){
		this.pb.x = toGrid(x_b);
		this.pb.y = toGrid(y_b);
	}
	void setA(int [] points){
		this.pa.x = points[0];
		this.pa.y = points[1];
	}
	void setB(int [] points){
		this.pb.x = points[0];
		this.pb.y = points[1];
	}
	void move(int x, int y){
		this.pa.x += toGrid(x);
		this.pa.y += toGrid(y);
		this.pb.x += toGrid(x);
		this.pb.y += toGrid(y);
	}
	boolean onLine(int x, int y, int grid_size){
		if (this.getX_a() != this.getX_b()){
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
			if (floor - grid_size <= y && ceil + grid_size >= y && x_1 <= x && x_2 >= x){
				return true;
			}
		}
		else{
			int y_1, y_2;
			if (this.getY_a() < this.getY_b()){
				y_1 = this.getY_a();
				y_2 = this.getY_b();
			}
			else{
				y_1 = this.getY_b();
				y_2 = this.getY_a();
			}
			if (y_1 <= y && y_2 >= y && toGrid(x)*grid_size == this.getX_a())
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
	boolean isTheSameLine(Line other){
		if (this.pa.x != other.pa.x) return false;
		if (this.pa.y != other.pa.y) return false;
		if (this.pb.x != other.pb.x) return false;
		if (this.pb.y != other.pb.y) return false;
		return true;
	}
}
