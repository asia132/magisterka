package grammar_graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

final class Rule{
	String name;
	Shape initialshape;
	Shape finalshape;
	Rule(String name, ArrayList <Line> initialLines, ArrayList <Line> finalLines, 
		Marker initialmarker, Marker finalmarker){
		this.name = name;
		this.initialshape = new Shape(initialLines, initialmarker, "Ra");
		this.finalshape = new Shape(RelativeComplement(finalLines, initialLines), finalmarker, "Rb");
	}
	Rule(String name){
		this.name = name;
		this.initialshape = new Shape();
		this.finalshape = new Shape();
	}
	String getName(){
		return this.name;
	}
	Marker getFinalMarker(){
		return finalshape.marker;
	} 
	Marker getInitialMarker(){
		return initialshape.marker;
	}
	ArrayList <Line> getInitialLines(){
		return initialshape.getLines();
	}
	ArrayList <Line> getFinalLines(){
		ArrayList <Line> finalLines = initialshape.getLines();
		finalLines.addAll(finalshape.getLines());
		return finalLines;
	}
	void apply(MainPanel panel) throws NoMarkerException{
		if (panel.programData.marker == null)
			throw new NoMarkerException("Please ass a marker to main panel\n");
		if (initialshape.marker == null)
			throw new NoMarkerException("Please ass a marker to left side of rule\n");
		System.out.println("-----------------------------------------------------------------------");
		Shape inputshape = new Shape(panel.programData.lines, panel.programData.marker, "Input");

		ArrayList <Line> found_lines = initialshape.findMatch(inputshape);

		if (found_lines.size() == initialshape.lines_dist.size()){
			panel.programData.lines.addAll(finalshape.setInPlace(panel.programData.marker));
			panel.repaint();
		}
	}
	ArrayList <Line>  RelativeComplement(ArrayList <Line> setA, ArrayList <Line> setB){
		ArrayList <Line> resultSet = new ArrayList<>();
		for (Line lineA: setA){
			boolean isInB = false;
			for (Line lineB: setB){
				if (lineA.isTheSameLine(lineB) == true){
					isInB = true;
					break;
				}
			}
			if (isInB == false) resultSet.add(lineA);
		}
		return resultSet;
	}
	private class Dist{
		double ms_la;
		double ms_lb;
		double ma_la;
		double ma_lb;
		double [] line_norm;
		String name;

		Dist(Line line, double [] marker_norm, String name){
			this.line_norm = line.getNormalized();
			this.name = name;
			this.ms_la = MainData.distans(line_norm[0], line_norm[1], marker_norm[0], marker_norm[1]);
			this.ms_lb = MainData.distans(line_norm[2], line_norm[3], marker_norm[0], marker_norm[1]);
			this.ma_la = MainData.distans(line_norm[0], line_norm[1], marker_norm[2], marker_norm[3]);
			this.ma_lb = MainData.distans(line_norm[2], line_norm[3], marker_norm[2], marker_norm[3]);
		}

