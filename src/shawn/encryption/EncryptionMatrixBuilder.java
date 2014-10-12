package shawn.encryption;

import shawn.projection.LonLatPoint;

public class EncryptionMatrixBuilder {

	public EncryptionMatrixBuilder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LonLatEncryption lle = new LonLatEncryption();
		
		LonLatPoint src, dst, delta;
		double x1, y1, x2, y2, dx, dy;
		
		x1 = 115.375;
		x2 = 117.500;
		y1 = 39.415;
		y2 = 41.085;
		dx = 0.001171;
		dy = 0.000898;
		
		for (double cx = x1; cx <= x2; cx += 0.005) {
			for (double cy = y1; cy <= y2; cy += 0.005) {
				src = new LonLatPoint(cx, cy);
				dst = lle.encrypt(src);
				delta = new LonLatPoint(dst.lon - src.lon, dst.lat - src.lat);
//				System.out.println(src + "\t" + dst + "\t" + delta);
				System.out.println(src + "\t" + delta);
			}
		}
		
		

	}

}
