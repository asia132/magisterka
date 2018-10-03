package grammar_graphs;

import java.awt.geom.Area;
import java.awt.Color;

import java.util.function.*;

import java.util.Stack;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.StringJoiner;

public class ColoringRule {
	Area paintCavnas;
	boolean isAplicable;
	String name;
	Color color;
	ArrayList<String> tagsSet;

	static String ruleApplied = "APPLY";
	static String ruleSkipped = "SKIP";

	// TAGS
	static final String levelAdd = "∪";
	static final String levelIntersect = "∩";
	static final String levelNot = "~";
	static final String levelSubstract = "\\";
	static final String levelXOR = "⊕";

	static final String levelBra = "(";
	static final String levelKet = ")";
	
	ColoringRule(String isAplicable, Color color, ArrayList<String> tagsSet) throws WrongTag{
		try{
			this.tagsSet = tagsSet;
			this.isAplicable = this.parseFlag(isAplicable);
			this.paintCavnas = this.parseTagSet(tagsSet);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
		this.color = color;
	}
	ColoringRule(boolean isAplicable, Color color, String [] tagsSet){
		try{
			this.tagsSet = new ArrayList<>(tagsSet.length);
			for (String tag: tagsSet) {
				this.tagsSet.add(tag);
			}
			this.isAplicable = isAplicable;
			this.paintCavnas = this.parseTagSet(this.tagsSet);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
		this.color = color;
	}
	public String toString(){
		StringJoiner info = new StringJoiner("");
		return info.add(FileSaver.paintingRule).add("\t").add(color.getRGB() + "\t").add(this.isAplicable + "\t").add(this.tagsSetToString()).toString();
	}
	Color getColor(){
		return this.color;
	}
	String getAplicableInfo(){
		if (isAplicable) return ruleApplied;
		else return ruleSkipped;
	}
	String tagsSetToString(){
		StringJoiner info = new StringJoiner(",");
		for (String tag: tagsSet) {
			info.add(tag);
		}
		return info.toString();
	}
	ArrayList<String> getTagSet(){
		return tagsSet;
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
	static String [] shuntingYardAlgorithm(ArrayList<String> text) throws WrongTag{
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
			}else if (token.equals(levelAdd) || token.equals(levelIntersect) || token.equals(levelXOR) || token.equals(levelNot) || token.equals(levelSubstract)){ // token is an operator
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
	static Function  <Area [], Area> parseOperation(String tag) throws WrongTag{
		switch(tag){
			case levelAdd:	return ColoringRule::add;
			case levelIntersect:	return ColoringRule::intersect;
			case levelXOR:	return ColoringRule::exclusiveOr;
			case levelSubstract:	return ColoringRule::subtract;
			default:
				throw new WrongTag("the operation is not in: [" + levelAdd + levelIntersect + levelXOR + levelSubstract + "]");
		}
	}
	static Level parseLevel(String tag) throws WrongTag{
		if (tag.charAt(0) != 'L') throw new WrongTag("Cannot parse level tag: " + tag);
		if (tag.charAt(1) == 'S') return MainData.coloringRuleLevels.limitingShape;
		try{
			int i = Integer.parseInt(tag.substring(1));
			return MainData.coloringRuleLevels.levels[i];
		}catch(NumberFormatException e){
			throw new WrongTag("Cannot parse level tag: " + tag);
		}
	}
	static Area parseTagSet(ArrayList<String> text) throws WrongTag{
		String [] rpnText = shuntingYardAlgorithm(text);

		Stack <String> sStack = new Stack<>();
		String sResult = "";

		Stack <Area> stack = new Stack<>();
		Area result = new Area();

		for (String token: rpnText){
			if (token.equals(levelAdd) || token.equals(levelIntersect) || token.equals(levelXOR) || token.equals(levelNot) || token.equals(levelSubstract)){
				String level_b = sStack.pop();
				String level_a = sStack.pop();
				sResult = "(" + level_a + token + level_b + ")";
				sStack.add(sResult);

				Function  <Area [], Area> operation = parseOperation(token);
				Area area_b = stack.pop();
				Area area_a = stack.pop();
				result = operation.apply(new Area[]{area_a, area_b});
				stack.add(result);
			}else{
				sStack.add(token);
				stack.add(parseLevel(token).area);
			}
		}
		sResult = sStack.pop();

		System.out.println("\n\nResult is: " + sResult);
		
		return result;
	}
	static Area add(Area [] areas){
		areas[0].add(areas[1]);
		return areas[0];
	}
	static Area intersect(Area [] areas){
		areas[0].intersect(areas[1]);
		return areas[0];
	}
	static Area exclusiveOr(Area [] areas){
		areas[0].exclusiveOr(areas[1]);
		return areas[0];
	}
	static Area subtract(Area [] areas){
		areas[0].subtract(areas[1]);
		return areas[0];
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