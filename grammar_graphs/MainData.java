package grammar_graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.lang.Math;

class MainData {
	MainData() {
	}
	boolean RIGHT = false;
	boolean MIDDLE = false;
	static boolean showDist = false;
	Rectangle checkingRect = null;
	ArrayList <Line> lines = new ArrayList<Line>();
	Marker marker = null;
	Marker modified_marker = null;
	private ArrayList <Line> modyfiedLines = new ArrayList<Line>();
	ArrayList <Line> initialLines = new ArrayList<Line>();
	ArrayList <Line> temp_shape = new ArrayList<Line>();
	static int grid_size = 20;
	static ArrayList <Rule> ruleList = new ArrayList<>(); 
	static ArrayList <Line> copiedLines = new ArrayList<Line>();
	static boolean SHOW_GRID = true;
	// BLACK BLUE CYAN DARK_GRAY GRAY GREEN LIGHT_GRAY MAGENTA ORANGE PINK RED WHITE YELLOW
	static Color default_figure_color = Color.BLACK;
	static Color default_background_color = Color.WHITE;
	static Color default_check_color = Color.RED;
	static Color default_rect_color = Color.BLUE;
	static Color default_check_marker_color = Color.MAGENTA;
	static Color default_marker_color = Color.CYAN;
	static Color default_grid_color = Color.LIGHT_GRAY;

