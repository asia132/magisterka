package grammar_graphs;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;

import java.awt.Point;

import java.util.ArrayList;

class FileSaver{
	File file;
	static String inputTag = "#INPUT";
	static String iSideTag = "#I";
	static String markerTag = "#M";
	static String lineTag = "#L";
	static String aSideTag = "#A";
	static String bSideTag = "#B";
	static String ruleList = "#RULELIST";
	static String limitShapeTag = "#LIMITSHAPE";
	static String level = "#LEVEL";
	static String n = "#N";

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
		marker.dir = Direct.parseValue(markerData[3]);
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
			MainData.coloringRuleLevels = new ColoringRuleLevels(panel);
			panel.programData.clear();
			String line = null;
			ArrayList <Line> linesASide = new ArrayList<>();
			Marker markerASide = new Marker();
			ArrayList <Line> linesBSide = new ArrayList<>();
			Marker markerBSide = new Marker();
			String newRuleName = "";
			String [] lineContent =  new String [1];
			while ((line = reader.readLine()) != null) {
				lineContent = line.split("\t");
				if (ruleList.equals(lineContent[0])) {
					if (!newRuleName.equals("") && markerASide != null){
						try{
							linesBSide.addAll(linesASide);
							if (markerBSide != null)	panel.programData.ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), markerBSide.copy()));
							else	panel.programData.ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), null));
						}catch (Exception e) {
							System.out.println("WARNING: Rule was not added. " + e.getMessage());
						}
					}
					break;
				}
				if (n.equals(lineContent[0])){
					MainData.coloringRuleLevels.setN(Integer.parseInt(lineContent[1]));
				} else if (level.equals(lineContent[0])){
					panel.programData.addLine(this.parseLine(lineContent), Integer.parseInt(lineContent[1].substring(1)));
				} else if (limitShapeTag.equals(lineContent[0]))
					MainData.coloringRuleLevels.limitingShape.levelLines.add(this.parseLine(lineContent));
				else if (inputTag.equals(lineContent[0])){
					if (markerTag.equals(lineContent[2])){
						try{
							panel.programData.marker = this.parseMarker(lineContent);
						}catch (Exception e) {
							System.out.println("WARNING: Marker was not added to input picture. " + e.getMessage());
						}
					}else{
						panel.programData.addLine(this.parseLine(lineContent), false);
					}
				}else{
					String ruleName = lineContent[0].substring(1);
					if (!newRuleName.equals(ruleName)){
						if (!newRuleName.equals("")){
							try{
								linesBSide.addAll(linesASide);
								if (markerBSide != null)	panel.programData.ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), markerBSide.copy()));
								else panel.programData.ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), null));
							}catch (Exception e) {
								System.out.println("WARNING: Rule " + newRuleName + " was not added. " + e.getMessage());
								// System.out.println((markerBSide.copy() == null) + " " + (markerASide.copy() == null));
							}
						}
						newRuleName = ruleName;
						linesASide = new ArrayList<>();
						linesBSide = new ArrayList<>();
						markerBSide = null;
						markerASide = null;
					}
					if (newRuleName.equals(ruleName)){
						if (aSideTag.equals(lineContent[1])){
							if (lineTag.equals(lineContent[2]))
								linesASide.add(this.parseLine(lineContent));
							if (markerTag.equals(lineContent[2])){
								try{
									markerASide = this.parseMarker(lineContent);
								}catch (Exception e) {
									System.out.println("WARNING: Marker was not added to A side of Rule. " + e.getMessage());
								}
							}
						}else{
							if (lineContent.length > 2 && markerTag.equals(lineContent[2])){
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
			if (ruleList.equals(lineContent[0])){
				panel.programData.ruleAppList.add(MainData.getRuleOfName(lineContent[1]));
				while ((line = reader.readLine()) != null) {
					lineContent = line.split("\t");
					panel.programData.ruleAppList.add(MainData.getRuleOfName(lineContent[1]));
				}
			}else{
				if (!newRuleName.equals("") && markerASide != null){
					try{
						linesBSide.addAll(linesASide);
						if (markerBSide != null)	panel.programData.ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), markerBSide.copy()));
						else	panel.programData.ruleList.add(new Rule(newRuleName, getArrayCopy(linesASide), getArrayCopy(linesBSide), markerASide.copy(), null));
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