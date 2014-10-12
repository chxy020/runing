package net.yaopao.match.track;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import net.yaopao.activity.YaoPao01App;
import net.yaopao.assist.CNAppDelegate;

import shawn.projection.GoogleProjection;
import shawn.projection.Projection;
import shawn.projection.ProjectionFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.distance.GeometryLocation;

public class TrackData {
	private GeometryFactory gf = new GeometryFactory();
	private Projection pj = new GoogleProjection();
	private ProjectionFactory pf = new ProjectionFactory(pj, 17);

	public String name;
	public boolean isLap;
	public int claimedLength; // 米

	public LineString srcLS;
	public double actualLength;

	public LineString lsStartZone;
	public Polygon pgStartZone;

	public ArrayList<LineString> lsTakeOverZones;
	public ArrayList<Polygon> pgTakeOverZones;
	public ArrayList<Double> positionsOfTOZs;

	public ArrayList<LineString> lsTracks;
	public ArrayList<Polygon> pgTracks;

	public GeometryLocation matchLocation;
	public LineString lsRunning;
	public double runningDistance;

	public TrackData() {
	}

	public TrackData(String name, boolean isLap, int claimedLength) {
		this.name = name;
		this.isLap = isLap;
		this.claimedLength = claimedLength;

		this.lsTakeOverZones = new ArrayList<LineString>();
		this.pgTakeOverZones = new ArrayList<Polygon>();
		this.positionsOfTOZs = new ArrayList<Double>();
		this.lsTracks = new ArrayList<LineString>();
		this.pgTracks = new ArrayList<Polygon>();
	}

	public void setSrc(String wkt) {
		try {
			this.srcLS = (LineString) new WKTReader().read(wkt);
			// 抽稀
			// LineString ls = (LineString) new WKTReader().read(wkt);
			//
			// DistanceFinder df = new DistanceFinder(pj);
			// LonLatPoint delta100M = df.getDeltaLonLat(
			// new LonLatPoint(ls.getCoordinateN(0).x, ls
			// .getCoordinateN(0).y), 100);
			// ArrayList<Coordinate> cos = new ArrayList<Coordinate>();
			// cos.add(ls.getCoordinateN(0));
			//
			// for (int i = 1; i < ls.getNumPoints(); i++) {
			// if (Math.abs(ls.getCoordinateN(i).x - cos.get(cos.size() - 1).x)
			// > delta100M.lon * 0.1
			// || Math.abs(ls.getCoordinateN(i).y
			// - cos.get(cos.size() - 1).y) > delta100M.lat * 0.1) {
			// // 10M
			// cos.add(ls.getCoordinateN(i));
			// }
			// }
			//
			// Coordinate[] acos = new Coordinate[cos.size()];
			// for (int i = 0; i < cos.size(); i++) {
			// acos[i] = new Coordinate(cos.get(i));
			// }
			// this.srcLS = gf.createLineString(acos);

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

			// 计算交接区起点在赛道上的位置，以从跑到起点算起的米数为准；
			GeometryLocation gl = match(ls.getCoordinates()[0].x,
					ls.getCoordinates()[0].y, 180);
			this.positionsOfTOZs.add(getRunningDistance(gl));
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
			sb.append("positionsOfTOZs(" + i + "): " + positionsOfTOZs.get(i)
					+ "\n");
		}

		for (int i = 0; i < lsTracks.size(); i++) {
			sb.append("lsTracks(" + i + "): " + lsTracks.get(i).toText() + "\n");
			sb.append("pgTracks(" + i + "): " + pgTracks.get(i).toText() + "\n");
		}
		return sb.toString();
	}

	/**
	 * 计算距离下一交接区的距离
	 * 
	 * @author shawndong
	 * @version 1.0 2014/8/26
	 * @param position
	 *            跑过的距离（依据claimedLength计算出的距离）
	 * @return 距离下一交接区的距离（依据claimedLength计算出的距离）
	 */
	public double getDistanceToNextTakeOverZone(double runningDistance) {
		for (int i = 0; i < positionsOfTOZs.size(); i++) {
			if (runningDistance < positionsOfTOZs.get(i)) {
				return positionsOfTOZs.get(i) - runningDistance;
			}
		}
		if (isLap) {
			return claimedLength - runningDistance + positionsOfTOZs.get(0);
		} else {
			return claimedLength - runningDistance; // 不是环形赛道的话，给个离终点的距离吧。
		}
	}

