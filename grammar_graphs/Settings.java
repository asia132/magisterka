package grammar_graphs;
import java.awt.Color;

class Settings {
	private Settings() {
		throw new AssertionError();
	}
	static boolean SHOW_DIST = false;
	static boolean SHOW_POINTS = false;
	static boolean SHOW_GRID = true;
	static boolean COLOR_RULES = false;
	static boolean LIMITING_SHAPE = false;
	static boolean DRAW_LEVELS = false;
	static boolean CLOSED_SHAPES = true;
	
	// BLACK BLUE CYAN DARK_GRAY GRAY GREEN LIGHT_GRAY MAGENTA ORANGE PINK RED WHITE YELLOW
	static Color default_figure_color = Color.BLACK;
	static Color default_background_color = Color.WHITE;
	static Color default_check_color = Color.RED;
	static Color default_rect_color = Color.BLUE;
	static Color default_check_marker_color = Color.MAGENTA;
	static Color default_marker_color = Color.CYAN;
	static Color default_grid_color = Color.LIGHT_GRAY;
	static Color default_point_color = Color.DARK_GRAY;
	
	static void setColorRules(){
		COLOR_RULES = !COLOR_RULES;
		MainData.updateLimitShapeColor();
	}
}
