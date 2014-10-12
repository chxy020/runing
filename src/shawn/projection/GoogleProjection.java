package shawn.projection;

public class GoogleProjection implements Projection {

	// http://msdn.microsoft.com/en-us/library/bb259689.aspx
	private static double EarthRadius = 6378137;
	private static double MinLongitude = -180;
	private static double MaxLongitude = 180;
	private static double MinLatitude = -85.05112878;
	private static double MaxLatitude = 85.05112878;

	public String getName() {
		return "Google";
	}

	public LonLatPoint lonlatToMercator(LonLatPoint llP, int level) {
		long mapSize = mapSize(level);

		double tlon = getRange(llP.lon, MinLongitude, MaxLongitude);
		double x = (tlon + 180) / 360;
		x = getRange(x * mapSize + 0.5, 0, mapSize - 1);

		double tlat = getRange(llP.lat, MinLatitude, MaxLatitude);
		double sinLatitude = Math.sin(tlat * Math.PI / 180);
		double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude))
				/ (4 * Math.PI);
		y = getRange(y * mapSize + 0.5, 0, mapSize - 1);

		return new LonLatPoint((long) x, (long) y);
	}

	public LonLatPoint mercatorToLonLat(LonLatPoint mcP, int level) {
		long mapSize = mapSize(level);

		double lon = (getRange(mcP.lon, 0, mapSize - 1) / mapSize) - 0.5;
		lon = 360 * lon;

		double lat = 0.5 - (getRange(mcP.lat, 0, mapSize - 1) / mapSize);
		lat = 90 - 360 * Math.atan(Math.exp(-lat * 2 * Math.PI)) / Math.PI;

		return new LonLatPoint(lon, lat);
	}

	public PixelPoint lonlatToPixel(LonLatPoint llp, int level) {
		LonLatPoint mcP = lonlatToMercator(llp, level);
		return new PixelPoint((long) mcP.lon, (long) mcP.lat);
	}

	public LonLatPoint pixelToLonLat(PixelPoint pp, int level) {
		LonLatPoint mcP = new LonLatPoint(pp.x, pp.y);
		return mercatorToLonLat(mcP, level);
	}

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

	public long mapSize(int levelOfDetail) {
		// return 256 << levelOfDetail;
		return (long) (256 * Math.pow(2, levelOfDetail));
	}

	public double groundResolution(double latitude, int levelOfDetail) {
		double tl = getRange(latitude, MinLatitude, MaxLatitude);
		return Math.cos(tl * Math.PI / 180) * 2 * Math.PI * EarthRadius
				/ mapSize(levelOfDetail);
	}

	public double mapScale(double latitude, int levelOfDetail, int screenDpi) {
		return groundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
	}

	public PixelPoint mercatorToPixel(LonLatPoint mcP, int level) {
		return new PixelPoint((long) mcP.lon, (long) mcP.lat);
	}

	public LonLatPoint pixelToMercator(PixelPoint pP, int level) {
		return new LonLatPoint(pP.x, pP.y);
	}

	public Tile lonlatToTile(LonLatPoint llP, int level) {
		LonLatPoint mcP = lonlatToMercator(llP, level);
		return mercatorToTile(mcP, level);
	}

	public Tile mercatorToTile(LonLatPoint mcP, int level) {
		int tileX = (int) mcP.lon / 256;
		int tileY = (int) mcP.lat / 256;
		return new Tile(tileX, tileY);
	}

	public Tile pixelToTile(PixelPoint pP, int level) {
		int tileX = (int) pP.x / 256;
		int tileY = (int) pP.y / 256;
		return new Tile(tileX, tileY);
	}

	public PixelPoint upperLeftOfTile(Tile t, int level) {
		return new PixelPoint(t.x * 256, t.y * 256);
	}

	public PixelPoint lowerRightOfTile(Tile t, int level) {
		return new PixelPoint((t.x + 1) * 256 - 1, (t.y + 1) * 256 - 1);
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

	private double toRadians(double a) {
		return Math.PI * a / 180;
	}

	private double getDistance(double radiansLon1, double radiansLon2,
			double radiansLat1, double radiansLat2) {
		return EarthRadius
				* Math.acos((Math.sin(radiansLat1) * Math.sin(radiansLat2) + Math
						.cos(radiansLat1)
						* Math.cos(radiansLat2)
						* Math.cos(radiansLon2 - radiansLon1)));
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

	public static void main(String[] args) {
		GoogleProjection p = new GoogleProjection();

		LonLatPoint p1 = new LonLatPoint(116.35553, 39.937999);
		System.out.println(p1.lon + " | " + p1.lat);

		PixelPoint p4 = p.lonlatToPixel(p1, 16);
		System.out.println(p4.x + " | " + p4.y);
		LonLatPoint p5 = p.pixelToLonLat(p4, 16);
		System.out.println(p5.lon + " | " + p5.lat);
		
		Tile t = p.lonlatToTile(p1, 16);
		System.out.println(t);
		// 116.35553 | 39.937999
		// 13811169 | 6355275
		// 116.3555359840393 | 39.937990918300855

		Grid grid = new Grid(0, 0);
		PixelPoint pp = p.gridToPixel(grid, 18);
		LonLatPoint llp = p.pixelToLonLat(pp, 18);
		Grid g = p.pixelToGrid(pp, 18);

		System.out.println(pp);
		System.out.println(llp);
		System.out.println(g);

	}

}
