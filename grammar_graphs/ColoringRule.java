package grammar_graphs;

import java.awt.geom.Area;
import java.awt.Color;
import java.util.regex.Pattern;
import java.util.function.*;

class ColoringRule {
	Area paintCavnas;
	boolean isAplicable;
	String name;
	Color color;

	String tagV = "V";
	String tagX = "X";

	String tagUnion = "∪";
	String tagInter = "∩";
	String tagXOR = "⊕";
	String tagNOT = "~";
	String tagBra = "(";
	String tagKet = ")";
	
	ColoringRule(String name, String isAplicable, Color color, String [] tagsSet){
		this.name = name;
		try{
			this.isAplicable = this.parseFlag(isAplicable);
		}catch(WrongTag e){
			System.out.println(e.getMessage());
		}
		this.color = color;
		this.paintCavnas = this.parseTagSet(tagsSet);
	}
	boolean parseFlag(String isAplicable) throws WrongTag{
		if (isAplicable.equals(tagV)) return true;
		if (isAplicable.equals(tagX)) return false;
		throw new WrongTag("Aplicable tag.");
	}
	Area parseTagSet(String [] tagSet){
		try{
			Area finalArea = new Area(this.parseLevel(tagSet[0]).getShape());
			try{
				Function <Area [], Area> operation = parseOperation(tagSet[1]);
				Area nextArea = new Area(this.parseLevel(tagSet[2]).getShape());
				Area [] areas = {finalArea, nextArea};
				finalArea = operation.apply(areas);
			}catch (WrongTag e){
				System.out.println(e.getMessage());
			}
			return finalArea;
		}catch(NotClosedShape e){
			System.out.println(e.getMessage());
		}catch(WrongTag e1){
			System.out.println(e1.getMessage());
		}
		return null;
	}
	Level parseLevel(String tag) throws WrongTag{
		if (Pattern.matches("L[0-9]+", tag.subSequence(0, tag.length())))
			throw new WrongTag("Level format");

		tag = tag.substring(1);
		int index = Integer.parseInt(tag);
		return MainData.coloringRuleLevels.levels[index];
	}
	Function  <Area [], Area> parseOperation(String tag) throws WrongTag{
		if (Pattern.matches(tagUnion, tag.subSequence(0, tag.length()))) return ColoringRule::add;
		if (Pattern.matches(tagInter, tag.subSequence(0, tag.length()))) return ColoringRule::intersect;
		if (Pattern.matches(tagXOR, tag.subSequence(0, tag.length()))) return ColoringRule::exclusiveOr;

		throw new WrongTag("The operation is not union / intersection / XOR");
	}
	Function <Area, Area> parseSubstract(String tag) throws WrongTag{
		if (Pattern.matches(tagNOT, tag.subSequence(0, tag.length()))) return ColoringRule::subtract;
		throw new WrongTag("The operation is not union / intersection / XOR");
	}
	boolean parseBra(String tag){
		if (Pattern.matches(tagBra, tag.subSequence(0, tag.length()))) return true;
		return false;
	}
	boolean parseKet(String tag){
		if (Pattern.matches(tagKet, tag.subSequence(0, tag.length()))) return true;
		return false;
	}
	static Area subtract(Area area){
		try{
			Area newArea = new Area(MainData.coloringRuleLevels.limitingShape.getShape());
			newArea.subtract(area);
			return newArea;
		}catch(NotClosedShape e){
			System.out.println(e.getMessage());
		}
		return null;
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
}
class WrongTag extends Exception {
	public WrongTag(String message){
		super("The tag is wrong" + ". " + message);
	}
	public WrongTag(){
		super("The tag is wrong");
	}
}