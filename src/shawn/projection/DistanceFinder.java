package shawn.projection;

import java.text.DecimalFormat;

public class DistanceFinder {
	private static int SA = 1000;
	private static int EA = 10000000;

	private Projection projection;
	private LonLatPoint src;
	private double distance;
	private LonLatPoint test;

	public DistanceFinder(Projection projection) {
		this.projection = projection;
	}

	public PixelPoint getDeltaPixel(LonLatPoint src, double distance, int level) {
		LonLatPoint delta = getDeltaLonLat(src, distance);
		LonLatPoint dst = new LonLatPoint(src.lon + delta.lon, src.lat
				+ delta.lat);
		PixelPoint srcP = projection.lonlatToPixel(src, level);
		PixelPoint dstP = projection.lonlatToPixel(dst, level);
		return new PixelPoint(Math.abs(dstP.x - srcP.x), Math.abs(dstP.y - srcP.y));
	}

	public LonLatPoint getDeltaLonLat(LonLatPoint src, double distance) {
		this.src = src;
		this.distance = distance;

		LonLatPoint r;
		double deltaX, deltaY;

		this.test = new LonLatPoint(src.lon + 1.0 / SA, src.lat);
		r = getAxisCo(SA, true);
		deltaX = Math.abs(r.lon - src.lon);

		this.test = new LonLatPoint(src.lon, src.lat + 1.0 / SA);
		r = getAxisCo(SA, false);
		deltaY = Math.abs(r.lat - src.lat);

		r = new LonLatPoint(deltaX, deltaY);
		return r;
	}

	private LonLatPoint getAxisCo(int f, boolean isAxisX) {
		LonLatPoint tt1, tt2;
		double d, d1, d2;
		double m = 1.0;

		if (isAxisX) {
			tt1 = new LonLatPoint(test.lon + 1.0 / f, test.lat);
			tt2 = new LonLatPoint(test.lon - 1.0 / f, test.lat);
		} else {
			tt1 = new LonLatPoint(test.lon, test.lat + 1.0 / f);
			tt2 = new LonLatPoint(test.lon, test.lat - 1.0 / f);
		}
		// System.out.println(f + "\t" + tt1 + "," + tt2);

		d = Math.abs(projection.getDistanceByLL(src, test) - distance);
		d1 = Math.abs(projection.getDistanceByLL(src, tt1) - distance);
		d2 = Math.abs(projection.getDistanceByLL(src, tt2) - distance);

		if (d < d1 && d < d2) {
			// µÝ¹é
			if (f < EA) {
				getAxisCo(f * 10, isAxisX);
			}
			return test;
		} else {
			if (d1 < d2) {
				m = 1;
				test = tt1;
				d = d1;
			} else {
				m = -1;
				test = tt2;
				d = d2;
			}
		}

		if (isAxisX) {
			tt1 = new LonLatPoint(test.lon + m / f, test.lat);
		} else {
			tt1 = new LonLatPoint(test.lon, test.lat + m / f);
		}
		d1 = Math.abs(projection.getDistanceByLL(src, tt1) - distance);

		while (d1 < d) {
			test = tt1;
			d = d1;
			if (isAxisX) {
				tt1 = new LonLatPoint(test.lon + m / f, test.lat);
			} else {
				tt1 = new LonLatPoint(test.lon, test.lat + m / f);
			}
			d1 = Math.abs(projection.getDistanceByLL(src, tt1) - distance);
		}

		if (f < EA) {
			getAxisCo(f * 10, isAxisX);
		}
		return test;
	}

	public static void main(String[] args) {
		Projection proj = new GoogleProjection();
		DecimalFormat df = new DecimalFormat("0.0000");

		double bjLon = 116.404269;
		double bjLat = 39.914271;
		double szLon = 114.066231;
		double szLat = 22.54552;
		double hebLon = 126.661516;
		double hebLat = 45.765879;

		DistanceFinder td2 = new DistanceFinder(proj);
		LonLatPoint llp1, llp2, delta;

		int meters = 3000;
		
		DecimalFormat df1 = new DecimalFormat("0.0");
		DecimalFormat df8 = new DecimalFormat("0.00000000");

//		for (double i = 0; i < 85; i += 0.1) {
//			llp1 = new LonLatPoint(0, i);
//			delta = td2.getDeltaLonLat(llp1, meters);
//			System.out.println(df1.format(i) + "\t" + df8.format(delta.lon)
//					+ "\t" + df8.format(delta.lat));
//		}
		
		System.out.println("¹þ¶û±õ");
		llp1 = new LonLatPoint(hebLon, hebLat);
		delta = td2.getDeltaLonLat(llp1, meters);
		llp2 = new LonLatPoint(llp1.lon + delta.lon, llp1.lat);
		System.out.println(llp1 + "," + llp2 + "," + delta);
		System.out.println("Lon: " + df.format(proj.getDistanceByLL(llp1, llp2))
				+ "m");
		llp2 = new LonLatPoint(llp1.lon, llp1.lat + delta.lat);
		System.out.println(llp1 + "," + llp2 + "," + delta);
		System.out.println("Lat: " + df.format(proj.getDistanceByLL(llp1, llp2))
				+ "m");

		System.out.println("±±¾©");
		llp1 = new LonLatPoint(bjLon, bjLat);
		delta = td2.getDeltaLonLat(llp1, meters);
		llp2 = new LonLatPoint(llp1.lon + delta.lon, llp1.lat);
		System.out.println(llp1 + "," + llp2 + "," + delta);
		System.out.println("Lon: " + df.format(proj.getDistanceByLL(llp1, llp2))
				+ "m");
		llp2 = new LonLatPoint(llp1.lon, llp1.lat + delta.lat);
		System.out.println(llp1 + "," + llp2 + "," + delta);
		System.out.println("Lat: " + df.format(proj.getDistanceByLL(llp1, llp2))
				+ "m");

		System.out.println("ÉîÛÚ");
		llp1 = new LonLatPoint(szLon, szLat);
		delta = td2.getDeltaLonLat(llp1, meters);
		llp2 = new LonLatPoint(llp1.lon + delta.lon, llp1.lat);
		System.out.println(llp1 + "," + llp2 + "," + delta);
		System.out.println("Lon: " + df.format(proj.getDistanceByLL(llp1, llp2))
				+ "m");
		llp2 = new LonLatPoint(llp1.lon, llp1.lat + delta.lat);
		System.out.println(llp1 + "," + llp2 + "," + delta);
		System.out.println("Lat: " + df.format(proj.getDistanceByLL(llp1, llp2))
				+ "m");

	}

}
