package shawn.projection;

import java.text.DecimalFormat;

public class BaiduProjection implements Projection {
	private static double EarthRadius = 6370996.81;
	private static double MinLongitude = -180;
	private static double MaxLongitude = 180;
	private static double MinLatitude = -74;
	private static double MaxLatitude = 74;

	private static LonLatPoint LonLatPointOfPixelOrigin;
	private static LonLatPoint MercatorPointOfPixelOrigin;
	
	private static double[] MCBAND = { 12890594.86, 8362377.87, 5591021,
			3481989.83, 1678043.12, 0 };
	private static int[] LLBAND = { 75, 60, 45, 30, 15, 0 };
	private static double[][] MC2LL = {
			{ 1.410526172116255e-8, 0.00000898305509648872, -1.9939833816331,
					200.9824383106796, -187.2403703815547, 91.6087516669843,
					-23.38765649603339, 2.57121317296198, -0.03801003308653,
					17337981.2 },
			{ -7.435856389565537e-9, 0.000008983055097726239,
					-0.78625201886289, 96.32687599759846, -1.85204757529826,
					-59.36935905485877, 47.40033549296737, -16.50741931063887,
					2.28786674699375, 10260144.86 },
			{ -3.030883460898826e-8, 0.00000898305509983578, 0.30071316287616,
					59.74293618442277, 7.357984074871, -25.38371002664745,
					13.45380521110908, -3.29883767235584, 0.32710905363475,
					6856817.37 },
			{ -1.981981304930552e-8, 0.000008983055099779535, 0.03278182852591,
					40.31678527705744, 0.65659298677277, -4.44255534477492,
					0.85341911805263, 0.12923347998204, -0.04625736007561,
					4482777.06 },
			{ 3.09191371068437e-9, 0.000008983055096812155, 0.00006995724062,
					23.10934304144901, -0.00023663490511, -0.6321817810242,
					-0.00663494467273, 0.03430082397953, -0.00466043876332,
					2555164.4 },
			{ 2.890871144776878e-9, 0.000008983055095805407, -3.068298e-8,
					7.47137025468032, -0.00000353937994, -0.02145144861037,
					-0.00001234426596, 0.00010322952773, -0.00000323890364,
					826088.5 } };
	private static double[][] LL2MC = {
			{ -0.0015702102444, 111320.7020616939, 1704480524535203d,
					-10338987376042340d, 26112667856603880d,
					-35149669176653700d, 26595700718403920d,
					-10725012454188240d, 1800819912950474d, 82.5 },
			{ 0.0008277824516172526, 111320.7020463578, 647795574.6671607,
					-4082003173.641316, 10774905663.51142, -15171875531.51559,
					12053065338.62167, -5124939663.577472, 913311935.9512032,
					67.5 },
			{ 0.00337398766765, 111320.7020202162, 4481351.045890365,
					-23393751.19931662, 79682215.47186455, -115964993.2797253,
					97236711.15602145, -43661946.33752821, 8477230.501135234,
					52.5 },
			{ 0.00220636496208, 111320.7020209128, 51751.86112841131,
					3796837.749470245, 992013.7397791013, -1221952.21711287,
					1340652.697009075, -620943.6990984312, 144416.9293806241,
					37.5 },
			{ -0.0003441963504368392, 111320.7020576856, 278.2353980772752,
					2485758.690035394, 6070.750963243378, 54821.18345352118,
					9540.606633304236, -2710.55326746645, 1405.483844121726,
					22.5 },
			{ -0.0003218135878613132, 111320.7020701615, 0.00369383431289,
					823725.6402795718, 0.46104986909093, 2351.343141331292,
					1.58060784298199, 8.77738589078284, 0.37238884252424, 7.45 } };

	public BaiduProjection() {
		LonLatPointOfPixelOrigin = new LonLatPoint(MinLongitude, MaxLatitude);
		MercatorPointOfPixelOrigin = lonlatToMercator(LonLatPointOfPixelOrigin, -1);
	}

	// LonLat --> Mercator --> Pixel
	// LonLat <-- Mercator <-- Pixel
	// LonLat/Mercator 之间的转换同level无关
	// Mercator/Pixel  之间的转换同level有关

	public String getName() {
		return "Baidu";
	}

	public LonLatPoint lonlatToMercator(LonLatPoint llP, int level) {
		// 验证
		double[] b = null;
		LonLatPoint p = new LonLatPoint(llP.lon, llP.lat);
		p.lon = getLoop(p.lon, MinLongitude, MaxLongitude);
		p.lat = getRange(p.lat, MinLatitude, MaxLatitude);

		for (int i = 0; i < LLBAND.length; i++) {
			if (Math.abs(p.lat) >= LLBAND[i]) {
				b = LL2MC[i];
				break;
			}
		}	
		LonLatPoint cp = convertor(p, b);
		LonLatPoint r = new LonLatPoint(toFixed(cp.lon, 2), toFixed(cp.lat, 2));
		return r;
	}

