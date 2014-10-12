package shawn.projection;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Grid implements Serializable {

	private static final long serialVersionUID = 8280084595956025628L;

	public double x;
	public double y;

	public Grid() {
		this.x = 0;
		this.y = 0;
	}

	public Grid(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Grid))
			return false;
		final Grid t = (Grid) o;
		if (x == t.x && y == t.y)
			return true;
		else
			return false;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat("0.000000");
		return df.format(x) + "," + df.format(y);
	}

}
