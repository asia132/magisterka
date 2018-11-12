package grammar_graphs; 	

enum FileSaverTags{
	INPUTTAG("#INPUT"), ISIDETAG("#I"), MARKERTAG("#M"), LINETAG("#L"), ASIDETAG("#A"), BSIDETAG("#B"), 
	RULELIST("#RULELIST"), LIMITSHAPETAG("#LIMITSHAPE"), LEVEL("#LEVEL"), N("#N"), PAINTINGRULE("#PAINTRULE");
	
	private String value;
	FileSaverTags(String value){
		this.value = value;
	}
	@Override
	public String toString(){
		return value;
	}
	boolean equals(String text){
		return this.value.equals(text);
	}
}