package shawn.encryption;

import shawn.projection.LonLatPoint;

public class LLE {

	public LonLatPoint encrypt(LonLatPoint wg) {
		LonLatPoint china = new LonLatPoint();

		china.lon = wg.lon + 0.006206;
		china.lat = wg.lat + 0.001369;
//		china.lon = wg.lon;
//		china.lat = wg.lat;
		return china;
	}

	public LLE() {
		super();
	}

	public static void main(String[] args) {

		LonLatEncryption lle = new LonLatEncryption();
		LonLatPoint wg = new LonLatPoint(116.359701, 39.941962);
		LonLatPoint china = lle.encrypt(wg);

		System.out.println("wg.lon = " + wg.lon);
		System.out.println("wg.lat = " + wg.lat);
		System.out.println("china.lon = " + china.lon);
		System.out.println("china.lat = " + china.lat);
		System.out.println(china.lon - wg.lon);
		System.out.println(china.lat - wg.lat);

	}

}
