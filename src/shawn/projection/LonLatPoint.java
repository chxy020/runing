package shawn.projection;

import java.io.Serializable;
import java.text.DecimalFormat;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class LonLatPoint implements Serializable {

	private static final long serialVersionUID = -6920465367164121302L;
	
	public double lon;
	public double lat;

	public LonLatPoint() {
		this.lon = 0;
		this.lat = 0;
	}

	public LonLatPoint(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}

	public LonLatPoint(Coordinate c) {
		this.lon = c.x;
		this.lat = c.y;
	}

	public LonLatPoint(Point p) {
		this.lon = p.getX();
		this.lat = p.getY();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof LonLatPoint))
			return false;
		final LonLatPoint t = (LonLatPoint) o;
		if (lon == t.lon && lat == t.lat)
			return true;
		else
			return false;
	}

	public Coordinate toCoordinate() {
		return new Coordinate(lon, lat);
	}
	
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.000000");
		return df.format(lon) + "," + df.format(lat);
	}
}
