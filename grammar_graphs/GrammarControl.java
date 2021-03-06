package grammar_graphs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

final class GrammarControl {
	private static final GrammarControl INSTANCE = new GrammarControl();
	

	PaintingRuleLevels paintingRuleLevels = null;
	List <Line> copiedLines = new ArrayList<Line>();

	File file = null;
	
	List <PaintingRule> rulePainting = new ArrayList<>();
	List <Rule> ruleList = new ArrayList<>(); 
	List <Rule> ruleAppList = new ArrayList<>();
	
	private List <MainPanel> panels = new ArrayList<>();
	
	private GrammarControl() {
		if (INSTANCE != null) {
			throw new AssertionError();
		}
	}
	void clear() {
		this.copiedLines.clear();
		this.rulePainting.clear();
		this.ruleList.clear();
		this.ruleAppList.clear();
		this.file = null;
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
	void moveLimitShape(int x1, int y1, int x2, int y2) {
		for (Line line: paintingRuleLevels.limitingShape.levelLines) {
			line.move(x2 - x1, y2 - y1);
		}
	}
	List <Line> getLimitShapeLines(){
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
