package shawn.projection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class ProjectionFactory {

	private GeometryFactory gf;
	private Projection pj;
	private int level;

	public ProjectionFactory(Projection pj, int level) {
		super();
		this.gf = new GeometryFactory();
		this.pj = pj;
		this.level = level;
	}

	public Point toPixel(Point lonlatP) {
		if (lonlatP == null)
			return null;

		LonLatPoint llp = new LonLatPoint(lonlatP.getCoordinate());
		PixelPoint pp = pj.lonlatToPixel(llp, level);
		Point r = gf.createPoint(pp.toCoordinate());
		return r;
	}

	public Point toLonLat(Point pixelP) {
		if (pixelP == null)
			return null;

		PixelPoint pp = new PixelPoint(pixelP.getCoordinate());
		LonLatPoint llp = pj.pixelToLonLat(pp, level);
		Point r = gf.createPoint(llp.toCoordinate());
		return r;
	}

	public Polygon toPixel(Polygon lonlatPG) {
		if (lonlatPG == null)
			return null;

		Coordinate[] cs = lonlatPG.getCoordinates();
		LonLatPoint llp;
		PixelPoint pp;

		for (int i = 0; i < cs.length; i++) {
			llp = new LonLatPoint(cs[i]);
			pp = pj.lonlatToPixel(llp, level);
			cs[i] = pp.toCoordinate();
		}
		LinearRing lr = gf.createLinearRing(cs);
		Polygon r = gf.createPolygon(lr, null);
		return r;
	}

	public Polygon toLonLat(Polygon pixelPG) {
		if (pixelPG == null)
			return null;

		Coordinate[] cs = pixelPG.getCoordinates();
		PixelPoint pp;
		LonLatPoint llp;
		
		for (int i = 0; i < cs.length; i++) {
			pp = new PixelPoint(cs[i]);
			llp = pj.pixelToLonLat(pp, level);
			cs[i] = llp.toCoordinate();
		}
		LinearRing lr = gf.createLinearRing(cs);
		Polygon r = gf.createPolygon(lr, null);
		return r;
	}

	public LineString toPixel(LineString lonlatLS) {
		if (lonlatLS == null)
			return null;

		Coordinate[] cs = new Coordinate[lonlatLS.getNumPoints()];
		LonLatPoint llp;
		PixelPoint pp;

		for (int i = 0; i < cs.length; i++) {
			llp = new LonLatPoint(lonlatLS.getCoordinateN(i));
			pp = pj.lonlatToPixel(llp, level);
			cs[i] = pp.toCoordinate();
		}
		LineString r = gf.createLineString(cs);
		return r;
	}

	public LineString toLonLat(LineString pixelLS) {
		if (pixelLS == null)
			return null;

		Coordinate[] cs = new Coordinate[pixelLS.getNumPoints()];
		PixelPoint pp;
		LonLatPoint llp;
		for (int i = 0; i < cs.length; i++) {
			pp = new PixelPoint(pixelLS.getCoordinateN(i));
			llp = pj.pixelToLonLat(pp, level);
			cs[i] = llp.toCoordinate();
		}
		LineString r = gf.createLineString(cs);
		return r;
	}

	public double getLength(LineString ls) {
		double length = 0.0;
		LonLatPoint llp1, llp2;

		if (ls != null) {
			for (int i = 1; i < ls.getNumPoints(); i++) {
				llp1 = new LonLatPoint(ls.getCoordinateN(i - 1));
				llp2 = new LonLatPoint(ls.getCoordinateN(i));
				length += pj.getDistanceByLL(llp1, llp2);
			}
		}
		return length;
	}

	public static void main(String[] args) throws ParseException {
		GoogleProjection gp = new GoogleProjection();
		BaiduProjection bp = new BaiduProjection();

		ProjectionFactory pf = new ProjectionFactory(gp, 17);

		String p = "POINT (149.128661 35.282083)";
		Point p1 = (Point) new WKTReader().read(p);
		System.out.println(p1.toText());
		Point p2 = pf.toPixel(p1);
		System.out.println(p2.toText());
		Point p3 = pf.toLonLat(p2);
		System.out.println(p3.toText());

		String ls = "LINESTRING (116.35543812566519 39.940444064414095, 116.35553 39.937999, 116.355621 39.935514, 116.35577261101903 39.93245452927279)";
		LineString l1 = (LineString) new WKTReader().read(ls);
		System.out.println(l1.toText());
		LineString l2 = pf.toPixel(l1);
		System.out.println(l2.toText());
		LineString l3 = pf.toLonLat(l2);
		System.out.println(l3.toText());

		ls = "LINESTRING (114.066231 22.545520, 114.066231 22.572470)";
		l1 = (LineString) new WKTReader().read(ls);
		System.out.println("LineString Length: " + pf.getLength(l1));
	}
}
