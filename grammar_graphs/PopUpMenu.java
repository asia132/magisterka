package grammar_graphs;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

import javax.swing.border.Border;

import java.awt.Color;

import java.util.Hashtable;
import java.util.ArrayList;

class PopUpMenu extends JPopupMenu {

	private final static int STEPS_QTY = 12;
	static final long serialVersionUID = 42L;
	private Rule changedRule;
	int index;

	PopUpMenu(MainPanel panel, boolean ruleAdding){
		if(!(ruleAdding)){
			showRulesChanging(panel);
			showViewSettings(panel);
		}
		showSimulate(panel);
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

		for (int i = 0; i < panel.programData.ruleList.size(); ++i){
			rulesList[i] = new JMenu(panel.programData.ruleList.get(i).getName());
			changedRule = panel.programData.ruleList.get(i);

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
					System.out.println(changedRule);
					try{
						changedRule.apply(panel);
					}
					catch(Rule.NoMarkerException exc){}
				}
			});
			rulesList[i].add(applyRule);

			JMenuItem editRule = new JMenuItem("Edit");
			index = i;
			editRule.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new CreateRuleFrame(index);
				}
			});
			rulesList[i].add(editRule);

			rulesMenu.add(rulesList[i]);
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
// remove element
	void showElemEdit(MainPanel panel){
		JMenu elemEdit = new JMenu(ProgramLabels.elemEdit);
		if (!panel.programData.isEmptyModified()){
			JMenuItem removeButton = new JMenuItem();
			removeButton.setText(ProgramLabels.elemRemove);
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					for (Line line: panel.programData.getModified()){
						panel.programData.lines.remove(line);
					}
					panel.programData.clearModified();
					panel.programData.clearModifiedMarker();
					panel.repaint();
				}
			});
			elemEdit.add(removeButton);
			JMenuItem copyButton = new JMenuItem();
			copyButton.setText(ProgramLabels.elemCopy);
			copyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					panel.programData.copyModyfied();
					panel.programData.clearModified();
					panel.programData.clearModifiedMarker();
					panel.repaint();
				}
			});
			elemEdit.add(copyButton);
		}
		if (!panel.programData.copiedLines.isEmpty()){
			JMenuItem pasteButton = new JMenuItem();
			pasteButton.setText(ProgramLabels.elemPaste);
			pasteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					panel.programData.pasteCopied(panel.x1, panel.y1);
					panel.repaint();
				}
			});
			elemEdit.add(pasteButton);
		}
		if (!panel.programData.copiedLines.isEmpty() || !panel.programData.isEmptyModified())
			add(elemEdit);
	}
// marker
	void showMarker(MainPanel panel){
		JMenu marker = new JMenu(ProgramLabels.marker);
		JMenuItem markerButton = new JMenuItem();
		if (panel.programData.marker == null)
			markerButton.setText(ProgramLabels.addMarker);
		else
			markerButton.setText(ProgramLabels.moveMarker);
		markerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (panel.programData.marker == null)
					panel.programData.marker = new Marker(panel.x1, panel.y1);
				else
					panel.programData.marker.setXY(panel.x1, panel.y1);
				panel.repaint();
			}
		});
		marker.add(markerButton);
		if (panel.programData.marker != null){
			JMenuItem removeMarkerButton = new JMenuItem();
			removeMarkerButton.setText(ProgramLabels.removeMarker);
			removeMarkerButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					panel.programData.marker = null;
					panel.repaint();
				}
			});
			marker.add(removeMarkerButton);
		}
		add(marker);
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
}