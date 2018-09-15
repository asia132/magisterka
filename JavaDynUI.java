import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JComboBox;
import java.util.ArrayList;
 
/**
 *
 * @web http://java-buddy.blogspot.com/
 */
public class JavaDynUI extends JFrame {
 
    static JavaDynUI myFrame;
    static int countMe = 0;
    JPanel mainPanel;
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
 
    private static void createAndShowGUI() {
        myFrame = new JavaDynUI();
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.prepareUI();
        myFrame.pack();
        myFrame.setVisible(true);
    }
 
    private void prepareUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
 
        JButton buttonAdd = new JButton("Add subPanel");
        buttonAdd.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.add(new subPanel());
                myFrame.pack();
            }
        });
         
        JButton buttonRemoveAll = new JButton("Remove All");
        buttonRemoveAll.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.removeAll();
                myFrame.pack();
            }
        });
 
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonAdd, BorderLayout.PAGE_START);
        getContentPane().add(buttonRemoveAll, BorderLayout.PAGE_END);
    }
 
    private class subPanel extends JPanel {
         
        subPanel me;
        ArrayList <JComboBox<String>> comboboxes = new ArrayList <>();
 
        public subPanel() {
            super();
            me = this;
            JLabel myLabel = new JLabel("Hello subPanel(): " + countMe++);
            add(myLabel);

            JButton myButtonRemoveMe = new JButton("remove me");
            myButtonRemoveMe.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    me.getParent().remove(me);
                    myFrame.pack();
                }
            });
            add(myButtonRemoveMe);

            JButton myButtonAddButton = new JButton("Add button");
            myButtonAddButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    comboboxes.add(0, new JComboBox<String>());

                    comboboxes.get(0).addItem("STAY");
                    comboboxes.get(0).addItem("REMOVE");

                    comboboxes.get(0).addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String s = (String) comboboxes.get(0).getSelectedItem();
                            switch (s){
                                case "STAY":    System.out.println("STAY");
                                                break;
                                case "REMOVE":  System.out.println("REMOVE");
                                                break;
                                default:    System.out.println("ERROR");
                                            break;
                            }
                        }
                    });
                    me.add(comboboxes.get(0));
                    myFrame.pack();
                }
            });
            add(myButtonAddButton);

            JButton myButtonRemoveButton = new JButton("Remove button");
            myButtonRemoveButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    me.remove(comboboxes.get(0));
                    comboboxes.remove(0);
                    me.revalidate();
                    myFrame.pack();
                }
            });
            add(myButtonRemoveButton);

        }
    }
}