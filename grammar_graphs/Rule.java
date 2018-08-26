package grammar_graphs;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.lang.Math;


final class Rule{
	String name;
	Shape initialshape;
	Shape finalshape;
	Category cat;

	Rule(String name, ArrayList <Line> initialLines, ArrayList <Line> finalLines, 
		Marker initialmarker, Marker finalmarker) throws NoMarkerException, MarkerRemovingRule, WrongNameException{
		if (initialmarker == null){
			throw new NoMarkerException("Please, add a marker on the left site of the rule");
		}

		if (name.contains(" ") || name.contains("\t")){
			throw new WrongNameException();
		}

		this.name = name;
		this.initialshape = new Shape(initialLines, initialmarker, "Rule A site");

		ArrayList <Line> addedLines = MainData.RelativeComplement(finalLines, initialLines);
		if (finalmarker != null)
			this.finalshape = new Shape(addedLines, finalmarker);
		else{
			this.finalshape = new Shape();
			if (!addedLines.isEmpty())
				throw new MarkerRemovingRule();
		}

		if (addedLines.isEmpty()){
			if (finalmarker == null)	cat = Category.A;
			else	cat = Category.B;
		}else{
			cat = Category.C;
		}
	}
	Category getCategory(){
		return this.cat;
	}
	void apply(MainPanel panel) throws NoMarkerException{
		if (panel.programData.marker == null)
			throw new NoMarkerException("Please add a marker to main panel\n");
		if (initialshape.marker == null)
			throw new NoMarkerException("Please add a marker to left side of rule\n");
		Shape inputshape = new Shape(panel.programData.getLines(), panel.programData.marker, "Input");

		System.out.println(cat.getChar() + " " + cat.toString());


		ArrayList <Line> found_lines = initialshape.findMatch(inputshape);
		if (finalshape != null)
			finalshape.needsToBeMirrored = inputshape.needsToBeMirrored;

		if (found_lines.size() >= initialshape.lines_dist.size()){
			if (finalshape != null && finalshape.marker != null){
				Marker inputMarker = panel.programData.marker.copy();

				try{
					double k = initialshape.marker.length() / inputshape.marker.length();
					panel.programData.marker.move((int)((finalshape.marker.getX() - initialshape.marker.getX())/k), (int)((finalshape.marker.getY() - initialshape.marker.getY())/k), inputshape.marker.calcRotation(initialshape.marker.dir));
					panel.programData.marker.rotateBasedOnDirSub(initialshape.marker.dir, finalshape.marker.dir, finalshape.needsToBeMirrored);
				
					panel.programData.marker.scale(this.findMarkerScaleParam());
					if (finalshape.needsToBeMirrored){
						System.out.println("MIRROR");
						if (inputMarker.dir.equals(Direct.N) || inputMarker.dir.equals(Direct.S)){
							panel.programData.marker.mirrorX(inputMarker.p.x);
						}
						else{
							panel.programData.marker.mirrorY(inputMarker.p.y);
						}
					}
					ArrayList <Line> finalLines = finalshape.setInPlace(inputMarker, initialshape.marker);
					panel.programData.addLinesByRule(finalLines);
					MainData.coloringRule.updateWithRule(this.cat, found_lines, finalLines);
				}catch (Exception e) {
					new MessageFrame("Rule could not be applicated. " + e.getMessage());
					panel.programData.marker = inputMarker;
				}
			}
			else{
				panel.programData.marker = null;
			}
			panel.repaint();
		}
	}
	@Override
	public String toString(){
		StringJoiner info = new StringJoiner("");
		info.add(initialshape.markerToString(name, FileSaver.aSideTag));
		info.add(initialshape.linesDistToString(name, FileSaver.aSideTag));
		if (finalshape != null){
			if (finalshape.marker != null)	info.add(finalshape.markerToString(name, FileSaver.bSideTag));
			info.add(finalshape.linesDistToString(name, FileSaver.bSideTag));
		}
		return info.toString();
	}
	String getName(){
		return this.name;
	}
	Marker getFinalMarker(){
		if (cat == Category.A)	return null;
		return finalshape.marker;
	} 
	Marker getInitialMarker(){
		return initialshape.marker;
	}
	ArrayList <Line> getInitialLines(){
		return initialshape.getLines();
	}
	ArrayList <Line> getFinalLines(){
		if (cat == Category.C) return finalshape.getLines();
		return null;
	}
	double findMarkerScaleParam(){
		return 1.*finalshape.marker.r / (1.*initialshape.marker.r);
	}
	public class NoMarkerException extends Exception {
		public NoMarkerException(String message) {
			super(message);
		}
	}
	public class WrongNameException extends Exception {
		public WrongNameException() {
			super("The name cannot contains white characters, like space, tab or enter.");
		}
		public WrongNameException(String message) {
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
}
enum Referral{
	SAME, DIFFERENT;
	private String value;
	private int num;
	static{
		SAME.value = "Same as original";
		DIFFERENT.value = "DIFFERENT TO ORIGINAL";

		SAME.num = 1;
		DIFFERENT.num = 0;
	}
	@Override
	public String toString(){
		return "" + value;
	}
	int getNum(){
		return num;
	}
}
enum Category{
	A, B, C;
	private String value;
	private char index;
	private int num;
	static{
		A.value = "RIGH SIDE CONTAINS TERMINAL LIST, SAME AS LEFT";
		B.value = "RIGH SIDE CONTAINS TERMINAL LIST, SAME AS LEFT AND AN MARKER";
		C.value = "RIGH SIDE CONTAINS TERMINAL LIST, SAME AS LEFT WITH OTHER TERMINALS AND AN MARKER";

		A.index = 'A';
		B.index = 'B';
		C.index = 'C';

		A.num = 1;
		B.num = 2;
		C.num = 3;
	}
	@Override
	public String toString(){
		return "" + value;
	}
	char getChar(){
		return index;
	}
	int getNum(){
		return num;
	}
}