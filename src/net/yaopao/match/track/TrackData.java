package net.yaopao.match.track;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import net.yaopao.activity.YaoPao01App;
import shawn.projection.GoogleProjection;
import shawn.projection.ProjectionFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.buffer.BufferOp;

public class TrackData {
	private GeometryFactory gf = new GeometryFactory();
	private ProjectionFactory pf = new ProjectionFactory(
			new GoogleProjection(), 17);

	public String name;
	public boolean isLap;
	public int claimedLength; // 米

	public LineString srcLS;
	public double actualLength;

	public LineString lsStartZone;
	public Polygon pgStartZone;

	public ArrayList<LineString> lsTakeOverZones;
	public ArrayList<Polygon> pgTakeOverZones;

	public ArrayList<LineString> lsTracks;
	public ArrayList<Polygon> pgTracks;

	public TrackData() {
	}

	public TrackData(String name, boolean isLap, int claimedLength) {
		this.name = name;
		this.isLap = isLap;
		this.claimedLength = claimedLength;

		this.lsTakeOverZones = new ArrayList<LineString>();
		this.pgTakeOverZones = new ArrayList<Polygon>();
		this.lsTracks = new ArrayList<LineString>();
		this.pgTracks = new ArrayList<Polygon>();
	}

	public void setSrc(String wkt) {
		try {
			this.srcLS = (LineString) new WKTReader().read(wkt);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		this.actualLength = pf.getLength(this.srcLS);
	}

	public void setStartZone(String wkt) {
		try {
			this.lsStartZone = (LineString) new WKTReader().read(wkt);
			this.pgStartZone = buffer(this.lsStartZone, 30, 8,
					BufferOp.CAP_BUTT);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void addTakeOverZones(String wkt) {
		try {
			LineString ls = (LineString) new WKTReader().read(wkt);
			this.lsTakeOverZones.add(ls);
			this.pgTakeOverZones.add(buffer(ls, 30, 8, BufferOp.CAP_ROUND));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void addTracks(String wkt) {
		try {
			LineString ls = (LineString) new WKTReader().read(wkt);
			this.lsTracks.add(ls);
			this.pgTracks.add(buffer(ls, 30, 8, BufferOp.CAP_ROUND));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private Polygon buffer(LineString llls, double distance,
			int quadrantSegments, int endCapStyle) {
		LineString pls = pf.toPixel(llls);
		Polygon ppg = (Polygon) pls.buffer(distance, quadrantSegments,
				endCapStyle);
		Polygon llpg = pf.toLonLat(ppg);

		return llpg;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("name: " + name + "\n");
		sb.append("claimedLength: " + claimedLength + "\n");
		sb.append("srcLS: " + srcLS.toText() + "\n");
		sb.append("actualLength: " + actualLength + "\n");
		sb.append("lsStartZone: " + lsStartZone.toText() + "\n");
		sb.append("pgStartZone: " + pgStartZone.toText() + "\n");

		for (int i = 0; i < lsTakeOverZones.size(); i++) {
			sb.append("lsTakeOverZones(" + i + "): "
					+ lsTakeOverZones.get(i).toText() + "\n");
			sb.append("pgTakeOverZones(" + i + "): "
					+ pgTakeOverZones.get(i).toText() + "\n");
		}

		for (int i = 0; i < lsTracks.size(); i++) {
			sb.append("lsTracks(" + i + "): " + lsTracks.get(i).toText() + "\n");
			sb.append("pgTracks(" + i + "): " + pgTracks.get(i).toText() + "\n");
		}
		return sb.toString();
	}

	/**
	 * 写入属性文件
	 * 
	 * @author shawndong
	 * @version 1.0 2014/8/18
	 * @param fileName
	 *            文件名
	 */
	public void write(String fileName) {
		StringBuffer sbLSTakeOverZones = new StringBuffer(lsTakeOverZones
				.get(0).toText());
		StringBuffer sbPGTakeOverZones = new StringBuffer(pgTakeOverZones
				.get(0).toText());
		for (int i = 1; i < lsTakeOverZones.size(); i++) {
			sbLSTakeOverZones.append(":" + lsTakeOverZones.get(i).toText());
			sbPGTakeOverZones.append(":" + pgTakeOverZones.get(i).toText());
		}

		StringBuffer sbLSTracks = new StringBuffer(lsTracks.get(0).toText());
		StringBuffer sbPGTracks = new StringBuffer(pgTracks.get(0).toText());
		for (int i = 1; i < lsTracks.size(); i++) {
			sbLSTracks.append(":" + lsTracks.get(i).toText());
			sbPGTracks.append(":" + pgTracks.get(i).toText());
		}

		Properties props = new Properties();
		props.setProperty("name", name);
		props.setProperty("isLap", isLap ? "True" : "False");
		props.setProperty("claimedLength", claimedLength + "");
		props.setProperty("srcLS", srcLS.toText());
		props.setProperty("actualLength", actualLength + "");
		props.setProperty("lsStartZone", lsStartZone.toText());
		props.setProperty("pgStartZone", pgStartZone.toText());
		props.setProperty("lsTakeOverZones", sbLSTakeOverZones.toString());
		props.setProperty("pgTakeOverZones", sbPGTakeOverZones.toString());
		props.setProperty("lsTracks", sbLSTracks.toString());
		props.setProperty("pgTracks", sbPGTracks.toString());

		OutputStream out;
		try {
			out = new BufferedOutputStream(new FileOutputStream(fileName));
			props.store(out, name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从属性文件读取
	 * 
	 * @author shawndong
	 * @version 1.0 2014/8/18
	 * @param fileName
	 *            文件名
	 */
	public void read(String fileName) {
		Properties props = new Properties();
		InputStream in;
		try {
//			in = new BufferedInputStream(new FileInputStream(fileName));
			in = YaoPao01App.getAppContext().getResources().getAssets().open(fileName);
			
			props.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		name = props.getProperty("name");
		if (props.getProperty("isLap").equals("True")
				|| props.getProperty("isLap").equals("true")) {
			isLap = true;
		} else {
			isLap = false;
		}
		claimedLength = Integer.parseInt(props.getProperty("claimedLength"));

		try {
			srcLS = (LineString) new WKTReader().read(props
					.getProperty("srcLS"));
			actualLength = Double
					.parseDouble(props.getProperty("actualLength"));

			lsStartZone = (LineString) new WKTReader().read(props
					.getProperty("lsStartZone"));
			pgStartZone = (Polygon) new WKTReader().read(props
					.getProperty("pgStartZone"));

			String[] wkts;
			int i;

			wkts = props.getProperty("lsTakeOverZones").split(":");
			lsTakeOverZones = new ArrayList<LineString>();
			for (i = 0; i < wkts.length; i++) {
				lsTakeOverZones.add((LineString) new WKTReader().read(wkts[i]));
			}
			wkts = props.getProperty("pgTakeOverZones").split(":");
			pgTakeOverZones = new ArrayList<Polygon>();
			for (i = 0; i < wkts.length; i++) {
				pgTakeOverZones.add((Polygon) new WKTReader().read(wkts[i]));
			}

			wkts = props.getProperty("lsTracks").split(":");
			lsTracks = new ArrayList<LineString>();
			for (i = 0; i < wkts.length; i++) {
				lsTracks.add((LineString) new WKTReader().read(wkts[i]));
			}
			wkts = props.getProperty("pgTracks").split(":");
			pgTracks = new ArrayList<Polygon>();
			for (i = 0; i < wkts.length; i++) {
				pgTracks.add((Polygon) new WKTReader().read(wkts[i]));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否在出发区
	 * 
	 * @author shawndong
	 * @version 1.0 2014/8/18
	 * @param lon
	 *            经度
	 * @param lat
	 *            纬度
	 * @return 是否在出发区
	 */
	public boolean isInTheStartZone(double lon, double lat) {
		Point p = gf.createPoint(new Coordinate(lon, lat));

		if (pgStartZone.contains(p))
			return true;
		else
			return false;
	}

	/**
	 * 是否在交接区
	 * 
	 * @author shawndong
	 * @version 1.0 2014/8/18
	 * @param lon
	 *            经度
	 * @param lat
	 *            纬度
	 * @return -1 表明未在交接区；0,1,2,3 表明所在的交接区序号
	 */
	public int isInTheTakeOverZones(double lon, double lat) {
		Point p = gf.createPoint(new Coordinate(lon, lat));

		for (int i = 0; i < pgTakeOverZones.size(); i++) {
			if (pgTakeOverZones.get(i).contains(p))
				return i;
		}

		return -1;
	}

	/**
	 * 是否在赛道上
	 * 
	 * @author shawndong
	 * @version 1.0 2014/8/18
	 * @param lon
	 *            经度
	 * @param lat
	 *            纬度
	 * @return 是否在赛道上
	 */
	public boolean isInTheTracks(double lon, double lat) {
		Point p = gf.createPoint(new Coordinate(lon, lat));

		for (int i = 0; i < pgTakeOverZones.size(); i++) {
			if (pgTakeOverZones.get(i).contains(p))
				return true;
		}
		for (int i = 0; i < pgTracks.size(); i++) {
			if (pgTracks.get(i).contains(p))
				return true;
		}

		return false;
	}

	public static void main(String[] args) {
		String ts = "123:321";
		String[] ta = ts.split(":");
		System.out.println(ta.length);
		System.out.println(ta[0]);
		System.out.println(ta[1]);

		String srcLS = "LINESTRING (116.390053 39.968191, 116.390114 39.96743, 116.390148 39.966718, 116.390167 39.966443, 116.390167 39.966385, 116.390171 39.966344, 116.390171 39.966321, 116.390167 39.966306, 116.390163 39.966298, 116.390156 39.966288, 116.390148 39.966281, 116.390141 39.966276, 116.390129 39.966273, 116.390118 39.96627, 116.390038 39.96626, 116.389919 39.966245, 116.389721 39.966227, 116.389687 39.966222, 116.389656 39.966217, 116.38961 39.966207, 116.389553 39.966192, 116.389462 39.966164, 116.389221 39.966085, 116.388733 39.965965, 116.388462 39.965925, 116.388195 39.965899, 116.388268 39.966054, 116.388275 39.96727, 116.388271 39.968147, 116.389381 39.968168, 116.389481 39.968173, 116.390053 39.968191)";
		String lsStartZone = "LINESTRING (116.389481 39.968173, 116.390053 39.968191)";
		String lsTakeOverZone = "LINESTRING (116.389481 39.968173, 116.390053 39.968191)";
		String lsTrack1 = "LINESTRING (116.390053 39.968191, 116.390114 39.96743, 116.390148 39.966718, 116.390167 39.966443, 116.390167 39.966385, 116.390171 39.966344, 116.390171 39.966321, 116.390167 39.966306, 116.390163 39.966298, 116.390156 39.966288, 116.390148 39.966281, 116.390141 39.966276, 116.390129 39.966273, 116.390118 39.96627, 116.390038 39.96626, 116.389919 39.966245, 116.389721 39.966227, 116.389687 39.966222, 116.389656 39.966217, 116.38961 39.966207, 116.389553 39.966192, 116.389462 39.966164, 116.389221 39.966085, 116.388733 39.965965, 116.388462 39.965925, 116.388195 39.965899)";
		String lsTrack2 = "LINESTRING (116.388195 39.965899, 116.388268 39.966054, 116.388275 39.96727, 116.388271 39.968147, 116.389381 39.968168, 116.389481 39.968173)";

		TrackData td;

		td = new TrackData("LunYang Test Track", true, 800);
		td.setSrc(srcLS);
		td.setStartZone(lsStartZone);
		td.addTakeOverZones(lsTakeOverZone);
		td.addTracks(lsTrack1);
		td.addTracks(lsTrack2);

		System.out.println(td);

		td.write("TrackData.properties");

		TrackData ttd = new TrackData();
		ttd.read("TrackData.properties");
		System.out.println(ttd);
	}

}
