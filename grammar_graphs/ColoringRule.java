package grammar_graphs;

import java.awt.geom.Area;
import java.awt.Color;

import java.util.function.*;

import java.util.Stack;
import java.util.LinkedList;

public class ColoringRule {
	Area paintCavnas;
	boolean isAplicable;
	String name;
	Color color;

	static String ruleApplied = "APPLY";
	static String ruleSkipped = "SKIP";

		// TAGS
	static String levelAdd = "∪";
	static String levelIntersect = "∩";
	static String levelNot = "~";
	static String levelXOR = "⊕";

	static String levelBra = "(";
	static String levelKet = ")";
	
	ColoringRule(String isAplicable, Color color, String [] tagsSet){
		try{
			this.isAplicable = this.parseFlag(isAplicable);
			this.paintCavnas = this.parseTagSet(tagsSet);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
		this.color = color;
	}
	boolean parseFlag(String isAplicable) throws WrongTag{
		if (isAplicable.equals(ruleApplied)) return true;
		if (isAplicable.equals(ruleSkipped)) return false;
		throw new WrongTag("Aplicable tag.");
	}
	static void arrayPrint(String [] text){
		for (String t: text) {
			System.out.print(t);
		}
	}
	static String [] shuntingYardAlgorithm(String [] text) throws WrongTag{
		LinkedList <String> output = new LinkedList<>();
		Stack <String> stack = new Stack<>();

		for (String token: text){
			if (token.equals(levelBra)){ // token = "("
				stack.add(token);
			}else if (token.equals(levelKet)){ // token = ")"
				
				int j = 0;
				while (!stack.peek().equals(levelBra)){
					j++;
					output.add(stack.pop());
				}
				if (j == 0){
					throw new WrongTag("Brackets are not closed");
				}
				stack.pop();
			}else if (token.equals(levelAdd) || token.equals(levelIntersect) || token.equals(levelXOR) || token.equals(levelNot)){ // token is an operator
				while (!stack.empty() && !stack.peek().equals(levelBra)){
					output.add(stack.pop());
				}
				stack.add(token);

			}else{ // token is a number
				output.add(token);
			}
		}

		while (!stack.empty()){
			if (stack.peek().equals(levelBra) || stack.peek().equals(levelKet)){
				throw new WrongTag("Brackets are not closed on the end");
			}
			output.add(stack.pop());
		}
		return output.toArray(new String[output.size()]);
	}
	static Area parseTagSet(String [] text) throws WrongTag{
		// arrayPrint(text);
		String [] rpnText = shuntingYardAlgorithm(text);
		// arrayPrint(text);

		Stack <String> stack = new Stack<>();
		String result = "";

		for (String token: rpnText){
			if (token.equals(levelAdd) || token.equals(levelIntersect) || token.equals(levelXOR) || token.equals(levelNot)){
				String level_b = stack.pop();
				String level_a = stack.pop();
				result = "(" + level_a + token + level_b + ")";
				stack.add(result);
			}else{
				stack.add(token);
			}
		}
		result = stack.pop();

		System.out.println("\n\nResult is: " + result);
		
		return new Area();
	}
	public static void main(String[] args) {
		String [] rule1 = {"L0", "∩", "L1", "∩", "L2"};
		String [] rule2 = {"(", "L0", "∩", "L1", "∩", "LS", "~", "L2", ")", "∪", "(", "L0", "∩", "LS", "~", "L1", "∩", "L2", ")", "∪", "(", "LS", "~", "L0", "∩", "L1", "∩", "L2", ")"};
		String [] rule3 = {"(", "L0", "∩", "LS", "~", "L1", "∩", "LS", "~", "L2", ")", "∪", "(", "LS", "~", "L0", "∩", "L1", "∩", "LS", "~", "L2", ")", "∪", "(", "LS", "~", "L0", "∩", "LS", "~", "L1", "∩", "L2", ")"};
		String [] rule4 = {"LS", "~", "(", "L0", "∩", "L1", "∩", "L2", ")"};
		try{
			parseTagSet(rule1);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
		try{
			parseTagSet(rule2);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
		try{
			parseTagSet(rule3);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
		try{
			parseTagSet(rule4);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
	}
}
class WrongTag extends Exception {
	public WrongTag(String message){
		super("The tag is wrong" + ". " + message);
	}
	public WrongTag(){
		super("The tag is wrong");
	}
}