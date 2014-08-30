package net.yaopao.bean;

public class DataBean {

	private long totalTime; // 秒数
	private double distance;// 距离 m
	private double pspeed;// 平均配速 m/s
	private int count;// 记录条数

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

	public double getPspeed() {
		return pspeed;
	}

	public void setPspeed(double pspeed) {
		this.pspeed = pspeed;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
