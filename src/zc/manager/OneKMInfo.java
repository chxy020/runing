package zc.manager;

/**
 * @author zhangchi
 * 整公里的一些数据
 */
public class OneKMInfo {
	private int number;//第几公里
	private double lon;
	private double lat;
	private int during;//跑完这个公里一共用的时间
	private int pace;//每公里配速
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
	public OneKMInfo(int number, double lon, double lat, int during, int pace) {
		super();
		this.number = number;
		this.lon = lon;
		this.lat = lat;
		this.during = during;
		this.pace = pace;
	}
	
	
}
