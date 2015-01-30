package net.yaopao.bean;

public class SportParaBean {
	private int id;// 记录id
	private int uid;// 一次运动的唯一标示
	private int countDown;// 0开，2 关
	private int vioce;// 0 开，2关
	private int targetdis; //目标距离
	private int targettime;// //目标时间
	private int typeIndex;// 跑步：1，：步行2，自行车骑行：3
	private int targetIndex;// 自由：1，距离：2，时间：3 
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getCountDown() {
		return countDown;
	}
	public void setCountDown(int countDown) {
		this.countDown = countDown;
	}
	public int getVioce() {
		return vioce;
	}
	public void setVioce(int vioce) {
		this.vioce = vioce;
	}
	public int getTargetdis() {
		return targetdis;
	}
	public void setTargetdis(int targetdis) {
		this.targetdis = targetdis;
	}
	public int getTargettime() {
		return targettime;
	}
	public void setTargettime(int targettime) {
		this.targettime = targettime;
	}
	public int getTypeIndex() {
		return typeIndex;
	}
	public void setTypeIndex(int typeIndex) {
		this.typeIndex = typeIndex;
	}
	public int getTargetIndex() {
		return targetIndex;
	}
	public void setTargetIndex(int targetIndex) {
		this.targetIndex = targetIndex;
	}


}
