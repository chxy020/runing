package shawn.projection;

public interface Projection {
	public String getName();
	
	public LonLatPoint lonlatToMercator(LonLatPoint llP, int level);
	public PixelPoint mercatorToPixel(LonLatPoint mcP, int level);
	public PixelPoint lonlatToPixel(LonLatPoint llP, int level);

	public LonLatPoint pixelToMercator(PixelPoint pP, int level);
	public LonLatPoint mercatorToLonLat(LonLatPoint mcP, int level);
	public LonLatPoint pixelToLonLat(PixelPoint pP, int level);
	
	public Tile lonlatToTile(LonLatPoint llP, int level);
	public Tile mercatorToTile(LonLatPoint mcP, int level);
	public Tile pixelToTile(PixelPoint pP, int level);

	public PixelPoint upperLeftOfTile(Tile t, int level);
	public PixelPoint lowerRightOfTile(Tile t, int level);

	public double getDistanceByLL(LonLatPoint p1, LonLatPoint p2);

	public PixelPoint gridToPixel(Grid grid, int level);
	public Grid pixelToGrid(PixelPoint pP, int level);
	public int getGridWidthHeight(int level);

}
