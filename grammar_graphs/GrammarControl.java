package grammar_graphs;

import java.io.File;
import java.util.ArrayList;
import java.util.StringJoiner;

final class GrammarControl {
	private static final GrammarControl INSTANCE = new GrammarControl();
	

	PaintingRuleLevels paintingRuleLevels = null;
	ArrayList <Line> copiedLines = new ArrayList<Line>();

	File file = null;
	
	ArrayList <PaintingRule> rulePainting = new ArrayList<>();
	ArrayList <Rule> ruleList = new ArrayList<>(); 
	ArrayList <Rule> ruleAppList = new ArrayList<>();
	
	private ArrayList <MainPanel> panels = new ArrayList<>();
	
	private GrammarControl() {
		if (INSTANCE != null) {
			throw new AssertionError();
		}
	}
	static GrammarControl getInstance() {
		return INSTANCE;
	}
	static void addPanel(MainPanel panel) {
		GrammarControl.getInstance().panels.add(panel);
		System.out.println("PANELS QTY: " + GrammarControl.getInstance().panels.size());
	}
	static void removePanel(MainPanel panel) {
		GrammarControl.getInstance().panels.remove(panel);
		System.out.println("PANELS QTY: " + GrammarControl.getInstance().panels.size());
	}
	static int getPanelsQty(MainPanel panel) {
		return GrammarControl.getInstance().panels.size();
	}
	static void repaintAll() {
		for (MainPanel panel: GrammarControl.getInstance().panels) {
			panel.repaint();
		}
	}
	ArrayList <Line> getLimitShapeLines(){
		return paintingRuleLevels.limitingShape.levelLines;
	}
	Rule getRuleOfName(String name){
		for (Rule rule : INSTANCE.ruleList)
			if (rule.getName().equals(name))
				return rule;
		return null;
	}
	String levelsToString(){
		StringJoiner info = new StringJoiner("");
		for (int n = 0; n < GrammarControl.getInstance().paintingRuleLevels.getN() + 1; ++n){
			for (Line line: PaintingRuleLevels.levels[n].levelLines){
				info.add(FileSaverTags.LEVEL.toString()).add("\t").add("L"+n).add("\t").add(line.toString()).add("\n");
			}
		}
		return info.toString();
	}
	String rulesToString(){
		StringJoiner info = new StringJoiner("");
		for (Rule rule : GrammarControl.getInstance().ruleList){
			info.add(rule.toString());
		}
		return info.toString();
	}
	String limitShapeToString(){
		return GrammarControl.getInstance().paintingRuleLevels.limitingShapeToString();
	}
}
