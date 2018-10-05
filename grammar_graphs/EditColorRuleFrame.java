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
import java.util.StringJoiner;

class EditColorRuleFrame extends JFrame {

	double loc = 0.3;
	double panScale = 0.8;

	JButton cancelButton;
	JButton saveButton;

	int n = 0;

	private JPanel mainPanel;
	JFrame me = this;

	ArrayList <String> tagList;
	RuleComponents parentRuleData;
	JFrame parent;

	JTextArea ruleBody;
	int pos = 0;
	int tagPos = 0;


	EditColorRuleFrame(RuleComponents ruleData, JFrame parent) {
		super(ProgramLabels.editColorRuleFrame);

		this.parentRuleData = ruleData;
		this.parent = parent;
		this.tagList = ruleData.tagsSetCopy();
		System.out.println(tagList.size() + " found");

		this.tagPos = ruleData.tagsSet.size();

		this.loadFrameData();
		this.loadPanel();

		this.setLayout(new FlowLayout());
		this.setVisible(true);
	}
	protected void loadFrameData(){
		Toolkit tk = Toolkit.getDefaultToolkit();
		this.setDefaultCloseOperation(CreateRuleFrame.DISPOSE_ON_CLOSE);
	}
	static String arrayListToString(ArrayList <String> stringList){
		StringJoiner result = new StringJoiner("");
		for (String tag: stringList) {
			result.add(tag);
		}
		return result.toString();
	}
	protected void loadPanel(){
		this.mainPanel = new JPanel();
		this.mainPanel.setBackground(Color.white);
		this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS));

		this.loadTextArea();		// show text section
		this.loadLevelPanel();		// show section of levels buttons
		this.loadOperatorPanel();	// show section on operator buttons
		this.loadFunctionPanel();	// show section on operator buttons

		this.add(mainPanel);
		this.pack();
	}
	void loadTextArea(){

		this.ruleBody = new JTextArea(arrayListToString(this.tagList));
		this.ruleBody.setEditable(false);
		this.ruleBody.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		this.ruleBody.setBackground(new Color(224, 224, 224));
		this.pos = ruleBody.getText().length();
		this.ruleBody.setCaretPosition(pos);
		this.ruleBody.getCaret().setVisible(true);
		this.mainPanel.add(this.ruleBody, BorderLayout.LINE_START);
	}
	void loadLevelPanel(){
		JPanel levelPanel = new JPanel();
		levelPanel.setBackground(Color.white);

		for (int i = 0; i <= MainData.coloringRuleLevels.getN(); i++){
			levelPanel.add(addSign("L" + i));
		}

		this.mainPanel.add(levelPanel);
	}
	void loadOperatorPanel(){
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);

		panel.add(addSign(ColoringRule.levelAdd));
		panel.add(addSign(ColoringRule.levelIntersect));
		panel.add(addNegationSign(ColoringRule.levelNot));
		panel.add(addSign(ColoringRule.levelXOR));
		panel.add(addSign(ColoringRule.levelSubstract));
		panel.add(addSign(ColoringRule.levelBra));
		panel.add(addSign(ColoringRule.levelKet));

		this.mainPanel.add(panel);
	}
	void loadFunctionPanel(){
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);

		panel.add(saveButton());
		panel.add(cancelButton());
		panel.add(delButton());
		panel.add(ACButton());
		panel.add(leftButton());
		panel.add(rigthButton());

		this.mainPanel.add(panel);
	}
	JButton ACButton(){
		JButton button = new JButton("AC");
		button.addActionListener(event -> {
			this.ruleBody.replaceRange(null, 0, ruleBody.getText().length());
			this.tagList.clear();
			this.pos = 0;
			this.tagPos = 0;
			this.ruleBody.setCaretPosition(pos);
			ruleBody.getCaret().setVisible(true);
		});
		return button;
	}
	JButton leftButton(){
		JButton button = new JButton("<");
		button.addActionListener(event -> {
			if (tagPos > 0){
				this.tagPos--;
				this.pos -= tagList.get(tagPos).length();
			}
			this.ruleBody.setCaretPosition(pos);
			ruleBody.getCaret().setVisible(true);
		});
		return button;
	}
	JButton rigthButton(){
		JButton button = new JButton(">");
		button.addActionListener(event -> {
			if (tagPos < tagList.size()){
				this.pos += tagList.get(tagPos).length();
				this.tagPos++;
			}
			this.ruleBody.setCaretPosition(pos);
			ruleBody.getCaret().setVisible(true);
		});
		return button;
	}
	JButton delButton(){
		JButton button = new JButton(ProgramLabels.delete);
		button.addActionListener(event -> {
			if (this.tagList.size() > 0){
				--tagPos;
				this.ruleBody.replaceRange(null, this.pos - tagList.get(tagPos).length(), this.pos);
				this.pos -= tagList.get(tagPos).length();
				this.tagList.remove(tagPos);
				this.ruleBody.setCaretPosition(pos);
				ruleBody.getCaret().setVisible(true);
			}
		});
		return button;
	}
	JButton saveButton(){
		JButton button = new JButton(ProgramLabels.save);
		button.addActionListener(event -> {

			parentRuleData.rulesBodies.replaceRange(null, 0, parentRuleData.rulesBodies.getText().length());
			parentRuleData.rulesBodies.insert(arrayListToString(tagList), 0);
			parentRuleData.tagsSet = tagList;
			this.parent.pack();
			closeFrame();
		});
		return button;
	}
	JButton cancelButton(){
		JButton button = new JButton(ProgramLabels.cancel);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
		return button;
	}
	JButton addSign(String operator){
		JButton button = new JButton(operator);
		button.addActionListener(event -> {

			ruleBody.insert(operator, pos);
			tagList.add(tagPos++, operator);
			pos += operator.length();
			this.ruleBody.setCaretPosition(pos);
			ruleBody.getCaret().setVisible(true);
		});
		button.setFont(button.getFont().deriveFont(32f));
		return button;
	}
	JButton addNegationSign(String operator){
		JButton button = new JButton(operator);
		button.addActionListener(event -> {

			String updatedOperator = "LS" + ColoringRule.levelSubstract;
			
			ruleBody.insert(updatedOperator, pos);
			tagList.add(tagPos++, "LS");
			tagList.add(tagPos++, ColoringRule.levelSubstract);
			pos += updatedOperator.length();

			this.ruleBody.setCaretPosition(pos);
			ruleBody.getCaret().setVisible(true);
		});
		button.setFont(button.getFont().deriveFont(32f));
		return button;
	}
	void closeFrame(){
		super.dispose();
	}
}