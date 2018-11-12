package grammar_graphs;

import java.awt.geom.Area;
import java.awt.Color;

import java.util.function.*;

import java.util.Stack;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.StringJoiner;

public class PaintingRule {
	Area paintCavnas;
	boolean isAplicable;
	String name;
	Color color;
	ArrayList<String> tagsSet;
	
	PaintingRule(String isAplicable, Color color, ArrayList<String> tagsSet) throws WrongTag{
		try{
			this.tagsSet = tagsSet;
			this.isAplicable = this.parseFlag(isAplicable);
			this.paintCavnas = PaintingRule.parseTagSet(tagsSet);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
		this.color = color;
	}
	PaintingRule(boolean isAplicable, Color color, String [] tagsSet){
		try{
			this.tagsSet = new ArrayList<>(tagsSet.length);
			for (String tag: tagsSet) {
				this.tagsSet.add(tag);
			}
			this.isAplicable = isAplicable;
			this.paintCavnas = PaintingRule.parseTagSet(this.tagsSet);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
		this.color = color;
	}
	public String toString(){
		StringJoiner info = new StringJoiner("");
		return info.add(FileSaverTags.PAINTINGRULE.toString()).add("\t").add(color.getRGB() + "\t").add(this.isAplicable + "\t").add(this.tagsSetToString()).toString();
	}
	Color getColor(){
		return this.color;
	}
	String getAplicableInfo(){
		if (isAplicable) return PaintingRuleTags.RULEAPPLIED.toString();
		else return PaintingRuleTags.RULESKIPPED.toString();
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
		if (PaintingRuleTags.RULEAPPLIED.equals(isAplicable)) return true;
		if (PaintingRuleTags.RULESKIPPED.equals(isAplicable)) return false;
		throw new WrongTag("Aplicable tag.");
	}
	static void arrayPrint(String [] text){
		for (String t: text) {
			System.out.print(t);
		}
	}
	static LinkedList <String> shuntingYardAlgorithm(ArrayList<String> text) throws WrongTag{
		LinkedList <String> output = new LinkedList<>();
		Stack <String> stack = new Stack<>();

		for (String token: text){
			if (PaintingRuleTags.LEVELBRA.equals(token)){ // token = "("
				stack.add(token);
			}else if (PaintingRuleTags.LEVELKET.equals(token)){ // token = ")"
				
				int j = 0;
				while (!PaintingRuleTags.LEVELBRA.equals(stack.peek())){
					j++;
					output.add(stack.pop());
				}
				if (j == 0){
					throw new WrongTag("Brackets are not closed");
				}
				stack.pop();
			}else if (PaintingRuleTags.LEVELADD.equals(token) || PaintingRuleTags.LEVELINTERSECT.equals(token) || PaintingRuleTags.LEVELXOR.equals(token) || PaintingRuleTags.LEVELNOT.equals(token) || PaintingRuleTags.LEVELSUBSTRACT.equals(token)){ // token is an operator
				while (!stack.empty() && !PaintingRuleTags.LEVELBRA.equals(stack.peek())){
					output.add(stack.pop());
				}
				stack.add(token);

			}else{ // token is a number
				output.add(token);
			}
		}

		while (!stack.empty()){
			if (PaintingRuleTags.LEVELBRA.equals(stack.peek()) || PaintingRuleTags.LEVELKET.equals(stack.peek())){
				throw new WrongTag("Brackets are not closed on the end");
			}
			output.add(stack.pop());
		}
		return output;
	}
	static Function  <Area [], Area> parseOperation(String tag) throws WrongTag{
			if (PaintingRuleTags.LEVELADD.equals(tag))			return PaintingRule::add;
			if (PaintingRuleTags.LEVELINTERSECT.equals(tag))	return PaintingRule::intersect;
			if (PaintingRuleTags.LEVELXOR.equals(tag))		return PaintingRule::exclusiveOr;
			if (PaintingRuleTags.LEVELSUBSTRACT.equals(tag))	return PaintingRule::subtract;
			throw new WrongTag("the operation is not in: [" + PaintingRuleTags.LEVELADD.toString() + PaintingRuleTags.LEVELINTERSECT.toString() + PaintingRuleTags.LEVELXOR.toString() + PaintingRuleTags.LEVELSUBSTRACT.toString() + "]");
	}
	static Level parseLevel(String tag) throws WrongTag{
		if (tag.charAt(0) != 'L') throw new WrongTag("Cannot parse level tag: " + tag);
		if (tag.charAt(1) == 'S') return GrammarControl.getInstance().paintingRuleLevels.limitingShape;
		try{
			int i = Integer.parseInt(tag.substring(1));
			if (PaintingRuleLevels.levels[i].area == null){
				PaintingRuleLevels.levels[i].closeLevel();
			}
			return PaintingRuleLevels.levels[i];
		}catch(NumberFormatException e){
			throw new WrongTag("Cannot parse level tag: " + tag);
		}
	}
	static Area parseTagSet(ArrayList<String> text) throws WrongTag{
		LinkedList <String> rpnText = shuntingYardAlgorithm(text);
		System.out.println("jeszcze działa");

		Stack <Area> stack = new Stack<>();
		Area result = new Area();

		if (rpnText.size() == 1)
			return parseLevel(rpnText.get(0)).area;

		for (String token: rpnText){
			if (PaintingRuleTags.LEVELADD.equals(token) || PaintingRuleTags.LEVELINTERSECT.equals(token) || PaintingRuleTags.LEVELXOR.equals(token) || PaintingRuleTags.LEVELNOT.equals(token) || PaintingRuleTags.LEVELSUBSTRACT.equals(token)){
				Function  <Area [], Area> operation = parseOperation(token);
				Area area_b = stack.pop();
				Area area_a = stack.pop();
				result = operation.apply(new Area[]{area_a, area_b});
				stack.add(result);
			}else{
				stack.add(parseLevel(token).area);
			}
		}		
		return result;
	}
	static Area add(Area [] areas){
		Area result = (Area)areas[0].clone();
		result.add(areas[1]);
		return result;
	}
	static Area intersect(Area [] areas){
		Area result = (Area)areas[0].clone();
		result.intersect(areas[1]);
		return result;
	}
	static Area exclusiveOr(Area [] areas){
		Area result = (Area)areas[0].clone();
		result.exclusiveOr(areas[1]);
		return result;
	}
	static Area subtract(Area [] areas){
		Area result = (Area)areas[0].clone();
		result.subtract(areas[1]);
		return result;
	}
	static class WrongTag extends Exception {
		public static final long serialVersionUID = 42L;
		public WrongTag(String message){
			super("The tag is wrong" + ". " + message);
		}
		public WrongTag(){
			super("The tag is wrong");
		}
	}
}