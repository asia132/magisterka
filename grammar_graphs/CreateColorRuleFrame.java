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

import java.lang.NumberFormatException;

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

	JFrame me = this;
	JTextArea nvalue;
	ArrayList <JTextArea> rulesBodies = new ArrayList<>();
	String [] tagsSet;

	// TAGS
	String ruleSeparator = " => ";


	CreateColorRuleFrame() {
		super(ProgramLabels.defineColorRulesFrame);

		this.loadFrameData();
		this.loadPanel();

		this.setLayout(new FlowLayout());
		this.setVisible(true);
	}
	protected void loadFrameData(){
		Toolkit tk = Toolkit.getDefaultToolkit();
		this.setDefaultCloseOperation(CreateRuleFrame.DISPOSE_ON_CLOSE);
	}
	protected void loadPanel(){
		this.mainPanel = new JPanel();
		this.mainPanel.setBackground(Color.white);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		this.showNEditSection(buttonPanel);
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
		panel.add(ruleColorButton());

		JTextArea label = new JTextArea(ruleSeparator);
		label.setEditable(false);
		label.setFont(label.getFont().deriveFont(32f));
		panel.add(label);

		rulesBodies.add(0, new JTextArea("\t"));
		rulesBodies.get(0).setEditable(false);
		rulesBodies.get(0).setFont(label.getFont().deriveFont(32f));
		rulesBodies.get(0).setBackground(Color.LIGHT_GRAY);
		rulesBodies.get(0).setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		panel.add(rulesBodies.get(0));


		JPanel comboBoxPanel = new JPanel();
		panel.add(comboBoxPanel);

		panel.add(addComboButton(comboBoxPanel, rulesBodies.get(0)));
	}
	protected JComboBox ruleComboIndex(){
		JComboBox<String> comboLevel = new JComboBox<String>();

		comboLevel.addItem(ColoringRule.ruleApplied);
		comboLevel.addItem(ColoringRule.ruleSkipped);

		return comboLevel;
	}
	protected JButton ruleColorButton(){
		JButton b = new JButton("\n\t\t\t\n");
		b.setBackground(Color.LIGHT_GRAY);
		b.setFocusPainted(false);
		b.addActionListener(event -> {
			Color color = JColorChooser.showDialog(this, ProgramLabels.chooseColor, Color.white);
			b.setBackground(color);
		});
		return b;
	}
	protected JButton addComboButton(JPanel comboBoxPanel, JTextArea textArea){
		JButton addButton = new JButton(ProgramLabels.editRule);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EditColorRuleFrame(textArea, tagsSet);
			}
		});
		return addButton;
	}
	void showSaveButton(JPanel mainPanel){
		saveButton = new JButton(ProgramLabels.save);
		saveButton.addActionListener(event -> {
			try{
				MainData.coloringRuleLevels.setMaxNAllowed(Integer.parseInt(nvalue.getText()));
				closeFrame();
			}catch(NumberFormatException e){
				new MessageFrame(nvalue.getText() + " - the provided N must be integer");
			}
			// TODO: tutaj trzeba uzupełnić zapis reguł
			try{
				for (JTextArea ruleBody: rulesBodies){
					// String tagsSet
				}
			}catch(WrongTag e){
				new MessageFrame(e.getMessage());
			}
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
	void showNEditSection(JPanel panel){

		JTextArea nlabel = new JTextArea("N = ");
		nlabel.setEditable(false);
		panel.add(nlabel, BorderLayout.LINE_START);

		this.nvalue = new JTextArea(MainData.coloringRuleLevels.max_n_allowed + "");
		this.nvalue.setEditable(true);
		this.nvalue.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		nvalue.setBackground(new Color(224, 224, 224));
		panel.add(this.nvalue, BorderLayout.LINE_START);

	}
	void closeFrame(){
		super.dispose();
	}
}