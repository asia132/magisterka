package grammar_graphs;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.lang.Math;

class Shape{
	Map <Line, Dist> lines_dist;
	double [] marker_norm;
	Marker marker;
	String name;
	boolean needsToBeMirrored;
	int same = 0;

	double precision = 1e-12;

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
	int mirrorX(int x, int xc){
		return 2*xc - x;
	}
	ArrayList <Line> findMatch(Shape inputShape) throws NotAllRuleLinesRecognized{
		double k = this.marker.length() / inputShape.marker.length();

		ArrayList <Line> ruleLines = new ArrayList<>(this.lines_dist.keySet());
		Line [][] matchedLines = new Line[this.lines_dist.size()][2];

		ArrayList <Line> mached_lines = new ArrayList<>();

		Marker tempMarker = inputShape.marker.copy();
		tempMarker.rotateBasedOnDirSub(inputShape.marker.dir, this.marker.dir, false);

		for (Map.Entry<Line, Dist> inShape: inputShape.lines_dist.entrySet()) {
			Line inLine_i = inShape.getKey();
			Dist inDist_i = inShape.getValue();

			Line transLine_i = inLine_i.copy();
			double alpha = -inputShape.marker.calcRotation(this.marker.dir);
			transLine_i.rotate(inputShape.marker.p.x, inputShape.marker.p.y, alpha);
			
			double [] params_i = null;
			try{
				params_i = transLine_i.getFunctionParamsOnGrid();
			}catch(Line.NotALinearFunction error){;}

			for (int i = 0; i < this.lines_dist.size(); i++) {
				Line line_r = ruleLines.get(i);
				Dist dist_j = this.lines_dist.get(line_r);
				double [] params_j = null;

				try{
					params_j = line_r.getFunctionParamsOnGrid();
				}catch(Line.NotALinearFunction error){;}

				if (params_i != null && params_j != null && params_i[0] == params_j[0]){ // poziome i skośne
					Referral ref = dist_j.compareLine(inDist_i, k);
					if (ref != null){ // odcinki dla poziomych i skośnych
						if (ref == Referral.DUPLICATED){
							matchedLines[i][0] = inLine_i;
							matchedLines[i][1] = inLine_i;
						}
						else{
							if (ref == Referral.SAME){
								matchedLines[i][0] = inLine_i;
							}else{ // ref == Referral.DIFFERENT
								matchedLines[i][1] = inLine_i;
							}
						}
					}
					if (line_r.length() / k < transLine_i.length()) { // pododcinki dla poziomych i skośnych

						if (params_i[0] == 0 && Math.abs(this.marker.p.y - line_r.pa.y)/k != Math.abs(inputShape.marker.p.y - transLine_i.pa.y)){ // dodatkowe sprawdzenie dla poziomych
							continue;
						}

						double [] markerCenter = {inputShape.marker.p.x*1., inputShape.marker.p.y*1.};
						double distAs = this.lines_dist.get(line_r).getMSLA();
						double distBs = this.lines_dist.get(line_r).getMSLB();

						double [] markerAPoint = {tempMarker.getax()*1., tempMarker.getay()*1.};
						double distAp = this.lines_dist.get(line_r).getMALA();
						double distBp = this.lines_dist.get(line_r).getMALB();

						Line newLine = null;

						try{
							int [] xrange = new int[2];
							if (transLine_i.pa.x < transLine_i.pb.x){
								xrange[0] = transLine_i.pa.x;
								xrange[1] = transLine_i.pb.x;								
							}else{

								xrange[0] = transLine_i.pb.x;	
								xrange[1] = transLine_i.pa.x;
							}
							
							int x1s_a = fixdX1OnLine(params_i[0], params_i[1], k, distAs, markerCenter);
							int x2s_a = fixdX2OnLine(params_i[0], params_i[1], k, distAs, markerCenter);
							
							int x1s_b = fixdX1OnLine(params_i[0], params_i[1], k, distBs, markerCenter);
							int x2s_b = fixdX2OnLine(params_i[0], params_i[1], k, distBs, markerCenter);


							int x1a_a = fixdX1OnLine(params_i[0], params_i[1], k, distAp, markerAPoint);
							int x2a_a = fixdX2OnLine(params_i[0], params_i[1], k, distAp, markerAPoint);

							int x1a_b = fixdX1OnLine(params_i[0], params_i[1], k, distBp, markerAPoint);
							int x2a_b = fixdX2OnLine(params_i[0], params_i[1], k, distBp, markerAPoint);

							
							int y1_a = findYOnLine(x1s_a, params_i[0], params_i[1]);
							int y2_a = findYOnLine(x2s_a, params_i[0], params_i[1]);
							int y1_b = findYOnLine(x1s_b, params_i[0], params_i[1]);
							int y2_b = findYOnLine(x2s_b, params_i[0], params_i[1]);

							boolean var_x1a = false;

							if (xrange[0] <= x1s_a && x1s_a <= xrange[1] && (x1s_a == x1a_a || x1s_a == x1a_b || x1s_a == x2a_a || x1s_a == x2a_b)){
								if (xrange[0] <= x1s_b && x1s_b <= xrange[1] && (x1s_b == x1a_a || x1s_b == x1a_b || x1s_b == x2a_a || x1s_b == x2a_b) && line_r.small_length() == (MainData.distans(x1s_a, y1_a, x1s_b, y1_b) * k)){
									newLine = new Line(x1s_a*MainData.grid_size, y1_a*MainData.grid_size, x1s_b*MainData.grid_size, y1_b*MainData.grid_size);
									var_x1a = true;
								}else if (xrange[0] <= x2s_b && x2s_b <= xrange[1] && (x2s_b == x1a_a || x2s_b == x1a_b || x2s_b == x2a_a || x2s_b == x2a_b) && line_r.small_length() == (MainData.distans(x1s_a, y1_a, x2s_b, y2_b) * k)){
									newLine = new Line(x1s_a*MainData.grid_size, y1_a*MainData.grid_size, x2s_b*MainData.grid_size, y2_b*MainData.grid_size);
									var_x1a = true;
								}
							}
							if (var_x1a == false && xrange[0] <= x2s_a && x2s_a <= xrange[1] && (x2s_a == x1a_a || x2s_a == x1a_b || x2s_a == x2a_a || x2s_a == x2a_b)){
								if (xrange[0] <= x1s_b && x1s_b <= xrange[1] && (x1s_b == x1a_a || x1s_b == x1a_b || x1s_b == x2a_a || x1s_b == x2a_b) && line_r.small_length() == (MainData.distans(x2s_a, y2_a, x1s_b, y1_b) * k)){
									newLine = new Line(x2s_a*MainData.grid_size, y2_a*MainData.grid_size, x1s_b*MainData.grid_size, y1_b*MainData.grid_size);
									var_x1a = true;
								}else{
									if (xrange[0] <= x2s_b && x2s_b <= xrange[1] && (x2s_b == x1a_a || x2s_b == x1a_b || x2s_b == x2a_a || x2s_b == x2a_b) && line_r.small_length() == (MainData.distans(x2s_a, y2_a, x2s_b, y2_b) * k)){
										newLine = new Line(x2s_a*MainData.grid_size, y2_a*MainData.grid_size, x2s_b*MainData.grid_size, y2_b*MainData.grid_size);
										var_x1a = true;
									}else{
										continue;
									}
								}
							}else if (var_x1a == false){
								continue;
							}
							newLine.rotate(inputShape.marker.p.x, inputShape.marker.p.y, -alpha);

							if (!(checkY(newLine.pa.y, inLine_i) && checkY(newLine.pb.y, inLine_i))){ continue;}

							Referral subRef = checkMirroringSide(k, inputShape.marker, line_r, newLine.pa.x, newLine.pb.x, newLine.pa.y, newLine.pb.y);
							if (subRef == Referral.SAME){
								matchedLines[i][0] = newLine;
							}else{
								if (subRef == Referral.DIFFERENT){
									matchedLines[i][1] = newLine;
								}else{ // subRef == Referral.DUPLICATED
									matchedLines[i][0] = newLine;
									matchedLines[i][1] = newLine;		
								}
							}							
						}catch (PointDoesNotExist pointDoesNotExist) {
							continue;
						}
					}
				} else if (params_i == null && params_j == null){ // pionowe
					Referral ref = dist_j.compareLine(inDist_i, k);
					if (ref != null){ // odcinki pionowe
						if (ref == Referral.DUPLICATED){
							matchedLines[i][0] = inLine_i;
							matchedLines[i][1] = inLine_i;
						}
						else{
							if (ref == Referral.SAME){
								matchedLines[i][0] = inLine_i;
							}else{ // ref == Referral.DIFFERENT
								matchedLines[i][1] = inLine_i;
							}
						}
					}
					int x = transLine_i.pa.x;
					if (line_r.length() / k < transLine_i.length()) { // pododcinki pionowe

						if (Math.abs(this.marker.p.x - line_r.pa.x)/k != Math.abs(inputShape.marker.p.x - x)){ // dodatkowe sprawdzenie dla poziomych
							continue;
						}

						double [] markerCenter = {inputShape.marker.p.x*1., inputShape.marker.p.y*1.};
						double distAs = this.lines_dist.get(line_r).getMSLA();
						double distBs = this.lines_dist.get(line_r).getMSLB();

						double [] markerAPoint = {tempMarker.getax()*1., tempMarker.getay()*1.};
						double distAp = this.lines_dist.get(line_r).getMALA();
						double distBp = this.lines_dist.get(line_r).getMALB();


						int [] yrange = new int[2];
						if (transLine_i.pa.y < transLine_i.pb.y){
							yrange[0] = transLine_i.pa.y;
							yrange[1] = transLine_i.pb.y;								
						}else{

							yrange[0] = transLine_i.pb.y;	
							yrange[1] = transLine_i.pa.y;
						}

						try{

							int y1_a = findY1OnLineForNonLinearFunction(x, k, distAs, markerCenter);
							int y2_a = findY2OnLineForNonLinearFunction(x, k, distAs, markerCenter);

							int y1_b = findY1OnLineForNonLinearFunction(x, k, distBs, markerCenter);
							int y2_b = findY2OnLineForNonLinearFunction(x, k, distBs, markerCenter);

							int y1Aa_a = findY1OnLineForNonLinearFunction(x, k, distAp, markerAPoint);
							int y2Aa_a = findY2OnLineForNonLinearFunction(x, k, distAp, markerAPoint);
							int y1Aa_b = findY1OnLineForNonLinearFunction(x, k, distBp, markerAPoint);
							int y2Aa_b = findY2OnLineForNonLinearFunction(x, k, distBp, markerAPoint);

							boolean var_x1a = false;

							Line newLine = null;							

							if (yrange[0] <= y1_a && y1_a <= yrange[1] && (y1_a == y1Aa_a || y1_a == y1Aa_b || y1_a == y2Aa_a || y1_a == y2Aa_b)){
								if (yrange[0] <= y1_b && y1_b <= yrange[1] && (y1_b == y1Aa_a || y1_b == y1Aa_b || y1_b == y2Aa_a || y1_b == y2Aa_b) && line_r.small_length() == (MainData.distans(x, y1_a, x, y1_b) * k)){
									newLine = new Line(x*MainData.grid_size, y1_a*MainData.grid_size, x*MainData.grid_size, y1_b*MainData.grid_size);
									var_x1a = true;
								}else if (yrange[0] <= y2_b && y2_b <= yrange[1] && (y2_b == y1Aa_a || y2_b == y1Aa_b || y2_b == y2Aa_a || y2_b == y2Aa_b) && line_r.small_length() == (MainData.distans(x, y1_a, x, y2_b) * k)){
									newLine = new Line(x*MainData.grid_size, y1_a*MainData.grid_size, x*MainData.grid_size, y2_b*MainData.grid_size);
									var_x1a = true;
								}
							}
							if (var_x1a == false && yrange[0] <= y2_a && y2_a <= yrange[1] && (y2_a == y1Aa_a || y2_a == y1Aa_b || y2_a == y2Aa_a || y2_a == y2Aa_b)){
								if (yrange[0] <= y1_b && y1_b <= yrange[1] && (y1_b == y1Aa_a || y1_b == y1Aa_b || y1_b == y2Aa_a || y1_b == y2Aa_b) && line_r.small_length() == (MainData.distans(x, y2_a, x, y1_b) * k)){
									newLine = new Line(x*MainData.grid_size, y2_a*MainData.grid_size, x*MainData.grid_size, y1_b*MainData.grid_size);
									var_x1a = true;
								}else{
									if (yrange[0] <= y2_b && y2_b <= yrange[1] && (y2_b == y1Aa_a || y2_b == y1Aa_b || y2_b == y2Aa_a || y2_b == y2Aa_b) && line_r.small_length() == (MainData.distans(x, y2_a, x, y2_b) * k)){
										newLine = new Line(x*MainData.grid_size, y2_a*MainData.grid_size, x*MainData.grid_size, y2_b*MainData.grid_size);
										var_x1a = true;
									}else{
										continue;
									}
								}
							}else if (var_x1a == false){
								continue;
							}

							newLine.rotate(inputShape.marker.p.x, inputShape.marker.p.y, -alpha);
							if (!(checkX(newLine.pa.x, inLine_i) && checkX(newLine.pb.x, inLine_i))){		continue;}

							Referral subRef = checkMirroringSide(k, inputShape.marker, line_r, newLine.pa.x, newLine.pb.x, newLine.pa.y, newLine.pb.y);
							if (subRef == Referral.SAME){
								matchedLines[i][0] = newLine;
							}else{
								if (subRef == Referral.DIFFERENT){
									matchedLines[i][1] = newLine;
								}else{ // subRef == Referral.DUPLICATED
									matchedLines[i][0] = newLine;
									matchedLines[i][1] = newLine;		
								}
							}
						}catch (PointDoesNotExist pointDoesNotExist1) {
								continue;
						}
					}
				}
			}
		}
	//
		this.same = 0;
		int same_counter = matchedLines.length;

		ArrayList <Integer> checkItAgain = new ArrayList <>();
		for (int i = 0; i < matchedLines.length; ++i){
			if (matchedLines[i][0] != null){
				if (matchedLines[i][1] == null){
					this.same++;
					mached_lines.add(matchedLines[i][0]);
				}else{
					checkItAgain.add(i);
					same_counter--;
				}
			}else{
				if (matchedLines[i][1] == null){;
					// throw new NotAllRuleLinesRecognized("No lines recognized for " + ruleLines.get(i));
				}else{
					mached_lines.add(matchedLines[i][1]);
				}
			} 
		}
		if ((this.same + checkItAgain.size()) == matchedLines.length){
			for (Integer i: checkItAgain) {
				mached_lines.add(matchedLines[i][0]);
			}
		}else{
			for (Integer i: checkItAgain) {
				mached_lines.add(matchedLines[i][1]);
			}
		}

		if (this.same != 0 && this.same != same_counter){
			throw new NotAllRuleLinesRecognized("The lines was not found on the same side");
		}

		inputShape.needsToBeMirrored = !(this.same == same_counter);
		return mached_lines;
	}
	boolean checkY(int y, Line line){
		return	((line.pa.y <= y && y <= line.pb.y) ||
				 (line.pb.y <= y && y <= line.pa.y));
	}
	boolean checkX(int x, Line line){
		return	((line.pa.x <= x && x <= line.pb.x) ||
				 (line.pb.x <= x && x <= line.pa.x));
	}
	Referral checkMirroringSide(double k, Marker marker, Line line, int x_a, int x_b, int y_a, int y_b){
		double precision = 1e-13;

		double dist_b = this.lines_dist.get(line).mb_la;
		double mb_la = MainData.distans(x_a, y_a, marker.getbx()*1., marker.getby()*1.);
		double mb_lb = MainData.distans(x_b, y_b, marker.getbx()*1., marker.getby()*1.);

		double dist_d = this.lines_dist.get(line).md_la;
		double md_la = MainData.distans(x_a, y_a, marker.getdx()*1., marker.getdy()*1.);
		double md_lb = MainData.distans(x_b, y_b, marker.getdx()*1., marker.getdy()*1.);

		if (Math.abs(mb_la - md_lb) < precision && Math.abs(md_la - mb_lb) < precision){
			return Referral.DUPLICATED;
		}

		if ((Math.abs(dist_b-mb_la*k) < precision && Math.abs(dist_d-md_la*k) < precision) || 
			(Math.abs(dist_b-mb_lb*k) < precision && Math.abs(dist_d-md_lb*k) < precision)){
						
			return Referral.SAME;
		}
		return Referral.DIFFERENT;
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
	static ArrayList <Line> groupLines(ArrayList <Line> shape_lines){
		ArrayList <Line> lines = new ArrayList<>();
		for (Line line: shape_lines)
			lines.add(line.copy());
		for (int i = 0; i < lines.size(); ++i){
			try{
				double [] funparam_i = lines.get(i).getFunctionParams();
				int [] points_i = lines.get(i).getSortedAB();
				for (int j = i + 1; j < lines.size(); ++j){
					try{
						double [] funparam_j = lines.get(j).getFunctionParams(); // poziome i skośne
						int [] points_j = lines.get(j).getSortedAB();
						if (funparam_i[0] == funparam_j[0] && funparam_i[1] == funparam_j[1]){
							if (((lines.get(j).pa.x <= lines.get(i).pa.x && lines.get(i).pa.x <= lines.get(j).pb.x) ||
								 (lines.get(j).pb.x <= lines.get(i).pa.x && lines.get(i).pa.x <= lines.get(j).pa.x) ||
								 (lines.get(j).pa.x <= lines.get(i).pb.x && lines.get(i).pb.x <= lines.get(j).pb.x) ||
								 (lines.get(j).pb.x <= lines.get(i).pb.x && lines.get(i).pb.x <= lines.get(j).pa.x)) ||
								((lines.get(i).pa.x <= lines.get(j).pa.x && lines.get(j).pa.x <= lines.get(i).pb.x) ||
								 (lines.get(i).pb.x <= lines.get(j).pa.x && lines.get(j).pa.x <= lines.get(i).pa.x) ||
								 (lines.get(i).pa.x <= lines.get(j).pb.x && lines.get(j).pb.x <= lines.get(i).pb.x) ||
								 (lines.get(i).pb.x <= lines.get(j).pb.x && lines.get(j).pb.x <= lines.get(i).pa.x))){
								int [] a = findPointA(lines.get(i), lines.get(j));
								int [] b = findPointB(lines.get(i), lines.get(j));
								lines.get(i).setA(a);
								lines.get(i).setB(b);
								points_i = lines.get(i).getSortedAB();
								lines.remove(j);
							}
						}
					}catch(Line.NotALinearFunction error){
						continue;
					}
				}
			}catch(Line.NotALinearFunction error){
				int x_i = lines.get(i).pa.x;
				for (int j = i + 1; j < lines.size(); ++j){ // pionowe
					if (!lines.get(j).isPartOfLinearFun()){
						int x_j = lines.get(j).pa.x;
						if (x_i == x_j){
							if (((lines.get(j).pa.y <= lines.get(i).pa.y && lines.get(i).pa.y <= lines.get(j).pb.y) ||
								 (lines.get(j).pb.y <= lines.get(i).pa.y && lines.get(i).pa.y <= lines.get(j).pa.y) ||
								 (lines.get(j).pa.y <= lines.get(i).pb.y && lines.get(i).pb.y <= lines.get(j).pb.y) ||
								 (lines.get(j).pb.y <= lines.get(i).pb.y && lines.get(i).pb.y <= lines.get(j).pa.y)) ||
								((lines.get(i).pa.y <= lines.get(j).pa.y && lines.get(j).pa.y <= lines.get(i).pb.y) ||
								 (lines.get(i).pb.y <= lines.get(j).pa.y && lines.get(j).pa.y <= lines.get(i).pa.y) ||
								 (lines.get(i).pa.y <= lines.get(j).pb.y && lines.get(j).pb.y <= lines.get(i).pb.y) ||
								 (lines.get(i).pb.y <= lines.get(j).pb.y && lines.get(j).pb.y <= lines.get(i).pa.y))){
								int [] a = findPointA(lines.get(i), lines.get(j));
								int [] b = findPointB(lines.get(i), lines.get(j));
								lines.get(i).setA(a);
								lines.get(i).setB(b);
								lines.remove(j);
							}
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
	boolean isInt(double value){
		return value % 1 < 1e-5 || 1. - (value % 1) < 1e-5;
	}
	int findY1OnLineForNonLinearFunction(double x, double k, double d, double [] M) throws PointDoesNotExist{
		double delta = -k*k * M[0]* M[0] + 2 * k*k * x * M[0] - k*k * x*x + d*d;
		if (Math.abs(delta) < precision){
			delta = 0.;
		}	
		if (delta < 0)	throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double y1 = -(Math.sqrt(delta) - k * M[1])/k;
			
		if (isInt(y1)){
			return (int)Math.round(y1);
		}

		throw new PointDoesNotExist("Found y_1 [" + y1 + "] do not lay on the grid for k = " + k);
	}
	int findY2OnLineForNonLinearFunction(double x, double k, double d, double [] M) throws PointDoesNotExist{
		double delta = -k*k * M[0]*M[0] + 2 * k*k * x * M[0] - k*k * x*x + d*d;
		if (Math.abs(delta) < precision){
			delta = 0.;
		}
		if (delta < 0)	throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double y2 =  (Math.sqrt(delta) + k * M[1])/k;

		if (isInt(y2)){
			return (int)Math.round(y2);
		}

		throw new PointDoesNotExist("Found y_2 [" + y2 + "] do not lay on the grid for k = " + k);
	}
	int fixdX1OnLine(double a, double b, double k, double d, double [] M) throws PointDoesNotExist{
		double delta = (1 + a*a)*d*d - k*k*M[1]*M[1] + (2*b*k*k + 2*a*k*k*M[0])*M[1] - a*a*k*k*M[0]*M[0] - 2*a*b*k*k*M[0] - b*b*k*k;
		if (Math.abs(delta) < precision){
			delta = 0.;
		}
		if (delta < 0)	throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double x1s = -(a*b*k - k*M[0] - a*k*M[1] + Math.sqrt(delta))/((a*a + 1)*k);
		if (isInt(x1s))	return (int)Math.round(x1s);

		throw new PointDoesNotExist("Found x_1 [" + x1s + "] do not lay on the line");
	}
	int fixdX2OnLine(double a, double b, double k, double d, double [] M) throws PointDoesNotExist{
		double delta = (1 + a*a)*d*d - k*k*M[1]*M[1] + (2*b*k*k+2*a*k*k*M[0])*M[1] - a*a*k*k*M[0]*M[0] - 2*a*b*k*k*M[0] - b*b*k*k;
		if (Math.abs(delta) < precision){
			delta = 0.;
		}
		if (delta < 0)	throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double x2s = (-a*b*k + k*M[0] + a*k*M[1] + Math.sqrt(delta))/((a*a + 1)*k);

		if (isInt(x2s))	return (int)Math.round(x2s);

		throw new PointDoesNotExist("Found x_1 [" + x2s + "] do not lay on the line");
	}
	int findYOnLine(int x, double a, double b) throws PointDoesNotExist{
		double y = a*x + b;
		if (isInt(y))	return (int)y;
		throw new PointDoesNotExist("Found y (" + y + ") does not lay on the grid");
	}
	static int [] findPointA(Line a, Line b){
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
	static int [] findPointB(Line a, Line b){
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
	// 
		double ms_la;
		double ms_lb;

		double ma_la;
		double ma_lb;

		double mc_la;
		double mc_lb;

		double mb_la;
		double mb_lb;

		double md_la;
		double md_lb;

		double precision = 1e-13;
	//
		Dist(Line line, Marker marker){
			double [] line_coordinates = line.getDoubleCoordinates();
			this.ms_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.p.x*1., marker.p.y*1.);
			this.ms_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.p.x*1., marker.p.y*1.);

			this.ma_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.getax()*1., marker.getay()*1.);
			this.ma_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.getax()*1., marker.getay()*1.);

			this.mc_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.getcx()*1., marker.getcy()*1.);
			this.mc_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.getcx()*1., marker.getcy()*1.);

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

			this.mc_la = 0.0;
			this.mc_lb = 0.0;

			this.mb_la = 0.0;
			this.mb_lb = 0.0;

			this.md_la = 0.0;
			this.md_lb = 0.0;
		}
		@Override
		public String toString(){
			StringJoiner info = new StringJoiner(" ");
			return info.add(Double.toString(ms_la)).add(Double.toString(ms_lb)).add(Double.toString(ma_la)).add(Double.toString(ma_lb)).toString();
		}
		Referral compareLine(Dist inputDist, double k){
			if ((Math.abs(this.ms_lb - inputDist.ms_lb * k) < precision && Math.abs(this.ma_la - inputDist.ma_la * k) < precision) ||
				(Math.abs(this.ms_la - inputDist.ms_lb * k) < precision && Math.abs(this.ma_lb - inputDist.ma_la * k) < precision)){
				
				if (Math.abs(this.mb_la - this.md_lb) < precision && Math.abs(this.md_la - this.mb_lb) < precision){
					return Referral.DUPLICATED;
				}
				if ((Math.abs(this.mb_la-inputDist.mb_la*k) < precision && Math.abs(this.md_la-inputDist.md_la*k) < precision) ||
					(Math.abs(this.mb_lb-inputDist.mb_la*k) < precision && Math.abs(this.md_lb-inputDist.md_la*k) < precision)){
					
					return Referral.SAME;
				}
				return Referral.DIFFERENT;
			}
			return null;
		}
		// returns distans between the first point (A) of the line section and the middle of the marker
		double getMSLA(){
			return ms_la;
		}
		// returns distans between the second point (B) of the line section and the middle of the marker
		double getMSLB(){
			return ms_lb;
		}
		// returns distans between the first point (A) of the line section and the A point of the marker
		double getMALA(){
			return ma_la;
		}
		// returns distans between the second point (B) of the line section and the A point of the marker
		double getMALB(){
			return ma_lb;
		}
		// returns distans between the first point (A) of the line section and the C point of the marker
		double getMCLA(){
			return mc_la;
		}
		// returns distans between the second point (B) of the line section and the C point of the marker
		double getMCLB(){
			return mc_lb;
		}
		// returns distans between the first point (A) of the line section and the B point of the marker
		double getMBLA(){
			return mb_la;
		}
		// returns distans between the second point (B) of the line section and the B point of the marker
		double getMBLB(){
			return mb_lb;
		}
		// returns distans between the first point (A) of the line section and the D point of the marker
		double getMDLA(){
			return md_la;
		}
		// returns distans between the second point (B) of the line section and the D point of the marker
		double getMDLB(){
			return md_lb;
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
class NotAllRuleLinesRecognized extends Exception{
	public NotAllRuleLinesRecognized(String message){
			super(message);
		}
		public NotAllRuleLinesRecognized(){
			super("Not All Rule Lines Recognized");
		}
}