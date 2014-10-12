package shawn.encryption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import shawn.projection.LonLatPoint;

public class DataBuilder2 {

	public DataBuilder2() {
	}

	public static void main(String[] args) {
		LLE lle = new LLE();
		DecimalFormat df = new DecimalFormat("0.000000");

		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(
					"cossrc.txt")));
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(
					"cosdst.txt")));

			String line = in.readLine();
			String[] group;
			String[] srcLLs;
			double lon, lat;
			LonLatPoint srcLLP, dstLLP;
			double deltaLon, deltaLat;

			while (line != null) {
				group = line.split("\\t");
				srcLLs = group[1].split(",");
				lon = Double.parseDouble(srcLLs[0]);
				lat = Double.parseDouble(srcLLs[1]);
				srcLLP = new LonLatPoint(lon, lat);
				dstLLP = lle.encrypt(srcLLP);

				deltaLon = dstLLP.lon - srcLLP.lon;
				deltaLat = dstLLP.lat - srcLLP.lat;

				out.write(group[0] + "\t" + df.format(lon) + "," + df.format(lat) + "\t"
						+ df.format(deltaLon) + "," + df.format(deltaLat));
				out.newLine();

				line = in.readLine();
			}

			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done!!!");

	}

}
