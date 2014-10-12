package shawn.projection;

import java.text.DecimalFormat;

public class Test_Distance {

	public Test_Distance() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BaiduProjection bp = new BaiduProjection();
		DecimalFormat df = new DecimalFormat("0.00");

		double bjLon = 116.404269;
		double bjLat = 39.914271;
		double szLon = 114.066231;
		double szLat = 22.54552;
		double hebLon = 126.661516;
		double hebLat = 45.765879;
		
		LonLatPoint llp1, llp2;
		PixelPoint pp1, pp2;

		System.out.println("¹þ¶û±õ");
		llp1 = new LonLatPoint(hebLon, hebLat);
		llp2 = new LonLatPoint(hebLon + 0.0001, hebLat);
		System.out.println(df.format(bp.getDistanceByLL(llp1, llp2)) + "m");
		pp1 = bp.lonlatToPixel(llp1, 18);
		pp2 = bp.lonlatToPixel(llp2, 18);
		System.out.println(pp1 + "\t" + pp2);
		llp1 = new LonLatPoint(hebLon, hebLat);
		llp2 = new LonLatPoint(hebLon, hebLat + 0.0001);
		System.out.println(df.format(bp.getDistanceByLL(llp1, llp2)) + "m");
		pp1 = bp.lonlatToPixel(llp1, 18);
		pp2 = bp.lonlatToPixel(llp2, 18);
		System.out.println(pp1 + "\t" + pp2);

		System.out.println("±±¾©");
		llp1 = new LonLatPoint(bjLon, bjLat);
		llp2 = new LonLatPoint(bjLon + 0.0001, bjLat);
		System.out.println(df.format(bp.getDistanceByLL(llp1, llp2)) + "m");
		pp1 = bp.lonlatToPixel(llp1, 18);
		pp2 = bp.lonlatToPixel(llp2, 18);
		System.out.println(pp1 + "\t" + pp2);
		llp1 = new LonLatPoint(bjLon, bjLat);
		llp2 = new LonLatPoint(bjLon, bjLat + 0.0001);
		System.out.println(df.format(bp.getDistanceByLL(llp1, llp2)) + "m");
		pp1 = bp.lonlatToPixel(llp1, 18);
		pp2 = bp.lonlatToPixel(llp2, 18);
		System.out.println(pp1 + "\t" + pp2);

		System.out.println("ÉîÛÚ");
		llp1 = new LonLatPoint(szLon, szLat);
		llp2 = new LonLatPoint(szLon + 0.0001, szLat);
		System.out.println(df.format(bp.getDistanceByLL(llp1, llp2)) + "m");
		pp1 = bp.lonlatToPixel(llp1, 18);
		pp2 = bp.lonlatToPixel(llp2, 18);
		System.out.println(pp1 + "\t" + pp2);
		llp1 = new LonLatPoint(szLon, szLat);
		llp2 = new LonLatPoint(szLon, szLat + 0.0001);
		System.out.println(df.format(bp.getDistanceByLL(llp1, llp2)) + "m");
		pp1 = bp.lonlatToPixel(llp1, 18);
		pp2 = bp.lonlatToPixel(llp2, 18);
		System.out.println(pp1 + "\t" + pp2);
		
		
	}

}
