import grammar_graphs.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.FlowLayout;

import java.awt.EventQueue;
import javax.swing.JFrame; 
 
public class Main extends JFrame {
	public static final long serialVersionUID = 42L;
	public Main() {
		super(ProgramLabels.programName);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();

		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		this.setSize(screenWidth, screenHeight);
		this.setLocation(0, 0);

		MainPanel panel = new MainPanel(screenWidth, screenHeight);

		this.add(panel);

		this.setDefaultCloseOperation(Main.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());

		this.pack();

		this.setVisible(true);
	}
 
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Main();
			}
		});
	}
}