	public PixelPoint mercatorToPixel(LonLatPoint mcP, int level) {
		int zu = getZoomUnits(level);
		long x = Math.round((mcP.lon - MercatorPointOfPixelOrigin.lon) / zu);
		long y = Math.round((MercatorPointOfPixelOrigin.lat - mcP.lat) / zu);
		return new PixelPoint(x, y);

	}

	public PixelPoint lonlatToPixel(LonLatPoint llP, int level) {
		LonLatPoint mcP = lonlatToMercator(llP, level);
		return mercatorToPixel(mcP, level);
	}

	public LonLatPoint pixelToMercator(PixelPoint pP, int level) {
		int zu = getZoomUnits(level);
		double lon = zu * pP.x + MercatorPointOfPixelOrigin.lon;
		double lat = MercatorPointOfPixelOrigin.lat - zu * pP.y;
		return new LonLatPoint(lon, lat);
	}

	public LonLatPoint mercatorToLonLat(LonLatPoint mcP, int level) {
		// 验证
		double[] b = null;
		LonLatPoint p = new LonLatPoint(mcP.lon, mcP.lat);
		for (int i = 0; i < MCBAND.length; i++) {
			if (Math.abs(p.lat) >= MCBAND[i]) {
				b = MC2LL[i];
				break;
			}
		}
		LonLatPoint cp = convertor(p, b);
		LonLatPoint r = new LonLatPoint(toFixed(cp.lon, 6), toFixed(cp.lat, 6));
		return r;
	}

	public LonLatPoint pixelToLonLat(PixelPoint pP, int level) {
		LonLatPoint mcP = pixelToMercator(pP, level);
		return mercatorToLonLat(mcP, level);
	}

	public Tile lonlatToTile(LonLatPoint llP, int level) {
		LonLatPoint mcP = lonlatToMercator(llP, level);
		return mercatorToTile(mcP, level);
	}

	public Tile mercatorToTile(LonLatPoint mcP, int level) {
		int zu = getZoomUnits(level);
		int x = (int) Math.floor(Math.round(mcP.lon / zu) / 256);
		int y = (int) Math.floor(Math.round(mcP.lat / zu) / 256);
		return new Tile(x, y);
	}

	public Tile pixelToTile(PixelPoint pP, int level) {
		LonLatPoint mcP = pixelToMercator(pP, level);
		return mercatorToTile(mcP, level);
	}

	public PixelPoint upperLeftOfTile(Tile t, int level) {
		LonLatPoint llEquatorOrigin = new LonLatPoint(0, 0);
		PixelPoint pEquatorOrigin = lonlatToPixel(llEquatorOrigin, level);
		return new PixelPoint(t.x * 256 + pEquatorOrigin.x, pEquatorOrigin.y
				- (t.y + 1) * 256 + 1);
	}

	public PixelPoint lowerRightOfTile(Tile t, int level) {
		LonLatPoint llEquatorOrigin = new LonLatPoint(0, 0);
		PixelPoint pEquatorOrigin = lonlatToPixel(llEquatorOrigin, level);
		return new PixelPoint((t.x + 1) * 256 - 1 + pEquatorOrigin.x, pEquatorOrigin.y
				- t.y * 256);
	}
	
	public PixelPoint gridToPixel(Grid grid, int level) {
		LonLatPoint llEquatorOrigin = new LonLatPoint(0, 0);
		PixelPoint pEquatorOrigin = lonlatToPixel(llEquatorOrigin, 18);
		PixelPoint p18 = new PixelPoint(Math.round(grid.x * 256 + 127)
				+ pEquatorOrigin.x, pEquatorOrigin.y
				- Math.round(grid.y * 256 + 127));
		LonLatPoint llp = pixelToLonLat(p18, 18);
		return lonlatToPixel(llp, level);
	}
	
	public Grid pixelToGrid(PixelPoint pP, int level) {
		LonLatPoint llEquatorOrigin = new LonLatPoint(0, 0);
		PixelPoint pEquatorOrigin = lonlatToPixel(llEquatorOrigin, 18);
		LonLatPoint llp = pixelToLonLat(pP, level);
		PixelPoint p18 = lonlatToPixel(llp, 18);
		Grid grid = new Grid();
		grid.x = (p18.x - pEquatorOrigin.x - 127) / 256d;
		grid.y = (pEquatorOrigin.y - p18.y - 127) / 256d;		
		return grid;
	}
	
	public int getGridWidthHeight(int level) {
		return 256 / getZoomUnits(level);
	}
	
	private int getZoomUnits(int level) {
		return (int) Math.pow(2, (18 - level));
	}

	public double getDistanceByMC(LonLatPoint p1, LonLatPoint p2) {
		if (p1 == null || p2 == null) {
			return 0;
		}
		LonLatPoint tp1 = this.mercatorToLonLat(p1, -1);
		double lon1 = this.toRadians(tp1.lon);
		double lat1 = this.toRadians(tp1.lat);
		LonLatPoint tp2 = this.mercatorToLonLat(p2, -1);
		double lon2 = this.toRadians(tp2.lon);
		double lat2 = this.toRadians(tp2.lat);
		return getDistance(lon1, lon2, lat1, lat2);
	}

