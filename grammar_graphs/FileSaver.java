package grammar_graphs;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;

import java.awt.Point;

import java.util.ArrayList;
import java.awt.Color;

class FileSaver{
	File file;

	FileSaver(File file){
		this.file = file;
	}
	String getName(){
		return file.getName();
	}
	void saveDataFile(MainPanel panel){
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
			writer.write(panel.toString());
			writer.close();
		}catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	Marker parseMarker(String [] markerData) throws IllegalArgumentException{
		Marker marker = new Marker(new Point(Integer.parseInt(markerData[5]), Integer.parseInt(markerData[6])));
		marker.r = Integer.parseInt(markerData[4]);
		marker.dir = Marker.Direct.parseValue(markerData[3]);
		return marker;
	}
	Line parseLine(String [] lineData){
		return new Line(new Point(Integer.parseInt(lineData[3]), Integer.parseInt(lineData[4])), new Point(Integer.parseInt(lineData[5]), Integer.parseInt(lineData[6])));
	}
	ArrayList <Line> getArrayCopy(ArrayList <Line> lineSet){
		ArrayList <Line> newLineSet = new ArrayList<>();
		for (Line line: lineSet) {
			newLineSet.add(line.copy());
		}
		return newLineSet;
	}
	void openDataFile(MainPanel panel){
		try (BufferedReader reader = new BufferedReader(new FileReader(file))){
			GrammarControl.getInstance().paintingRuleLevels = new PaintingRuleLevels(panel);
			panel.programData.clear();
			String line = null;
			ArrayList <Line> linesASide = new ArrayList<>();
			Marker markerASide = null;
			ArrayList <Line> linesBSide = new ArrayList<>();
			Marker markerBSide = null;
			String newRuleName = "";
			String [] lineContent =  new String [1];
			while ((line = reader.readLine()) != null) {
				lineContent = line.split("\t");
				if (FileSaverTags.RULELIST.equals(lineContent[0])) {
					if (!newRuleName.equals("") && markerASide != null){
						try{
							linesBSide.addAll(linesASide);
							if (markerBSide != null)	GrammarControl.getInstance().ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), markerBSide.copy()));
							else	GrammarControl.getInstance().ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), null));
						}catch (Exception e) {
							System.out.println("WARNING: Rule was not added. " + e.getMessage());
						}
					}
					break;
				}
				if (FileSaverTags.N.equals(lineContent[0])){
					GrammarControl.getInstance().paintingRuleLevels.setN(Integer.parseInt(lineContent[1]));
				} else if (FileSaverTags.PAINTINGRULE.equals(lineContent[0])){
					GrammarControl.getInstance().paintingRuleLevels.limitingShape.closeLevel();
					for (int i = 0; i <= GrammarControl.getInstance().paintingRuleLevels.getN(); ++i){
						PaintingRuleLevels.levels[i].closeLevel();
					}
					Color color = new Color(Integer.parseInt(lineContent[1]));
					boolean isApp = Boolean.parseBoolean(lineContent[2]);
					String [] tagsSet = lineContent[3].split(",");
					GrammarControl.getInstance().rulePainting.add(new PaintingRule(isApp, color, tagsSet));

				} else if (FileSaverTags.LEVEL.equals(lineContent[0])){

					panel.programData.lines.addLine(this.parseLine(lineContent), Integer.parseInt(lineContent[1].substring(1)));

				} else if (FileSaverTags.LIMITSHAPETAG.equals(lineContent[0])){
					Line shapeLine = this.parseLine(lineContent);
					shapeLine.changeColor(Settings.default_check_marker_color);
					GrammarControl.getInstance().paintingRuleLevels.limitingShape.levelLines.add(shapeLine);
				}else if (FileSaverTags.INPUTTAG.equals(lineContent[0])){
					if (FileSaverTags.MARKERTAG.equals(lineContent[2])){
						try{
							panel.programData.marker = this.parseMarker(lineContent);
						}catch (Exception e) {
							System.out.println("WARNING: Marker was not added to input picture. " + e.getMessage());
						}
					}else{
						panel.programData.lines.addLine(this.parseLine(lineContent), true);
					}
				}else{
					String ruleName = lineContent[0].substring(1);
					if (!newRuleName.equals(ruleName)){
						if (!newRuleName.equals("")){
							try{
								linesBSide.addAll(linesASide);
								if (markerBSide != null)	GrammarControl.getInstance().ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), markerBSide.copy()));
								else GrammarControl.getInstance().ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), null));
							}catch (Exception e) {
								System.out.println("WARNING: Rule " + newRuleName + " was not added. " + e.getMessage());
							}
						}
						newRuleName = ruleName;
						linesASide = new ArrayList<>();
						linesBSide = new ArrayList<>();
						markerBSide = null;
						markerASide = null;
					}
					if (newRuleName.equals(ruleName)){
						if (FileSaverTags.ASIDETAG.equals(lineContent[1])){
							if (FileSaverTags.LINETAG.equals(lineContent[2]))
								linesASide.add(this.parseLine(lineContent));
							if (FileSaverTags.MARKERTAG.equals(lineContent[2])){
								try{
									markerASide = this.parseMarker(lineContent);
								}catch (Exception e) {
									System.out.println("WARNING: Marker was not added to A side of Rule. " + e.getMessage());
								}
							}
						}else{
							if (lineContent.length > 2 && FileSaverTags.MARKERTAG.equals(lineContent[2])){
								try{
									markerBSide = this.parseMarker(lineContent);
								}catch (Exception e) {
									System.out.println("WARNING: Marker was not added to B side of Rule. " + e.getMessage());
								}
							}else{
								if (lineContent.length > 6)
									linesBSide.add(this.parseLine(lineContent));
							}
						}
					}
				}
			}
			if (FileSaverTags.RULELIST.equals(lineContent[0])){
				GrammarControl.getInstance().ruleAppList.add(GrammarControl.getInstance().getRuleOfName(lineContent[1]));
				while ((line = reader.readLine()) != null) {
					lineContent = line.split("\t");
					GrammarControl.getInstance().ruleAppList.add(GrammarControl.getInstance().getRuleOfName(lineContent[1]));
				}
			}else{
				if (!newRuleName.equals("") && markerASide != null){
					try{
						linesBSide.addAll(linesASide);
						if (markerBSide != null)	GrammarControl.getInstance().ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), markerBSide.copy()));
						else	GrammarControl.getInstance().ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), null));
					}catch (Exception e) {
						System.out.println("WARNING: Rule was not added. " + e.getMessage());
					}
				}
			}
			reader.close();
			
			panel.repaint();
		}catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}