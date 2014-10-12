package shawn.projection;

import java.io.Serializable;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class PixelPoint implements Serializable {

	private static final long serialVersionUID = -2417787494936033701L;

	public long x;
	public long y;

	public PixelPoint() {
		this.x = 0;
		this.y = 0;
	}

	public PixelPoint(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public PixelPoint(Coordinate c) {
		this.x = Math.round(c.x);
		this.y = Math.round(c.y);
	}

	public PixelPoint(Point p) {
		this.x = Math.round(p.getX());
		this.y = Math.round(p.getY());
	}

	public Coordinate toCoordinate() {
		return new Coordinate(x, y);
	}
	
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PixelPoint))
			return false;
		final PixelPoint t = (PixelPoint) o;
		if (x == t.x && y == t.y)
			return true;
		else
			return false;
	}

	public String toString() {
		return x + "," + y;
	}
}
