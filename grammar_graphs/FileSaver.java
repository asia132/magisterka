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
	void openDataFile(MainPanel panel){
		try (BufferedReader reader = new BufferedReader(new FileReader(file))){
			panel.programData.clear();
			String line = null;
			ArrayList <Line> linesASide = new ArrayList<>();
			Marker markerASide = new Marker();
			ArrayList <Line> linesBSide = new ArrayList<>();
			Marker markerBSide = new Marker();
			String newRuleName = "";
			while ((line = reader.readLine()) != null) {
				String [] lineContent = line.split("\t");
				if (inputTag.equals(lineContent[0])){
					if (markerTag.equals(lineContent[2])){
						try{
							panel.programData.marker = this.parseMarker(lineContent);
						}catch (Exception e) {
							System.out.println("WARNING: Marker was not added to input picture. " + e.getMessage());
						}
					}else{
						panel.programData.lines.add(this.parseLine(lineContent));
					}
				}else{
					String ruleName = lineContent[0].substring(1);
					if (newRuleName.equals(ruleName)){
						if (aSideTag.equals(lineContent[1])){
							linesASide.add(this.parseLine(lineContent));
						}else{
							if (markerTag.equals(lineContent[2])){
								try{
									markerBSide = this.parseMarker(lineContent);
								}catch (Exception e) {
									System.out.println("WARNING: Marker was not added to B side of Rule. " + e.getMessage());
								}
							}else{
								linesBSide.add(this.parseLine(lineContent));
							}
						}
					}else{
						if (!newRuleName.equals("") && markerBSide != null  && linesBSide.size() > 0){
							try{
								panel.programData.ruleList.add(new Rule(newRuleName, linesASide, linesBSide, markerASide, markerBSide));
							}catch (Exception e) {
								System.out.println("WARNING: Rule was not added. " + e.getMessage());
							}
						}
						newRuleName = ruleName;
						linesASide = new ArrayList<>();
						linesBSide = new ArrayList<>();
						try{
							markerASide = this.parseMarker(lineContent);
						}catch (Exception e) {
								System.out.println("WARNING: Marker was not added to A side of Rule. " + e.getMessage());
							}
						markerBSide = null;
					}
				}
			}
			if (!newRuleName.equals("") && markerASide != null){
				try{
					linesBSide.addAll(linesASide);
					panel.programData.ruleList.add(new Rule(newRuleName, linesASide, linesBSide, markerASide, markerBSide));
				}catch (Exception e) {
					System.out.println("WARNING: Rule was not added. " + e.getMessage());
				}
			}
			reader.close();
			panel.repaint();
		}catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}