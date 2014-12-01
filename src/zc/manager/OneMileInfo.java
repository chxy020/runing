package zc.manager;

public class OneMileInfo {
	
	private int number;//第几英里
	private double lon;
	private double lat;
	private int during;//跑完这个英里一共用的时间（加上之前的，是从运动开始算起）
	private int pace;//每英里配速
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
	public int getDuring() {
		return during;
	}
	public void setDuring(int during) {
		this.during = during;
	}
	public int getPace() {
		return pace;
	}
	public void setPace(int pace) {
		this.pace = pace;
	}
	public OneMileInfo(int number, double lon, double lat, int during, int pace) {
		super();
		this.number = number;
		this.lon = lon;
		this.lat = lat;
		this.during = during;
		this.pace = pace;
	}
	public OneMileInfo(int number, double lon, double lat, int pace) {
		super();
		this.number = number;
		this.lon = lon;
		this.lat = lat;
		this.pace = pace;
	}
}
