package grammar_graphs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

class PaintingRuleComponents {
	JTextArea rulesBodies;
	List<String> tagsSet;
	JComboBox<String> comboLevel;
	JButton colorButton;
	JPanel panel;

	PaintingRuleComponents() {
		tagsSet = new ArrayList<>();
	}

	List<String> tagsSetCopy() {
		List<String> copy = new ArrayList<>(tagsSet.size());
		for (String tag : tagsSet)
			copy.add(tag);
		return copy;
	}

	Color getColor() {
		return colorButton.getBackground();
	}

	String getApplicableTag() {
		return (String) (comboLevel.getSelectedItem());
	}
}
