package net.yaopao.bean;

public class DataBean {

	private long totalTime; // 秒数
	private double distance;// 距离 m
	private int pspeed;// 平均配速 s
	private int count;// 记录条数
	private int points;// 积分

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getPspeed() {
		return pspeed;
	}

	public void setPspeed(int pspeed) {
		this.pspeed = pspeed;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

}
