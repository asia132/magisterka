package grammar_graphs;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent; 

public class RuleListPrepraration extends JFrame {
	
	public static final long serialVersionUID = 42L;
	
	private JList<String> sourceList = new JList<>(new DefaultListModel<>());
	private JList<String> destList = new JList<>(new DefaultListModel<>());

	public RuleListPrepraration() {
		
		for (int i = 0; i < GrammarControl.getInstance().ruleList.size(); i++) {
			((DefaultListModel<String>) sourceList.getModel()).add(i, GrammarControl.getInstance().ruleList.get(i).getName());
		}
		
		for (int i = 0; i < GrammarControl.getInstance().ruleAppList.size(); i++) {
			((DefaultListModel<String>) destList.getModel()).add(i, GrammarControl.getInstance().ruleAppList.get(i).getName());
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
		JButton saveButton = new JButton(ProgramLabels.save);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				GrammarControl.getInstance().ruleAppList.clear();
				for (int i = 0; i < destList.getModel().getSize(); ++i) {
					GrammarControl.getInstance().ruleAppList.add(GrammarControl.getInstance().getRuleOfName(destList.getModel().getElementAt(i)));				
				}
				closeFrame();
			}
		});
		box.add(saveButton);
	}
	void showCancelButton(Box box){
		JButton cancelButton = new JButton(ProgramLabels.cancel);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {;
				closeFrame();
			}
		});
		box.add(cancelButton);
	}
	class SourceListTransferHandler extends TransferHandler {
		public static final long serialVersionUID = 42L;
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
			return true;
		}
	}
	class DestListTransferHandler extends SourceListTransferHandler {
		public static final long serialVersionUID = 42L;
		@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
			@SuppressWarnings("unchecked")
			JList<String> sourceList = (JList<String>) source;
			int movedItemIndx = sourceList.getSelectedIndex();
			if (action == TransferHandler.MOVE) {
				DefaultListModel<String> listModel = (DefaultListModel<String>) sourceList.getModel();
				System.out.println("REMOVE RULE OF INDEX: " + movedItemIndx + "\tRULE NAME: " + listModel.get(movedItemIndx));
				listModel.remove(movedItemIndx);
				System.out.println("SIZE CHECK: " + listModel.size());
				// MainData.ruleAppList.remove(movedItemIndx);
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
			DefaultListModel<String> listModel = (DefaultListModel<String>) targetList.getModel();
			if (dropLocation.isInsert()) {
				listModel.add(dropIndex, data);
			} 
			return true;
		}
	}
}
