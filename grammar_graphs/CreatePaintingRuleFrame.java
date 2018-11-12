package grammar_graphs;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame; 
import javax.swing.JTextArea; 
import javax.swing.JButton; 
import javax.swing.JPanel;  
import javax.swing.BorderFactory; 
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.ArrayList;

import java.lang.NumberFormatException;

class CreatePaintingRuleFrame extends JFrame {
	
	public static final long serialVersionUID = 42L;

	double loc = 0.3;
	double panScale = 0.8;

	JButton cancelButton;
	JButton saveButton;
	JButton addRuleButton;
	JButton removeRuleButton;

	int n = 0;

	private JPanel mainPanel;

	JFrame me = this;
	JTextArea nvalue;
	ArrayList <PaintingRuleComponents> rules;
	
	// TAGS
	String ruleSeparator = " => ";


	CreatePaintingRuleFrame() {
		super(ProgramLabels.defineColorRulesFrame);

		this.rules = new ArrayList<>();

		this.loadFrameData();
		this.loadPanel();

		this.setLayout(new FlowLayout());
		this.setVisible(true);
	}
	protected void loadFrameData(){
		this.setDefaultCloseOperation(CreateRuleFrame.DISPOSE_ON_CLOSE);
	}
	protected void loadPanel(){
		this.mainPanel = new JPanel();
		this.mainPanel.setBackground(Color.white);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
//		this.showNEditSection(buttonPanel);
		this.showSaveButton(buttonPanel);
		this.showAddRuleButton(buttonPanel);
		this.showCancelButton(buttonPanel);
		this.mainPanel.add(buttonPanel);

		for (PaintingRule oldRule: GrammarControl.getInstance().rulePainting){
			rules.add(0, new PaintingRuleComponents());
			rules.get(0).panel = new JPanel();
			mainPanel.add(rules.get(0).panel);
			showRemoveRuleButton(rules.get(0));
			ruleBox(rules.get(0).panel);
			rules.get(0).tagsSet = oldRule.getTagSet();
			rules.get(0).colorButton.setBackground(oldRule.getColor());
			rules.get(0).comboLevel.setSelectedItem(oldRule.getAplicableInfo());
			rules.get(0).rulesBodies.insert(EditPaintingRuleFrame.arrayListToString(oldRule.getTagSet()), 0);
			me.pack();
		}

		this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS));


		this.add(mainPanel);
		this.pack();
	}
	protected void ruleBox(JPanel panel){

		panel.add(ruleComboIndex(rules.get(0)));
		panel.add(ruleColorButton(rules.get(0)));

		JTextArea label = new JTextArea(ruleSeparator);
		label.setEditable(false);
		label.setFont(label.getFont().deriveFont(32f));
		panel.add(label);

		// tagsSet.add(0, new ArrayList <String>());
		rules.get(0).rulesBodies = new JTextArea("");
		rules.get(0).rulesBodies.setEditable(false);
		rules.get(0).rulesBodies.setFont(label.getFont().deriveFont(32f));
		rules.get(0).rulesBodies.setBackground(Color.LIGHT_GRAY);
		rules.get(0).rulesBodies.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		panel.add(rules.get(0).rulesBodies);


		JPanel comboBoxPanel = new JPanel();
		panel.add(comboBoxPanel);

		panel.add(addEditButton(rules.get(0)));
	}
	protected JComboBox <String> ruleComboIndex(PaintingRuleComponents rule){
		rule.comboLevel = new JComboBox<String>();

		rule.comboLevel.addItem(PaintingRuleTags.RULEAPPLIED.toString());
		rule.comboLevel.addItem(PaintingRuleTags.RULESKIPPED.toString());

		return rule.comboLevel;
	}
	protected JButton ruleColorButton(PaintingRuleComponents rule){
		rule.colorButton = new JButton("\n\t\t\t\n");
		rule.colorButton.setBackground(Color.LIGHT_GRAY);
		rule.colorButton.setFocusPainted(false);
		rule.colorButton.addActionListener(event -> {
			Color color = JColorChooser.showDialog(this, ProgramLabels.chooseColor, Color.white);
			rule.colorButton.setBackground(color);
		});
		return rule.colorButton;
	}
	protected JButton addEditButton(PaintingRuleComponents rule){
		JButton addButton = new JButton(ProgramLabels.editRule);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EditPaintingRuleFrame(rule, me);
			}
		});
		return addButton;
	}
	void showSaveButton(JPanel mainPanel){
		saveButton = new JButton(ProgramLabels.save);
		saveButton.addActionListener(event -> {
			try{
				GrammarControl.getInstance().paintingRuleLevels.setMaxNAllowed(Integer.parseInt(nvalue.getText()));
				closeFrame();
			}catch(NumberFormatException e){
				new MessageFrame(nvalue.getText() + " - the provided N must be integer");
			}
			try{
				GrammarControl.getInstance().rulePainting.clear();
				for (PaintingRuleComponents ruleData: rules){
					GrammarControl.getInstance().rulePainting.add(new PaintingRule(ruleData.getApplicableTag(), ruleData.getColor(), ruleData.tagsSet));
				}
			}catch(PaintingRule.WrongTag e){
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
				rules.add(0, new PaintingRuleComponents());
				rules.get(0).panel = new JPanel();
				mainPanel.add(rules.get(0).panel);
				showRemoveRuleButton(rules.get(0));
				ruleBox(rules.get(0).panel);
				me.pack();
			}
		});
		panel.add(addRuleButton, BorderLayout.LINE_START);
	}
	void showRemoveRuleButton(PaintingRuleComponents rule){
		removeRuleButton = new JButton(ProgramLabels.removeRule);
		removeRuleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rule.panel.getParent().remove(rule.panel);
				rules.remove(rule);
				me.pack();
			}
		});
		rule.panel.add(removeRuleButton, BorderLayout.LINE_START);
	}
	void showNEditSection(JPanel panel){

		JTextArea nlabel = new JTextArea("N = ");
		nlabel.setEditable(false);
		panel.add(nlabel, BorderLayout.LINE_START);

		this.nvalue = new JTextArea(GrammarControl.getInstance().paintingRuleLevels.max_n_allowed + "");
		this.nvalue.setEditable(true);
		this.nvalue.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		nvalue.setBackground(new Color(224, 224, 224));
		panel.add(this.nvalue, BorderLayout.LINE_START);

	}
	void closeFrame(){
		super.dispose();
	}
}

