import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
//from  ww w  .j  av  a 2 s .c o  m
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent; 

public class DragAndDrop extends JFrame {
  // private JTextField newTextField = new JTextField(10);
  private JList<String> sourceList = new JList<>(new DefaultListModel<>());
  private JList<String> destList = new JList<>(new DefaultListModel<>());
  private JButton saveButton;
  private JButton cancelButton;
  public DragAndDrop() {
    
    for (int i = 0; i < 15; i++) {
      ((DefaultListModel<String>) sourceList.getModel()).add(i, "A " + i);
      ((DefaultListModel<String>) destList.getModel()).add(i, "B " + i);
    }
    Box nameBox = Box.createHorizontalBox();
    nameBox.add(new JLabel("New:"));

    // saveButton = new JButton(ProgramLabels.saveRule);
    saveButton = new JButton("Save");
    saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
           System.out.println("Save");    
      }
    });
    // panel.add(saveButton, BorderLayout.LINE_START);
    nameBox.add(saveButton);

    // saveButton = new JButton(ProgramLabels.saveRule);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        System.out.println("Cancel");
      }
    });
    // panel.add(saveButton, BorderLayout.LINE_START);
    nameBox.add(cancelButton);

    Box sourceBox = Box.createVerticalBox();
    sourceBox.add(new JLabel("Source"));
    sourceBox.add(new JScrollPane(sourceList));

    Box destBox = Box.createVerticalBox();
    destBox.add(new JLabel("Destination"));
    destBox.add(new JScrollPane(destList));

    Box listBox = Box.createHorizontalBox();
    listBox.add(sourceBox);
    listBox.add(destBox);

    Box allBox = Box.createVerticalBox();
    allBox.add(nameBox);
    allBox.add(listBox);

    this.getContentPane().add(allBox, BorderLayout.CENTER);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    destList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // newTextField.setDragEnabled(true);
    sourceList.setDragEnabled(true);
    destList.setDragEnabled(true);

    sourceList.setDropMode(DropMode.INSERT);
    destList.setDropMode(DropMode.INSERT);

    sourceList.setTransferHandler(new SourceListTransferHandler());
    destList.setTransferHandler(new DestListTransferHandler());
  }
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      DragAndDrop frame = new DragAndDrop();
      frame.pack();
      frame.setVisible(true);
    });
  }
}
class SourceListTransferHandler extends TransferHandler {
  @Override
  public int getSourceActions(JComponent c) {
    return TransferHandler.COPY_OR_MOVE;
  }
  @Override
  protected Transferable createTransferable(JComponent source) {
    JList<String> sourceList = (JList<String>) source;
    String data = sourceList.getSelectedValue();
    Transferable t = new StringSelection(data);
    return t;
  }

  @Override
  protected void exportDone(JComponent source, Transferable data, int action) {
    @SuppressWarnings("unchecked")
    JList<String> sourcelist = (JList<String>) source;
    String movedItem = sourcelist.getSelectedValue();
    if (action == TransferHandler.MOVE) {
      DefaultListModel<String> listModel = (DefaultListModel<String>) sourcelist.getModel();
    }
  }
  @Override
  public boolean canImport(TransferHandler.TransferSupport support) {
    if (!support.isDrop()) {
      return false;
    }
    return support.isDataFlavorSupported(DataFlavor.stringFlavor);
  }
  @Override
  public boolean importData(TransferHandler.TransferSupport support) {
    if (!this.canImport(support)) {
      return false;
    }
    Transferable t = support.getTransferable();
    String data = null;
    try {
      data = (String) t.getTransferData(DataFlavor.stringFlavor);
      if (data == null) {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
    int dropIndex = dropLocation.getIndex();
    JList<String> targetList = (JList<String>) support.getComponent();
    DefaultListModel<String> listModel = (DefaultListModel<String>) targetList.getModel();
    // if (dropLocation.isInsert()) {
    //   listModel.add(dropIndex, data);
    // } else {
    //   listModel.set(dropIndex, data);
    // }
    return true;
  }
}
class DestListTransferHandler extends SourceListTransferHandler {
  @Override
  protected void exportDone(JComponent source, Transferable data, int action) {
    @SuppressWarnings("unchecked")
    JList<String> sourcelist = (JList<String>) source;
    String movedItem = sourcelist.getSelectedValue();
    if (action == TransferHandler.MOVE) {
      DefaultListModel<String> listModel = (DefaultListModel<String>) sourcelist.getModel();
      listModel.removeElement(movedItem);
    }
  }
  @Override
  public boolean importData(TransferHandler.TransferSupport support) {
    if (!this.canImport(support)) {
      return false;
    }
    Transferable t = support.getTransferable();
    String data = null;
    try {
      data = (String) t.getTransferData(DataFlavor.stringFlavor);
      if (data == null) {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
    int dropIndex = dropLocation.getIndex();
    JList<String> targetList = (JList<String>) support.getComponent();
    DefaultListModel<String> listModel = (DefaultListModel<String>) targetList.getModel();
    if (dropLocation.isInsert()) {
      listModel.add(dropIndex, data);
      System.out.println("CONTROL ADD: " + listModel.size());
    } else {
      listModel.set(dropIndex, data);
      System.out.println("CONTROL SET: " + listModel.size());
    }
    return true;
  }
}