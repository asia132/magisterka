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
 
class MessageFrame extends JFrame {
	static final long serialVersionUID = 42L;

	JTextArea newRuleName = null;
	JButton okButton;
	JPanel panelB;

	int screenHeight;
	int screenWidth;

	double rfScale = 0.5;
	double loc = 0.3;
	double panScale = 0.8;

	String message;

	MessageFrame(String message) {
		super(ProgramLabels.rulleWinName);
		loadFrameData();
		this.message = message;
		this.loadBottomPanel();

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
	void loadBottomPanel() {
		panelB = new JPanel();
		this.getContentPane().add(panelB);
		showCancelButton(panelB);
	}
	void showCancelButton(JPanel panel){
		JTextArea label = new JTextArea(this.message);
		label.setEditable(false);

		okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});

		panel.add(label, BorderLayout.NORTH);
		panel.add(okButton, BorderLayout.SOUTH);
	}
	void closeFrame(){
		super.dispose();
	}
}