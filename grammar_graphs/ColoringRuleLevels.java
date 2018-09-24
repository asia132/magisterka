package grammar_graphs;

import java.util.ArrayList;
import java.util.StringJoiner;

import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;
import java.lang.Math;

class ColoringRuleLevels {
	static Level levels[];
	Level limitingShape = new Level();
	int n;
	int max_n_allowed;
	private boolean n_change = false;

	ColoringRuleLevels(MainPanel panel){
		this.n = 0;
		this.max_n_allowed = 10;
		this.levels = new Level [100];

		for (int i = 0; i < this.max_n_allowed; ++i)
			this.levels[i] = new Level();
		// this.print();
	}
	void updateLevel0(Line newLine){
		this.levels[0].levelLines.add(newLine);
	}
	void updateLevel0(ArrayList <Line> newLines){
		this.levels[0].levelLines = newLines;
	}
	void updateWithRule(Category ruleCat, ArrayList <Line> ruleInitialLines, ArrayList <Line> ruleFinalLines){
		if (ruleCat == Category.A){ // left side => n
			if (n != 0)	levels[n].update(ruleInitialLines);
		}else if (ruleCat == Category.B){ // left side => n | right side => n+1
			if (n != 0)	levels[n].update(ruleInitialLines);
			if (this.n_change){
				if (n+1 < max_n_allowed)	n++;
				this.n_change = false;
			}
			if (n+1 < max_n_allowed)	levels[n+1].update(ruleInitialLines);
		}else if (ruleCat == Category.C){ // right side - left side => n+1
			if (n+1 < max_n_allowed)	levels[n+1].update(ruleFinalLines);
			this.n_change = true;
		}
		// System.out.println("KONTROLA N: n = " + n + ", ilosc leveli: " + levels.length);
		// print();
	}
	void print(){
		System.out.println("Limiting shape");
		limitingShape.print();
		for (int i = 0; i <= n + 1; ++i){
			System.out.println("Level " + i);
			levels[i].print();
		}
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
				MainData.COLOR_RULES = false;
				new MessageFrame(e.getMessage() + ". Limiting shape.");
			}
		}

		for (int i = 0; i <= n; ++i) {
			try{
				Level level = levels[i];
				if (level.levelLines.size() <= 2) continue;

				g2d.setPaint(level.getColor());				
				g2d.fill(level.getShape());

			}catch (NotClosedShape e) {;
				MainData.COLOR_RULES = false;
				new MessageFrame(e.getMessage() + ". Level index: " + i);
			}
		}
		g2d.dispose();
	}
}
class NotClosedShape extends Exception {
	NotClosedShape(){
		super("The shape is not closed");
	}
	NotClosedShape(String message){
		super("The shape is not closed. " + message);
	}
}