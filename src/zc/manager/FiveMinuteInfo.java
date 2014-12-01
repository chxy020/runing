package zc.manager;

public class FiveMinuteInfo {
	
	private int number;//第几个5分钟
	private double lon;
	private double lat;
	private double distance;//这个5分钟跑了多远
	private double total_distance;//跑完这个5分钟一共跑得距离（加上之前的，是从运动开始算起）
	private int pace;//该5分钟的配速（公里）
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
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getTotal_distance() {
		return total_distance;
	}
	public void setTotal_distance(double total_distance) {
		this.total_distance = total_distance;
	}
	public int getPace() {
		return pace;
	}
	public void setPace(int pace) {
		this.pace = pace;
	}
	public FiveMinuteInfo(int number, double lon, double lat, double distance,
			double total_distance, int pace) {
		super();
		this.number = number;
		this.lon = lon;
		this.lat = lat;
		this.distance = distance;
		this.total_distance = total_distance;
		this.pace = pace;
	}
	public FiveMinuteInfo(int number, double lon, double lat, double distance,
			int pace) {
		super();
		this.number = number;
		this.lon = lon;
		this.lat = lat;
		this.distance = distance;
		this.pace = pace;
	}
	
	
}