		Dist(){
			this.line_norm = new double [4];
			this.name = "";
			this.ms_la = 0.0;
			this.ms_lb = 0.0;
			this.ma_la = 0.0;
			this.ma_lb = 0.0;
		}
		double lineNormLength(){
			return MainData.distans(line_norm[0], line_norm[1], line_norm[2], line_norm[3]);
		}
		boolean compareLine(Dist other_dist){
			if (this.lineNormLength() != other_dist.lineNormLength())	return false;
			if ((this.ms_lb == other_dist.ms_lb &&
				 this.ms_la == other_dist.ms_la &&
				 this.ma_lb == other_dist.ma_lb &&
				 this.ma_la == other_dist.ma_la) ||
				(this.ms_la == other_dist.ms_lb &&
				 this.ms_lb == other_dist.ms_la &&
				 this.ma_la == other_dist.ma_lb &&
				 this.ma_lb == other_dist.ma_la)){
				return true;
			}
			return false;
		}
		boolean compareLine(Dist other_dist, double k){
			double precision = 0.00000000000001;
			if (this.lineNormLength() != other_dist.lineNormLength() * k)	return false;
			if ((Math.abs(this.ms_lb - other_dist.ms_lb * k) < precision  &&
				 Math.abs(this.ma_la - other_dist.ma_la * k) < precision ) ||
				(Math.abs(this.ms_la - other_dist.ms_lb * k) < precision  &&  
				 Math.abs(this.ma_lb - other_dist.ma_la * k) < precision )){
				return true;
			}
			return false;
		}
	}
	private class Shape{
		Map <Line, Dist> lines_dist;
		double [] marker_norm;
		Marker marker;
		String name;
		Shape(ArrayList <Line> lines, Marker marker, String name){
			this.marker = marker;
			this.lines_dist = new HashMap<Line, Dist>();
			this.marker_norm = marker.getNormalized();
			int before = lines.size();
			System.out.print(name + ": " + lines.size());
			do{
				before = lines.size();
				lines = groupLines(lines);
			}while (before != lines.size());
			System.out.println(" - " + lines.size());
			for (Line line: lines){
				this.lines_dist.put(line.copy(), new Dist(line, marker_norm, name));
			}
		}
		Shape(){
			this.marker_norm = new double [4];
			this.lines_dist = new HashMap<Line, Dist>();
		}
		// Consider joining lines
		ArrayList <Line> groupLines(ArrayList <Line> shape_lines){
			ArrayList <Line> lines = new ArrayList<>();
			for (Line line: shape_lines)
				lines.add(line.copy());
			for (int i = 0; i < lines.size(); ++i){
				double [] funparam_i = lines.get(i).getFunctionParams();
				int [] points_i = lines.get(i).getSortedAB();
				for (int j = i + 1; j < lines.size(); ++j){
					double [] funparam_j = lines.get(j).getFunctionParams();
					int [] points_j = lines.get(j).getSortedAB();
					if (funparam_i[0] == funparam_j[0] && funparam_i[1] == funparam_j[1]
						&& points_i[0] <= points_j[0] && points_i[2] >= points_j[0] 
						&& points_i[1] <= points_j[1] && points_i[3] >= points_j[1]){
						lines.get(i).setA(findPointA(lines.get(i), lines.get(j)));
						lines.get(i).setB(findPointB(lines.get(i), lines.get(j)));
						points_i = lines.get(i).getSortedAB();
						lines.remove(j);
					}
				}
			}
			return lines;
		}
		double markerNormLength(){
			return MainData.distans(marker_norm[0], marker_norm[1], marker_norm[3], marker_norm[4]);
		}
		ArrayList <Line> getLines(){
			ArrayList <Line> lines = new ArrayList<>();
			lines.addAll(this.lines_dist.keySet());
			return lines;
		}
		boolean compareLineDistanses(Dist distans){
			for (Map.Entry<Line, Dist> dist: this.lines_dist.entrySet()) {
				Line line_i = dist.getKey();
				Dist dist_i = dist.getValue();
				if (dist_i.compareLine(distans) == true){
					return true;
				}
			}
			return false;
		}
		boolean compareLineDistanses(Dist distans, double k){
			for (Map.Entry<Line, Dist> dist: this.lines_dist.entrySet()) {
				Line line_i = dist.getKey();
				Dist dist_i = dist.getValue();
				if (dist_i.compareLine(distans, k) == true){
					return true;
				}
			}
			return false;
		}
		ArrayList <Line> findMatch(Shape other_shape){
			double k = this.marker.length() / other_shape.marker.length();
			ArrayList <Line> mached_lines = new ArrayList<>();
			if (k == 1){
				for (Map.Entry<Line, Dist> shape: other_shape.lines_dist.entrySet()) {
					Line line_i = shape.getKey();
					Dist dist_i = shape.getValue();
					if (this.compareLineDistanses(dist_i) == true)	mached_lines.add(line_i);
				}
			}else{
				for (Map.Entry<Line, Dist> shape: other_shape.lines_dist.entrySet()) {
					Line line_i = shape.getKey();
					Dist dist_i = shape.getValue();
					if (this.compareLineDistanses(dist_i, k) == true)	mached_lines.add(line_i);
				}
			}
			return mached_lines;
		}
		ArrayList <Line> setInPlace(Marker refMarker){
			double k = refMarker.length() / this.marker.length();
			ArrayList <Line> resultLines = new ArrayList<>();
			for (Line line: this.getLines()){
				Line resultline = line.copy();
				resultline.move(refMarker.getX() - this.marker.getX(), refMarker.getY() - this.marker.getY());
				if (k != 1)	resultline.scale(refMarker.p.x, refMarker.p.y, k);
				resultline.rotate(refMarker.p.x, refMarker.p.y, refMarker.calcRotation(this.marker.dir));
				resultLines.add(resultline);
			}
			return resultLines;
		}
		int [] findPointA(Line a, Line b){
			int [] pointA = {a.pa.x, a.pa.y};
			if (pointA[0] > a.pb.x || (pointA[0] == a.pb.x && pointA[1] > a.pb.y)){
				pointA[0] = a.pb.x;
				pointA[1] = a.pb.y;
			}
			if (pointA[0] > b.pa.x || (pointA[0] == b.pa.x && pointA[1] > b.pa.y)){
				pointA[0] = b.pa.x;
				pointA[1] = b.pa.y;
			}
			if (pointA[0] > b.pb.x || (pointA[0] == b.pb.x && pointA[1] > b.pb.y)){
				pointA[0] = b.pb.x;
				pointA[1] = b.pb.y;
			}
			return pointA;
		}
		int [] findPointB(Line a, Line b){
			int [] pointA = {a.pa.x, a.pa.y};
			if (pointA[0] < a.pb.x || (pointA[0] == a.pb.x && pointA[1] < a.pb.y)){
				pointA[0] = a.pb.x;
				pointA[1] = a.pb.y;
			}
			if (pointA[0] < b.pa.x || (pointA[0] == b.pa.x && pointA[1] < b.pa.y)){
				pointA[0] = b.pa.x;
				pointA[1] = b.pa.y;
			}
			if (pointA[0] < b.pb.x || (pointA[0] == b.pb.x && pointA[1] < b.pb.y)){
				pointA[0] = b.pb.x;
				pointA[1] = b.pb.y;
			}
			return pointA;
		}

	}
	public class NoMarkerException extends Exception {
		public NoMarkerException(String message) {
			super(message);
		}
	}
}