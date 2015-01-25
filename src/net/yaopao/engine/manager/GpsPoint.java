package net.yaopao.engine.manager;

public class GpsPoint {
	
	private double lon;
	private double lat;
	private int status;
	private long time;
	private int course;
	private double altitude;
	private int speed;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	public int getCourse() {
		return course;
	}
	public void setCourse(int course) {
		this.course = course;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public GpsPoint() {
		// TODO Auto-generated constructor stub
	}
	public GpsPoint(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}
	public GpsPoint(double lon, double lat, int status, long time,
			int course, double altitude, int speed) {
		super();
		this.lon = lon;
		this.lat = lat;
		this.status = status;
		this.time = time;
		this.course = course;
		this.altitude = altitude;
		this.speed = speed;
	}
	
	
	
	
	

}
