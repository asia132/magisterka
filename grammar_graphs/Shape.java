package grammar_graphs;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.StringJoiner;

class Shape{
	Map <Line, Dist> lines_dist;
	double [] marker_norm;
	Marker marker;
	String name;
	boolean needsToBeMirrored;
	int same = 0;
	Shape(ArrayList <Line> lines, Marker marker){
		this.marker = marker;
		this.lines_dist = new HashMap<Line, Dist>();
		this.name = "Rule B site";
		for (Line line: lines){
			this.lines_dist.put(line.copy(), new Dist(line, marker));
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
			this.lines_dist.put(line.copy(), new Dist(line, marker));
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
	ArrayList <Line> setInPlace(Marker inputMarker, Marker initialMarker){
		double k = inputMarker.length() / initialMarker.length();
		ArrayList <Line> resultLines = new ArrayList<>();
		for (Line line: this.getLines()){
			Line resultline = line.copy();
			resultline.move(inputMarker.getX() - initialMarker.getX(), inputMarker.getY() - initialMarker.getY());
			resultline.rotate(inputMarker.p.x, inputMarker.p.y, inputMarker.calcRotation(initialMarker.dir));
			if (k != 1)		resultline.scale(inputMarker.p.x, inputMarker.p.y, k);
			if (this.needsToBeMirrored && (inputMarker.dir.equals(Direct.N) || inputMarker.dir.equals(Direct.S)))	resultline.mirrorX(inputMarker.p.x);
			if (this.needsToBeMirrored && (inputMarker.dir.equals(Direct.E) || inputMarker.dir.equals(Direct.W)))	resultline.mirrorY(inputMarker.p.y);
			resultLines.add(resultline);
		}
		return resultLines;
	}
	ArrayList <Line> findMatch(Shape inputShape){
		double k = this.marker.length() / inputShape.marker.length();
		ArrayList <Line> mached_lines = new ArrayList<>();
		this.same = 0;
		for (Map.Entry<Line, Dist> inShape: inputShape.lines_dist.entrySet()) {
			Line inLine_i = inShape.getKey();
			Dist inDist_i = inShape.getValue();
			Referral ref = this.compareLineDistanses(inDist_i, inLine_i, k);
			if (ref != null){
				this.same += ref.getNum();
				// System.out.print("\t" + ref.toString() + "\t" + same + "\t");
				System.out.print("Standard Found:\t");
				inLine_i.print();
				mached_lines.add(inLine_i);
			}
			// check sublines
			else{
				try{
					Line newLine = findSubLine(inLine_i, inDist_i, k, inputShape.marker);
					mached_lines.add(inLine_i);
				}
				catch(PointDoesNotExist p){;
				}
			}
		}
		// System.out.println("\n" + this.same*1. + " "  + lines_dist.size());
		inputShape.needsToBeMirrored = (this.same*1. < lines_dist.size());
		return mached_lines;
	}
	Line findSubLine(Line inputLine, Dist inputDist, double k, Marker inputMarker) throws PointDoesNotExist{
		ArrayList <Line> matchedRuleLines = compareLineAParam(inputLine);
		Line line = null;
		try{
			double [] params = inputLine.getFunctionParamsOnGrid();
			for (Line matchedRuleLine: matchedRuleLines){
				if (matchedRuleLine.length() / k < inputLine.length()){
					int [] markerCenter = {inputMarker.getax(), inputMarker.getay()};
					double distAs = this.lines_dist.get(matchedRuleLine).getMALA();
					double distBs = this.lines_dist.get(matchedRuleLine).getMALB();
					try{
						int x_b = findXOnLine(params[0], params[1], distBs, k, markerCenter);
						int y_b = findYOnLine(x_b, params[0], params[1]);
						int x_a = findXOnLine(params[0], params[1], distAs, k, markerCenter);
						int y_a = findYOnLine(x_a, params[0], params[1]);

						double dist_b = this.lines_dist.get(matchedRuleLine).mb_la;
						double mb_la = MainData.distans(x_a, y_a, inputMarker.getbx()*1., inputMarker.getby()*1.);
						double mb_lb = MainData.distans(x_b, y_b, inputMarker.getbx()*1., inputMarker.getby()*1.);

						double dist_d = this.lines_dist.get(matchedRuleLine).md_la;
						double md_la = MainData.distans(x_a, y_a, inputMarker.getdx()*1., inputMarker.getdy()*1.);
						double md_lb = MainData.distans(x_b, y_b, inputMarker.getdx()*1., inputMarker.getdy()*1.);
						// System.out.print("\nlinear subline " + (mb_la*k) + " " + (mb_lb*k) + " " + (dist_b));
						// System.out.print("linear subline " + (md_la*k) + " " + (md_lb*k) + " " + (dist_d) + "\t");

						if ((Math.abs(dist_b-mb_la*k) < 1e-13 && Math.abs(dist_d-md_la*k) < 1e-13) || 
							(Math.abs(dist_b-mb_lb*k) < 1e-13 && Math.abs(dist_d-md_lb*k) < 1e-13)){
							this.same += 1;
							System.out.print("Linear Found:\t");
							new Line(x_a*MainData.grid_size, y_a*MainData.grid_size, x_b*MainData.grid_size, y_b*MainData.grid_size).print();
							System.out.println(Referral.SAME.toString());
							return new Line(x_a*MainData.grid_size, y_a*MainData.grid_size, x_b*MainData.grid_size, y_b*MainData.grid_size);

						}else{
							System.out.println(Referral.DIFFERENT.toString());
							System.out.println("linear skip.");
							line = new Line(x_a*MainData.grid_size, y_a*MainData.grid_size, x_b*MainData.grid_size, y_b*MainData.grid_size);
						}
					}catch (PointDoesNotExist pointDoesNotExist) {
						continue;
					}
				}
			}
		}catch(Line.NotALinearFunction error){
			int x = error.getX();
			for (Line matchedRuleLine: matchedRuleLines){
				if (matchedRuleLine.length() / k < inputLine.length()){
					int [] markerCenter = {inputMarker.getax(), inputMarker.getay()};
					double distAs = this.lines_dist.get(matchedRuleLine).getMALA();
					double distBs = this.lines_dist.get(matchedRuleLine).getMALB();
					try{
						int y_b = findYOnLineForNonLinearFunction(x, distBs, k, markerCenter);
						int y_a = findYOnLineForNonLinearFunction(x, distAs, k, markerCenter);

						double dist_b = this.lines_dist.get(matchedRuleLine).mb_la;
						double mb_la = MainData.distans(x, y_a, inputMarker.getbx()*1., inputMarker.getby()*1.);
						double mb_lb = MainData.distans(x, y_b, inputMarker.getbx()*1., inputMarker.getby()*1.);

						double dist_d = this.lines_dist.get(matchedRuleLine).md_la;
						double md_la = MainData.distans(x, y_a, inputMarker.getdx()*1., inputMarker.getdy()*1.);
						double md_lb = MainData.distans(x, y_b, inputMarker.getdx()*1., inputMarker.getdy()*1.);
						// System.out.println("\nnot linear subline " + (mb_la*k) + " " + (mb_lb*k) + " " + (dist_b));
						// System.out.print("not linear subline " + (md_la*k) + " " + (md_lb*k) + " " + (dist_d) + "\t");

						if ((Math.abs(dist_b-mb_la*k) < 1e-13 && Math.abs(dist_d-md_la*k) < 1e-13) || 
							(Math.abs(dist_b-mb_lb*k) < 1e-13 && Math.abs(dist_d-md_lb*k) < 1e-13)){
							this.same += 1;
							System.out.println(Referral.SAME.toString());
							System.out.print("Not linear Found:\t");
							new Line(x*MainData.grid_size, y_a*MainData.grid_size, x*MainData.grid_size, y_b*MainData.grid_size).print();
							return new Line(x*MainData.grid_size, y_a*MainData.grid_size, x*MainData.grid_size, y_b*MainData.grid_size);
						}else{
							System.out.println(Referral.DIFFERENT.toString());
							System.out.println("Not linear skip.");
							line = new Line(x*MainData.grid_size, y_a*MainData.grid_size, x*MainData.grid_size, y_b*MainData.grid_size);
						}
					}catch (PointDoesNotExist pointDoesNotExist) {
						continue;
					}
				}
			}
		}
		if (line != null){
			// System.out.println(Referral.SAME.to/String());
			System.out.print("Subline Found:\t");
			line.print();
			this.same++;
			return line;
		}
		throw new PointDoesNotExist();
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
		if (marker != null)	return info.add(marker.toString()).add(this.linesDistToString()).toString();
		return info.add(this.linesDistToString()).toString();
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
	Referral compareLineDistanses(Dist inputDistans, Line inputLine, double k){
		int i = 0;
		boolean different = false;
		for (Map.Entry<Line, Dist> dist: this.lines_dist.entrySet()) {
			Line line_i = dist.getKey();
			Dist dist_i = dist.getValue();
			Referral ref = dist_i.compareLine(inputDistans, k);
			if (ref != null){
				if (ref == Referral.SAME)	return ref;
				else different = true;
			}
		}
		if (different) return Referral.DIFFERENT;
		return null;
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
	private class Dist{
		double ms_la;
		double ms_lb;

		double ma_la;
		double ma_lb;

		double mb_la;
		double mb_lb;

		double md_la;
		double md_lb;

		Dist(Line line, Marker marker){
			double [] line_coordinates = line.getDoubleCoordinates();
			this.ms_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.p.x*1., marker.p.y*1.);
			this.ms_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.p.x*1., marker.p.y*1.);
			this.ma_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.getax()*1., marker.getay()*1.);
			this.ma_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.getax()*1., marker.getay()*1.);

			this.mb_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.getbx()*1., marker.getby()*1.);
			this.mb_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.getbx()*1., marker.getby()*1.);

