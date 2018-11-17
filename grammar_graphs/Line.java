package grammar_graphs;

import java.awt.Point;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics2D;

import java.lang.Math;

import java.util.StringJoiner;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Line{
	Point pa;
	Point pb;

	private List <Line> childs = new ArrayList<>();
	private Color color = Settings.default_figure_color;

	Line(Point a, Point b){
		this.pa = a;
		this.pb = b;
	}
	static Line createRotatedLine(Point a, Point b, double alpha, Point rotationPoint){
		Line line = new Line(a, b);
//		line.rotate(rotationPoint, alpha);
		line.rotate(rotationPoint.x, rotationPoint.y, alpha);
		return line;
	}
	static Line createLineAtScreenPoint(int x_a, int y_a, int x_b, int y_b){
		return new Line(new Point(GridControl.getInstance().toGrid(x_a), GridControl.getInstance().toGrid(y_a)), new Point(GridControl.getInstance().toGrid(x_b), GridControl.getInstance().toGrid(y_b)));
	}
	boolean checkY(int y){
		return	(this.pa.y <= y && y <= this.pb.y) || (this.pb.y <= y && y <= this.pa.y);
	}
	boolean checkX(int x){
		return	(this.pa.x <= x && x <= this.pb.x) || (this.pb.x <= x && x <= this.pa.x);
	}
	boolean checkLineY(Line line){
		return	this.checkY(line.pa.y) && this.checkY(line.pb.y);
	}
	boolean checkLineX(Line line){
		return	this.checkX(line.pa.x) && this.checkX(line.pb.x);
	}
	void mirrorX(int x){
		pa.x = 2*x - pa.x;
		pb.x = 2*x - pb.x;
	}
	void mirrorY(int y){
		pa.y = 2*y - pa.y;
		pb.y = 2*y - pb.y;
	}
	void addChild(Line line){
		this.childs.add(line);
	}
	void scale(int x, int y, double s){
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
	double length(){
		return MainData.distans(this.getX_a(), this.getY_a(), this.getX_b(), this.getY_b());
	}
	double small_length(){
		return MainData.distans(this.pa.x, this.pa.y, this.pb.x, this.pb.y);
	}
	boolean compareLen(double k, int x_a, int y_a, int x_b, int y_b){
		return this.small_length() == (MainData.distans(x_a, y_a, x_b, y_b) * k);
	}
	double [] getDoubleCoordinates(){
		double [] norm = new double [4];
		norm[0] = this.pa.x * 1.;
		norm[1] = this.pa.y * 1.;
		norm[2] = this.pb.x * 1.;
		norm[3] = this.pb.y * 1.;
		return norm;
	}
	void drawLine(Graphics2D g2d, Point point0){
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(3));

		g2d.drawLine(this.getX_a(), this.getY_a(), this.getX_b(), this.getY_b());
		
		if (Settings.SHOW_DIST == true){
			int x = (int)((getX_a() + getX_b()) * 0.5);
			int y = (int)((getY_a() + getY_b()) * 0.5) - (int)(GridControl.getInstance().grid_size*0.5);
			String text = Integer.toString((int)Math.floor(disx()))  + ", " + Integer.toString((int)Math.floor(disy()));
			g2d.setColor(Settings.default_rect_color);
			g2d.drawString(text, x + 1, y + 1);
		}
		if (Settings.SHOW_POINTS == true){
			g2d.setColor(Settings.default_point_color);
			String text = "A[" + (pa.x - point0.x) + ", " + (pa.y - point0.y) + "]";
			g2d.drawString(text, getX_a() + 1, getY_a() - (int)(GridControl.getInstance().grid_size*0.5));
			
			text = "B[" + (pb.x - point0.x) + ", " + (pb.y - point0.y) + "]";
			g2d.drawString(text, getX_b() + 1, getY_b() - (int)(GridControl.getInstance().grid_size*0.5));
		}
	}
	void changeColor(Color new_color){
		this.color = new_color;
	}
// gets
	int getX_a(){
		return pa.x*GridControl.getInstance().grid_size;
	}
	int getY_a(){
		return pa.y*GridControl.getInstance().grid_size;
	}
	int getX_b(){
		return pb.x*GridControl.getInstance().grid_size;
	}
	int getY_b(){
		return pb.y*GridControl.getInstance().grid_size;
	}
	Point getA(){
		return pa;
	}
	Point getB(){
		return pb;
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
		this.pa.x = GridControl.getInstance().toGrid(x_a);
		this.pa.y = GridControl.getInstance().toGrid(y_a);
		if (!childs.isEmpty()){
			for (Line child: childs){
				child.setXY_a(x_a, y_a);
			}
		}
	}
	void setXY_b(int x_b, int y_b){
		this.pb.x = GridControl.getInstance().toGrid(x_b);
		this.pb.y = GridControl.getInstance().toGrid(y_b);
		if (!childs.isEmpty()){
			for (Line child: childs){
				child.setXY_b(x_b, y_b);
			}
		}
	}
	void setA(int [] points){
		this.pa.x = points[0];
		this.pa.y = points[1];
		if (!childs.isEmpty()){
			for (Line child: childs){
				child.setA(points);
			}
		}
	}
	void setB(int [] points){
		this.pb.x = points[0];
		this.pb.y = points[1];
		if (!childs.isEmpty()){
			for (Line child: childs){
				child.setB(points);
			}
		}
	}
	void move(int x, int y){
		this.pa.x += GridControl.getInstance().toGrid(x); 
		this.pa.y += GridControl.getInstance().toGrid(y);
		this.pb.x += GridControl.getInstance().toGrid(x);
		this.pb.y += GridControl.getInstance().toGrid(y);
		if (!childs.isEmpty()){
			for (Line child: childs){
				System.out.println("child: Move line: " + this);
				child.move(x, y);
			}
		}
	}
	// check if the other line has the same a nad b params as the line
	boolean compareABParams(Line otherLine){
		return this.onLine(otherLine.getX_a(), otherLine.getY_a(), GridControl.getInstance().grid_size) || this.onLine(otherLine.getX_b(), otherLine.getY_b(), GridControl.getInstance().grid_size);
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
			if (y_1 <= y && y_2 >= y && GridControl.getInstance().toGrid(x)*threshold == this.getX_a())
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
		return new Line(new Point(this.pa.x, this.pa.y), new Point(this.pb.x, this.pb.y));
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
//	@Override
//	public String toString(){
//		StringJoiner info = new StringJoiner("\t");
//		return info.add(FileSaverTags.LINETAG.toString()).add("" + pa.x).add("" + pa.y).add("" + pb.x).add(pb.y + "").toString();
//	}
	@Override
	public final boolean equals(Object obj){
		if (obj == null)
			return false;
		if (!(obj instanceof Line))
			return false;
		Line other = (Line)obj;
		if (this.pa.equals(other.getA()) && this.pb.equals(other.getB())) 
			return true;
		if (this.pa.equals(other.getB()) && this.pb.equals(other.getA())) 
			return true;
		return false;
	}
	@Override
	public final int hashCode() {
		int [] ab = this.getSortedAB();
		return Objects.hash(ab[0], ab[1], ab[2], ab[3]);
	}
	class NotALinearFunction extends Exception{
		public static final long serialVersionUID = 42L;
		
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
