package grammar_graphs;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.border.Border;

import java.awt.Color;

import java.util.Hashtable;
import java.util.ArrayList;

import java.io.File;



class PopUpMenu extends JPopupMenu {
	int index;

	PopUpMenu(MainPanel panel){
		if(!(panel instanceof LeftRulePanel) && !(panel instanceof RigthRulePanel)){
			showRulesChanging(panel);
			showRuleApplicationList(panel);
			showApplyRuleApplicationList(panel);
			showSimulate(panel);
			showResetOption(panel);
			showSaveFile(panel);
			showOpenFile(panel);
		}
		showViewSettings(panel);
		showElemEdit(panel);
		showMarker(panel);
	}  
// change rules
	void showRulesChanging(MainPanel panel){
		JMenu rulesMenu = new JMenu(ProgramLabels.rulleList);

		if (!panel.programData.isEmptyModified()){
			JMenuItem ruleforlineButton = new JMenuItem();
			ruleforlineButton.setText(ProgramLabels.rulleAddingForLines);
			ruleforlineButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					if (panel.programData.modified_marker != null)
						new CreateRuleFrame(panel.programData.getModified(), panel.programData.getModified(), panel.programData.marker.copy(), panel.programData.marker.copy());
					else 
						new CreateRuleFrame(panel.programData.getModified(), panel.programData.getModified(), null, null);

				}
			});
			Border border = BorderFactory.createLineBorder(Color.BLACK);
			ruleforlineButton.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
			rulesMenu.add(ruleforlineButton);
		}
		JMenuItem addRules = new JMenuItem(ProgramLabels.rulleAdding);
		addRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CreateRuleFrame(new ArrayList<Line>(), new ArrayList<Line>(), null, null);
			}
		});
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		addRules.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		rulesMenu.add(addRules);
		
		JMenu [] rulesList = new JMenu [panel.programData.ruleList.size()];
		int i = 0;

		for (Rule changedRule: panel.programData.ruleList){
			rulesList[i] = new JMenu(panel.programData.ruleList.get(i).getName());

			JMenuItem removeRule = new JMenuItem("Remove");
			removeRule.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					panel.programData.ruleList.remove(changedRule);
				}
			});
			rulesList[i].add(removeRule);

			JMenuItem applyRule = new JMenuItem("Apply");
			applyRule.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try{
						changedRule.apply(panel);
					}
					catch(Rule.NoMarkerException exc){
						MessageFrame error = new MessageFrame(exc.getMessage());
					}
				}
			});
			rulesList[i].add(applyRule);

			JMenuItem editRule = new JMenuItem("Edit");
			index = panel.programData.ruleList.indexOf(changedRule);
			editRule.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new CreateRuleFrame(index);
				}
			});
			rulesList[i].add(editRule);

			rulesMenu.add(rulesList[i]);
			i++;
		}
		add(rulesMenu);
	}
// change view settings
	void showViewSettings(MainPanel panel){
		JMenu panelSettings = new JMenu(ProgramLabels.panelSettings);
		if (panel.programData.default_background_color == Color.WHITE){
			JMenuItem blackMode = new JMenuItem(ProgramLabels.blackMode);
			blackMode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					panel.programData.changeColorModeToBlack();
					panel.repaint();
				}
			});
			panelSettings.add(blackMode);
		}else{
			JMenuItem whiteMode = new JMenuItem(ProgramLabels.whiteMode);
			whiteMode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					panel.programData.changeColorModeToWhite();
					panel.repaint();
				}
			});
			panelSettings.add(whiteMode);
		}
		JMenuItem gridButton = new JMenuItem();
		if (panel.programData.SHOW_GRID)
			gridButton.setText(ProgramLabels.hideGrid);
		else
			gridButton.setText(ProgramLabels.showGrid);
		gridButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				panel.programData.setGrig();
				panel.repaint();
			}
		});
		panelSettings.add(gridButton);
		JMenuItem markerSimulate = new JMenuItem();
		markerSimulate.setText(ProgramLabels.showDist);
		markerSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				panel.programData.showDist = !panel.programData.showDist;
				panel.repaint();
			}
		});
		panelSettings.add(markerSimulate);
		add(panelSettings);
	}
