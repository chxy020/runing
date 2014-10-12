package shawn.projection;

import java.text.DecimalFormat;

public class Test_Projection {

	public static void main(String[] args) {
		Projection proj = new GoogleProjection();

		LonLatPoint llp, llpx, llpy;
		PixelPoint pp, ppx, ppy;

		int pixels = 1000;

		DecimalFormat df1 = new DecimalFormat("0.0");
		DecimalFormat df8 = new DecimalFormat("0.00000000");

		for (int level = 10; level <= 18; level++) {
//			for (double i = 0; i < 85; i += 0.1) {
//				llp = new LonLatPoint(116, i);
				llp = new LonLatPoint(104.91935, 28.3282);
				pp = proj.lonlatToPixel(llp, level);
				ppx = new PixelPoint(pp.x + pixels, pp.y);
				ppy = new PixelPoint(pp.x, pp.y + pixels);
				llpx = proj.pixelToLonLat(ppx, level);
				llpy = proj.pixelToLonLat(ppy, level);

				System.out.println(level + "\t"/* + df1.format(i) + "\t"*/
						+ df8.format(llpx.lon - llp.lon) + "\t"
						+ df8.format(llp.lat - llpy.lat));
//			}
		}

	}
}
