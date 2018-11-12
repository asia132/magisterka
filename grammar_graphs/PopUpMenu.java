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
import java.util.ArrayList;

import java.io.File;

class PopUpMenu extends JPopupMenu {

	public static final long serialVersionUID = 42L;

	PopUpMenu(MainPanel panel) {
		if (!(panel instanceof LeftRulePanel) && !(panel instanceof RigthRulePanel)) {
			if (!Settings.COLOR_RULES && !Settings.LIMITING_SHAPE) {
				showRulesChanging(panel);
				showRuleListOptions(panel);
				showGrammarOptions(panel);
			}
			showPaintingRuleLevelss(panel);
		}
		if (!Settings.COLOR_RULES) {
			showViewSettings(panel);
			showElemEdit(panel);
			if (!Settings.LIMITING_SHAPE)
				showMarker(panel);
		}
	}

	// change rules
	JMenuItem addRuleForLines(MainPanel panel) {
		JMenuItem ruleforlineButton = new JMenuItem();
		ruleforlineButton.setText(ProgramLabels.rulleAddingForLines);
		ruleforlineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (panel.programData.modified_marker != null)
					new CreateRuleFrame(panel.programData.getModified(), new ArrayList<>(),
							panel.programData.marker.copy(), panel.programData.marker.copy());
				else
					new CreateRuleFrame(panel.programData.getModified(), new ArrayList<>(), null, null);

			}
		});
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		ruleforlineButton
				.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		return ruleforlineButton;
	}

	JMenuItem addRule(MainPanel panel) {
		JMenuItem addRules = new JMenuItem(ProgramLabels.rulleAdding);
		addRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CreateRuleFrame(new ArrayList<Line>(), new ArrayList<Line>(), null, null);
			}
		});
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		addRules.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		return addRules;
	}

	void showRulesChanging(MainPanel panel) {
		JMenu rulesMenu = new JMenu(ProgramLabels.rulleList);

		JMenu[] rulesList = new JMenu[GrammarControl.getInstance().ruleList.size()];
		int i = 0;

		for (Rule changedRule : GrammarControl.getInstance().ruleList) {
			rulesList[i] = new JMenu(GrammarControl.getInstance().ruleList.get(i).getName());

			JMenuItem applyRule = new JMenuItem("Apply");
			applyRule.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						changedRule.apply(panel);
					} catch (Rule.NoMarkerException exc) {
						new MessageFrame(exc.getMessage());
						System.out.println("PopUp line 86" + exc.getLocalizedMessage());
					}
				}
			});
			rulesList[i].add(applyRule);

			JMenuItem editRule = new JMenuItem("Edit");
			editRule.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new CreateRuleFrame(changedRule);
				}
			});
			rulesList[i].add(editRule);

			JMenuItem removeRule = new JMenuItem("Remove");
			removeRule.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GrammarControl.getInstance().ruleList.remove(changedRule);
				}
			});
			rulesList[i].add(removeRule);

			rulesMenu.add(rulesList[i]);
			i++;
		}

		if (!panel.programData.isEmptyModified())
			rulesMenu.add(addRuleForLines(panel));
		rulesMenu.add(addRule(panel));

		add(rulesMenu);
	}

	// change view settings
	void showViewSettings(MainPanel panel) {
		JMenu panelSettings = new JMenu(ProgramLabels.panelSettings);
		panelSettings.add(showGridOpt(panel));
		panelSettings.add(showLenOpt(panel));
		panelSettings.add(showPointOpt(panel));
		add(panelSettings);
	}

	JMenuItem showGridOpt(MainPanel panel) {
		JMenuItem gridButton = new JMenuItem();
		if (Settings.SHOW_GRID)
			gridButton.setText(ProgramLabels.hideGrid);
		else
			gridButton.setText(ProgramLabels.showGrid);
		gridButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Settings.SHOW_GRID = !Settings.SHOW_GRID;
				// panel.repaint();
				GrammarControl.repaintAll();
			}
		});
		return gridButton;
	}

	JMenuItem showLenOpt(MainPanel panel) {
		JMenuItem markerSimulate = new JMenuItem();
		if (Settings.SHOW_DIST)
			markerSimulate.setText(ProgramLabels.hideDist);
		else
			markerSimulate.setText(ProgramLabels.showDist);
		markerSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Settings.SHOW_DIST = !Settings.SHOW_DIST;
				// panel.repaint();
				GrammarControl.repaintAll();
			}
		});
		return markerSimulate;
	}

	JMenuItem showPointOpt(MainPanel panel) {
		JMenuItem markerSimulate = new JMenuItem();
		if (Settings.SHOW_POINTS)
			markerSimulate.setText(ProgramLabels.hidePoints);
		else
			markerSimulate.setText(ProgramLabels.showPoints);
		markerSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Settings.SHOW_POINTS = !Settings.SHOW_POINTS;
				// panel.repaint();
				GrammarControl.repaintAll();
			}
		});
		return markerSimulate;
	}

	// painting rules
	void showPaintingRuleLevelss(MainPanel panel) {
		JMenu buttonMenu = new JMenu(ProgramLabels.colorRuleMenu);
		if (Settings.CLOSED_SHAPES) {
			if (!Settings.LIMITING_SHAPE) {
				buttonMenu.add(showPaintingRuleOption(panel));
				buttonMenu.add(addPaintngRuleLevels(panel));
			}
		} else {
			buttonMenu.add(info(panel));
		}
		if (!Settings.COLOR_RULES) {
			buttonMenu.add(showColorRuleLimitingShape(panel));
			buttonMenu.add(drawLevels(panel));
			buttonMenu.add(showResetLevels(panel));
		}
		add(buttonMenu);
	}

	JMenuItem info(MainPanel panel) {
		JMenuItem button = new JMenuItem(
				"One of level shapes is not closed\n- it is not possible to define\npainting rules");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.setColorRules();
				// panel.repaint();
				GrammarControl.repaintAll();
			}
		});
		return button;
	}

	JMenuItem drawLevels(MainPanel panel) {
		JMenuItem button = new JMenuItem();
		if (Settings.DRAW_LEVELS)
			button.setText(ProgramLabels.stopDrawLevels);
		else
			button.setText(ProgramLabels.drawLevels);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.DRAW_LEVELS = !Settings.DRAW_LEVELS;
				panel.programData.changeFiguresColor();
				// panel.repaint();
				GrammarControl.repaintAll();
			}
		});
		return button;
	}

	JMenuItem addPaintngRuleLevels(MainPanel panel) {
		JMenuItem addRules = new JMenuItem(ProgramLabels.colorRuleDefine);
		addRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GrammarControl.getInstance().paintingRuleLevels.limitingShape.closeLevel();
				for (int i = 0; i <= GrammarControl.getInstance().paintingRuleLevels.getN(); ++i) {
					PaintingRuleLevels.levels[i].closeLevel();
				}
				new CreatePaintingRuleFrame();
			}
		});
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		addRules.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		return addRules;
	}

	JMenuItem showResetLevels(MainPanel panel) {
		JMenuItem colorRuleOption = new JMenuItem();
		colorRuleOption.setText(ProgramLabels.resetLevels);
		colorRuleOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GrammarControl.getInstance().paintingRuleLevels = new PaintingRuleLevels(panel);
				panel.programData.lines.fillPaintingRuleLevelsWithInput();
			}
		});
		return colorRuleOption;
	}

	JMenuItem showPaintingRuleOption(MainPanel panel) {
		JMenuItem colorRuleOption = new JMenuItem();
		if (Settings.COLOR_RULES)
			colorRuleOption.setText(ProgramLabels.colorRuleOptionOff);
		else
			colorRuleOption.setText(ProgramLabels.colorRuleOptionOn);
		colorRuleOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.out.println("----------------TURN RENDERING ON WITH N = "
						+ GrammarControl.getInstance().paintingRuleLevels.getN() + "----------------");
				GrammarControl.getInstance().paintingRuleLevels.limitingShape.closeLevel();
				for (int i = 0; i <= GrammarControl.getInstance().paintingRuleLevels.getN(); ++i) {
					PaintingRuleLevels.levels[i].closeLevel();
				}
				Settings.setColorRules();
			}
		});
		return colorRuleOption;
	}

	JMenuItem showColorRuleLimitingShape(MainPanel panel) {
		JMenuItem colorRuleOption = new JMenuItem();
		if (Settings.LIMITING_SHAPE) {
			colorRuleOption.setText(ProgramLabels.limitShapeOff);
		} else
			colorRuleOption.setText(ProgramLabels.limitShapeOn);
		colorRuleOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (Settings.LIMITING_SHAPE) {
					panel.programData.endDefininingLimitShape();
					// panel.repaint();
					GrammarControl.repaintAll();
				} else {
					panel.programData.startDefininingLimitShape();
					// panel.repaint();
					GrammarControl.repaintAll();
				}
				Settings.LIMITING_SHAPE = !Settings.LIMITING_SHAPE;
			}
		});
		return colorRuleOption;
	}

	JMenuItem showColorRuleSettings(MainPanel panel) {
		JMenuItem colorRuleOption = new JMenuItem();
		colorRuleOption.setText(ProgramLabels.colorRuleSettings);
		colorRuleOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				;

			}
		});
		return colorRuleOption;
	}

	// edit selected elements
	void showElemEdit(MainPanel panel) {
		JMenu elemEdit = new JMenu(ProgramLabels.elemEdit);
		if (!panel.programData.isEmptyModified()) {
			elemEdit.add(showElemEditRem(panel));
			elemEdit.add(showElemEditCopy(panel));
		}
		if (!GrammarControl.getInstance().copiedLines.isEmpty()) {
			elemEdit.add(showElemEditPaste(panel));
		}
		if (panel.programData.lines.getLines() != null)
			elemEdit.add(showElemEditGroup(panel));
		add(elemEdit);
	}

	JMenuItem showElemEditRem(MainPanel panel) {
		JMenuItem removeButton = new JMenuItem();
		removeButton.setText(ProgramLabels.elemRemove);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.out.println("-----------------REMOVE-LINES------------------");
				panel.removeSelectedLines();
			}
		});
		return removeButton;
	}

	JMenuItem showElemEditCopy(MainPanel panel) {
		JMenuItem copyButton = new JMenuItem();
		copyButton.setText(ProgramLabels.elemCopy);
		copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.out.println("-----------------COPY-LINES--------------------");
				panel.copyLines();
			}
		});
		return copyButton;
	}

	JMenuItem showElemEditPaste(MainPanel panel) {
		JMenuItem pasteButton = new JMenuItem();
		pasteButton.setText(ProgramLabels.elemPaste);
		pasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.out.println("-----------------PASTE-LINES-------------------");
				panel.pasteLines(panel.x1, panel.y1);
			}
		});
		return pasteButton;
	}

	JMenuItem showElemEditGroup(MainPanel panel) {
		JMenuItem pasteButton = new JMenuItem();
		pasteButton.setText(ProgramLabels.elemGroup);
		pasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.out.println("-----------------GROUP-LINES-------------------");

				int before = panel.programData.lines.size();
				do {
					before = panel.programData.lines.size();
					panel.programData.lines.setLines(Shape.groupLines(panel.programData.lines.getLines()));
				} while (before != panel.programData.lines.size());
				// panel.repaint();
				GrammarControl.repaintAll();
			}
		});
		return pasteButton;
	}

	// marker
	void showMarker(MainPanel panel) {
		if (panel.programData.marker == null) {
			if (!(panel instanceof RigthRulePanel))
				add(showAddMarker(panel));
			else {
				if (((RigthRulePanel) panel).parent.programData.marker != null) {
					JMenu marker = new JMenu(ProgramLabels.marker);
					marker.add(showAddMarker(panel));
					marker.add(showCopyFromLeftSideMarker(panel));
					add(marker);
				} else {
					add(showAddMarker(panel));
				}
			}
		} else {
			JMenu marker = new JMenu(ProgramLabels.marker);
			marker.add(showMoveMarker(panel));
			marker.add(showRemoveMarker(panel));
			if ((panel instanceof LeftRulePanel)) {
				marker.add(showCopyToRigthSideMarker(panel));
			}
			if ((panel instanceof RigthRulePanel)) {
				if (((RigthRulePanel) panel).parent.programData.marker != null)
					marker.add(showCopyFromLeftSideMarker(panel));
			}
			add(marker);
		}
	}

	JMenuItem showAddMarker(MainPanel panel) {
		JMenuItem markerButton = new JMenuItem();
		markerButton.setText(ProgramLabels.addMarker);
		markerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				panel.addMarker(panel.x1, panel.y1);
			}
		});
		return markerButton;
	}

	JMenuItem showMoveMarker(MainPanel panel) {
		JMenuItem markerButton = new JMenuItem();
		markerButton.setText(ProgramLabels.moveMarker);
		markerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				panel.addMarker(panel.x1, panel.y1);
			}
		});
		return markerButton;
	}

	JMenuItem showRemoveMarker(MainPanel panel) {
		JMenuItem removeMarkerButton = new JMenuItem();
		removeMarkerButton.setText(ProgramLabels.removeMarker);
		removeMarkerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				panel.removeMarker();
			}
		});
		return removeMarkerButton;
	}

	JMenuItem showCopyToRigthSideMarker(MainPanel panel) {
		JMenuItem copyMarkerButton = new JMenuItem();
		copyMarkerButton.setText(ProgramLabels.copyMarkerToRigthSide);
		copyMarkerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((LeftRulePanel) panel).copyMarker();
				((LeftRulePanel) panel).rigthRulePanel.repaint();
			}
		});
		return copyMarkerButton;
	}

	JMenuItem showCopyFromLeftSideMarker(MainPanel panel) {
		JMenuItem copyMarkerButton = new JMenuItem();
		copyMarkerButton.setText(ProgramLabels.copyMarkerFromLeftSide);
		copyMarkerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((RigthRulePanel) panel).parent.copyMarker();
				((RigthRulePanel) panel).repaint();
			}
		});
		return copyMarkerButton;
	}

	// grammar options
	void showGrammarOptions(MainPanel panel) {
		JMenu grammar = new JMenu(ProgramLabels.grammar);

		grammar.add(showOpenFile(panel));
		grammar.add(showSaveFile(panel));
		if (GrammarControl.getInstance().file != null) {
			grammar.add(showSaveTheFile(panel));
			grammar.add(showResetOption(panel));
		}
		grammar.add(showResetAllOption(panel));
//		grammar.add(showSimulate(panel));

		add(grammar);
	}

	// run simulation
	JMenuItem showSimulate(MainPanel panel) {
		JMenuItem markerSimulate = new JMenuItem();
		markerSimulate.setText(ProgramLabels.runSimulation);
		markerSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Simulation.runSimulation(panel);
				panel.repaint();
			}
		});
		return markerSimulate;
	}

	// remove all rules and lines
	JMenuItem showResetAllOption(MainPanel panel) {
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.resetAll);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Settings.CLOSED_SHAPES = true;
				panel.programData.clear();
				GrammarControl.getInstance().paintingRuleLevels = new PaintingRuleLevels(panel);
				panel.repaint();
				GrammarControl.getInstance().file = null;
				System.out.println("-----------------RESET-ALL-----------------");
			}
		});
		return resetButton;
	}

	// remove all rules and lines
	JMenuItem showResetOption(MainPanel panel) {
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.reset + " " + GrammarControl.getInstance().file.getName());
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Settings.CLOSED_SHAPES = true;
				FileSaver fileSaver = new FileSaver(GrammarControl.getInstance().file);
				fileSaver.openDataFile(panel);
				panel.repaint();
				System.out.println("-------------------RESET-" + fileSaver.getName() + "------------------");
			}
		});
		return resetButton;
	}

	// save the document
	JMenuItem showSaveTheFile(MainPanel panel) {
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.saveGrammar + " " + GrammarControl.getInstance().file.getName());
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				FileSaver fileSaver = new FileSaver(GrammarControl.getInstance().file);
				fileSaver.saveDataFile(panel);
				// panel.repaint();
			}
		});
		return resetButton;
	}

	// save as document
	JMenuItem showSaveFile(MainPanel panel) {
		JFileChooser fc = new JFileChooser("./temp/");
		fc.setFileFilter(
				new FileNameExtensionFilter(ProgramLabels.extensionGrammarInfo, ProgramLabels.extensionGrammar));
		fc.setSelectedFile(new File("." + ProgramLabels.extensionGrammar));
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.saveGrammar);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int returnVal = fc.showOpenDialog(panel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					FileSaver fileSaver = new FileSaver(fc.getSelectedFile());
					fileSaver.saveDataFile(panel);
					GrammarControl.getInstance().file = fc.getSelectedFile();
				}
				panel.repaint();
			}
		});
		return resetButton;
	}

	// open document
	JMenuItem showOpenFile(MainPanel panel) {
		JFileChooser fc = new JFileChooser("./temp/");
		fc.setFileFilter(
				new FileNameExtensionFilter(ProgramLabels.extensionGrammarInfo, ProgramLabels.extensionGrammar));
		fc.setSelectedFile(new File("." + ProgramLabels.extensionGrammar));
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.openGrammar);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int returnVal = fc.showOpenDialog(panel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					FileSaver fileSaver = new FileSaver(fc.getSelectedFile());
					fileSaver.openDataFile(panel);
					System.out.println(
							"-------------------OPEN " + fc.getSelectedFile().getName() + "-------------------");
					GrammarControl.getInstance().file = fc.getSelectedFile();
				}
				panel.repaint();
			}
		});
		return resetButton;
	}

	// prepare rule application list
	void showRuleListOptions(MainPanel panel) {
		JMenu ruleList = new JMenu(ProgramLabels.rulleAppListOpt);

		ruleList.add(showRuleApplicationList(panel));
		ruleList.add(showApplyRuleApplicationList(panel));

		add(ruleList);
	}

	JMenuItem showRuleApplicationList(MainPanel panel) {
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.ruleAppList);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				new RuleListPrepraration();
			}
		});
		return resetButton;
	}// apply rule application list

	JMenuItem showApplyRuleApplicationList(MainPanel panel) {
		JMenuItem resetButton = new JMenuItem();
		resetButton.setText(ProgramLabels.applyruleAppList);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					for (Rule rule : GrammarControl.getInstance().ruleAppList)
						rule.apply(panel);
				} catch (Rule.NoMarkerException exc) {
					new MessageFrame(exc.getMessage());
					System.out.println(exc.getLocalizedMessage());
				}
			}
		});
		return resetButton;
	}
}