	/**
	 * 找到GPS在赛道上的匹配点后，计算跑过的距离
	 * 
	 * @author shawndong
	 * @version 1.0 2014/8/26
	 * @param gl
	 *            匹配点的位置信息
	 * @return 跑过的距离，这个距离并非真实的GPS距离，而是依据赛道的claimedLength计算出的距离
	 */
	public double getRunningDistance(GeometryLocation gl) {
		Coordinate co = gl.getCoordinate();
		int segmentIndex = gl.getSegmentIndex();

		if (co.x == srcLS.getCoordinateN(0).x
				&& co.y == srcLS.getCoordinateN(0).y)
			return 0;

		Coordinate[] cos = new Coordinate[segmentIndex + 2];
		for (int i = 0; i <= segmentIndex; i++) {
			cos[i] = srcLS.getCoordinateN(i);
		}
		cos[cos.length - 1] = co;

		lsRunning = gf.createLineString(cos);
		runningDistance = pf.getLength(lsRunning) * claimedLength
				/ actualLength;

		return runningDistance;
	}

	/**
	 * 找到GPS在赛道上的匹配点
	 * 
	 * @author shawndong
	 * @version 1.0 2014/8/18
	 * @param lon
	 *            经度
	 * @param lat
	 *            纬度
	 * @param direction
	 *            行进方向，整型，从正北方向顺时针旋转指向的度数，取值范围0-359（此参数暂时未进行考虑）
	 * @return 匹配点的位置信息
	 */
	public GeometryLocation match(double lon, double lat, int direction) {
		Point p = gf.createPoint(new Coordinate(lon, lat));
		DistanceOp dop = new DistanceOp(srcLS, p);
		GeometryLocation[] gl = dop.closestLocations();

		matchLocation = gl[0];
		lsRunning = null;
		runningDistance = 0;

		return matchLocation;
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
		StringBuffer sbPositionsOfTOZs = new StringBuffer(
				positionsOfTOZs.get(0) + "");
		for (int i = 1; i < lsTakeOverZones.size(); i++) {
			sbLSTakeOverZones.append(":" + lsTakeOverZones.get(i).toText());
			sbPGTakeOverZones.append(":" + pgTakeOverZones.get(i).toText());
			sbPositionsOfTOZs.append(":" + positionsOfTOZs.get(i));
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
		props.setProperty("positionsOfTOZs", sbPositionsOfTOZs.toString());
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
			in = YaoPao01App.getAppContext().getResources().getAssets()
					.open(fileName);
			props.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		CNAppDelegate.match_track_line = props.getProperty("match_track_line");
		CNAppDelegate.match_takeover_zone = props.getProperty("takeoverzone");
		CNAppDelegate.match_stringTrackZone = props.getProperty("stringTrackZone");
		CNAppDelegate.match_stringStartZone = props.getProperty("stringStartZone");
	    
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
			wkts = props.getProperty("positionsOfTOZs").split(":");
			positionsOfTOZs = new ArrayList<Double>();
			for (i = 0; i < wkts.length; i++) {
				positionsOfTOZs.add(Double.parseDouble(wkts[i]));
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

//		for (int i = 0; i < pgTakeOverZones.size(); i++) {
//			if (pgTakeOverZones.get(i).contains(p))
//				return true;
//		}
		for (int i = 0; i < pgTracks.size(); i++) {
			if (pgTracks.get(i).contains(p))
				return true;
		}

		return false;
	}

	public static void main(String[] args) {

		String srcLS = "LINESTRING (116.390053 39.968191, 116.390114 39.96743, 116.390148 39.966718, 116.390167 39.966443, 116.390167 39.966385, 116.390171 39.966344, 116.390171 39.966321, 116.390167 39.966306, 116.390163 39.966298, 116.390156 39.966288, 116.390148 39.966281, 116.390141 39.966276, 116.390129 39.966273, 116.390118 39.96627, 116.390038 39.96626, 116.389919 39.966245, 116.389721 39.966227, 116.389687 39.966222, 116.389656 39.966217, 116.38961 39.966207, 116.389553 39.966192, 116.389462 39.966164, 116.389221 39.966085, 116.388733 39.965965, 116.388462 39.965925, 116.388195 39.965899, 116.388268 39.966054, 116.388275 39.96727, 116.388271 39.968147, 116.389381 39.968168, 116.389481 39.968173, 116.390053 39.968191)";
		String lsStartZone = "LINESTRING (116.389481 39.968173, 116.390053 39.968191)";
		String lsTakeOverZone = "LINESTRING (116.389481 39.968173, 116.390053 39.968191)";
		String lsTrack1 = "LINESTRING (116.390053 39.968191, 116.390114 39.96743, 116.390148 39.966718, 116.390167 39.966443, 116.390167 39.966385, 116.390171 39.966344, 116.390171 39.966321, 116.390167 39.966306, 116.390163 39.966298, 116.390156 39.966288, 116.390148 39.966281, 116.390141 39.966276, 116.390129 39.966273, 116.390118 39.96627, 116.390038 39.96626, 116.389919 39.966245, 116.389721 39.966227, 116.389687 39.966222, 116.389656 39.966217, 116.38961 39.966207, 116.389553 39.966192, 116.389462 39.966164, 116.389221 39.966085, 116.388733 39.965965, 116.388462 39.965925, 116.388195 39.965899)";
		String lsTrack2 = "LINESTRING (116.388195 39.965899, 116.388268 39.966054, 116.388275 39.96727, 116.388271 39.968147, 116.389381 39.968168, 116.389481 39.968173, 116.390053 39.968191)";

		TrackData td;

		// 手动生成赛道数据，并写入文件
		td = new TrackData("LunYang Test Track", true, 800);
		td.setSrc(srcLS);
		td.setStartZone(lsStartZone);
		td.addTakeOverZones(lsTakeOverZone);
		td.addTracks(lsTrack1);
		td.addTracks(lsTrack2);
		System.out.println(td);
		td.write("TrackData.properties");

		// 从文件中读取赛道数据
		td = new TrackData();
		td.read("TrackData.properties");
		System.out.println(td);

		// 判断GPS点是否在区域里，如果在的话，得出匹配点和距离
		if (td.isInTheTracks(116.390195, 39.967796)) {
			// 匹配点的位置：
			GeometryLocation gl = td.match(116.390195, 39.967796, 180);
			System.out.println("Match Point: " + gl.getCoordinate());

			// 跑过的距离：
			double runningLength = td.getRunningDistance(gl);
			System.out.println("runningLength: " + runningLength);

			// 距离下一交接区的距离
			if (td.isInTheTakeOverZones(116.390195, 39.967796) == -1) {
				// 如果未在交接区
				System.out.println("Distance to next take over zone: "
						+ td.getDistanceToNextTakeOverZone(runningLength));
			} else {
				System.out.println("Already in a take over zone.");
			}
		}

		// Coordinate [] testPoint = new Coordinate[5];
		//
		// testPoint[0] = new Coordinate(116.390195, 39.967796);
		// testPoint[1] = new Coordinate(116.39008283615112,
		// 39.966349060506666);
		// testPoint[2] = new Coordinate(116.38931572437286, 39.96628738937238);
		// testPoint[3] = new Coordinate(116.38798534870148,
		// 39.966936989193606);
		// testPoint[4] = new Coordinate(116.3897556066513, 39.96835950901508);
		//
		// for (Coordinate c: testPoint) {
		// // 匹配点的位置：
		// GeometryLocation gl = td.match(c.x, c.y, 180);
		// System.out.println("GPS: " + c);
		// System.out.println("Match Point: " + gl.getCoordinate());
		// System.out.println("Segment Index: " + gl.getSegmentIndex());
		//
		// // 跑过的距离：
		// double runningLength = td.getRunningDistance(gl);
		// System.out.println("Running Length: " + runningLength);
		// }
	}

}
