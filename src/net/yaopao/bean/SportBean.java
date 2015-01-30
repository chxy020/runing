package net.yaopao.bean;

public class SportBean {

	private int id;// 记录id
	private String rid;// 一次运动的唯一标示
	private String tarinfo;// 运动目标详情
	private String remarks;// 个人备注
	private String runtra;// 一条轨迹轨迹--点的集合格式：rentra=
							// [{slon:"1100",slat:"1110"},{slon:"2100",slat:"2110"}]
	private String statusIndex;//
	// 一个点的 属性
	private long addtime; // 毫秒数
	private int slon; // 经度 WGS84坐标系，保留6位小数，乘以1000000取整
	private int slat;// 纬度 WGS84坐标系，保留6位小数，乘以1000000取整
	private int speed;// 米/秒
	private int orient;// 方向 正北为0度，顺时针方向增加值359度
	private int height; // 米，保留1位小数，乘以10取整
	private int state; // 运动中：0，暂停中：1

	private int runtar;// 自由：0，距离：1，时间：2
	private int runty;// 步行：1，跑步：2，自行车骑行：3
	private int mind; // 心情
	private int runway;// 跑道
	private int aheart;// 平均心率
	private int mheart;// 最高心率
	private int weather;// 天气代码
	private int temp;// 温度
	private int utime;// 所用时间  毫秒数
	private int heat;// 热量 卡路里

	private int distance;// 距离
	private int pspeed;// 配速
	private String hspeed;// 时速
	private int points;// 积分
	public int sportty;// 跑步类型0-日常，1-比赛
	public String clientImagePaths;// 跑步拍照图片路径
	public String clientImagePathsSmall;// 跑步拍照图片缩略图路径
	public String clientBinaryFilePath;// 跑步记录数据二进制文件路径

	public String getClientBinaryFilePath() {
		return clientBinaryFilePath;
	}

	public void setClientBinaryFilePath(String clientBinaryFilePath) {
		this.clientBinaryFilePath = clientBinaryFilePath;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getTarinfo() {
		return tarinfo;
	}

	public void setTarinfo(String tarinfo) {
		this.tarinfo = tarinfo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRuntra() {
		return runtra;
	}

	public void setRuntra(String runtra) {
		this.runtra = runtra;
	}

	public long getAddtime() {
		return addtime;
	}

	public void setAddtime(long addtime) {
		this.addtime = addtime;
	}

	public int getSlon() {
		return slon;
	}

	public void setSlon(int slon) {
		this.slon = slon;
	}

	public int getSlat() {
		return slat;
	}

	public void setSlat(int slat) {
		this.slat = slat;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getOrient() {
		return orient;
	}

	public void setOrient(int orient) {
		this.orient = orient;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getRuntar() {
		return runtar;
	}

	public void setRuntar(int runtar) {
		this.runtar = runtar;
	}

	public int getRunty() {
		return runty;
	}

	public void setRunty(int runty) {
		this.runty = runty;
	}

	public int getMind() {
		return mind;
	}

	public void setMind(int mind) {
		this.mind = mind;
	}

	public int getRunway() {
		return runway;
	}

	public void setRunway(int runway) {
		this.runway = runway;
	}

	public int getAheart() {
		return aheart;
	}

	public void setAheart(int aheart) {
		this.aheart = aheart;
	}

	public int getMheart() {
		return mheart;
	}

	public void setMheart(int mheart) {
		this.mheart = mheart;
	}

	public int getWeather() {
		return weather;
	}

	public void setWeather(int weather) {
		this.weather = weather;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	public int getUtime() {
		return utime;
	}

	public void setUtime(int utime) {
		this.utime = utime;
	}

	public int getHeat() {
		return heat;
	}

	public void setHeat(int heat) {
		this.heat = heat;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getPspeed() {
		return pspeed;
	}

	public void setPspeed(int pspeed) {
		this.pspeed = pspeed;
	}

	public String getHspeed() {
		return hspeed;
	}

	public void setHspeed(String hspeed) {
		this.hspeed = hspeed;
	}

	public String getStatusIndex() {
		return statusIndex;
	}

	public void setStatusIndex(String statusIndex) {
		this.statusIndex = statusIndex;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getSportty() {
		return sportty;
	}

	public void setSportty(int sportty) {
		this.sportty = sportty;
	}

	public String getClientImagePaths() {
		return clientImagePaths;
	}

	public void setClientImagePaths(String clientImagePaths) {
		this.clientImagePaths = clientImagePaths;
	}

	public String getClientImagePathsSmall() {
		return clientImagePathsSmall;
	}

	public void setClientImagePathsSmall(String clientImagePathsSmall) {
		this.clientImagePathsSmall = clientImagePathsSmall;
	}

	@Override
	public String toString() {
		return "rid= " + rid + " distance=" + distance + " runtra=" + runtra
				+ " statusIndex=" + statusIndex;
	}

}
