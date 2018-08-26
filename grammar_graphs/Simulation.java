package grammar_graphs;

import java.util.ArrayList;

import java.lang.Math;

import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import java.nio.charset.Charset;

import java.io.BufferedReader;
import java.io.IOException;

import java.awt.Point;

class Simulation{

	static boolean SIMULATE = false;
	static int i = 0;

	static String file = "initial_shape.txt";
	static String fileL2 = "L2.txt";
	static String fileFinale = "finale_shape.txt";

	static String file_folder = "./shapes";

	static void runSimulation(MainPanel panel){
		SIMULATE = true;
		panel.programData.clear();
		load0Level(panel);
		load1Level(panel);
		load2Level(panel);

		panel.programData.marker = new Marker(new Point(114, 3));
		panel.programData.marker.scale(4);
		panel.programData.grid_size = 15;
		panel.repaint();

	}
	static void load0Level(MainPanel panel){
		Path path = FileSystems.getDefault().getPath(file_folder, file);
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String [] spoint = line.split(" ");
				Point a = new Point(Integer.parseInt(spoint[0]), Integer.parseInt(spoint[1]));
				Point b = new Point(Integer.parseInt(spoint[2]), Integer.parseInt(spoint[3]));
				panel.programData.addLine(new Line(a, b), true);
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}
	static void load1Level(MainPanel panel){
		Path path = FileSystems.getDefault().getPath(file_folder, fileL2);
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String [] spoint = line.split(" ");
				Point a = new Point(Integer.parseInt(spoint[0]), Integer.parseInt(spoint[1]));
				Point b = new Point(Integer.parseInt(spoint[2]), Integer.parseInt(spoint[3]));
				Line newline = new Line(a, b);
				panel.programData.addLine(newline, false);
				MainData.coloringRule.levels[1].addLine(newline);
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}
	static void load2Level(MainPanel panel){
		Path path = FileSystems.getDefault().getPath(file_folder, fileFinale);
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String [] spoint = line.split(" ");
				Point a = new Point(Integer.parseInt(spoint[0]), Integer.parseInt(spoint[1]));
				Point b = new Point(Integer.parseInt(spoint[2]), Integer.parseInt(spoint[3]));
				Line newline = new Line(a, b);
				panel.programData.addLine(newline, false);
				MainData.coloringRule.levels[2].addLine(newline);
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}
}