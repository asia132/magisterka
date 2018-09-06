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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.ArrayList;

class CreateColorRuleFrame extends JFrame {

	double rfXScale = 0.15;
	double rfYScale = 0.5;
	double loc = 0.3;
	double panScale = 0.8;

	int screenHeight;
	int screenWidth;

	JButton cancelButton;
	JButton saveButton;

	int n = 0;

	Box ruleBox;

	private JPanel panel;

	CreateColorRuleFrame() {
		super(ProgramLabels.rulleWinName);
		this.loadFrameData();
		this.loadPanel();

		this.setLayout(new FlowLayout());
		this.setVisible(true);
	}
	protected void loadFrameData(){
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		this.screenHeight = (int)(screenSize.height*rfXScale);
		this.screenWidth = (int)(screenSize.width*rfYScale);
		System.out.println(screenHeight + " " + screenWidth);
		this.setSize(screenWidth, screenHeight);
		this.setLocation((int)(screenWidth*loc), (int)(screenHeight*loc));
		this.setDefaultCloseOperation(CreateRuleFrame.DISPOSE_ON_CLOSE);
	}
	protected void loadPanel(){
		this.panel = new JPanel();
		this.panel.setBackground(Color.white);
		this.panel.setPreferredSize(new Dimension(screenWidth, screenHeight));

		Box buttonBox = Box.createVerticalBox();

		buttonBox.add(this.ruleBox());

		this.panel.add(buttonBox);
		this.showSaveButton(panel);
		this.showCancelButton(panel);
		this.add(panel);
	}
	protected Box ruleBox(){
		ruleBox = Box.createHorizontalBox();
		ruleBox.createRigidArea(new Dimension(100, 100));
		

		ruleBox.add(ruleComboIndex());
		ruleBox.add(ruleName());
		ruleBox.add(ruleColorButton());

		JTextArea label = new JTextArea(" => ");
		label.setEditable(false);
		label.setFont(label.getFont().deriveFont(32f));

		ruleBox.add(label);
		
		ruleBox.add(ruleComboLevels());
		ruleBox.add(ruleComboOperator());
		ruleBox.add(ruleComboLevels());
		this.ruleBox.revalidate();
		return ruleBox;

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
		JButton b = new JButton();
		b.setBackground(Color.LIGHT_GRAY);
		b.setFocusPainted(false);
		b.setPreferredSize(new Dimension(40, 40));
		b.setMinimumSize(new Dimension(60, 60));
		b.setSize(new Dimension(60, 60));
		b.addActionListener(event -> {
			Color color = JColorChooser.showDialog(this, "Choose Color", Color.white);
			b.setBackground(color);
		});
		return b;
	}
	void showSaveButton(JPanel panel){
		saveButton = new JButton(ProgramLabels.save);
		saveButton.addActionListener(event -> {

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
		super.dispose();
	}
}