package grammar_graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.lang.Math;


final class Rule{
	String name;
	Shape initialshape;
	Shape finalshape;
	Rule(String name, ArrayList <Line> initialLines, ArrayList <Line> finalLines, 
		Marker initialmarker, Marker finalmarker) throws NoMarkerException, MarkerRemovingRule{
		if (initialmarker == null){
			throw new NoMarkerException("Please, add a marker on the left site of the rule");
		}
		this.name = name;
		this.initialshape = new Shape(initialLines, initialmarker, "Rule A site");
		ArrayList <Line> addedLines = RelativeComplement(finalLines, initialLines);
		if (finalmarker != null)
			this.finalshape = new Shape(addedLines, finalmarker);
		else{
			if (!addedLines.isEmpty())
				throw new MarkerRemovingRule();
			this.finalshape = new Shape("Rule B site");
		}
	}
	Rule(String name){
		this.name = name;
		this.initialshape = new Shape();
		this.finalshape = new Shape();
	}
	@Override
	public String toString(){
		StringJoiner info = new StringJoiner("");
		info.add(initialshape.markerToString(name, FileSaver.aSideTag));
		info.add(initialshape.linesDistToString(name, FileSaver.aSideTag));
		info.add(finalshape.markerToString(name, FileSaver.bSideTag));
		info.add(finalshape.linesDistToString(name, FileSaver.bSideTag));
		return info.toString();
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
			throw new NoMarkerException("Please add a marker to main panel\n");
		if (initialshape.marker == null)
			throw new NoMarkerException("Please add a marker to left side of rule\n");
		Shape inputshape = new Shape(panel.programData.lines, panel.programData.marker, "Input");

		ArrayList <Line> found_lines = initialshape.findMatch(inputshape);

		if (found_lines.size() == initialshape.lines_dist.size()){
			if (finalshape.marker != null){
				System.out.println("-------------------------------------------------");
				System.out.println("Input: " + panel.programData.marker.dir.toString());
				System.out.println("A: " + initialshape.marker.dir.toString());
				System.out.println("B: " + finalshape.marker.dir.toString());
				panel.programData.marker.rotateBasedOnDirSub(initialshape.marker.dir, finalshape.marker.dir);
				System.out.println("Output: " + panel.programData.marker.dir.toString());
				panel.programData.marker.move(initialshape.marker.p, finalshape.marker.p);
				try{
					panel.programData.marker.scale(this.findMarkerScaleParam());
				}catch (Exception e) {
					System.out.println(e.getMessage());
				}
				panel.programData.lines.addAll(finalshape.setInPlace(panel.programData.marker));
			}
			else{
				panel.programData.marker = null;
			}
			panel.repaint();
		}
	}
	double findMarkerScaleParam(){
		return 1.*finalshape.marker.r / (1.*initialshape.marker.r);
	}
	ArrayList <Line> RelativeComplement(ArrayList <Line> setA, ArrayList <Line> setB){
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

		Dist(Line line, double [] marker_norm){
			double [] line_coordinates = line.getDoubleCoordinates();
			this.ms_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker_norm[0], marker_norm[1]);
			this.ms_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker_norm[0], marker_norm[1]);
			this.ma_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker_norm[2], marker_norm[3]);
			this.ma_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker_norm[2], marker_norm[3]);
		}

		Dist(){
			this.ms_la = 0.0;
			this.ms_lb = 0.0;
			this.ma_la = 0.0;
			this.ma_lb = 0.0;
		}
		@Override
		public String toString(){
			StringJoiner info = new StringJoiner(" ");
			return info.add(Double.toString(ms_la)).add(Double.toString(ms_lb)).add(Double.toString(ma_la)).add(Double.toString(ma_lb)).toString();
		}
		boolean compareLine(Dist other_dist, double k){
			double precision = 0.00000000000001;
			
			if ((Math.abs(this.ms_lb - other_dist.ms_lb * k) < precision  &&
				 Math.abs(this.ma_la - other_dist.ma_la * k) < precision ) ||
				(Math.abs(this.ms_la - other_dist.ms_lb * k) < precision  &&  
				 Math.abs(this.ma_lb - other_dist.ma_la * k) < precision )){
				return true;
			}
			return false;
		}
		// return distans between the first point (A) of the line section and the middle of the marker
		double getMSLA(){
			return ms_la;
		}
		// return distans between the second point (B) of the line section and the middle of the marker
		double getMSLB(){
			return ms_lb;
		}
		// return distans between the first point (A) of the line section and the middle of the marker
		double getMALA(){
			return ma_la;
		}
		// return distans between the second point (B) of the line section and the middle of the marker
		double getMALB(){
			return ma_lb;
		}
	}
	private class Shape{
		Map <Line, Dist> lines_dist;
		double [] marker_norm;
		Marker marker;
		String name;
		Shape(ArrayList <Line> lines, Marker marker){
			this.marker = marker;
			this.lines_dist = new HashMap<Line, Dist>();
			this.marker_norm = marker.getNormalized();
			this.name = "Rule B site";
			for (Line line: lines){
				this.lines_dist.put(line.copy(), new Dist(line, marker_norm));
			}
		}
		Shape(ArrayList <Line> lines, Marker marker, String name){
			this.marker = marker;
			this.lines_dist = new HashMap<Line, Dist>();
			this.marker_norm = marker.getNormalized();
			this.name = name;
			int before = lines.size();
			do{
				before = lines.size();
				lines = groupLines(lines);
			}while (before != lines.size());
			for (Line line: lines){
				this.lines_dist.put(line.copy(), new Dist(line, marker_norm));
			}
		}
		Shape(String name){
			this.marker = null;
			this.lines_dist = new HashMap<Line, Dist>();
			this.marker_norm = null;
			this.name = name;
		}
		Shape(){
			this.marker_norm = new double [4];
			this.lines_dist = new HashMap<Line, Dist>();
		}
		String linesDistToString(String ruleName, String siteTag){
			StringJoiner info = new StringJoiner("");
			for (Line line_i: this.lines_dist.keySet()) {
				info.add("#").add(ruleName).add("\t").add(siteTag).add("\t").add(line_i.toString()).toString();
			}
			return info.toString();
		}
		String markerToString(String ruleName, String siteTag){
			StringJoiner info = new StringJoiner("");
			return info.add("#").add(ruleName).add("\t").add(siteTag).add("\t").add(marker.toString()).toString();
		}
		private String linesDistToString(){
			StringJoiner info = new StringJoiner("\n");
			for (Line line_i: this.lines_dist.keySet()) {
				info.add(line_i.toString());
			}
			return info.toString();
		}
		@Override
		public String toString(){
			StringJoiner info = new StringJoiner("\n");
			return info.add(marker.toString()).add(this.linesDistToString()).toString();
		}
		// Consider joining lines
		ArrayList <Line> groupLines(ArrayList <Line> shape_lines){
			ArrayList <Line> lines = new ArrayList<>();
			for (Line line: shape_lines)
				lines.add(line.copy());
			for (int i = 0; i < lines.size(); ++i){
				try{
					double [] funparam_i = lines.get(i).getFunctionParams();
					int [] points_i = lines.get(i).getSortedAB();
					for (int j = i + 1; j < lines.size(); ++j){
						try{
							double [] funparam_j = lines.get(j).getFunctionParams();
							int [] points_j = lines.get(j).getSortedAB();
							if (funparam_i[0] == funparam_j[0] && funparam_i[1] == funparam_j[1]
								&& points_i[0] <= points_j[0] && points_i[2] >= points_j[0] 
								&& points_i[1] >= points_j[1] && points_i[3] >= points_j[1]){
								lines.get(i).setA(findPointA(lines.get(i), lines.get(j)));
								lines.get(i).setB(findPointB(lines.get(i), lines.get(j)));
								points_i = lines.get(i).getSortedAB();
								lines.remove(j);
							}
						}catch(Line.NotALinearFunction error){
							continue;
						}
					}
				}catch(Line.NotALinearFunction error){
					int x_i = lines.get(i).pa.x;
					int [] points_i = lines.get(i).getSortedAB();
					for (int j = i + 1; j < lines.size(); ++j){
						if (!lines.get(j).isPartOfLinearFun()){
							int x_j = lines.get(j).pa.x;
							int [] points_j = lines.get(j).getSortedAB();
							if (x_i == x_j
								&& ((points_i[1] > points_j[1] && points_i[3] >= points_j[1])
									|| (points_i[1] < points_j[1] && points_i[3] <= points_j[1]))){
								lines.get(i).setA(findPointA(lines.get(i), lines.get(j)));
								lines.get(i).setB(findPointB(lines.get(i), lines.get(j)));
								points_i = lines.get(i).getSortedAB();
								lines.remove(j);
							}
						}
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
		boolean compareLineDistanses(Dist inputDistans, Line inputLine, double k){
			int i = 0;
			for (Map.Entry<Line, Dist> dist: this.lines_dist.entrySet()) {
				Line line_i = dist.getKey();
				Dist dist_i = dist.getValue();
				if (dist_i.compareLine(inputDistans, k) == true)	return true;
			}
			return false;
		}
		ArrayList <Line> compareLineAParam(Line inputLine){
			ArrayList <Line> ruleLinesWithSameA = new ArrayList <>();
			boolean isLinear = true;
			double [] params_input_line = new double [2];
			try{
				params_input_line = inputLine.getFunctionParamsOnGrid();
			}catch(Line.NotALinearFunction error){
				isLinear = false;
			}
			for (Line line_i: this.lines_dist.keySet()) {
				try{
					double [] params_rule_line_i = line_i.getFunctionParamsOnGrid();
					if (params_rule_line_i[0] == params_input_line[0])
						ruleLinesWithSameA.add(line_i);
				}catch(Line.NotALinearFunction error){
					// input line has the same format as rule line_i (x = val)
					if (!isLinear)
						ruleLinesWithSameA.add(line_i); 
				}
			}
			return ruleLinesWithSameA;
		}
		Line findSubLine(Line inputLine, Dist inputDist, double k, Marker inputMarker) throws PointDoesNotExist{
			ArrayList <Line> matchedRuleLines = compareLineAParam(inputLine);
			try{
				double [] params = inputLine.getFunctionParamsOnGrid();
				for (Line matchedRuleLine: matchedRuleLines){
					if (matchedRuleLine.length() / k < inputLine.length()){
						int [] markerCenter = {inputMarker.getax(), inputMarker.getay()};
						double distAs = this.lines_dist.get(matchedRuleLine).getMALA();
						double distBs = this.lines_dist.get(matchedRuleLine).getMALB();
						int x_b = findXOnLine(params[0], params[1], distBs, k, markerCenter);
						int y_b = findYOnLine(x_b, params[0], params[1]);
						int x_a = findXOnLine(params[0], params[1], distAs, k, markerCenter);
						int y_a = findYOnLine(x_a, params[0], params[1]);
						return new Line(x_a*MainData.grid_size, y_a*MainData.grid_size, x_b*MainData.grid_size, y_b*MainData.grid_size);
					}
				}
			}catch(Line.NotALinearFunction error){
				int x = error.getX();
				for (Line matchedRuleLine: matchedRuleLines){
					if (matchedRuleLine.length() / k < inputLine.length()){
						int [] markerCenter = {inputMarker.getax(), inputMarker.getay()};
						double distAs = this.lines_dist.get(matchedRuleLine).getMALA();
						double distBs = this.lines_dist.get(matchedRuleLine).getMALB();
						int y_b = findYOnLineForNonLinearFunction(x, distBs, k, markerCenter);
						int y_a = findYOnLineForNonLinearFunction(x, distAs, k, markerCenter);
						return new Line(x*MainData.grid_size, y_a*MainData.grid_size, x*MainData.grid_size, y_b*MainData.grid_size);
					}
				}
			}
			throw new PointDoesNotExist();
		}
		boolean isInt(double value){
			return value % 1 < 1e-8 || 1. - (value % 1) < 1e-8;
		}
		int findYOnLineForNonLinearFunction(double x, double d, double k, int [] Mc) throws PointDoesNotExist{
			double delta = -k*k * Mc[0]* Mc[0] + 2 * k*k * x * Mc[0] - k*k * x*x + d*d;
			if (delta < 0)	throw new PointDoesNotExist("Delta is less than zero: " + delta);

			double y1 = -(Math.sqrt(delta) - k * Mc[1])/k;
			double y2 =  (Math.sqrt(delta) + k * Mc[1])/k;
			
			if (isInt(y1)) 
				return (int)y1;
			if (isInt(y2)) 
				return (int)y2;

			throw new PointDoesNotExist("Found x_1 [" + y1 + "] and x_2 [" + y2 + "] do not lay on the grid for k = " + k);
		}
		int findXOnLine(double a, double b, double d, double k, int [] Mc) throws PointDoesNotExist{
			double delta = (1. + a*a) * d*d - k*k * Mc[1]*Mc[1]*1. + (2. * b * k*k + 2 * a * k*k * Mc[0]*1.) * Mc[1]*1. - a*a * k*k * Mc[0]*Mc[0]*1. - 2.* a * b * k*k * Mc[0]*1. - b*b * k*k;
			if (delta < 0)	throw new PointDoesNotExist("Delta is less than zero: " + delta);

			double x1 = -(a * b * k - k * Mc[0]*1. - a * k * Mc[1]*1. + Math.sqrt(delta)) / ((a*a + 1.) * k);
			double x2 = (-a * b * k + k * Mc[0]*1. + a * k * Mc[1]*1. + Math.sqrt(delta)) / ((a*a + 1.) * k);
			
			if (isInt(x1)) 
				return (int)x1;
			if (isInt(x2)) 
				return (int)x2;

			throw new PointDoesNotExist("Found x_1 [" + x1 + ", " + findYOnLine((int)x1, a, b) + "] and x_2 [" + x2 + ", " + findYOnLine((int)x2, a, b) + "] do not lay on the grid for k = " + k);
		}
		int findYOnLine(int x, double a, double b) throws PointDoesNotExist{
			double y = a*x + b;
			if (isInt(y))	return (int)y;
			throw new PointDoesNotExist("Found y (" + y + ") does not lay on the grid");
		}
		ArrayList <Line> findMatch(Shape inputShape){
			double k = this.marker.length() / inputShape.marker.length();
			ArrayList <Line> mached_lines = new ArrayList<>();
			for (Map.Entry<Line, Dist> inShape: inputShape.lines_dist.entrySet()) {
				Line inLine_i = inShape.getKey();
				Dist inDist_i = inShape.getValue();
				if (this.compareLineDistanses(inDist_i, inLine_i, k) == true)	mached_lines.add(inLine_i);
				// check sublines
				else{
					try{
						Line newLine = findSubLine(inLine_i, inDist_i, k, inputShape.marker);
						newLine.changeColor(MainData.default_check_marker_color);
						mached_lines.add(inLine_i);
					}
					catch(PointDoesNotExist p){;
						// System.out.println(p.getMessage());
					}
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
	public class MarkerRemovingRule extends Exception {
		public MarkerRemovingRule(String message) {
			super(message);
		}
		public MarkerRemovingRule() {
			super("The rule was designed to remove the marker from input.\nIt will not add any of definded line.\n");
		}
	}
	public class PointDoesNotExist extends Exception {
		public PointDoesNotExist(String message){
			super(message);
		}
		public PointDoesNotExist(){
			super("Point was not found");
		}
	}
}