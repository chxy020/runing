package net.yaopao.assist;

public class GpsPoint {
	public long time;
	public double lon;
	public double lat;
	public double speed;
	public float course;
	public double altitude;
	public int status;// 0-运动状态，1-暂停状态

	public GpsPoint() {
		// TODO Auto-generated constructor stub
	}

	public GpsPoint(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}
	public GpsPoint(double lon, double lat,long time) {
		this.lon = lon;
		this.lat = lat;
		this.time = time;
	}
	public GpsPoint(double lon, double lat, int status) {
		this.lon = lon;
		this.lat = lat;
		this.status = status;
	}

	@Override
	public String toString() {
		// return
		// "time:"+time+" lon:"+lon+" lat:"+lat+" speed"+speed+" course"+course+" altitude"+altitude;
		return lon + "," + lat;
	}

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

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public float getCourse() {
		return course;
	}

	public void setCourse(float course) {
		this.course = course;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