// edit selected elements
	void showElemEdit(MainPanel panel){
		JMenu elemEdit = new JMenu(ProgramLabels.elemEdit);
		if (!panel.programData.isEmptyModified()){
			JMenuItem removeButton = new JMenuItem();
			removeButton.setText(ProgramLabels.elemRemove);
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					panel.removeSelectedLines();
				}
			});
			elemEdit.add(removeButton);
			JMenuItem copyButton = new JMenuItem();
			copyButton.setText(ProgramLabels.elemCopy);
			copyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					panel.copyLines();
				}
			});
			elemEdit.add(copyButton);
		}
		if (!panel.programData.copiedLines.isEmpty()){
			JMenuItem pasteButton = new JMenuItem();
			pasteButton.setText(ProgramLabels.elemPaste);
			pasteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					panel.pasteLines(panel.x1, panel.y1);
				}
			});
			elemEdit.add(pasteButton);
		}
		if (!panel.programData.copiedLines.isEmpty() || !panel.programData.isEmptyModified())
			add(elemEdit);
	}
// marker
	void showMarker(MainPanel panel){
		if (panel.programData.marker == null){
			JMenuItem markerButton = new JMenuItem();
			markerButton.setText(ProgramLabels.addMarker);
			markerButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					panel.addMarker(panel.x1, panel.y1);
				}
			});
			add(markerButton);
		}
		else{
			JMenu marker = new JMenu(ProgramLabels.marker);
			JMenuItem markerButton = new JMenuItem();
			markerButton.setText(ProgramLabels.moveMarker);
			markerButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					panel.addMarker(panel.x1, panel.y1);
				}
			});
			marker.add(markerButton);
			JMenuItem removeMarkerButton = new JMenuItem();
			removeMarkerButton.setText(ProgramLabels.removeMarker);
			removeMarkerButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					panel.removeMarker();
				}
			});
			marker.add(removeMarkerButton);
			add(marker);
		}
	}
// run simulation
	void showSimulate(MainPanel panel){
		JMenuItem markerSimulate = new JMenuItem();
		markerSimulate.setText(ProgramLabels.runSimulation);
		markerSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Simulation.runSimulation(panel);
				panel.repaint();
			}
		});
		add(markerSimulate);
	}
// remove all rules and lines
	void showResetOption(MainPanel panel){
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.reset);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				panel.programData.clear();
				panel.repaint();
			}
		});
		add(resetButton);
	}
// save document
	void showSaveFile(MainPanel panel){
		JFileChooser fc = new JFileChooser("./temp/");
		fc.setFileFilter(new FileNameExtensionFilter(ProgramLabels.extensionGrammarInfo, ProgramLabels.extensionGrammar));
		fc.setSelectedFile(new File("." + ProgramLabels.extensionGrammar));
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.saveGrammar);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int returnVal = fc.showOpenDialog(panel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					FileSaver fileSaver = new FileSaver(fc.getSelectedFile());
					fileSaver.saveDataFile(panel);
				}
				panel.repaint();
			}
		});
		add(resetButton);
	}
// open document
	void showOpenFile(MainPanel panel){
		JFileChooser fc = new JFileChooser("./temp/");
		fc.setFileFilter(new FileNameExtensionFilter(ProgramLabels.extensionGrammarInfo, ProgramLabels.extensionGrammar));
		fc.setSelectedFile(new File("." + ProgramLabels.extensionGrammar));
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.openGrammar);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int returnVal = fc.showOpenDialog(panel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					FileSaver fileSaver = new FileSaver(fc.getSelectedFile());
					fileSaver.openDataFile(panel);
				}
				panel.repaint();
			}
		});
		add(resetButton);
	}
// prepare rule application list
	void showRuleApplicationList(MainPanel panel){
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.ruleAppList);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RuleListPrepraration frame = new RuleListPrepraration();
			}
		});
		add(resetButton);
	}// apply rule application list
	void showApplyRuleApplicationList(MainPanel panel){
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.applyruleAppList);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try{
					for (Rule rule: MainData.ruleAppList)
						rule.apply(panel);
				}
				catch(Rule.NoMarkerException exc){
					MessageFrame error = new MessageFrame(exc.getMessage());
				}
			}
		});
		add(resetButton);
	}
}