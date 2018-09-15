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
import javax.swing.JColorChooser;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.ArrayList;

class CreateColorRuleFrame extends JFrame {

	double loc = 0.3;
	double panScale = 0.8;

	JButton cancelButton;
	JButton saveButton;
	JButton addRuleButton;
	JButton removeRuleButton;

	int n = 0;

	private JPanel mainPanel;
	private ArrayList <JPanel> panelList = new ArrayList<>();
	private ArrayList <JComboBox> boxList = new ArrayList<>();
	ArrayList <String> tags;
	JFrame me = this;


	CreateColorRuleFrame() {
		super(ProgramLabels.rulleWinName);

		this.loadFrameData();
		this.loadPanel();

		this.setLayout(new FlowLayout());
		this.setVisible(true);
	}
	protected void loadFrameData(){
		Toolkit tk = Toolkit.getDefaultToolkit();
		// this.setLocation((int)(screenWidth*loc), (int)(screenHeight*loc));
		this.setDefaultCloseOperation(CreateRuleFrame.DISPOSE_ON_CLOSE);
	}
	protected void loadPanel(){
		this.mainPanel = new JPanel();
		this.mainPanel.setBackground(Color.white);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		this.showSaveButton(buttonPanel);
		this.showAddRuleButton(buttonPanel);
		this.showCancelButton(buttonPanel);
		this.mainPanel.add(buttonPanel);

		this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS));


		this.add(mainPanel);
		this.pack();
	}
	protected void ruleBox(JPanel panel){

		panel.add(ruleComboIndex());
		panel.add(ruleName());
		panel.add(ruleColorButton());

		JTextArea label = new JTextArea(" => ");
		label.setEditable(false);
		label.setFont(label.getFont().deriveFont(32f));

		panel.add(label);


		JPanel comboBoxPanel = new JPanel();
		panel.add(addComboButton(comboBoxPanel));
		panel.add(removeComboButton(comboBoxPanel));
		panel.add(comboBoxPanel);
		comboBoxPanel.add(ruleComboLevels());

		panel.add(addComboButton(comboBoxPanel));
		panel.add(removeComboButton(comboBoxPanel));
	}
	protected JTextArea ruleName(){
		JTextArea newRuleName = new JTextArea(ProgramLabels.defaultNewRule);
		newRuleName.setEditable(true);
		newRuleName.setBackground(Color.WHITE);
		newRuleName.setForeground(Color.BLACK);

		Border border = BorderFactory.createLineBorder(Color.BLACK);
		newRuleName.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		return newRuleName;
	}
	protected JComboBox ruleComboIndex(){
		JComboBox<String> comboLevel = new JComboBox<String>();

		// add items to the combo box
		comboLevel.addItem("V");
		comboLevel.addItem("X");

		return comboLevel;
	}
	protected JComboBox ruleComboOperator(){
		JComboBox<String> comboLevel = new JComboBox<String>();

		// add items to the combo box
		comboLevel.addItem("∪");
		comboLevel.addItem("∩");
		// comboLevel.addItem("~");
		comboLevel.addItem("⊕");
		// comboLevel.addItem("(");
		// comboLevel.addItem(")");

		// comboLevel.addActionListener(this);

		return comboLevel;
	}
	protected JComboBox ruleComboLevels(){

		String [] levels = new String [MainData.coloringRuleLevels.n + 1];
		for (int i = 0; i <= MainData.coloringRuleLevels.n; ++i) {
			levels[i] = "L" + i;
		}

		JComboBox<String> comboLevel = new JComboBox<String>(levels);

		return comboLevel;
	}
	protected JButton ruleColorButton(){
		JButton b = new JButton("\n\t\t\t\n");
		b.setBackground(Color.LIGHT_GRAY);
		b.setFocusPainted(false);
		b.addActionListener(event -> {
			Color color = JColorChooser.showDialog(this, "Choose Color", Color.white);
			b.setBackground(color);
		});
		return b;
	}
	protected JButton addComboButton(JPanel comboBoxPanel){
		JButton addButton = new JButton(ProgramLabels.addElem);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boxList.add(0, ruleComboOperator());
				comboBoxPanel.add(boxList.get(0));
				boxList.add(0, ruleComboLevels());
				comboBoxPanel.add(boxList.get(0));
				me.pack();
			}
		});
		return addButton;
	}
	protected JButton removeComboButton(JPanel comboBoxPanel){
		JButton remButton = new JButton(ProgramLabels.remElem);
		remButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (boxList.size() > 1){
					comboBoxPanel.remove(boxList.get(0));
					boxList.remove(0);
					comboBoxPanel.remove(boxList.get(0));
					boxList.remove(0);
					me.pack();
				}
			}
		});
		return remButton;
	}
	void showSaveButton(JPanel mainPanel){
		saveButton = new JButton(ProgramLabels.save);
		saveButton.addActionListener(event -> {

		});
		mainPanel.add(saveButton, BorderLayout.LINE_START);
	}
	void showCancelButton(JPanel mainPanel){
		cancelButton = new JButton(ProgramLabels.cancel);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
		mainPanel.add(cancelButton, BorderLayout.LINE_START);
	}
	void showAddRuleButton(JPanel panel){
		addRuleButton = new JButton(ProgramLabels.addRule);
		addRuleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelList.add(0, new JPanel());
				mainPanel.add(panelList.get(0));
				showRemoveRuleButton(panelList.get(0));
				ruleBox(panelList.get(0));
				me.pack();
			}
		});
		panel.add(addRuleButton, BorderLayout.LINE_START);
	}
	void showRemoveRuleButton(JPanel panel){
		removeRuleButton = new JButton(ProgramLabels.removeRule);
		removeRuleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelList.remove(panel);
				panel.getParent().remove(panel);
				me.pack();
			}
		});
		panel.add(removeRuleButton, BorderLayout.LINE_START);
	}
	void closeFrame(){
		super.dispose();
	}
}