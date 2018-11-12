package grammar_graphs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	public static final long serialVersionUID = 42L;
	public MainFrame() {
		super(ProgramLabels.programName);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();

		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		this.setSize(screenWidth, screenHeight);
		this.setLocation(0, 0);

		MainPanel panel = new MainPanel(this, screenWidth, screenHeight);
		GrammarControl.addPanel(panel);

		this.add(panel);

		this.setDefaultCloseOperation(MainFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());

		this.pack();

		this.setVisible(true);
	}
}
