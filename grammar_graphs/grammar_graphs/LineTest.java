package grammar_graphs;

import static org.assertj.core.api.Assertions.*;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;
import org.junit.Before;

import java.awt.Point;

public class LineTest {
	int x_1, y_1;
	int x_2, y_2;

	Line lineLinear, lineVertical, lineHorizontal;

	@Before
	public void before() {
		x_1 = 100;
		y_1 = 120;
		x_2 = 1023;
		y_2 = 3452;
		lineLinear = new Line(new Point(x_1, y_1), new Point(x_2, y_2));
		lineVertical = new Line(new Point(x_1, y_1), new Point(x_1, y_2));
		lineHorizontal = new Line(new Point(x_1, y_1), new Point(x_2, y_1));
	}

	@Test
	public void testCreateLineAtScreenPoint() {
		Line line = Line.createLineAtScreenPoint(x_1, y_1, x_2, y_2);
		assertThat(line.pa.x).isEqualTo(GridControl.getInstance().toGrid(x_1));
		assertThat(line.pa.y).isEqualTo(GridControl.getInstance().toGrid(y_1));
		assertThat(line.pb.x).isEqualTo(GridControl.getInstance().toGrid(x_2));
		assertThat(line.pb.y).isEqualTo(GridControl.getInstance().toGrid(y_2));
	}

	@Test
	public void testCreateRotatedLine() {
		double alpha = 90;
		Point rotationPoint = new Point(50, 50);

		Line testLine = Line.createRotatedLine(new Point(x_1, y_1), new Point(x_2, y_2), alpha, rotationPoint);
		lineLinear.rotate(rotationPoint.x, rotationPoint.y, alpha);

		assertThat(lineLinear).isEqualTo(testLine);
	}

