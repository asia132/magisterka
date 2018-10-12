package grammar_graphs;

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

import java.util.List;

public class RuleListPrepraration extends JFrame {
	private JList<String> sourceList = new JList<>(new DefaultListModel<>());
	private JList<String> destList = new JList<>(new DefaultListModel<>());

	private JButton saveButton;
	private JButton cancelButton;

	public RuleListPrepraration() {
		
		for (int i = 0; i < MainData.ruleList.size(); i++) {
			((DefaultListModel<String>) sourceList.getModel()).add(i, MainData.ruleList.get(i).getName());
		}
		
		for (int i = 0; i < MainData.ruleAppList.size(); i++) {
			((DefaultListModel<String>) destList.getModel()).add(i, MainData.ruleAppList.get(i).getName());
		}
		Box buttonBox = Box.createHorizontalBox();
		showSaveButton(buttonBox);
		showCancelButton(buttonBox);

		Box sourceBox = Box.createVerticalBox();
		sourceBox.add(new JLabel("Rule List:"));
		sourceBox.add(new JScrollPane(sourceList));

		Box destBox = Box.createVerticalBox();
		destBox.add(new JLabel("Application Order:"));
		destBox.add(new JScrollPane(destList));

		Box listBox = Box.createHorizontalBox();
		listBox.add(sourceBox);
		listBox.add(destBox);

		Box allBox = Box.createVerticalBox();
		allBox.add(buttonBox);
		allBox.add(listBox);

		this.getContentPane().add(allBox, BorderLayout.CENTER);

		sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		destList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		sourceList.setDragEnabled(true);
		destList.setDragEnabled(true);

		sourceList.setDropMode(DropMode.INSERT);
		destList.setDropMode(DropMode.INSERT);

		sourceList.setTransferHandler(new SourceListTransferHandler());
		destList.setTransferHandler(new DestListTransferHandler());

		this.pack();
		this.setVisible(true);

	}
	void closeFrame(){
		super.dispose();
	}
	void showSaveButton(Box box){
		saveButton = new JButton(ProgramLabels.save);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				MainData.ruleAppList.clear();
				for (int i = 0; i < destList.getModel().getSize(); ++i) {
					MainData.ruleAppList.add(MainData.getRuleOfName(destList.getModel().getElementAt(i)));				
				}
				closeFrame();
			}
		});
		box.add(saveButton);
	}
	void showCancelButton(Box box){
		cancelButton = new JButton(ProgramLabels.cancel);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {;
				closeFrame();
			}
		});
		box.add(cancelButton);
	}
}
class SourceListTransferHandler extends TransferHandler {
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY_OR_MOVE;
	}
	@Override
	protected Transferable createTransferable(JComponent source) {
		@SuppressWarnings("unchecked")
		JList<String> sourceList = (JList<String>) source;
		String data = sourceList.getSelectedValue();
		return new StringSelection(data);
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		@SuppressWarnings("unchecked")
		JList<String> sourceList = (JList<String>) source;
		String movedItem = sourceList.getSelectedValue();
		if (action == TransferHandler.MOVE) {
			DefaultListModel<String> listModel = (DefaultListModel<String>) sourceList.getModel();
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
		@SuppressWarnings("unchecked")
		JList<String> targetList = (JList<String>) support.getComponent();
		@SuppressWarnings("unchecked")
		DefaultListModel<String> listModel = (DefaultListModel<String>) targetList.getModel();
		return true;
	}
}
class DestListTransferHandler extends SourceListTransferHandler {
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		@SuppressWarnings("unchecked")
		JList<String> sourceList = (JList<String>) source;
		String movedItem = sourceList.getSelectedValue();
		if (action == TransferHandler.MOVE) {
			DefaultListModel<String> listModel = (DefaultListModel<String>) sourceList.getModel();
			listModel.removeElement(movedItem);
			MainData.ruleAppList.remove(MainData.getRuleOfName(movedItem));
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
		@SuppressWarnings("unchecked")
		JList<String> targetList = (JList<String>) support.getComponent();
		@SuppressWarnings("unchecked")
		DefaultListModel<String> listModel = (DefaultListModel<String>) targetList.getModel();
		if (dropLocation.isInsert()) {
			listModel.add(dropIndex, data);
		} 
		return true;
	}
}