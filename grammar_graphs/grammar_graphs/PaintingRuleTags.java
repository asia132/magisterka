package grammar_graphs;

enum PaintingRuleTags {

	RULEAPPLIED("APPLY"), RULESKIPPED("SKIP"), LEVELADD("∪"), LEVELINTERSECT("∩"), LEVELNOT("~"), LEVELSUBSTRACT(
			"\\"), LEVELXOR("⊕"), LEVELBRA("("), LEVELKET(")");

	private String value;

	PaintingRuleTags(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	boolean equals(String text) {
		return this.value.equals(text);
	}
}
