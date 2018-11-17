package grammar_graphs;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.lang.Math;

class Shape {
	Map<Line, Dist> lines_dist;
	double[] marker_norm;
	Marker marker;
	String name;
	boolean needsToBeMirrored;
	int same = 0;

	double precision = 1e-12;

	Shape(List<Line> lines, Marker marker) {
		this.name = "Rule B site";
		this.marker = marker.copy();
		this.lines_dist = new HashMap<Line, Dist>();
		for (Line line : lines) {
			this.lines_dist.put(line.copy(), new Dist(line, marker));
		}
	}

	Shape(List<Line> lines, Marker marker, String name) {
		this.marker = marker.copy();
		this.lines_dist = new HashMap<Line, Dist>();
		this.marker_norm = marker.getNormalized();
		this.name = name;

		List<Line> linesCopy = new ArrayList<>(lines.size());
		for (Line line : lines) {
			Line newLine = line.copy();
			line.addChild(newLine);
			linesCopy.add(newLine);
		}

		int before = linesCopy.size();
		do {
			System.out.print("\nShape const: ");
			before = linesCopy.size();
			linesCopy = groupLines(linesCopy);
		} while (before != linesCopy.size());
		for (Line line : linesCopy) {
			this.lines_dist.put(line, new Dist(line, marker));
		}
	}

	Shape() {
		this.marker_norm = new double[4];
		this.lines_dist = new HashMap<Line, Dist>();
	}

	List<Line> setInPlace(Marker inputMarker, Marker initialMarker) {
		// inputMarker: N 2 60 8
		// initiMarker: W 2 12 5
		double k = inputMarker.length() / initialMarker.length();
		List<Line> resultLines = new ArrayList<>();
		for (Line line : this.getLines()) {
			Line resultline = line.copy();
			resultline.move(inputMarker.getX() - initialMarker.getX(), inputMarker.getY() - initialMarker.getY());
			resultline.rotate(inputMarker.p.x, inputMarker.p.y, inputMarker.calcRotation(initialMarker.dir));
			if (k != 1)
				resultline.scale(inputMarker.p.x, inputMarker.p.y, k);
			if (this.needsToBeMirrored
					&& (inputMarker.dir.equals(Marker.Direct.N) || inputMarker.dir.equals(Marker.Direct.S))) {
				resultline.mirrorX(inputMarker.p.x);
			}
			if (this.needsToBeMirrored
					&& (inputMarker.dir.equals(Marker.Direct.E) || inputMarker.dir.equals(Marker.Direct.W))) {
				resultline.mirrorY(inputMarker.p.y);
			}
			resultLines.add(resultline);
		}
		return resultLines;
	}