			this.md_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.getdx()*1., marker.getdy()*1.);
			this.md_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.getdx()*1., marker.getdy()*1.);
		}
		Dist(){
			this.ms_la = 0.0;
			this.ms_lb = 0.0;
			this.ma_la = 0.0;
			this.ma_lb = 0.0;

			this.mb_la = 0.0;
		}
		@Override
		public String toString(){
			StringJoiner info = new StringJoiner(" ");
			return info.add(Double.toString(ms_la)).add(Double.toString(ms_lb)).add(Double.toString(ma_la)).add(Double.toString(ma_lb)).toString();
		}
		Referral compareLine(Dist inputDist, double k){
			double precision = 1e-13;

			if ((Math.abs(this.ms_lb - inputDist.ms_lb * k) < precision  &&
				 Math.abs(this.ma_la - inputDist.ma_la * k) < precision ) ||
				(Math.abs(this.ms_la - inputDist.ms_lb * k) < precision  &&  
				 Math.abs(this.ma_lb - inputDist.ma_la * k) < precision )){
				// System.out.print("\nstandard line : " + this.mb_la + " " + this.mb_lb + " " + (inputDist.mb_la*k));
				// System.out.print("\nstandard line : " + this.md_la + " " + this.md_lb + " " + (inputDist.md_la*k));
				if ((Math.abs(this.mb_la-inputDist.mb_la*k) < precision && Math.abs(this.md_la-inputDist.md_la*k) < precision) ||
					(Math.abs(this.mb_lb-inputDist.mb_la*k) < precision && Math.abs(this.md_lb-inputDist.md_la*k) < precision)){
					return Referral.SAME;
				}
				return Referral.DIFFERENT;
			}
			return null;
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
	public class PointDoesNotExist extends Exception {
		public PointDoesNotExist(String message){
			super(message);
		}
		public PointDoesNotExist(){
			super("Point was not found");
		}
	}
}