	@Test
	public void testEqualsAndHash() {
		EqualsVerifier.forClass(Line.class)
				.withPrefabValues(Line.class, new Line(new Point(0, 0), new Point(1, 1)),
						new Line(new Point(1, 2), new Point(0, 0)))
				.withIgnoredFields("childs", "color").withNonnullFields("pa", "pb").suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

	@Test
	public void testScaleUp() {
		Point refPoint = new Point(71, 24);
		double s = 4. / 3.;

		Line testLine1 = new Line(new Point(62, 21), new Point(80, 21));
		Line resultLine1 = new Line(new Point(59, 20), new Point(83, 20));
		testLine1.scale(refPoint.x, refPoint.y, s);
		assertThat(testLine1).isEqualTo(resultLine1);

		Line testLine2 = new Line(new Point(80, 21), new Point(74, 27));
		Line resultLine2 = new Line(new Point(83, 20), new Point(75, 28));
		testLine2.scale(refPoint.x, refPoint.y, s);
		assertThat(testLine2).isEqualTo(resultLine2);

		Line testLine3 = new Line(new Point(74, 27), new Point(68, 27));
		Line resultLine3 = new Line(new Point(75, 28), new Point(67, 28));
		testLine3.scale(refPoint.x, refPoint.y, s);
		assertThat(testLine3).isEqualTo(resultLine3);

		Line testLine4 = new Line(new Point(68, 27), new Point(62, 21));
		Line resultLine4 = new Line(new Point(67, 28), new Point(59, 20));
		testLine4.scale(refPoint.x, refPoint.y, s);
		assertThat(testLine4).isEqualTo(resultLine4);
	}

	@Test
	public void testScale90() {
		Point refPoint = new Point(129, 7);
		double s = 3;

		Line testLine1 = new Line(new Point(127, 7), new Point(127, 12));
		Line resultLine1 = new Line(new Point(123, 7), new Point(123, 22));
		testLine1.scale(refPoint.x, refPoint.y, s);
		assertThat(testLine1).isEqualTo(resultLine1);

		Line testLine2 = new Line(new Point(127, 12), new Point(133, 12));
		Line resultLine2 = new Line(new Point(123, 22), new Point(141, 22));
		testLine2.scale(refPoint.x, refPoint.y, s);
		assertThat(testLine2).isEqualTo(resultLine2);

		Line testLine3 = new Line(new Point(133, 12), new Point(133, 14));
		Line resultLine3 = new Line(new Point(141, 22), new Point(141, 28));
		testLine3.scale(refPoint.x, refPoint.y, s);
		assertThat(testLine3).isEqualTo(resultLine3);

		Line testLine4 = new Line(new Point(133, 14), new Point(127, 14));
		Line resultLine4 = new Line(new Point(141, 28), new Point(123, 28));
		testLine4.scale(refPoint.x, refPoint.y, s);
		assertThat(testLine4).isEqualTo(resultLine4);
	}

	@Test
	public void testIsPartOfLinearFunTrue() {
		assertThat(lineLinear.isPartOfLinearFun()).isEqualTo(true);
		assertThat(lineHorizontal.isPartOfLinearFun()).isEqualTo(true);
	}

	@Test
	public void testIsPartOfLinearFunFalse() {
		assertThat(lineVertical.isPartOfLinearFun()).isEqualTo(false);
	}
	
	@Test
	public void testRotate90() {
		double alpha = Math.PI*0.5;
		Point refPoint = new Point(5, 7);
		
		Line l1 = new Line(new Point(5, 3), new Point(5, 6));
		Line l2 = new Line(new Point(5, 7), new Point(7, 5));
		Line l3 = new Line(new Point(6, 7), new Point(8, 7));
		
		l1.rotate(refPoint.x, refPoint.y, alpha);		
		l2.rotate(refPoint.x, refPoint.y, alpha);		
		l3.rotate(refPoint.x, refPoint.y, alpha);
		
		assertThat(l1).isEqualTo(new Line(new Point(6, 7), new Point(9, 7)));
		assertThat(l2).isEqualTo(new Line(new Point(5, 7), new Point(7, 9)));
		assertThat(l3).isEqualTo(new Line(new Point(5, 8), new Point(5, 10)));
	}
	
	@Test
	public void testRotate180() {
		double alpha = Math.PI;
		Point refPoint = new Point(5, 7);
		
		Line l1 = new Line(new Point(5, 3), new Point(5, 6));
		Line l2 = new Line(new Point(5, 7), new Point(7, 5));
		Line l3 = new Line(new Point(6, 7), new Point(8, 7));
		
		l1.rotate(refPoint.x, refPoint.y, alpha);		
		l2.rotate(refPoint.x, refPoint.y, alpha);		
		l3.rotate(refPoint.x, refPoint.y, alpha);
		
		assertThat(l1).isEqualTo(new Line(new Point(5, 8), new Point(5, 11)));
		assertThat(l2).isEqualTo(new Line(new Point(3, 9), new Point(5, 7)));
		assertThat(l3).isEqualTo(new Line(new Point(2, 7), new Point(4, 7)));
	}
	
	@Test
	public void testRotate270() {
		double alpha = Math.PI*1.5;
		Point refPoint = new Point(5, 7);
		
		Line l1 = new Line(new Point(5, 3), new Point(5, 6));
		Line l2 = new Line(new Point(5, 7), new Point(7, 5));
		Line l3 = new Line(new Point(6, 7), new Point(8, 7));
		
		l1.rotate(refPoint.x, refPoint.y, alpha);		
		l2.rotate(refPoint.x, refPoint.y, alpha);		
		l3.rotate(refPoint.x, refPoint.y, alpha);
		
		assertThat(l1).isEqualTo(new Line(new Point(1, 7), new Point(4, 7)));
		assertThat(l2).isEqualTo(new Line(new Point(3, 5), new Point(5, 7)));
		assertThat(l3).isEqualTo(new Line(new Point(5, 4), new Point(5, 6)));
	}
}
