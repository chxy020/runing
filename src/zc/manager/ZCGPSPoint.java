package zc.manager;

public class ZCGPSPoint {
	
	private double lon;
	private double lat;
	private int status;
	private long time;
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
	public ZCGPSPoint(double lon, double lat, int status, long time) {
		super();
		this.lon = lon;
		this.lat = lat;
		this.status = status;
		this.time = time;
	}
	
	
	
	

}
