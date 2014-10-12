package shawn.encryption;

import java.text.DecimalFormat;

import shawn.projection.BaiduProjection;
import shawn.projection.LonLatPoint;
import shawn.projection.PixelPoint;
import shawn.projection.Tile;

public class DataBuilder {

	public DataBuilder() {
	}

	public static void main(String[] args) {
		BaiduProjection bp = new BaiduProjection();
		LLE lle = new LLE();
		DecimalFormat df = new DecimalFormat("0.000000");
		int level = 11;

		LonLatPoint llp1 = new LonLatPoint(73, 18);
		LonLatPoint llp2 = new LonLatPoint(136, 54);
		Tile t1 = bp.lonlatToTile(llp1, level);
		Tile t2 = bp.lonlatToTile(llp2, level);

		System.out.println(t1);
		System.out.println(t2);

		int c = 0;
		Tile tt, ttx, tty;
		PixelPoint pp, ppx, ppy;
		LonLatPoint llp, llpx, llpy, llpt, llpt2;
		for (int i = t1.x; i <= t2.x; i++) {
			for (int j = t1.y; j <= t2.y; j++) {
				tt = new Tile(i, j);
				ttx = new Tile(i + 1, j);
				tty = new Tile(i, j - 1);
				pp = bp.upperLeftOfTile(tt, level);
				ppx = bp.upperLeftOfTile(ttx, level);
				ppy = bp.upperLeftOfTile(tty, level);
				llp = bp.pixelToLonLat(pp, level);
				llpx = bp.pixelToLonLat(ppx, level);
				llpy = bp.pixelToLonLat(ppy, level);

				llpt = new LonLatPoint((llp.lon + llpx.lon) / 2.0d,
						(llp.lat + llpy.lat) / 2.0d);
				llpt2 = lle.encrypt(llpt);
				if (c == 0) {
					System.out.println(llp);
					System.out.println(llpt);
					System.out.println(llpt2);
					System.out.println(df.format(llpt2.lon - llpt.lon) + ","
							+ df.format(llpt2.lat - llpt.lat));
				}
				c++;
			}
		}
		System.out.println(c);
	}

}
