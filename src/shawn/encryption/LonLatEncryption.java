package shawn.encryption;

import shawn.projection.LonLatPoint;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class LonLatEncryption {

	public static double CHINA_LON_LAT_PRECISION_FLOAT = 3686400.0;

	public double yj_sin2(double x) {
		double tt;
		double ss;
		int ff;
		double s2;
		int cc;
		ff = 0;
		if (x < 0) {
			x = -x;
			ff = 1;
		}
		cc = (int) (x / 6.28318530717959);
		tt = x - cc * 6.28318530717959;
		if (tt > 3.1415926535897932) {
			tt = tt - 3.1415926535897932;
			if (ff == 1)
				ff = 0;
			else if (ff == 0)
				ff = 1;
		}
		x = tt;
		ss = x;
		s2 = x;
		tt = tt * tt;
		s2 = s2 * tt;
		ss = ss - s2 * 0.166666666666667;
		s2 = s2 * tt;
		ss = ss + s2 * 8.33333333333333E-03;
		s2 = s2 * tt;
		ss = ss - s2 * 1.98412698412698E-04;
		s2 = s2 * tt;
		ss = ss + s2 * 2.75573192239859E-06;
		s2 = s2 * tt;
		ss = ss - s2 * 2.50521083854417E-08;
		if (ff == 1)
			ss = -ss;
		return ss;
	}

	public double Transform_yj5(double x, double y) {
		double tt;
		tt = 300 + 1 * x + 2 * y + 0.1 * x * x + 0.1 * x * y + 0.1
				* Math.sqrt(Math.sqrt(x * x));
		tt = tt
				+ (20 * yj_sin2(18.849555921538764 * x) + 20 * yj_sin2(6.283185307179588 * x))
				* 0.6667;
		tt = tt
				+ (20 * yj_sin2(3.141592653589794 * x) + 40 * yj_sin2(1.047197551196598 * x))
				* 0.6667;
		tt = tt
				+ (150 * yj_sin2(0.2617993877991495 * x) + 300 * yj_sin2(0.1047197551196598 * x))
				* 0.6667;
		return tt;
	}

	public double Transform_yjy5(double x, double y) {
		double tt;
		tt = -100 + 2 * x + 3 * y + 0.2 * y * y + 0.1 * x * y + 0.2
				* Math.sqrt(Math.sqrt(x * x));
		tt = tt
				+ (20 * yj_sin2(18.849555921538764 * x) + 20 * yj_sin2(6.283185307179588 * x))
				* 0.6667;
		tt = tt
				+ (20 * yj_sin2(3.141592653589794 * y) + 40 * yj_sin2(1.047197551196598 * y))
				* 0.6667;
		tt = tt
				+ (160 * yj_sin2(0.2617993877991495 * y) + 320 * yj_sin2(0.1047197551196598 * y))
				* 0.6667;
		return tt;
	}

	public double Transform_jy5(double x, double xx) {
		double n;
		double a;
		double e;
		a = 6378245;
		e = 0.00669342;
		n = Math.sqrt(1 - e * yj_sin2(x * 0.0174532925199433)
				* yj_sin2(x * 0.0174532925199433));
		n = (xx * 180) / (a / n * Math.cos(x * 0.0174532925199433) * 3.1415926);
		return n;
	}

	public double Transform_jyj5(double x, double yy) {
		double m;
		double a;
		double e;
		double mm;
		a = 6378245;
		e = 0.00669342;
		mm = 1 - e * yj_sin2(x * 0.0174532925199433)
				* yj_sin2(x * 0.0174532925199433);
		m = (a * (1 - e)) / (mm * Math.sqrt(mm));
		return (yy * 180) / (m * 3.1415926);
	}

	public Point encrypt(Point wg) {
		LonLatPoint src = new LonLatPoint(wg.getX(), wg.getY());
		LonLatPoint dst = encrypt(src);
		Coordinate c = new Coordinate(dst.lon, dst.lat);
		GeometryFactory gf = new GeometryFactory();
		return gf.createPoint(c);
	}

	public LonLatPoint encrypt(LonLatPoint wg) {
		LonLatPoint china = new LonLatPoint();

		double x_add;
		double y_add;
		double x_l;
		double y_l;

		x_l = (int) Math.round(wg.lon * CHINA_LON_LAT_PRECISION_FLOAT);
		x_l = x_l / CHINA_LON_LAT_PRECISION_FLOAT;
		y_l = (int) Math.round(wg.lat * CHINA_LON_LAT_PRECISION_FLOAT);
		y_l = y_l / CHINA_LON_LAT_PRECISION_FLOAT;

		if (x_l < 72.004 || x_l > 137.8347 || y_l < 0.8293 || y_l > 55.8271) {
			return china;
		}

		x_add = Transform_yj5(x_l - 105, y_l - 35);
		y_add = Transform_yjy5(x_l - 105, y_l - 35);
		x_l = (x_l + Transform_jy5(y_l, x_add)) * CHINA_LON_LAT_PRECISION_FLOAT;
		y_l = (y_l + Transform_jyj5(y_l, y_add))
				* CHINA_LON_LAT_PRECISION_FLOAT;

		if (x_l > 2147483647 || y_l > 2147483647) {
			return china;
		}

		china.lon = x_l / CHINA_LON_LAT_PRECISION_FLOAT;
		china.lat = y_l / CHINA_LON_LAT_PRECISION_FLOAT;
		return china;
	}

	public LonLatEncryption() {
		super();
	}

	public static void main(String[] args) {

		LonLatEncryption lle = new LonLatEncryption();
		// LonLatPoint wg = new LonLatPoint(116.3776, 39.9104);
		LonLatPoint wg = new LonLatPoint(116.396291, 39.974247);
		LonLatPoint china = lle.encrypt(wg);

		System.out.println("wg.lon = " + wg.lon);
		System.out.println("wg.lat = " + wg.lat);
		System.out.println("china.lon = " + china.lon);
		System.out.println("china.lat = " + china.lat);

		double x = 430814792 / (double) (3600 * 1024);
		double y = 148165842 / (double) (3600 * 1024);
		System.out.println("x1 = " + x);
		System.out.println("y1 = " + y);

		x = 430814792 / 1024.0 / 3600.0;
		y = 148165842 / 1024.0 / 3600.0;
		System.out.println("x2 = " + x);
		System.out.println("y2 = " + y);

		x = 430814792 / 3686400.0;
		y = 148165842 / 3686400.0;
		System.out.println("x3 = " + x);
		System.out.println("y3 = " + y);

		// x1 = 116.3776
		// y1 = 39.9104
		// x2 = 116.3838294813368
		// y2 = 39.911791178385414

	}

}