	void printAllLines(){
		for (Line line : lines){
			line.print();
		}
		System.out.println("------------------------------");
	}
	ArrayList <Line> copy(){
		ArrayList <Line> copy = new ArrayList<Line>();
		for (Line line: this.lines)
			copy.add(line.copy());
		return copy;
	}
	static int toGrid(int x){
		int modX = x%grid_size;
		if (modX <= (grid_size/2)*1.){
			return (int)Math.floor((x/grid_size)*1);
		}
		return (int)Math.ceil((x/grid_size)*1);
	}
	void addMarkerToModified(){
		modified_marker = marker;
	}
	void clearModifiedMarker(){
		modified_marker = null;
	}
	void clearModified(){
		changeModifiedColor(default_figure_color);
		modyfiedLines.clear();
	}
	boolean isEmptyModified(){
		return modyfiedLines.isEmpty();
	}
	ArrayList <Line> getModified(){
		return modyfiedLines;
	}
	void changeColorModeToBlack(){
		default_background_color = Color.BLACK;
		default_figure_color = Color.WHITE;
		changeFiguresColor();
	}
	void changeColorModeToWhite(){
		default_background_color = Color.WHITE;
		default_figure_color = Color.BLACK;
		changeFiguresColor();
	}
	static void setGrig(){
		SHOW_GRID = !SHOW_GRID;
	}
	static double distans(double x1, double y1, double x2, double y2){
		double x = (x1 - x2) * (x1 - x2);
		double y = (y1 - y2) * (y1 - y2);
		return Math.sqrt(x + y);
	}
	static double distans(int x1, int y1, int x2, int y2){
		double x = (x1 - x2) * (x1 - x2);
		double y = (y1 - y2) * (y1 - y2);
		return Math.sqrt(x + y);
	}
	static double distans(int [] p1, int [] p2){
		double x = (p1[0] - p2[0]) * (p1[0] - p2[0]);
		double y = (p1[1] - p2[1]) * (p1[1] - p2[1]);
		return Math.sqrt(x + y);
	}
	static int [] findCenter(ArrayList <Line> linesList){
		if (linesList.size() > 0){
			int min_x = linesList.get(0).getX_a();
			int max_x = linesList.get(0).getX_a();
			int min_y = linesList.get(0).getY_a();
			int max_y = linesList.get(0).getY_a();
			for (Line line: linesList) {
				min_x = min_x < line.getX_a() ? min_x : line.getX_a();
				min_x = min_x < line.getX_b() ? min_x : line.getX_b();

				max_x = max_x > line.getX_a() ? max_x : line.getX_a();
				max_x = max_x > line.getX_b() ? max_x : line.getX_b();
				
				min_y = min_y < line.getY_a() ? min_y : line.getY_a();
				min_y = min_y < line.getY_b() ? min_y : line.getY_b();
				
				max_y = min_y > line.getY_a() ? max_y : line.getY_a();
				max_y = min_y > line.getY_b() ? max_y : line.getY_b();
			}

			int [] point = {(int)((min_x + max_x)*0.5), (int)((min_y + max_y)*0.5)};
			return point;
		}
		int [] point = {0,0};
		return point;
	} 
// grid size
	void setGridSize(int i, int screenWidth, int screenHeight){
		int max_grid = screenWidth < screenHeight ? screenWidth : screenHeight;
		if (i + grid_size > 1 && grid_size + i < max_grid){
			grid_size += i;
		}
	}
	int getGridSize(){
		return grid_size;
	}
// lines and modified lines method
	void changeFiguresColor(){
		for (Line line : lines){
			if (modyfiedLines.indexOf(line) == -1){
				line.changeColor(default_figure_color);
			}
		}
	}
	void changeModifiedColor(Color color){
		for (Line line: modyfiedLines) {
			line.changeColor(color);
		}
	}
	void addToModified(Line line){
		if (modyfiedLines.indexOf(line) == -1){
			modyfiedLines.add(line);
			line.changeColor(default_check_color);
		}
		else{
			line.changeColor(default_figure_color);
			modyfiedLines.remove(line);
		}
	}
	void pasteCopied(int x, int y){
		if (!copiedLines.isEmpty()){
			changeModifiedColor(default_figure_color);
			modyfiedLines.clear();
			int [] point = findCenter(copiedLines);
			for (Line line: copiedLines) {
				line.move(x - point[0], y - point[1]);
				lines.add(line.copy());
				modyfiedLines.add(lines.get(lines.size() - 1));
				modyfiedLines.get(modyfiedLines.size() - 1).changeColor(default_check_color);
			}
		}
	}
	void copyModyfied(){
		if (!copiedLines.isEmpty())
			copiedLines.clear();
		for (Line line: modyfiedLines) {
			copiedLines.add(line.copy());
		}
		changeModifiedColor(default_figure_color);
		modyfiedLines.clear();
	}
	void allLinesScale(int i, int screenWidth, int screenHeight){
		if (i == 1 || grid_size > 5){
			setGridSize(i, screenWidth, screenHeight);
		}
	}
//
	Line onLine(int x, int y){
		for (Line s : lines){
			if (s.onLine(x, y, grid_size)) return s;
		}
		return null;
	}
	Line isPoint_a(int x, int y){
		for (Line s : lines){
			if (s.isPoint_a(x, y, grid_size)) return s;
		}
		return null;
	}
	Line isPoint_b(int x, int y){
		for (Line s : lines){
			if (s.isPoint_b(x, y, grid_size)) return s;
		}
		return null;
	}
	boolean wasMoved(int x1, int y1, int x2, int y2){
		if (Math.abs(x1 - x2) >= (int)(grid_size*0.5) || Math.abs(y1 - y2) >= (int)(grid_size*0.5))
			return true;
		return false;
	}
	void drawLines(Graphics2D g2d) {
		for (Line s : lines) {
			s.drawLine(g2d);
		}
	}
	void findLinesInRect(){
		for (Line line: lines){
			if (checkingRect.insideRect(line)){
				addToModified(line);
				initialLines.add(line.copy());
			}
		}
		if (marker != null && checkingRect.insideRect(marker)){
			addMarkerToModified();
		}
	}
	void drawTempLine(Graphics2D g2d) {
		for (Line line: temp_shape)
			line.drawLine(g2d);
	}
	void paintGrid(Graphics2D g2d, int screenWidth, int screenHeight){
		g2d.setColor(default_grid_color);
		for (int i = 0, j = 0; i < screenWidth; i += grid_size, j++){
			if (j%12 == 0) g2d.setStroke(new BasicStroke(2));
			else g2d.setStroke(new BasicStroke(1));
			g2d.drawLine(i, 0, i, screenHeight);
		}
		for (int i = 0, j = 0; i < screenHeight; i += grid_size, j++){
			if (j%12 == 0) g2d.setStroke(new BasicStroke(2));
			else g2d.setStroke(new BasicStroke(1));
			g2d.drawLine(0, i, screenWidth, i);
		}
	}
}