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

class EditColorRuleFrame extends JFrame {

	double loc = 0.3;
	double panScale = 0.8;

	JButton cancelButton;
	JButton saveButton;

	int n = 0;

	private JPanel mainPanel;
	JFrame me = this;

	JTextArea parentText;

	// TAGS
	String levelAdd = "∪";
	String levelIntersect = "∩";
	String levelNot = "~";
	String levelXOR = "⊕";
	String levelBra = "(";
	String levelKet = ")";

	JTextArea ruleBody;
	int pos = 0;


	EditColorRuleFrame(JTextArea parentText) {
		super(ProgramLabels.editColorRuleFrame);

		this.parentText = parentText;

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
		this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS));

		this.loadTextArea();		// show text section
		this.loadLevelPanel();		// show section of levels buttons
		this.loadOperatorPanel();	// show section on operator buttons
		this.loadFunctionPanel();	// show section on operator buttons

		this.add(mainPanel);
		this.pack();
	}
	void loadTextArea(){

		this.ruleBody = new JTextArea(this.parentText.getText());
		this.ruleBody.setEditable(false);
		this.ruleBody.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		ruleBody.setBackground(new Color(224, 224, 224));
		ruleBody.getCaret().setVisible(true);
		this.mainPanel.add(this.ruleBody, BorderLayout.LINE_START);
	}
	void loadLevelPanel(){
		JPanel levelPanel = new JPanel();
		levelPanel.setBackground(Color.white);

		for (int i = 0; i < MainData.coloringRuleLevels.getN(); i++){
			levelPanel.add(addSign("L" + i));
		}

		this.mainPanel.add(levelPanel);
	}
	void loadOperatorPanel(){
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);

		panel.add(addSign(levelAdd));
		panel.add(addSign(levelIntersect));
		panel.add(addSign(levelNot));
		panel.add(addSign(levelXOR));
		panel.add(addSign(levelBra));
		panel.add(addSign(levelKet));

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
			this.pos = 0;
			this.ruleBody.setCaretPosition(pos);
			ruleBody.getCaret().setVisible(true);
		});
		return button;
	}
	JButton leftButton(){
		JButton button = new JButton("<");
		button.addActionListener(event -> {
			if (pos > 0)
				this.pos--;
			this.ruleBody.setCaretPosition(pos);
			ruleBody.getCaret().setVisible(true);
		});
		return button;
	}
	JButton rigthButton(){
		JButton button = new JButton(">");
		button.addActionListener(event -> {
			if (pos < ruleBody.getText().length())
				this.pos++;
			this.ruleBody.setCaretPosition(pos);
			ruleBody.getCaret().setVisible(true);
		});
		return button;
	}
	JButton delButton(){
		JButton button = new JButton(ProgramLabels.delete);
		button.addActionListener(event -> {
			this.ruleBody.replaceRange(null, this.pos - 1, this.pos);
			this.pos--;
			this.ruleBody.setCaretPosition(pos);
			ruleBody.getCaret().setVisible(true);
		});
		return button;
	}
	JButton saveButton(){
		JButton button = new JButton(ProgramLabels.save);
		button.addActionListener(event -> {
			this.parentText.replaceRange(null, 0, this.parentText.getText().length());
			this.parentText.insert(this.ruleBody.getText(), 0);
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
			pos += operator.length();
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