	List<Line> findMatch(Shape inputShape) throws NotAllRuleLinesRecognized {
		double k = this.marker.length() / inputShape.marker.length();

		MirroringTable mirrorTable = new MirroringTable(this.lines_dist.size(), this.lines_dist.keySet());

		Marker tempMarker = inputShape.marker.copy();
		tempMarker.rotateBasedOnDirSub(inputShape.marker.dir, this.marker.dir, false);

		for (Map.Entry<Line, Dist> inShape : inputShape.lines_dist.entrySet()) {
			Line inLine_i = inShape.getKey();
			Dist inDist_i = inShape.getValue();

			Line transLine_i = inLine_i.copy();
			double alpha = -inputShape.marker.calcRotation(this.marker.dir);
			transLine_i.rotate(inputShape.marker.p.x, inputShape.marker.p.y, alpha);

			double[] params_i = null;
			try {
				params_i = transLine_i.getFunctionParamsOnGrid();
			} catch (Line.NotALinearFunction error) {
				;
			}

			for (int i = 0; i < this.lines_dist.size(); i++) {
				Line line_r = mirrorTable.ruleLines.get(i);
				Dist dist_j = this.lines_dist.get(line_r);
				double[] params_j = null;

				try {
					params_j = line_r.getFunctionParamsOnGrid();
				} catch (Line.NotALinearFunction error) {
					;
				}

				if (params_i != null && params_j != null && params_i[0] == params_j[0]) { // poziome i skośne
					Referral ref = dist_j.compareLine(inDist_i, k);
					if (ref != null) { // odcinki dla poziomych i skośnych
						mirrorTable.setValue(i, inLine_i, ref);
					}
					if (line_r.length() / k < transLine_i.length()) { // pododcinki dla poziomych i skośnych

						if (params_i[0] == 0 && Math.abs(this.marker.p.y - line_r.pa.y) / k != Math
								.abs(inputShape.marker.p.y - transLine_i.pa.y)) { // dodatkowe sprawdzenie dla poziomych
							continue;
						}

						double[] markerCenter = { inputShape.marker.p.x * 1., inputShape.marker.p.y * 1. };
						double distAs = this.lines_dist.get(line_r).getMSLA();
						double distBs = this.lines_dist.get(line_r).getMSLB();

						double[] markerAPoint = { tempMarker.getax() * 1., tempMarker.getay() * 1. };
						double distAp = this.lines_dist.get(line_r).getMALA();
						double distBp = this.lines_dist.get(line_r).getMALB();

						try {
							int[] xrange = new int[2];
							if (transLine_i.pa.x < transLine_i.pb.x) {
								xrange[0] = transLine_i.pa.x;
								xrange[1] = transLine_i.pb.x;
							} else {

								xrange[0] = transLine_i.pb.x;
								xrange[1] = transLine_i.pa.x;
							}

							int x1s_a = fixdX1OnLine(params_i[0], params_i[1], k, distAs, markerCenter);
							int x2s_a = fixdX2OnLine(params_i[0], params_i[1], k, distAs, markerCenter);

							int x1s_b = fixdX1OnLine(params_i[0], params_i[1], k, distBs, markerCenter);
							int x2s_b = fixdX2OnLine(params_i[0], params_i[1], k, distBs, markerCenter);

							List<Integer> xValuesFromA = new ArrayList<>(4);
							xValuesFromA.add(fixdX1OnLine(params_i[0], params_i[1], k, distAp, markerAPoint));
							xValuesFromA.add(fixdX2OnLine(params_i[0], params_i[1], k, distAp, markerAPoint));
							xValuesFromA.add(fixdX1OnLine(params_i[0], params_i[1], k, distBp, markerAPoint));
							xValuesFromA.add(fixdX2OnLine(params_i[0], params_i[1], k, distBp, markerAPoint));

							int y1_a = findYOnLine(x1s_a, params_i[0], params_i[1]);
							int y2_a = findYOnLine(x2s_a, params_i[0], params_i[1]);
							int y1_b = findYOnLine(x1s_b, params_i[0], params_i[1]);
							int y2_b = findYOnLine(x2s_b, params_i[0], params_i[1]);

							// CHECK POINT S1_A
							if (xrange[0] <= x1s_a && x1s_a <= xrange[1] && xValuesFromA.contains(x1s_a)) {
								// CHECK POINT S1_B
								if (xrange[0] <= x1s_b && x1s_b <= xrange[1] && xValuesFromA.contains(x1s_b)
										&& line_r.compareLen(k, x1s_a, y1_a, x1s_b, y1_b)) {

									Line newLine = Line.createRotatedLine(new Point(x1s_a, y1_a),
											new Point(x1s_b, y1_b), -alpha, inputShape.marker.p);
									if (!inLine_i.checkLineY(newLine)) {
										continue;
									}
									mirrorTable.setValue(i, newLine, Referral.checkMirroringSide(k, inputShape.marker,
											this.lines_dist.get(line_r), newLine));
									inLine_i.addChild(newLine);

									// CHECK POINT S2_B
								} else {
									if (xrange[0] <= x2s_b && x2s_b <= xrange[1] && xValuesFromA.contains(x2s_b)
											&& line_r.compareLen(k, x1s_a, y1_a, x2s_b, y2_b)) {

										Line newLine = Line.createRotatedLine(new Point(x1s_a, y1_a),
												new Point(x2s_b, y2_b), -alpha, inputShape.marker.p);
										if (!inLine_i.checkLineY(newLine)) {
											continue;
										}
										mirrorTable.setValue(i, newLine, Referral.checkMirroringSide(k,
												inputShape.marker, this.lines_dist.get(line_r), newLine));
										inLine_i.addChild(newLine);

									}
								}
							}
							// CHECK POINT S2_A
							if (xrange[0] <= x2s_a && x2s_a <= xrange[1] && xValuesFromA.contains(x2s_a)) {
								// CHECK POINT S1_B
								if (xrange[0] <= x1s_b && x1s_b <= xrange[1] && xValuesFromA.contains(x1s_b)
										&& line_r.compareLen(k, x2s_a, y2_a, x1s_b, y1_b)) {

									Line newLine = Line.createRotatedLine(new Point(x2s_a, y2_a),
											new Point(x1s_b, y1_b), -alpha, inputShape.marker.p);
									if (!inLine_i.checkLineY(newLine)) {
										continue;
									}
									mirrorTable.setValue(i, newLine, Referral.checkMirroringSide(k, inputShape.marker,
											this.lines_dist.get(line_r), newLine));
									inLine_i.addChild(newLine);

								} else {
									// CHECK POINT S2_B
									if (xrange[0] <= x2s_b && x2s_b <= xrange[1] && xValuesFromA.contains(x2s_b)
											&& line_r.compareLen(k, x2s_a, y2_a, x2s_b, y2_b)) {

										Line newLine = Line.createRotatedLine(new Point(x2s_a, y2_a),
												new Point(x2s_b, y2_b), -alpha, inputShape.marker.p);
										if (!inLine_i.checkLineY(newLine)) {
											continue;
										}
										mirrorTable.setValue(i, newLine, Referral.checkMirroringSide(k,
												inputShape.marker, this.lines_dist.get(line_r), newLine));
										inLine_i.addChild(newLine);

									} else {
										continue;
									}
								}
							}
						} catch (PointDoesNotExist pointDoesNotExist) {
							continue;
						}
					}
				} else if (params_i == null && params_j == null) { // pionowe
					Referral ref = dist_j.compareLine(inDist_i, k);
					if (ref != null) { // odcinki pionowe
						mirrorTable.setValue(i, inLine_i, ref);
					}
					int x = transLine_i.pa.x;

					if (line_r.length() / k < transLine_i.length()) { // pododcinki pionowe

						if (Math.abs(this.marker.p.x - line_r.pa.x) / k != Math.abs(inputShape.marker.p.x - x)) { // dodatkowe
																													// sprawdzenie
																													// dla
																													// poziomych
							continue;
						}

						double[] markerCenter = { inputShape.marker.p.x * 1., inputShape.marker.p.y * 1. };
						double distAs = this.lines_dist.get(line_r).getMSLA();
						double distBs = this.lines_dist.get(line_r).getMSLB();

						double[] markerAPoint = { tempMarker.getax() * 1., tempMarker.getay() * 1. };
						double distAp = this.lines_dist.get(line_r).getMALA();
						double distBp = this.lines_dist.get(line_r).getMALB();

						int[] yrange = new int[2];
						if (transLine_i.pa.y < transLine_i.pb.y) {
							yrange[0] = transLine_i.pa.y;
							yrange[1] = transLine_i.pb.y;
						} else {

							yrange[0] = transLine_i.pb.y;
							yrange[1] = transLine_i.pa.y;
						}

						try {

							int y1_a = findY1OnLineForNonLinearFunction(x, k, distAs, markerCenter);
							int y2_a = findY2OnLineForNonLinearFunction(x, k, distAs, markerCenter);
							int y1_b = findY1OnLineForNonLinearFunction(x, k, distBs, markerCenter);
							int y2_b = findY2OnLineForNonLinearFunction(x, k, distBs, markerCenter);

							List<Integer> yValuesFromA = new ArrayList<>(4);
							yValuesFromA.add(findY1OnLineForNonLinearFunction(x, k, distAp, markerAPoint));
							yValuesFromA.add(findY2OnLineForNonLinearFunction(x, k, distAp, markerAPoint));
							yValuesFromA.add(findY1OnLineForNonLinearFunction(x, k, distBp, markerAPoint));
							yValuesFromA.add(findY2OnLineForNonLinearFunction(x, k, distBp, markerAPoint));

							if (yrange[0] <= y1_a && y1_a <= yrange[1] && yValuesFromA.contains(y1_a)) {
								if (yrange[0] <= y1_b && y1_b <= yrange[1] && yValuesFromA.contains(y1_b)
										&& line_r.compareLen(k, x, y1_a, x, y1_b)) {

									Line newLine = Line.createRotatedLine(new Point(x, y1_a), new Point(x, y1_b),
											-alpha, inputShape.marker.p);
									if (!inLine_i.checkLineX(newLine)) {
										continue;
									}
									mirrorTable.setValue(i, newLine, Referral.checkMirroringSide(k, inputShape.marker,
											this.lines_dist.get(line_r), newLine));
									inLine_i.addChild(newLine);

								} else if (yrange[0] <= y2_b && y2_b <= yrange[1] && yValuesFromA.contains(y2_b)
										&& line_r.compareLen(k, x, y1_a, x, y2_b)) {

									Line newLine = Line.createRotatedLine(new Point(x, y1_a), new Point(x, y2_b),
											-alpha, inputShape.marker.p);
									if (!inLine_i.checkLineX(newLine)) {
										continue;
									}
									mirrorTable.setValue(i, newLine, Referral.checkMirroringSide(k, inputShape.marker,
											this.lines_dist.get(line_r), newLine));
									line_r.addChild(newLine);

								}
							}
							if (yrange[0] <= y2_a && y2_a <= yrange[1] && yValuesFromA.contains(y2_a)) {
								if (yrange[0] <= y1_b && y1_b <= yrange[1] && yValuesFromA.contains(y1_b)
										&& line_r.compareLen(k, x, y2_a, x, y1_b)) {

									Line newLine = Line.createRotatedLine(new Point(x, y2_a), new Point(x, y1_b),
											-alpha, inputShape.marker.p);
									if (!inLine_i.checkLineX(newLine)) {
										continue;
									}
									mirrorTable.setValue(i, newLine, Referral.checkMirroringSide(k, inputShape.marker,
											this.lines_dist.get(line_r), newLine));
									inLine_i.addChild(newLine);

								} else {
									if (yrange[0] <= y2_b && y2_b <= yrange[1] && yValuesFromA.contains(y2_b)
											&& line_r.compareLen(k, x, y2_a, x, y2_b)) {

										Line newLine = Line.createRotatedLine(new Point(x, y2_a), new Point(x, y2_b),
												-alpha, inputShape.marker.p);
										if (!inLine_i.checkLineX(newLine)) {
											continue;
										}
										mirrorTable.setValue(i, newLine, Referral.checkMirroringSide(k,
												inputShape.marker, this.lines_dist.get(line_r), newLine));
										inLine_i.addChild(newLine);

									} else {
										continue;
									}
								}
							}
						} catch (PointDoesNotExist pointDoesNotExist1) {
							continue;
						}
					}
				}
			}
		}
		//
		this.same = 0;
		List<Line> mached_lines = mirrorTable.getMatchedLines();

		inputShape.needsToBeMirrored = mirrorTable.needsToBeMirrored();
		return mached_lines;
	}

