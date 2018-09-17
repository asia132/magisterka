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
	ArrayList <Line> findMatch(Shape inputShape) throws NotAllRuleLinesRecognized{
		double k = this.marker.length() / inputShape.marker.length();

		ArrayList <Line> ruleLines = new ArrayList<>(this.lines_dist.keySet());
		Line [][] matchedLines = new Line[this.lines_dist.size()][2];

		ArrayList <Line> mached_lines = new ArrayList<>();

		int same_counter = matchedLines.length;

		for (Map.Entry<Line, Dist> inShape: inputShape.lines_dist.entrySet()) {
			Line inLine_i = inShape.getKey();
			Dist inDist_i = inShape.getValue();
			int [] result = this.compareLineDistanses(inDist_i, inLine_i, k);
			if (result != null){
				System.out.print("++++++++++++++++++++++Add matched line for " + ruleLines.get(result[0]) + "++++++++++++++++++++++");
				if (result[1] == -1){
					same_counter--;
					matchedLines[result[0]][0] = inLine_i;
					matchedLines[result[0]][1] = inLine_i;
					System.out.println("\t0\t1");
				}
				else{
					this.same += result[1];
					if (result[1] == 1){
						matchedLines[result[0]][0] = inLine_i;
						System.out.println("\t0");
					}else{
						matchedLines[result[0]][1] = inLine_i;
						System.out.println("\t1");
					}
				}
			}
			// check sublines
			else{
				try{
					Object[] subLineResult = findSubLine(inLine_i, inDist_i, k, inputShape.marker, null);
					int index = ruleLines.indexOf((Line)subLineResult[1]);
					if ((int)subLineResult[2] == 1){;
						matchedLines[index][0] = (Line)subLineResult[0];
					}else{;
						matchedLines[index][1] = (Line)subLineResult[0];
					}
					System.out.println("----------------------Add matched SUBline for " + ruleLines.get(index) + "----------------------");

					subLineResult = findSubLine(inLine_i, inDist_i, k, inputShape.marker, (Line)subLineResult[1]);
					index = ruleLines.indexOf((Line)subLineResult[1]);
					if ((int)subLineResult[2] == 1){;
						matchedLines[index][0] = (Line)subLineResult[0];
					}else{;
						matchedLines[index][1] = (Line)subLineResult[0];
					}
					System.out.println("----------------------Add another matched SUBline for " + ruleLines.get(index) + "----------------------");

					subLineResult = findSubLine(inLine_i, inDist_i, k, inputShape.marker, (Line)subLineResult[1]);
					index = ruleLines.indexOf((Line)subLineResult[1]);
					if ((int)subLineResult[2] == 1){;
						matchedLines[index][0] = (Line)subLineResult[0];
					}else{;
						matchedLines[index][1] = (Line)subLineResult[0];
					}
					System.out.println("----------------------Add another matched SUBline for " + ruleLines.get(index) + "----------------------");

					subLineResult = findSubLine(inLine_i, inDist_i, k, inputShape.marker, (Line)subLineResult[1]);
					index = ruleLines.indexOf((Line)subLineResult[1]);
					if ((int)subLineResult[2] == 1){;
						matchedLines[index][0] = (Line)subLineResult[0];
					}else{;
						matchedLines[index][1] = (Line)subLineResult[0];
					}
					System.out.println("----------------------Add another matched SUBline for " + ruleLines.get(index) + "----------------------");

				}
				catch(PointDoesNotExist p){;
				}
			}
		}

		this.same = 0;
		System.out.println("MATCHING RESULT:");
		ArrayList <Integer> checkItAgain = new ArrayList <>();
		for (int i = 0; i < matchedLines.length; ++i){
			System.out.println(matchedLines[i][0] + " - " + matchedLines[i][1]);
			if (matchedLines[i][0] != null){
				if (matchedLines[i][1] == null){
					this.same++;
					mached_lines.add(matchedLines[i][0]);
				}else{
					checkItAgain.add(i);
				}
			}else{
				if (matchedLines[i][1] == null){
					throw new NotAllRuleLinesRecognized("No lines recognized for " + ruleLines.get(i));
				}else{
					mached_lines.add(matchedLines[i][1]);
				}
			} 
		}
		if ((this.same + checkItAgain.size()) == same_counter){
			for (Integer i: checkItAgain) {
				this.same++;
				mached_lines.add(matchedLines[i][0]);
			}
		}else{
			for (Integer i: checkItAgain) {
				mached_lines.add(matchedLines[i][1]);
			}
		}

		System.out.println("Przerzucić? " + this.same + " " + same_counter);
		if (this.same != 0 && this.same != same_counter){
			throw new NotAllRuleLinesRecognized("The lines was not found on the same side");
		}

		inputShape.needsToBeMirrored = !(this.same == same_counter);

		return mached_lines;
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
				if (isLinear && params_rule_line_i[0] == params_input_line[0]){
					ruleLinesWithSameA.add(line_i);
				}
			}catch(Line.NotALinearFunction error){
				// input line has the same format as rule line_i (x = val)
				if (!isLinear){
					ruleLinesWithSameA.add(line_i); 
				}
			}
		}
		return ruleLinesWithSameA;
	}
	Object [] findSubLine(Line inputLine, Dist inputDist, double k, Marker inputMarker, Line alreadyFoundLine) throws PointDoesNotExist{
		Line transLine = inputLine.copy();
		double alpha = -inputMarker.calcRotation(this.marker.dir);
		transLine.rotate(inputMarker.p.x, inputMarker.p.y, alpha);


		ArrayList <Line> matchedRuleLines = compareLineAParam(transLine);

		Line line = null;
		Line mline = null;
		try{
			double [] params = transLine.getFunctionParamsOnGrid();
			for (Line matchedRuleLine: matchedRuleLines){
				if (alreadyFoundLine != null && alreadyFoundLine.isTheSameLine(matchedRuleLine))
					continue;
				if (matchedRuleLine.length() / k < transLine.length()){

					System.out.println("------------------------");
					System.out.print("Checked input line: "); inputLine.print();
					System.out.print("After translation : "); transLine.print();
					inputMarker.print();

					int [] markerCenter = {inputMarker.p.x, inputMarker.p.y};
					double distAs = this.lines_dist.get(matchedRuleLine).getMSLA();
					double distBs = this.lines_dist.get(matchedRuleLine).getMSLB();
					try{
						System.out.println("find X [" + markerCenter[0] + ", " + markerCenter[1] + "]");
						int x_a = findXOnLine(params[0], params[1], distAs, k, markerCenter);
						int x_b = findXOnLine(params[0], params[1], distBs, k, markerCenter);

						System.out.println("find Y");
						int y_a = findYOnLine(x_a, params[0], params[1]);
						int y_b = findYOnLine(x_b, params[0], params[1]);
						Line newLine = new Line(x_a*MainData.grid_size, y_a*MainData.grid_size, x_b*MainData.grid_size, y_b*MainData.grid_size);
						newLine.rotate(inputMarker.p.x, inputMarker.p.y, -alpha);

						if (!(checkY(y_a, transLine) && checkY(y_b, transLine))){
							// System.out.println("nie zgadza sie!");
							continue;
						}
						if (!(checkX(x_a, transLine) && checkX(x_b, transLine))){
							// System.out.println("nie zgadza sie!");
							continue;
						} 

						if (checkMirroringSide(k, inputMarker, matchedRuleLine, x_a, x_b, y_a, y_b)){
							this.same += 1;
							System.out.print("Initial line "); matchedRuleLine.print();
							System.out.print("\nFound ok line: "); 
							newLine.print();
							return new Object[] {newLine, matchedRuleLine, 1};

						}else{
							System.out.print("Initial line "); matchedRuleLine.print();
							System.out.print("\nFound skipped line: ");
							line = newLine;
							line.rotate(inputMarker.p.x, inputMarker.p.y, -alpha);
							mline = matchedRuleLine;
							// line.print();
						}
					}catch (PointDoesNotExist pointDoesNotExist) {
						// System.out.println(pointDoesNotExist.getMessage());
						continue;
					}
				}
			}
		}catch(Line.NotALinearFunction error){
			int x = error.getX();
			for (Line matchedRuleLine: matchedRuleLines){
				if (alreadyFoundLine != null && alreadyFoundLine.isTheSameLine(matchedRuleLine))
					continue;
				if (matchedRuleLine.length() / k < transLine.length()){

					System.out.println("------------------------");
					System.out.print("Checked input line: "); inputLine.print();
					System.out.print("After translation : "); transLine.print();
					inputMarker.print();

					int [] markerCenter = {inputMarker.p.x, inputMarker.p.y};
					double distAs = this.lines_dist.get(matchedRuleLine).getMSLA();
					double distBs = this.lines_dist.get(matchedRuleLine).getMSLB();

					Marker tempMarker = inputMarker.copy();
					tempMarker.rotateBasedOnDirSub(inputMarker.dir, this.marker.dir, false);
					System.out.println("Temp marker: ");
					tempMarker.print();

					int [] markerAPoint = {tempMarker.getax(), tempMarker.getay()};
					double distAp = this.lines_dist.get(matchedRuleLine).getMALA();
					double distBp = this.lines_dist.get(matchedRuleLine).getMALB();

					try{

						System.out.println("find Y [" + markerCenter[0] + ", " + markerCenter[1] + "]");
						int y_a = findYOnLineForNonLinearFunction(x, distAs, k, markerCenter, distAp, markerAPoint);
						int y_b = findYOnLineForNonLinearFunction(x, distBs, k, markerCenter, distBp, markerAPoint);

						Line newLine = new Line(x*MainData.grid_size, y_a*MainData.grid_size, x*MainData.grid_size, y_b*MainData.grid_size);
						System.out.println("...............");
						newLine.print();
						newLine.rotate(inputMarker.p.x, inputMarker.p.y, -alpha);
						newLine.print();
						System.out.println();
						inputLine.print();
						System.out.println("...............");

						if (!(checkY(newLine.pa.y, inputLine) && checkY(newLine.pb.y, inputLine))){
							Line lineCopy = newLine.copy();
							lineCopy.mirrorY(markerCenter[1]);
							// if (!(checkY(lineCopy.pa.y, inputLine) && checkY(lineCopy.pb.y, inputLine))){
								System.out.println("nie zgadza sie! - Y");
								continue;
							// }
						}
						if (!(checkX(newLine.pa.x, inputLine) && checkX(newLine.pb.x, inputLine))){
							Line lineCopy = newLine.copy();
							lineCopy.mirrorX(markerCenter[0]);
							// if (!(checkX(lineCopy.pa.x, inputLine) && checkX(lineCopy.pb.x, inputLine))){
								System.out.println("nie zgadza sie! - X");
								continue;
							// }
						} 
						if (checkMirroringSide(k, inputMarker, matchedRuleLine, newLine.pa.x, newLine.pb.x, newLine.pa.y, newLine.pb.y)){
							this.same += 1;
							System.out.print("Initial line "); matchedRuleLine.print();
							System.out.print("\nFound ok line: ");
							newLine.print();
							return new Object[] {newLine, matchedRuleLine, 1};
						}else{
							System.out.print("Initial line "); matchedRuleLine.print();
							System.out.print("\nFound skipped line: ");
							line = newLine;
							mline = matchedRuleLine;
							line.print();
						}
					}catch (PointDoesNotExist pointDoesNotExist) {
						// System.out.println(pointDoesNotExist.getMessage());
						continue;
					}
				}
			}
		}
		if (line != null){
			return new Object[]{line, mline, 0};
		}
		throw new PointDoesNotExist();
	}
	boolean checkY(int y, Line line){
		return	((line.pa.y <= y && y <= line.pb.y) ||
				 (line.pb.y <= y && y <= line.pa.y));
	}
	boolean checkX(int x, Line line){
		return	((line.pa.x <= x && x <= line.pb.x) ||
				 (line.pb.x <= x && x <= line.pa.x));
	}
	boolean checkMirroringSide(double k, Marker marker, Line line, int x_a, int x_b, int y_a, int y_b){
		double dist_b = this.lines_dist.get(line).mb_la;
		double mb_la = MainData.distans(x_a, y_a, marker.getbx()*1., marker.getby()*1.);
		double mb_lb = MainData.distans(x_b, y_b, marker.getbx()*1., marker.getby()*1.);

		double dist_d = this.lines_dist.get(line).md_la;
		double md_la = MainData.distans(x_a, y_a, marker.getdx()*1., marker.getdy()*1.);
		double md_lb = MainData.distans(x_b, y_b, marker.getdx()*1., marker.getdy()*1.);

		return (Math.abs(dist_b-mb_la*k) < 1e-13 && Math.abs(dist_d-md_la*k) < 1e-13) || 
							(Math.abs(dist_b-mb_lb*k) < 1e-13 && Math.abs(dist_d-md_lb*k) < 1e-13);
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
						if (funparam_i[0] == funparam_j[0] && funparam_i[1] == funparam_j[1]){
							if (((lines.get(j).pa.x <= lines.get(i).pa.x && lines.get(i).pa.x <= lines.get(j).pb.x) ||
								 (lines.get(j).pb.x <= lines.get(i).pa.x && lines.get(i).pa.x <= lines.get(j).pa.x) ||
								 (lines.get(j).pa.x <= lines.get(i).pb.x && lines.get(i).pb.x <= lines.get(j).pb.x) ||
								 (lines.get(j).pb.x <= lines.get(i).pb.x && lines.get(i).pb.x <= lines.get(j).pa.x)) ||
								((lines.get(i).pa.x <= lines.get(j).pa.x && lines.get(j).pa.x <= lines.get(i).pb.x) ||
								 (lines.get(i).pb.x <= lines.get(j).pa.x && lines.get(j).pa.x <= lines.get(i).pa.x) ||
								 (lines.get(i).pa.x <= lines.get(j).pb.x && lines.get(j).pb.x <= lines.get(i).pb.x) ||
								 (lines.get(i).pb.x <= lines.get(j).pb.x && lines.get(j).pb.x <= lines.get(i).pa.x))){
								lines.get(i).setA(findPointA(lines.get(i), lines.get(j)));
								lines.get(i).setB(findPointB(lines.get(i), lines.get(j)));
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
				for (int j = i + 1; j < lines.size(); ++j){
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
								lines.get(i).setA(findPointA(lines.get(i), lines.get(j)));
								lines.get(i).setB(findPointB(lines.get(i), lines.get(j)));
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
	int [] compareLineDistanses(Dist inputDistans, Line inputLine, double k){
		int i = 0;
		for (Map.Entry<Line, Dist> dist: this.lines_dist.entrySet()) {
			Line line_i = dist.getKey();
			Dist dist_i = dist.getValue();
			Referral ref = dist_i.compareLine(inputDistans, k);
			if (ref != null){
				System.out.println("Found" + inputLine);
				if (ref == Referral.SAME)	return new int[]{i, ref.getNum()};
				if (ref == Referral.DUPLICATED)	return new int[]{i, ref.getNum()};
				if (ref == Referral.DIFFERENT)	return new int[]{i, ref.getNum()};
			}
			i++;
		}
		return null;
	}
	boolean isInt(double value){
		return value % 1 < 1e-5 || 1. - (value % 1) < 1e-5;
	}
	int findYOnLineForNonLinearFunction(double x, double dc, double k, int [] Mc, double da, int [] Ma) throws PointDoesNotExist{
		double delta = -k*k * Mc[0]* Mc[0] + 2 * k*k * x * Mc[0] - k*k * x*x + dc*dc;
		if (delta < 0)	throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double y1s = -(Math.sqrt(delta) - k * Mc[1])/k;
		double y2s =  (Math.sqrt(delta) + k * Mc[1])/k;

		System.out.println("Sprawdź to drugie y: " + y1s + " vs " + y2s);
		if (isInt(y1s) && ((int)y1s == (int)y2s)) return (int)y1s;

		delta = -k*k * Ma[0]* Ma[0] + 2 * k*k * x * Ma[0] - k*k * x*x + da*da;
		if (delta < 0)	throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double y1a = -(Math.sqrt(delta) - k * Ma[1])/k;
		double y2a =  (Math.sqrt(delta) + k * Ma[1])/k;

		System.out.println("Sprawdź to drugie y: " + y1a + " vs " + y2a);
		if (isInt(y1a) && ((int)y1a == (int)y2a)) return (int)y1a;

		if (isInt(y1a)){
			if (((int)y1a == (int)y1s))	return (int)y1a;
			if (((int)y1a == (int)y2s))	return (int)y1a;
		}
		if (isInt(y2a)){
			if (((int)y2a == (int)y1s))	return (int)y2a;
			if (((int)y2a == (int)y2s))	return (int)y2a;
		} 

		throw new PointDoesNotExist("Found y_1 [" + y1s + "] and y_2 [" + y2s + "] do not lay on the grid for k = " + k + "(" + (y1s % 1) + ", " + (y2s % 1) + ")");
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

		double mc_la;
		double mc_lb;

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

			this.mb_la = 0.0;
		}
		@Override
		public String toString(){
			StringJoiner info = new StringJoiner(" ");
			return info.add(Double.toString(ms_la)).add(Double.toString(ms_lb)).add(Double.toString(ma_la)).add(Double.toString(ma_lb)).toString();
		}
		Referral compareLine(Dist inputDist, double k){
			double precision = 1e-13;


			if ((Math.abs(this.ms_lb - inputDist.ms_lb * k) < precision && Math.abs(this.ma_la - inputDist.ma_la * k) < precision && Math.abs(this.mc_lb - inputDist.mc_lb * k) < precision) ||
				(Math.abs(this.ms_la - inputDist.ms_lb * k) < precision && Math.abs(this.ma_lb - inputDist.ma_la * k) < precision && Math.abs(this.mc_la - inputDist.mc_lb * k) < precision)){
				
				System.out.println("---");
				System.out.println("[" + this.ms_la + " " + inputDist.ms_la +"], [" + this.ma_la + " " + inputDist.ma_la + "], [" + this.mc_la + " " + inputDist.mc_la +"]");
				System.out.println("[" + this.ms_lb + " " + inputDist.ms_lb +"], [" + this.ma_lb + " " + inputDist.ma_lb + "], [" + this.mc_lb + " " + inputDist.mc_lb +"]");
				System.out.println("---");
				
				System.out.print("Poszło");
				if (Math.abs(this.mb_la - this.md_lb) < precision && Math.abs(this.md_la - this.mb_lb) < precision){
					System.out.println("-DUPLICATED");
					return Referral.DUPLICATED;
				}
				if ((Math.abs(this.mb_la-inputDist.mb_la*k) < precision && Math.abs(this.md_la-inputDist.md_la*k) < precision) ||
					(Math.abs(this.mb_lb-inputDist.mb_la*k) < precision && Math.abs(this.md_lb-inputDist.md_la*k) < precision)){
					System.out.println("-SAME");
					return Referral.SAME;
				}
				System.out.println("-DIFFERENT");
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
class NotAllRuleLinesRecognized extends Exception{
	public NotAllRuleLinesRecognized(String message){
			super(message);
		}
		public NotAllRuleLinesRecognized(){
			super("Not All Rule Lines Recognized");
		}
}