	public double getDistanceByLL(LonLatPoint p1, LonLatPoint p2) {
		if (p1 == null || p2 == null) {
			return 0;
		}
		double lon1 = getLoop(p1.lon, MinLongitude, MaxLongitude);
		double lat1 = getRange(p1.lat, MinLatitude, MaxLatitude);
		double lon2 = getLoop(p2.lon, MinLongitude, MaxLongitude);
		double lat2 = getRange(p2.lat, MinLatitude, MaxLatitude);

		lon1 = toRadians(lon1);
		lat1 = toRadians(lat1);
		lon2 = toRadians(lon2);
		lat2 = toRadians(lat2);

		return getDistance(lon1, lon2, lat1, lat2);
	}

	private double toFixed(double n, int fixed) {
		// 验证
		double f = Math.round(Math.pow(10, fixed));
		return Math.round(n * f) / f;
	}

	private LonLatPoint convertor(LonLatPoint src, double[] b) {
		// 验证
		double lon = b[0] + b[1] * Math.abs(src.lon);
		double p = Math.abs(src.lat) / b[9];
		double lat = b[2] + b[3] * p + b[4] * p * p + b[5] * p * p * p + b[6]
				* p * p * p * p + b[7] * p * p * p * p * p + b[8] * p * p * p
				* p * p * p;
		lon *= (src.lon < 0 ? -1 : 1);
		lat *= (src.lat < 0 ? -1 : 1);
		return new LonLatPoint(lon, lat);
	}

	private double getDistance(double radiansLon1, double radiansLon2,
			double radiansLat1, double radiansLat2) {
		return EarthRadius
				* Math.acos((Math.sin(radiansLat1) * Math.sin(radiansLat2) + Math
						.cos(radiansLat1)
						* Math.cos(radiansLat2)
						* Math.cos(radiansLon2 - radiansLon1)));
	}

	private double toRadians(double a) {
		return Math.PI * a / 180;
	}

//	private double toDegrees(double a) {
//		return (180 * a) / Math.PI;
//	}

	private double getRange(double n, double min, double max) {
		return Math.min(Math.max(n, min), max);
	}

	private double getLoop(double n, double min, double max) {
		double r = n;
		while (r > max) {
			r -= max - min;
		}
		while (r < min) {
			r += max - min;
		}
		return r;
	}

	public static void main(String[] args) {
		BaiduProjection bp = new BaiduProjection();
		DecimalFormat df = new DecimalFormat("#.##############");
		
		
//		LonLatPoint p1 = new LonLatPoint(MinLongitude, MaxLatitude);
		LonLatPoint p1 = new LonLatPoint(0, 0);
//		LonLatPoint p1 = new LonLatPoint(116.404, 39.915);
//		LonLatPoint p1 = new LonLatPoint(116.204226, 39.742481);
//		LonLatPoint p1 = new LonLatPoint(116.5509, 40.061304);
		System.out.println(df.format(p1.lon) + " | " + df.format(p1.lat));
		LonLatPoint p2 = bp.lonlatToMercator(p1, -1);
		System.out.println(df.format(p2.lon) + " | " + df.format(p2.lat));
		LonLatPoint p3 = bp.mercatorToLonLat(p2, -1);
		System.out.println(df.format(p3.lon) + " | " + df.format(p3.lat));

		int level = 18;

		System.out.println("level: " + level);
		Tile t1 = bp.lonlatToTile(p1, level);
		System.out.println("TileX: " + t1.x);
		System.out.println("TileY: " + t1.y);
		Tile t2 = bp.mercatorToTile(p2, level);
		System.out.println("TileX: " + t2.x);
		System.out.println("TileY: " + t2.y);
		
		PixelPoint p4 = bp.lonlatToPixel(p1, level);
		System.out.println(p4.x + " | " + p4.y);
		LonLatPoint p5 = bp.pixelToLonLat(p4, level);
		System.out.println(df.format(p5.lon) + " | " + df.format(p5.lat));

		PixelPoint p6 = bp.upperLeftOfTile(t1, level);
		System.out.println(p6.x + " | " + p6.y);
		PixelPoint p7 = bp.lowerRightOfTile(t1, level);
		System.out.println(p7.x + " | " + p7.y);

//		149.128661 | -35.282083
//		1.660110724E7 | -4177372.21
//		149.128661 | -35.282083
//		8300554 | -2088686
//		149.128668 | -35.282081
		
//		116.404 | 39.915
//		1.2958175E7 | 4825923.77
//		116.404 | 39.915
//		level: 18
//		TileX: 50617
//		TileY: 18851
//		TileX: 50617
//		TileY: 18851
//		32995901 | 7648180
//		116.403997 | 39.915003
		
		
		Grid grid = new Grid(0, 0);
		PixelPoint pp = bp.gridToPixel(grid, 18);
		LonLatPoint llp = bp.pixelToLonLat(pp, 18);
		Grid g = bp.pixelToGrid(pp, 18);

		System.out.println(pp);
		System.out.println(llp);
		System.out.println(g);

	}
}
