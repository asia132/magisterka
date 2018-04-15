package grammar_graphs;

import java.awt.FlowLayout;
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
	MainPanel panelL;
	MainPanel panelR;
	JPanel panelB;

	int screenHeight;
	int screenWidth;

	double rfScale = 0.5;
	double loc = 0.3;
	double panScale = 0.8;

	String ruleName;

	CreateRuleFrame(ArrayList <Line> initialLines, ArrayList <Line> finalLines, 
		Marker initialmarker, Marker finalmarker) {
		super(ProgramLabels.rulleWinName);
		loadFrameData();
		this.ruleName = "";

		this.loadLeftPanel(initialLines, initialmarker);
		this.loadRightPanel(initialLines, finalLines, finalmarker);
		this.loadBottomPanel();

		splitPanel();

		this.setLayout(new FlowLayout());
		this.pack();
		this.setVisible(true);
	}

	CreateRuleFrame(int ruleindex) {
		super(ProgramLabels.rulleWinName);
		loadFrameData();
		this.ruleName = MainData.ruleList.get(ruleindex).name;

		this.loadLeftPanel(MainData.ruleList.get(ruleindex).getInitialLines(), MainData.ruleList.get(ruleindex).getInitialMarker());
		this.loadRightPanel(MainData.ruleList.get(ruleindex).getInitialLines(), MainData.ruleList.get(ruleindex).getFinalLines(), MainData.ruleList.get(ruleindex).getFinalMarker());
		this.loadBottomPanel();

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
		this.setDefaultCloseOperation(CreateRuleFrame.DISPOSE_ON_CLOSE);
	}
	protected void splitPanel(){
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		this.getContentPane().add(topPanel);

		JSplitPane splitPaneV = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		topPanel.add(splitPaneV, BorderLayout.CENTER);

		JSplitPane splitPaneH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPaneH.setLeftComponent(panelL);
		splitPaneH.setRightComponent(panelR);

		splitPaneV.setLeftComponent(splitPaneH);
		splitPaneV.setRightComponent(panelB);
	}
	private int [] extremeXY(ArrayList <Line> lines, Marker marker){
		int min_x = screenWidth;
		int min_y = screenHeight;
		int max_x = 0;
		int max_y = 0;
		if (marker != null){
			min_x = marker.getX();
			min_y = marker.getY();
			max_x = marker.getX();
			max_y = marker.getY();
		}
		for (Line line : lines){
			min_x = min_x < line.getX_a() ? min_x : line.getX_a();
			min_x = min_x < line.getX_b() ? min_x : line.getX_b();

			min_y = min_y < line.getY_a() ? min_y : line.getY_a();
			min_y = min_y < line.getY_b() ? min_y : line.getY_b();

			max_x = max_x > line.getX_a() ? max_x : line.getX_a();
			max_x = max_x > line.getX_b() ? max_x : line.getX_b();

			max_y = max_y > line.getY_a() ? max_y : line.getY_a();
			max_y = max_y > line.getY_b() ? max_y : line.getY_b();
		}
		int [] result = {max_x, max_y, min_x, min_y};
		return result;
	}
	void loadLeftPanel(ArrayList <Line> lines, Marker initialmarker) {
		int width = (int)(screenWidth*rfScale*0.5);
		int height = (int)(screenHeight*rfScale*panScale);
		panelL = new MainPanel(width, height, true);

		int [] tran = extremeXY(lines, initialmarker);

		for (Line line : lines){
			Line newLine = line.copy();
			newLine.move(-tran[2] + panelL.programData.grid_size, -tran[3] + panelL.programData.grid_size);
			panelL.programData.lines.add(newLine);
		}
		if (initialmarker != null){
			panelL.programData.marker = initialmarker;
			panelL.programData.marker.move(-tran[2] + panelL.programData.grid_size, -tran[3] + panelL.programData.grid_size);
		}
	}
	void loadRightPanel(ArrayList <Line> initialLines, ArrayList <Line> finalLines, Marker finalmarker) {
		int width = (int)(screenWidth*rfScale*0.5);
		int height = (int)(screenHeight*rfScale*panScale);
		panelR = new MainPanel(width, height, true);

		ArrayList <Line> lines = new ArrayList<>();
		lines.addAll(initialLines);
		lines.addAll(finalLines);

		int [] tran = extremeXY(lines, finalmarker);

		for (Line line : initialLines){
			Line newLine = line.copy();
			newLine.move(-tran[2] + panelR.programData.grid_size, -tran[3] + panelR.programData.grid_size);
			panelR.programData.lines.add(newLine);
		}
		for (Line line : finalLines){
			Line newLine = line.copy();
			newLine.move(-tran[2] + panelR.programData.grid_size, -tran[3] + panelR.programData.grid_size);
			panelR.programData.lines.add(newLine);
		}
		if (finalmarker != null){
			panelR.programData.marker = finalmarker;
			panelR.programData.marker.move(-tran[2] + panelL.programData.grid_size, -tran[3] + panelL.programData.grid_size);
		}
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
		newRuleName = new JTextArea(ProgramLabels.defaultNewRule);
		newRuleName.setEditable(true);
		newRuleName.setBackground(Color.WHITE);
		newRuleName.setForeground(Color.BLACK);

		Border border = BorderFactory.createLineBorder(Color.BLACK);
		newRuleName.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		panel.add(label, BorderLayout.PAGE_START);
		panel.add(newRuleName, BorderLayout.LINE_START);
	}
	void showSaveButton(JPanel panel){
		saveButton = new JButton(ProgramLabels.saveRule);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String theName = newRuleName.getText();
				for (Rule rule: panelL.programData.ruleList){
					if (rule.getName().equals(theName)){
						int serialNumber = panelL.programData.ruleList.size() + 1;
						theName += Integer.toString(serialNumber);
						break;
					}
				}
				panelL.programData.ruleList.add(new Rule(theName, panelL.programData.copy(), panelR.programData.copy(), panelL.programData.marker, panelR.programData.marker));
				closeFrame();
			}
		});
		panel.add(saveButton, BorderLayout.LINE_START);
	}
	void showRemoveButton(JPanel panel){
		removeButton = new JButton(ProgramLabels.removeRule);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("REMOVE");
			}
		});
		panel.add(removeButton, BorderLayout.LINE_START);
	}
	void showCancelButton(JPanel panel){
		cancelButton = new JButton(ProgramLabels.cancelRule);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
		panel.add(cancelButton, BorderLayout.LINE_START);
	}
	void closeFrame(){
		super.dispose();
	}
}