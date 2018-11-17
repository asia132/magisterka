package grammar_graphs;

import java.util.List;
import java.util.StringJoiner;

import java.awt.RenderingHints;
import java.awt.Graphics2D;

class PaintingRuleLevels {
	static Level levels[];
	Level limitingShape = new Level("Limit Shape");
	private int n = 0;
	int max_n_allowed;
	private boolean n_change = false;

	PaintingRuleLevels(MainPanel panel){
		this.n = 0;
		this.max_n_allowed = 100;
		PaintingRuleLevels.levels = new Level [100];

		for (int i = 0; i < this.max_n_allowed; ++i)
			PaintingRuleLevels.levels[i] = new Level(i + "");
		// this.print();
	}
	void setMaxNAllowed(int max_n){
		for (int i = this.max_n_allowed; i < max_n; ++i){
			if (PaintingRuleLevels.levels[i] == null)
				PaintingRuleLevels.levels[i] = new Level(i + "");
		}
		this.max_n_allowed = max_n;
	}
	int getN(){
		return n;
	}
	void setN(int n){
		for (int i = this.n; i <n; ++i){
			levels[i].closeLevel();
		}
		this.n = n;
	}
	void increaseN(){
		System.out.println("INCREASE N");
		levels[n].closeLevel();
		this.n++;
	}
	void updateLevel0(Line newLine){
		PaintingRuleLevels.levels[0].levelLines.add(newLine);
	}
	void updateLevel0(List <Line> newLines){
		PaintingRuleLevels.levels[0].levelLines = newLines;
	}
	void updateWithRule(Rule.Category ruleCat, List <Line> ruleInitialLines, List <Line> ruleFinalLines, MainData programData){
		if (ruleCat == Rule.Category.A){ // left side => n
			this.increaseN();
			levels[n].update(ruleInitialLines);
			levels[n].closeLevel();
		}else if (ruleCat == Rule.Category.B){ // left side => n | right side => n+1
			if (this.n_change){
				if (n+1 < max_n_allowed){
					this.increaseN();
				}
				this.n_change = false;
			}
			if (n != 0){
				levels[n].update(ruleInitialLines);
			}
			if (n+1 < max_n_allowed){
				levels[n+1].update(ruleInitialLines);
			}	
		}else if (ruleCat == Rule.Category.C){ // right side - left side => n+1
			programData.lines.lines.addAll(ruleFinalLines);
			if (n+1 < max_n_allowed){
				System.out.println("Add lines to L" + (n + 1));
				levels[n+1].levelLines.addAll(ruleFinalLines);
			}
			this.n_change = true;
		}
	}
	String limitingShapeToString(){
		StringJoiner info = new StringJoiner("");
		for (Line line: limitingShape.levelLines){
			info.add(FileSaverTags.LIMITSHAPETAG.toString()).add("\t").add(FileSaverTags.ISIDETAG.toString()).add("\t").add(line.toString()).add("\n");
		}
		return info.toString();
	}
	void paintLevels(Graphics2D g2d){
		// this.print();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		if (limitingShape.levelLines.size() > 0){
			try{
				g2d.setPaint(limitingShape.getColor());				
				g2d.fill(limitingShape.getShape()); 
			}catch (NotClosedShape e) {;
				Settings.setColorRules();
				new MessageFrame(e.getMessage() + ". Limiting shape.");
				System.out.println(e.getMessage());
			}
		}

		try{
			Level level = levels[0];
			g2d.setPaint(level.getColor());			
			g2d.fill(level.getShape());
		}catch (NotClosedShape e) {;
			Settings.setColorRules();
			new MessageFrame(e.getMessage() + ". Level index: 0");
			System.out.println(e.getMessage());
		}

		for (int i = 1; i <= n; ++i) {
			try{
				Level level = levels[i];
				if (level.levelLines.size() <= 2) continue;
				g2d.setPaint(level.getColor());		
				g2d.fill(level.getShape());

			}catch (NotClosedShape e) {;
				Settings.setColorRules();
				new MessageFrame(e.getMessage() + ". Level index: " + i);
				System.out.println(e.getMessage() + ". Level index: " + i);
			}
		}
		g2d.dispose();
	}
	static class NotClosedShape extends Exception {
		public static final long serialVersionUID = 42L;
		NotClosedShape(){
			super("The shape is not closed");
		}
		NotClosedShape(String message){
			super("The shape is not closed. " + message);
		}
	}
}
