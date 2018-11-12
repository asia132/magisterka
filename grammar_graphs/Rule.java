package grammar_graphs;

import java.util.ArrayList;
import java.util.StringJoiner;


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
		Shape inputshape = new Shape(panel.programData.lines.getLines(), panel.programData.marker, "Input");

		try{
			ArrayList <Line> found_lines = initialshape.findMatch(inputshape);
			if (finalshape != null){
				finalshape.needsToBeMirrored = inputshape.needsToBeMirrored;
			}
			if (found_lines.size() >= initialshape.lines_dist.size()){
				System.out.println(" - end with success----------");
				if (finalshape != null && finalshape.marker != null){
					Marker inputMarker = panel.programData.marker.copy();
					try{
						double k = initialshape.marker.length() / inputshape.marker.length();
						panel.programData.marker.move((int)((finalshape.marker.getX() - initialshape.marker.getX())/k), (int)((finalshape.marker.getY() - initialshape.marker.getY())/k), inputshape.marker.calcRotation(initialshape.marker.dir));
						panel.programData.marker.rotateBasedOnDirSub(initialshape.marker.dir, finalshape.marker.dir, finalshape.needsToBeMirrored);
					
						panel.programData.marker.scale(this.findMarkerScaleParam());
						if (finalshape.needsToBeMirrored){
							if (inputMarker.dir.equals(Marker.Direct.N) || inputMarker.dir.equals(Marker.Direct.S)){
								panel.programData.marker.mirrorX(inputMarker.p.x);
							}
							else{
								panel.programData.marker.mirrorY(inputMarker.p.y);
							}
						}
						System.out.println("INPUT MARKER COPY: " + inputMarker);
						ArrayList <Line> finalLines = finalshape.setInPlace(inputMarker, initialshape.marker);
						if (Settings.CLOSED_SHAPES)
							GrammarControl.getInstance().paintingRuleLevels.updateWithRule(this.cat, found_lines, finalLines, panel.programData);
						else
							panel.programData.lines.addLinesByRule(finalLines);
					}catch (Marker.ToSmallRException e) {
						new MessageFrame("Rule could not be applicated. " + e.getMessage());
						panel.programData.marker = inputMarker;
						System.out.println("Rule apply: " + e.getLocalizedMessage());
					}
				}
				else{
					panel.programData.marker = null;
					if (Settings.CLOSED_SHAPES)
						GrammarControl.getInstance().paintingRuleLevels.updateWithRule(this.cat, found_lines, null, panel.programData);
				}
				panel.repaint();
			}else{
				System.out.println(" - end without success----------");
			}
		}catch(Shape.NotAllRuleLinesRecognized error){
			System.out.println(" - end without success----------");
			System.out.println(error.getMessage());;
		}
	}
	@Override
	public String toString(){
		StringJoiner info = new StringJoiner("");
		info.add(initialshape.markerToString(name, FileSaverTags.ASIDETAG.toString()));
		info.add(initialshape.linesDistToString(name, FileSaverTags.ASIDETAG.toString()));
		if (finalshape != null){
			if (finalshape.marker != null)	info.add(finalshape.markerToString(name, FileSaverTags.BSIDETAG.toString()));
			info.add(finalshape.linesDistToString(name, FileSaverTags.BSIDETAG.toString()));
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
		public static final long serialVersionUID = 42L;
		public NoMarkerException(String message) {
			super(message);
		}
	}
	public class WrongNameException extends Exception {
		public static final long serialVersionUID = 42L;
		public WrongNameException() {
			super("The name cannot contains white characters, like space, tab or enter.");
		}
		public WrongNameException(String message) {
			super(message);
		}
	}
	public class MarkerRemovingRule extends Exception {
		public static final long serialVersionUID = 42L;
		public MarkerRemovingRule(String message) {
			super(message);
		}
		public MarkerRemovingRule() {
			super("The rule was designed to remove the marker from input.\nIt will not add any of definded line.\n");
		}
	}
	enum Category{
		A('A', 1, "RIGH SIDE CONTAINS TERMINAL LIST, SAME AS LEFT"), 
		B('B', 2, "RIGH SIDE CONTAINS TERMINAL LIST, SAME AS LEFT AND AN MARKER"), 
		C('C', 3, "RIGH SIDE CONTAINS TERMINAL LIST, SAME AS LEFT WITH OTHER TERMINALS AND AN MARKER");
		private String value;
		private char index;
		private int num;
		Category(char index, int num, String value){
			this.value = value;
			this.index = index;
			this.num = num;
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
}
