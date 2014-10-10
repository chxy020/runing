package net.yaopao.assist;

public class CNGPSPoint4Match {
	long time;
	double lon;
	double lat;
	int isInTrack;
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
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
	public int getIsInTrack() {
		return isInTrack;
	}
	public void setIsInTrack(int isInTrack) {
		this.isInTrack = isInTrack;
	}
	public CNGPSPoint4Match(long time, double lon, double lat, int isInTrack) {
		super();
		this.time = time;
		this.lon = lon;
		this.lat = lat;
		this.isInTrack = isInTrack;
	}

	

}