	String linesDistToString(String ruleName, String siteTag) {
		StringJoiner info = new StringJoiner("");
		for (Line line_i : this.lines_dist.keySet()) {
			info.add("#").add(ruleName).add("\t").add(siteTag).add("\t").add(line_i.toString()).add("\n").toString();
		}
		return info.toString();
	}

	String markerToString(String ruleName, String siteTag) {
		StringJoiner info = new StringJoiner("");
		return info.add("#").add(ruleName).add("\t").add(siteTag).add("\t").add(marker.toString() + "\n").toString();
	}

	private String linesDistToString() {
		StringJoiner info = new StringJoiner("\n");
		for (Line line_i : this.lines_dist.keySet()) {
			info.add(line_i.toString());
		}
		return info.toString();
	}

	@Override
	public String toString() {
		StringJoiner info = new StringJoiner("\n");
		if (marker != null)
			return info.add(marker.toString()).add(this.linesDistToString()).toString();
		return info.add(this.linesDistToString()).toString();
	}

	// Consider joining lines
	static List<Line> groupLines(List<Line> shape_lines) {
		System.out.println("GROUP LINES");
		List<Line> lines = new ArrayList<>();
		for (Line line : shape_lines)
			lines.add(line);
		for (int i = 0; i < lines.size(); ++i) {
			try {
				double[] funparam_i = lines.get(i).getFunctionParams();
				lines.get(i).getSortedAB();
				for (int j = i + 1; j < lines.size(); ++j) {
					try {
						double[] funparam_j = lines.get(j).getFunctionParams(); // poziome i skośne
						lines.get(j).getSortedAB();
						if (funparam_i[0] == funparam_j[0] && funparam_i[1] == funparam_j[1]) {
							if (((lines.get(j).pa.x <= lines.get(i).pa.x && lines.get(i).pa.x <= lines.get(j).pb.x)
									|| (lines.get(j).pb.x <= lines.get(i).pa.x
											&& lines.get(i).pa.x <= lines.get(j).pa.x)
									|| (lines.get(j).pa.x <= lines.get(i).pb.x
											&& lines.get(i).pb.x <= lines.get(j).pb.x)
									|| (lines.get(j).pb.x <= lines.get(i).pb.x
											&& lines.get(i).pb.x <= lines.get(j).pa.x))
									|| ((lines.get(i).pa.x <= lines.get(j).pa.x
											&& lines.get(j).pa.x <= lines.get(i).pb.x)
											|| (lines.get(i).pb.x <= lines.get(j).pa.x
													&& lines.get(j).pa.x <= lines.get(i).pa.x)
											|| (lines.get(i).pa.x <= lines.get(j).pb.x
													&& lines.get(j).pb.x <= lines.get(i).pb.x)
											|| (lines.get(i).pb.x <= lines.get(j).pb.x
													&& lines.get(j).pb.x <= lines.get(i).pa.x))) {
								int[] a = findPointA(lines.get(i), lines.get(j));
								int[] b = findPointB(lines.get(i), lines.get(j));
								lines.get(i).setA(a);
								lines.get(i).setB(b);
								lines.get(i).getSortedAB();
								lines.remove(j);
							}
						}
					} catch (Line.NotALinearFunction error) {
						continue;
					}
				}
			} catch (Line.NotALinearFunction error) {
				int x_i = lines.get(i).pa.x;
				for (int j = i + 1; j < lines.size(); ++j) { // pionowe
					if (!lines.get(j).isPartOfLinearFun()) {
						int x_j = lines.get(j).pa.x;
						if (x_i == x_j) {
							if (((lines.get(j).pa.y <= lines.get(i).pa.y && lines.get(i).pa.y <= lines.get(j).pb.y)
									|| (lines.get(j).pb.y <= lines.get(i).pa.y
											&& lines.get(i).pa.y <= lines.get(j).pa.y)
									|| (lines.get(j).pa.y <= lines.get(i).pb.y
											&& lines.get(i).pb.y <= lines.get(j).pb.y)
									|| (lines.get(j).pb.y <= lines.get(i).pb.y
											&& lines.get(i).pb.y <= lines.get(j).pa.y))
									|| ((lines.get(i).pa.y <= lines.get(j).pa.y
											&& lines.get(j).pa.y <= lines.get(i).pb.y)
											|| (lines.get(i).pb.y <= lines.get(j).pa.y
													&& lines.get(j).pa.y <= lines.get(i).pa.y)
											|| (lines.get(i).pa.y <= lines.get(j).pb.y
													&& lines.get(j).pb.y <= lines.get(i).pb.y)
											|| (lines.get(i).pb.y <= lines.get(j).pb.y
													&& lines.get(j).pb.y <= lines.get(i).pa.y))) {
								int[] a = findPointA(lines.get(i), lines.get(j));
								int[] b = findPointB(lines.get(i), lines.get(j));
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

	double markerNormLength() {
		return MainData.distans(marker_norm[0], marker_norm[1], marker_norm[3], marker_norm[4]);
	}

	List<Line> getLines() {
		List<Line> lines = new ArrayList<>();
		lines.addAll(this.lines_dist.keySet());
		return lines;
	}

	static boolean isInt(double value) {
		return value % 1 < 1e-5 || 1. - (value % 1) < 1e-5;
	}

	int findY1OnLineForNonLinearFunction(double x, double k, double d, double[] M) throws PointDoesNotExist {
		double delta = -k * k * M[0] * M[0] + 2 * k * k * x * M[0] - k * k * x * x + d * d;
		if (Math.abs(delta) < precision) {
			delta = 0.;
		}
		if (delta < 0)
			throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double y1 = -(Math.sqrt(delta) - k * M[1]) / k;

		if (isInt(y1)) {
			return (int) Math.round(y1);
		}

		throw new PointDoesNotExist("Found y_1 [" + y1 + "] do not lay on the grid for k = " + k);
	}

	int findY2OnLineForNonLinearFunction(double x, double k, double d, double[] M) throws PointDoesNotExist {
		double delta = -k * k * M[0] * M[0] + 2 * k * k * x * M[0] - k * k * x * x + d * d;
		if (Math.abs(delta) < precision) {
			delta = 0.;
		}
		if (delta < 0)
			throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double y2 = (Math.sqrt(delta) + k * M[1]) / k;

		if (isInt(y2)) {
			return (int) Math.round(y2);
		}

		throw new PointDoesNotExist("Found y_2 [" + y2 + "] do not lay on the grid for k = " + k);
	}

	int fixdX1OnLine(double a, double b, double k, double d, double[] M) throws PointDoesNotExist {
		double delta = (1 + a * a) * d * d - k * k * M[1] * M[1] + (2 * b * k * k + 2 * a * k * k * M[0]) * M[1]
				- a * a * k * k * M[0] * M[0] - 2 * a * b * k * k * M[0] - b * b * k * k;
		if (Math.abs(delta) < precision) {
			delta = 0.;
		}
		if (delta < 0)
			throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double x1s = -(a * b * k - k * M[0] - a * k * M[1] + Math.sqrt(delta)) / ((a * a + 1) * k);
		if (isInt(x1s))
			return (int) Math.round(x1s);

		throw new PointDoesNotExist("Found x_1 [" + x1s + "] do not lay on the line");
	}

	int fixdX2OnLine(double a, double b, double k, double d, double[] M) throws PointDoesNotExist {
		double delta = (1 + a * a) * d * d - k * k * M[1] * M[1] + (2 * b * k * k + 2 * a * k * k * M[0]) * M[1]
				- a * a * k * k * M[0] * M[0] - 2 * a * b * k * k * M[0] - b * b * k * k;
		if (Math.abs(delta) < precision) {
			delta = 0.;
		}
		if (delta < 0)
			throw new PointDoesNotExist("Delta is less than zero: " + delta);

		double x2s = (-a * b * k + k * M[0] + a * k * M[1] + Math.sqrt(delta)) / ((a * a + 1) * k);

		if (isInt(x2s))
			return (int) Math.round(x2s);

		throw new PointDoesNotExist("Found x_1 [" + x2s + "] do not lay on the line");
	}

	int findYOnLine(int x, double a, double b) throws PointDoesNotExist {
		double y = a * x + b;
		if (isInt(y))
			return (int) y;
		throw new PointDoesNotExist("Found y (" + y + ") does not lay on the grid");
	}

	static int[] findPointA(Line a, Line b) {
		int[] pointA = { a.pa.x, a.pa.y };
		if (pointA[0] > a.pb.x || (pointA[0] == a.pb.x && pointA[1] > a.pb.y)) {
			pointA[0] = a.pb.x;
			pointA[1] = a.pb.y;
		}
		if (pointA[0] > b.pa.x || (pointA[0] == b.pa.x && pointA[1] > b.pa.y)) {
			pointA[0] = b.pa.x;
			pointA[1] = b.pa.y;
		}
		if (pointA[0] > b.pb.x || (pointA[0] == b.pb.x && pointA[1] > b.pb.y)) {
			pointA[0] = b.pb.x;
			pointA[1] = b.pb.y;
		}
		return pointA;
	}

	static int[] findPointB(Line a, Line b) {
		int[] pointA = { a.pa.x, a.pa.y };
		if (pointA[0] < a.pb.x || (pointA[0] == a.pb.x && pointA[1] < a.pb.y)) {
			pointA[0] = a.pb.x;
			pointA[1] = a.pb.y;
		}
		if (pointA[0] < b.pa.x || (pointA[0] == b.pa.x && pointA[1] < b.pa.y)) {
			pointA[0] = b.pa.x;
			pointA[1] = b.pa.y;
		}
		if (pointA[0] < b.pb.x || (pointA[0] == b.pb.x && pointA[1] < b.pb.y)) {
			pointA[0] = b.pb.x;
			pointA[1] = b.pb.y;
		}
		return pointA;
	}

	private class MirroringTable {
		List<Line> ruleLines;
		Line[] correctSideLines;
		Line[] opositeSideLines;

		int size;
		int correctCounter = 0;
		int opositeCounter = 0;
		int duplicatedCounter = 0;

		MirroringTable(int size, Set<Line> ruleLines) {
			this.size = size;
			this.ruleLines = new ArrayList<>(ruleLines);
			this.correctSideLines = new Line[size];
			this.opositeSideLines = new Line[size];
		}

		void setValue(int i, Line newLine, Referral subRef) {
			if (subRef == Referral.SAME) {
				if (correctSideLines[i] == null) {
					if (opositeSideLines[i] != null) {
						duplicatedCounter++;
						opositeCounter--;
					} else
						correctCounter++;
				}
				correctSideLines[i] = newLine;
			} else {
				if (subRef == Referral.DIFFERENT) {
					if (opositeSideLines[i] == null) {
						if (correctSideLines[i] != null) {
							duplicatedCounter++;
							correctCounter--;
						} else
							opositeCounter++;
					}
					opositeSideLines[i] = newLine;
				} else { // subRef == Referral.DUPLICATED
					if (opositeSideLines[i] == null) {
						duplicatedCounter++;
					}
					correctSideLines[i] = newLine;
					opositeSideLines[i] = newLine;
				}
			}
		}

		List<Line> getMatchedLines() throws NotAllRuleLinesRecognized {
			if (size == correctCounter + duplicatedCounter) {
				List<Line> result = new ArrayList<>(size);
				for (Line line : correctSideLines) {
					result.add(line);
				}
				return result;
			}
			if (size == opositeCounter + duplicatedCounter) {
				List<Line> result = new ArrayList<>(size);
				for (Line line : opositeSideLines) {
					result.add(line);
				}
				return result;
			}
			throw new NotAllRuleLinesRecognized("The lines was not found on the same side");
		}

		boolean needsToBeMirrored() {
			return size != correctCounter + duplicatedCounter;
		}
	}

	private enum Referral {
		SAME, DIFFERENT, DUPLICATED;
		private String value;
		static {
			SAME.value = "Same as original";
			DIFFERENT.value = "DIFFERENT TO ORIGINAL";
			DUPLICATED.value = "LINE IS DUPLICATED";
		}

		@Override
		public String toString() {
			return "" + value;
		}

		static Referral checkMirroringSide(double k, Marker marker, Dist distanses, Line newLine) {
			double precision = 1e-13;

			double r_mb_la = distanses.mb_la; // RULE LINE
			double r_mb_lb = distanses.mb_lb; // RULE LINE
			double l_mb_la = MainData.distans(newLine.pa.x, newLine.pa.y, marker.getbx() * 1., marker.getby() * 1.);
			double l_mb_lb = MainData.distans(newLine.pb.x, newLine.pb.y, marker.getbx() * 1., marker.getby() * 1.);

			double r_md_la = distanses.md_la; // RULE LINE
			double r_md_lb = distanses.md_lb; // RULE LINE
			double l_md_la = MainData.distans(newLine.pa.x, newLine.pa.y, marker.getdx() * 1., marker.getdy() * 1.);
			double l_md_lb = MainData.distans(newLine.pb.x, newLine.pb.y, marker.getdx() * 1., marker.getdy() * 1.);

			if (Math.abs(r_mb_la - r_md_la) < precision && Math.abs(r_md_lb - r_mb_lb) < precision) {
				return Referral.DUPLICATED;
			}

			if ((Math.abs(r_mb_la - l_mb_lb * k) < precision && Math.abs(r_mb_lb - l_mb_la * k) < precision
					&& Math.abs(r_md_la - l_md_lb * k) < precision && Math.abs(r_md_lb - l_md_la * k) < precision)
					|| (Math.abs(r_mb_la - l_mb_la * k) < precision && Math.abs(r_mb_lb - l_mb_lb * k) < precision
							&& Math.abs(r_md_lb - l_md_lb * k) < precision
							&& Math.abs(r_md_la - l_md_la * k) < precision)) {

				return Referral.SAME;
			}
			return Referral.DIFFERENT;
		}
	}

	private class Dist {
		//
		double ms_la;
		double ms_lb;

		double ma_la;
		double ma_lb;

		double mb_la;
		double mb_lb;

		double md_la;
		double md_lb;

		double precision = 1e-13;

		//
		Dist(Line line, Marker marker) {
			double[] line_coordinates = line.getDoubleCoordinates();
			this.ms_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.p.x * 1., marker.p.y * 1.);
			this.ms_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.p.x * 1., marker.p.y * 1.);

			this.ma_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.getax() * 1.,
					marker.getay() * 1.);
			this.ma_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.getax() * 1.,
					marker.getay() * 1.);

			this.mb_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.getbx() * 1.,
					marker.getby() * 1.);
			this.mb_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.getbx() * 1.,
					marker.getby() * 1.);

			this.md_la = MainData.distans(line_coordinates[0], line_coordinates[1], marker.getdx() * 1.,
					marker.getdy() * 1.);
			this.md_lb = MainData.distans(line_coordinates[2], line_coordinates[3], marker.getdx() * 1.,
					marker.getdy() * 1.);
		}

		@Override
		public String toString() {
			StringJoiner info = new StringJoiner(" ");
			return info.add(Double.toString(ms_la)).add(Double.toString(ms_lb)).add(Double.toString(ma_la))
					.add(Double.toString(ma_lb)).toString();
		}

		Referral compareLine(Dist inputDist, double k) {
			if ((Math.abs(this.ms_lb - inputDist.ms_lb * k) < precision
					&& Math.abs(this.ma_la - inputDist.ma_la * k) < precision)
					|| (Math.abs(this.ms_la - inputDist.ms_lb * k) < precision
							&& Math.abs(this.ma_lb - inputDist.ma_la * k) < precision)) {

				if (Math.abs(this.mb_la - this.md_la) < precision && Math.abs(this.md_lb - this.mb_lb) < precision) {
					return Referral.DUPLICATED;
				}

				if ((Math.abs(this.mb_la - inputDist.mb_lb * k) < precision
						&& Math.abs(this.mb_lb - inputDist.mb_la * k) < precision
						&& Math.abs(this.md_la - inputDist.md_lb * k) < precision
						&& Math.abs(this.md_lb - inputDist.md_la * k) < precision)
						|| (Math.abs(this.mb_la - inputDist.mb_la * k) < precision
								&& Math.abs(this.mb_lb - inputDist.mb_lb * k) < precision
								&& Math.abs(this.md_lb - inputDist.md_lb * k) < precision
								&& Math.abs(this.md_la - inputDist.md_la * k) < precision)) {

					return Referral.SAME;
				}
				return Referral.DIFFERENT;
			}
			return null;
		}

		// returns distans between the first point (A) of the line section and the
		// middle of the marker
		double getMSLA() {
			return ms_la;
		}

		// returns distans between the second point (B) of the line section and the
		// middle of the marker
		double getMSLB() {
			return ms_lb;
		}

		// returns distans between the first point (A) of the line section and the A
		// point of the marker
		double getMALA() {
			return ma_la;
		}

		// returns distans between the second point (B) of the line section and the A
		// point of the marker
		double getMALB() {
			return ma_lb;
		}

	}

	public class PointDoesNotExist extends Exception {
		public static final long serialVersionUID = 42L;

		public PointDoesNotExist(String message) {
			super(message);
		}

		public PointDoesNotExist() {
			super("Point was not found");
		}
	}

	class NotAllRuleLinesRecognized extends Exception {
		public static final long serialVersionUID = 42L;

		public NotAllRuleLinesRecognized(String message) {
			super(message);
		}

		public NotAllRuleLinesRecognized() {
			super("Not All Rule Lines Recognized");
		}
	}
}

