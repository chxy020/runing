package net.yaopao.engine.manager;

public class OneMileInfo {
	
	private int number;//第几英里
	private double lon;
	private double lat;
	private int totalDistance;//跑完这一英里一共的距离（加上之前的，是从运动开始算起）
	private int distance;//多少米，例如1005米
	private int totalDuring;//跑完这个英里一共用的时间（加上之前的，是从运动开始算起）
	private int during;//该英里用时
	private double totalAltitudeAdd;//跑完这一英里一共的高程增加值
	private double altitudeAdd;//每英里高程增加值
	private double totalAltitudeReduce;//跑完这一英里一共的高程降低值
	private double altitudeReduce;//每英里总高程降低值
//	private int step;//每英里步数
//	private int calorie;//每英里卡路里
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public int getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(int totalDistance) {
		this.totalDistance = totalDistance;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getTotalDuring() {
		return totalDuring;
	}
	public void setTotalDuring(int totalDuring) {
		this.totalDuring = totalDuring;
	}
	public int getDuring() {
		return during;
	}
	public void setDuring(int during) {
		this.during = during;
	}
	public double getTotalAltitudeAdd() {
		return totalAltitudeAdd;
	}
	public void setTotalAltitudeAdd(double totalAltitudeAdd) {
		this.totalAltitudeAdd = totalAltitudeAdd;
	}
	public double getAltitudeAdd() {
		return altitudeAdd;
	}
	public void setAltitudeAdd(double altitudeAdd) {
		this.altitudeAdd = altitudeAdd;
	}
	public double getTotalAltitudeReduce() {
		return totalAltitudeReduce;
	}
	public void setTotalAltitudeReduce(double totalAltitudeReduce) {
		this.totalAltitudeReduce = totalAltitudeReduce;
	}
	public double getAltitudeReduce() {
		return altitudeReduce;
	}
	public void setAltitudeReduce(double altitudeReduce) {
		this.altitudeReduce = altitudeReduce;
	}
	public OneMileInfo(int number, double lon, double lat, int totalDistance,
			int distance, int totalDuring, int during, double totalAltitudeAdd,
			double altitudeAdd, double totalAltitudeReduce,
			double altitudeReduce) {
		super();
		this.number = number;
		this.lon = lon;
		this.lat = lat;
		this.totalDistance = totalDistance;
		this.distance = distance;
		this.totalDuring = totalDuring;
		this.during = during;
		this.totalAltitudeAdd = totalAltitudeAdd;
		this.altitudeAdd = altitudeAdd;
		this.totalAltitudeReduce = totalAltitudeReduce;
		this.altitudeReduce = altitudeReduce;
	}
	public OneMileInfo(int number, double lon, double lat, int distance,
			int during, double altitudeAdd, double altitudeReduce) {
		super();
		this.number = number;
		this.lon = lon;
		this.lat = lat;
		this.distance = distance;
		this.during = during;
		this.altitudeAdd = altitudeAdd;
		this.altitudeReduce = altitudeReduce;
	}
	
	

}
