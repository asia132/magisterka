import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;

import java.awt.Color;

public class Split {
  public static void main(String args[]) {
    String title = "Resize Split";

    final JFrame vFrame = new JFrame(title);
    vFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel topButton = new Panel();
    JPanel bottomButton = new Panel();
    
    final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    splitPane.setTopComponent(topButton);
    splitPane.setBottomComponent(bottomButton);

    vFrame.getContentPane().add(splitPane, BorderLayout.CENTER);
    vFrame.setSize(300, 150);
    vFrame.setVisible(true);

  }
  // public static void main(String args[]) {
  //   String title = "Resize Split";

  //   final JFrame vFrame = new JFrame(title);
  //   vFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  //   JButton topButton = new JButton("Top");
  //   JButton bottomButton = new JButton("Bottom");

  //   final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

  //   splitPane.setTopComponent(topButton);
  //   splitPane.setBottomComponent(bottomButton);

  //   ActionListener oneActionListener = new ActionListener() {
  //     public void actionPerformed(ActionEvent event) {
  //       splitPane.setResizeWeight(1.0);
  //       vFrame.setSize(300, 250);
  //       vFrame.validate();
  //     }
  //   };
  //   bottomButton.addActionListener(oneActionListener);

  //   ActionListener anotherActionListener = new ActionListener() {
  //     public void actionPerformed(ActionEvent event) {
  //       splitPane.setResizeWeight(0.5);
  //       vFrame.setSize(300, 250);
  //       vFrame.validate();
  //     }
  //   };
  //   topButton.addActionListener(anotherActionListener);

  //   vFrame.getContentPane().add(splitPane, BorderLayout.CENTER);
  //   vFrame.setSize(300, 150);
  //   vFrame.setVisible(true);

  // }
}
class Panel extends JPanel{
    @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D)g;
    g2d.setColor(Color.CYAN);
    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
  }
}