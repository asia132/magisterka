package grammar_graphs;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame; 
import javax.swing.JTextArea; 
import javax.swing.JButton; 
import javax.swing.JPanel;  
import javax.swing.JSplitPane; 
import javax.swing.BorderFactory; 
import javax.swing.border.Border;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.ArrayList;
 
class CreateRuleFrame extends JFrame {
	static final long serialVersionUID = 42L;

	JTextArea newRuleName = null;
	JButton saveButton;
	JButton removeButton;
	JButton cancelButton;
	
	LeftRulePanel panelL;
	JPanel panelB;

	int screenHeight;
	int screenWidth;

	double rfScale = 0.6;
	double loc = 0.3;
	double panScale = 0.8;

	String ruleName;

	CreateRuleFrame(ArrayList <Line> initialLines, ArrayList <Line> finalLines, 
		Marker initialmarker, Marker finalmarker) {
		super(ProgramLabels.rulleWinName);
		loadFrameData();
		this.ruleName = "";
		
		Point tran = findTrans(finalLines, initialLines, finalmarker, initialmarker);

		this.loadLeftPanel(initialLines, initialmarker, tran.x, tran.y);
		this.loadRightPanel(initialLines, finalLines, finalmarker, tran.x, tran.y);
		
		GrammarControl.addPanel(panelL);
		GrammarControl.addPanel(panelL.rigthRulePanel);
		
		this.loadBottomPanel();

		splitPanel();

		this.setLayout(new FlowLayout());
		this.pack();
		this.setVisible(true);
	}
	CreateRuleFrame(Rule rule) {
		super(ProgramLabels.rulleWinName + ": " + rule.name);
		loadFrameData();
		this.ruleName = rule.name;
		
		Point tran = findTrans(rule.getFinalLines(), rule.getInitialLines(), rule.getFinalMarker(), rule.getInitialMarker());
		
		this.loadLeftPanel(rule.getInitialLines(), rule.getInitialMarker(), tran.x, tran.y);
		this.loadRightPanel(rule.getInitialLines(), rule.getFinalLines(), rule.getFinalMarker(), tran.x, tran.y);
		this.loadBottomPanel();
		
		GrammarControl.addPanel(panelL);
		GrammarControl.addPanel(panelL.rigthRulePanel);

		splitPanel();

		this.setLayout(new FlowLayout());
		this.pack();
		this.setVisible(true);
	}
	protected void loadFrameData(){
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		this.screenHeight = screenSize.height;
		this.screenWidth = screenSize.width;
		this.setSize((int)(screenWidth*rfScale), (int)(screenHeight*rfScale));
		this.setLocation((int)(screenWidth*loc), (int)(screenHeight*loc));
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.pack();
	}
	protected void splitPanel(){
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		JSplitPane splitPaneH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPaneH.setLeftComponent(panelL);
		splitPaneH.setRightComponent(panelL.rigthRulePanel);
		
		topPanel.add(panelB, BorderLayout.NORTH);
		topPanel.add(splitPaneH, BorderLayout.CENTER);
		
		this.getContentPane().add(topPanel);
		this.pack();
	}
	private Point findTrans(ArrayList <Line> finalLines, ArrayList <Line> initialLines, Marker finalmarker, Marker initialmarker){
		int min_x = initialmarker.getX();
		int min_y = initialmarker.getY();
		if (finalmarker != null){
			min_x = min_x < finalmarker.getX() ? min_x : finalmarker.getX();
			min_y = min_y < finalmarker.getY() ? min_y : finalmarker.getY();
		}
		
		for (Line line : initialLines){
			min_x = min_x < line.getX_a() ? min_x : line.getX_a();
			min_x = min_x < line.getX_b() ? min_x : line.getX_b();

			min_y = min_y < line.getY_a() ? min_y : line.getY_a();
			min_y = min_y < line.getY_b() ? min_y : line.getY_b();
		}
		if (finalLines != null)
		for (Line line : finalLines){
			min_x = min_x < line.getX_a() ? min_x : line.getX_a();
			min_x = min_x < line.getX_b() ? min_x : line.getX_b();

			min_y = min_y < line.getY_a() ? min_y : line.getY_a();
			min_y = min_y < line.getY_b() ? min_y : line.getY_b();
		}
		
		int height = (int)(screenHeight*rfScale*panScale);
		int width = (int)(screenWidth*rfScale*0.5);
		
		return new Point(-min_x + GridControl.getInstance().grid_size + (int)(width*0.2), -min_y + GridControl.getInstance().grid_size + (int)(height*0.2));
	}
	void loadLeftPanel(ArrayList <Line> lines, Marker initialmarker, int tranX, int tranY) {
		int width = (int)(screenWidth*rfScale*0.5);
		panelL = new LeftRulePanel(this, width, screenHeight);

		for (Line line : lines){
			Line newLine = line.copy();
			newLine.move(tranX, tranY);
			panelL.programData.lines.addLine(newLine, false);
		}
		if (initialmarker != null){
			panelL.programData.marker = initialmarker.copy();
			panelL.programData.marker.move(tranX, tranY);
		}
		panelL.setLayout(new BorderLayout());
	}
	void loadRightPanel(ArrayList <Line> initialLines, ArrayList <Line> finalLines, Marker finalmarker, int tranX, int tranY) {
		int width = (int)(screenWidth*rfScale*0.5);
		panelL.rigthRulePanel = new RigthRulePanel(this, width, screenHeight, panelL);

		for (Line line : initialLines){
			Line newLine = line.copy();
			newLine.move(tranX, tranY);
			panelL.rigthRulePanel.programData.lines.addLine(newLine, false);
			panelL.rigthRulePanel.leftLines.add(newLine);
		}
		if (finalLines != null && !finalLines.isEmpty()){
			for (Line line : finalLines){
				Line newLine = line.copy();
				newLine.move(tranX, tranY);
				panelL.rigthRulePanel.programData.lines.addLine(newLine, false);
			}
		}
		if (finalmarker != null){
			panelL.rigthRulePanel.programData.marker = finalmarker.copy();
			panelL.rigthRulePanel.programData.marker.move(tranX, tranY);
		}
		panelL.rigthRulePanel.setLayout(new BorderLayout());
	}
	void loadBottomPanel() {
		panelB = new JPanel();

		showNameRules(panelB);
		showSaveButton(panelB);
		showCancelButton(panelB);
	}
	void showNameRules(JPanel panel){
		JTextArea label = new JTextArea(ProgramLabels.newRuleLabel);
		label.setEditable(false);
		
		if (this.ruleName.equals("")){
			System.out.println("Rule name: " + this.ruleName);
			newRuleName = new JTextArea(ProgramLabels.defaultNewRule);
		}
		else{
			System.out.println("Rule name: " + this.ruleName);
			newRuleName = new JTextArea(this.ruleName);
		}

		newRuleName.setEditable(true);
		newRuleName.setBackground(Color.WHITE);
		newRuleName.setForeground(Color.BLACK);

		Border border = BorderFactory.createLineBorder(Color.BLACK);
		newRuleName.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		panel.add(label, BorderLayout.PAGE_START);
		panel.add(newRuleName, BorderLayout.LINE_START);
	}
	void showSaveButton(JPanel panel){
		saveButton = new JButton(ProgramLabels.save);
		saveButton.addActionListener(event -> {
			String theName = newRuleName.getText();

			if (this.ruleName.equals(theName)){
				try{
					GrammarControl.getInstance().ruleList.remove(GrammarControl.getInstance().getRuleOfName(theName));
					GrammarControl.getInstance().ruleList.add(new Rule(theName, panelL.programData.lines.copy(), panelL.rigthRulePanel.programData.lines.copy(), panelL.programData.marker, panelL.rigthRulePanel.programData.marker));			
					closeFrame();
				}
				catch(Exception exc){
					new MessageFrame(exc.getMessage());
					System.out.println(exc.getLocalizedMessage());
				}
			}else{
				for (Rule rule: GrammarControl.getInstance().ruleList){
					if (rule.getName().equals(theName)){
						int serialNumber = GrammarControl.getInstance().ruleList.size() + 1;
						theName += Integer.toString(serialNumber);
						break;
					}
				}

				try{
					GrammarControl.getInstance().ruleList.add(new Rule(theName, panelL.programData.lines.copy(), panelL.rigthRulePanel.programData.lines.copy(), panelL.programData.marker, panelL.rigthRulePanel.programData.marker));			
					closeFrame();
				}
				catch(Exception exc){
					new MessageFrame(exc.getMessage());
					System.out.println(exc.getLocalizedMessage());
				}				
			}
		});
		panel.add(saveButton, BorderLayout.LINE_START);
	}
	void showCancelButton(JPanel panel){
		cancelButton = new JButton(ProgramLabels.cancel);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
		panel.add(cancelButton, BorderLayout.LINE_START);
	}
	void closeFrame(){
		
		GrammarControl.removePanel(panelL.rigthRulePanel);
		GrammarControl.removePanel(panelL);
		super.dispose();
	}
}
