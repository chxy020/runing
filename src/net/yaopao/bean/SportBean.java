package net.yaopao.bean;

public class SportBean {

	private int id;// 记录id
	private int averageHeart;// 平均心率
	private int maxHeart;// 最高心率
	private String clientImagePaths;// 跑步拍照图片路径
	private String clientImagePathsSmall;// 跑步拍照图片缩略图路径
	private String clientBinaryFilePath;// 跑步记录数据二进制文件路径
	private int distance;// 距离
	private int heat;// 热量 卡路里
	private int isMatch;// 跑步类型 日常，比赛
	private String jsonParam;// 预留
	private int kmCount;// 
	private int mileCount;// 
	private int feeling; // 心情
	private int secondPerKm;// 配速
	private String remark;// 运动记录备注
	private String rid;// 一次运动的唯一标示
	private String gpsString;// 预留
	private int targetType;// 自由：1，距离：2，时间：3
	private int howToMove;// 跑步：1，步行：2，自行车骑行：3
	private int runway;// 跑道
	private String serverImagePathsSmall;// 跑步拍照图片服务器缩略图路径
	private String serverImagePaths;// 跑步拍照图片服务器路径
	private String serverBinaryFilePath;// 跑步二进制文件服务器路径
	private int score;// 积分
	private long startTime;// 跑步开始时间
	private int temp;// 温度
	private int uid;// 温度
	private long updateTime;// 修改时间
	private int duration;// 所用时间  毫秒数
	private int dbVersion;// 数据库版本
	private int weather;// 天气代码
	private int targetValue;// 目标值
	private long generateTime;// 记录添加时间
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAverageHeart() {
		return averageHeart;
	}
	public void setAverageHeart(int averageHeart) {
		this.averageHeart = averageHeart;
	}
	public int getMaxHeart() {
		return maxHeart;
	}
	public void setMaxHeart(int maxHeart) {
		this.maxHeart = maxHeart;
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
	public String getClientBinaryFilePath() {
		return clientBinaryFilePath;
	}
	public void setClientBinaryFilePath(String clientBinaryFilePath) {
		this.clientBinaryFilePath = clientBinaryFilePath;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getHeat() {
		return heat;
	}
	public void setHeat(int heat) {
		this.heat = heat;
	}
	public int getIsMatch() {
		return isMatch;
	}
	public void setIsMatch(int isMatch) {
		this.isMatch = isMatch;
	}
	public String getJsonParam() {
		return jsonParam;
	}
	public void setJsonParam(String jsonParam) {
		this.jsonParam = jsonParam;
	}
	public int getKmCount() {
		return kmCount;
	}
	public void setKmCount(int kmCount) {
		this.kmCount = kmCount;
	}
	public int getMileCount() {
		return mileCount;
	}
	public void setMileCount(int mileCount) {
		this.mileCount = mileCount;
	}
	public int getFeeling() {
		return feeling;
	}
	public void setFeeling(int feeling) {
		this.feeling = feeling;
	}
	public int getSecondPerKm() {
		return secondPerKm;
	}
	public void setSecondPerKm(int secondPerKm) {
		this.secondPerKm = secondPerKm;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getGpsString() {
		return gpsString;
	}
	public void setGpsString(String gpsString) {
		this.gpsString = gpsString;
	}
	public int getTargetType() {
		return targetType;
	}
	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}
	public int getHowToMove() {
		return howToMove;
	}
	public void setHowToMove(int howToMove) {
		this.howToMove = howToMove;
	}
	public int getRunway() {
		return runway;
	}
	public void setRunway(int runway) {
		this.runway = runway;
	}
	public String getServerImagePathsSmall() {
		return serverImagePathsSmall;
	}
	public void setServerImagePathsSmall(String serverImagePathsSmall) {
		this.serverImagePathsSmall = serverImagePathsSmall;
	}
	public String getServerImagePaths() {
		return serverImagePaths;
	}
	public void setServerImagePaths(String serverImagePaths) {
		this.serverImagePaths = serverImagePaths;
	}
	public String getServerBinaryFilePath() {
		return serverBinaryFilePath;
	}
	public void setServerBinaryFilePath(String serverBinaryFilePath) {
		this.serverBinaryFilePath = serverBinaryFilePath;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public int getTemp() {
		return temp;
	}
	public void setTemp(int temp) {
		this.temp = temp;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getDbVersion() {
		return dbVersion;
	}
	public void setDbVersion(int dbVersion) {
		this.dbVersion = dbVersion;
	}
	public int getWeather() {
		return weather;
	}
	public void setWeather(int weather) {
		this.weather = weather;
	}
	public int getTargetValue() {
		return targetValue;
	}
	public void setTargetValue(int targetValue) {
		this.targetValue = targetValue;
	}
	public long getGenerateTime() {
		return generateTime;
	}
	public void setGenerateTime(long generateTime) {
		this.generateTime = generateTime;
	}

}
