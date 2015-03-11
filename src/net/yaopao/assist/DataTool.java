package net.yaopao.assist;


import net.yaopao.activity.YaoPao01App;
import net.yaopao.bean.DataBean;
import android.content.SharedPreferences;

public class DataTool {


	/**
	 * 获取uid
	 * @return int
	 */
	public static int getUid() {
		return YaoPao01App.sharedPreferences.getInt("uid", 0);
	}
	/**
	 * 保存uid
	 * @param uid
	 */
	public static void setUid(int uid) {
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putInt("uid", uid);
		editor.commit();
	}
	
	/**
	 * 是否需要验证手机标志 0-未验证，1-已验证
	 * @return
	 */
	public static int getIsPhoneVerfied() {
		return YaoPao01App.sharedPreferences.getInt("isPhoneVerfied", 0);
	}
	/**
	 * 修改是否需要验证手机标志
	 * @param isPhoneVerfied   0-未验证，1-已验证
	 */
	public static void setIsPhoneVerfied(int isPhoneVerfied) {
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putInt("isPhoneVerfied", isPhoneVerfied);
		editor.commit();
	}
	/**
	 * 更新运动统计数据
	 * @param distance
	 * @param utime
	 * @param score
	 * @param secondPerKm
	 * @param count
	 * @return boolean
	 */
	public static boolean saveTotalData(int distance ,int utime,int score,int secondPerKm,int count){
		int totalDistance = Integer.parseInt(YaoPao01App.sharedPreferences.getString("totalDistance", "0"))+distance;
		int scount = YaoPao01App.sharedPreferences.getInt("recordCount", 0);
		if (scount<0) {
			scount=0;
		}
		int recordCount =scount+count;
		int totalScore = YaoPao01App.sharedPreferences.getInt("totalScore", 0)+score;
		Long totalTime = YaoPao01App.sharedPreferences.getLong("totalTime", 0)+utime;
		int totalSecondPerKm =0;
		if (recordCount!=0) {
			totalSecondPerKm = (YaoPao01App.sharedPreferences.getInt("totalSecondPerKm", 0)+secondPerKm);
		}
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putString("totalDistance", totalDistance+""); 
		editor.putInt("recordCount", recordCount);
		editor.putInt("totalScore", totalScore);
		editor.putLong("totalTime", totalTime);
		editor.putInt("totalSecondPerKm", totalSecondPerKm);
		return  editor.commit();
	}
	/**
	 * 获取运动统计数据 ，总距离，时间，平均配速，积分
	 * @return DataBean
	 */
	public static DataBean getTotalData(){
		int totalDistance = Integer.parseInt(YaoPao01App.sharedPreferences
				.getString("totalDistance", "0"));
		int recordCount = YaoPao01App.sharedPreferences
				.getInt("recordCount", 0);
		int totalScore = YaoPao01App.sharedPreferences.getInt("totalScore", 0);
		long totalTime=YaoPao01App.sharedPreferences.getLong("totalTime", 0);
		int totalSecondPerKm =0;
//		if (recordCount!=0) {
//			totalSecondPerKm = YaoPao01App.sharedPreferences.getInt("totalSecondPerKm", 0)/recordCount;
//		}
		if (totalDistance!=0) {
			totalSecondPerKm = (int) (totalTime/totalDistance);
		}
		
		DataBean data = new DataBean();

		data.setDistance(totalDistance);
		data.setCount(recordCount);
		data.setPoints(totalScore);
		data.setTotalTime(totalTime);
	     data.setPspeed(totalSecondPerKm);
		
		return data;
	}
	/**
	 * 删除指定条数记录
	 * @param distance 删除的距离
	 * @param utime 删除的用时
	 * @param score 删除的积分
	 * @param secondPerKm 删除的配速
	 * @param count 删除条数
	 * @return DataBean
	 */
	public static DataBean deleteOneSportRecord(int distance ,int utime,int score,int secondPerKm,int count){
		int totalDistance = Integer.parseInt(YaoPao01App.sharedPreferences.getString("totalDistance", "0")) - distance;
		int recordCount = YaoPao01App.sharedPreferences
				.getInt("recordCount", 0) - count;
		int totalScore = YaoPao01App.sharedPreferences.getInt("totalScore", 0)
				- score;
		Long totalTime = YaoPao01App.sharedPreferences.getLong("totalTime", 0)
				- utime;
		int totalSecondPerKm =YaoPao01App.sharedPreferences.getInt("totalSecondPerKm", 0) - secondPerKm;
		
		totalDistance = totalDistance<0?0:totalDistance;
		recordCount = recordCount<0?0:recordCount;
		totalScore = totalDistance<0?0:totalScore;
		totalTime = totalTime<0?0:totalTime;
		totalSecondPerKm = totalSecondPerKm<0?0:totalSecondPerKm;
		
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putString("totalDistance", totalDistance + "");
		editor.putInt("recordCount", recordCount);
		editor.putInt("totalScore", totalScore);
		editor.putInt("totalSecondPerKm", totalSecondPerKm);
		editor.putLong("totalTime", totalTime);

		DataBean data = new DataBean();

		data.setDistance(totalDistance);
		data.setCount(recordCount);
		data.setPoints(totalScore);
		data.setTotalTime(totalTime);
		
		if (recordCount!=0) {
			totalSecondPerKm = YaoPao01App.sharedPreferences.getInt("totalSecondPerKm", 0)/recordCount;
		}
		data.setPspeed(totalSecondPerKm);
		editor.commit();
		return data;
	}
	/**
	 * 将本地删除记录的uid更新到SharedPreferences
	 * @param rid
	 */
	public static void updateDleteArray(String rid){
		SharedPreferences.Editor editor = YaoPao01App.cloudDiary.edit();
		String deleteArray = YaoPao01App.cloudDiary.getString("deleteArray", "");
		if ("".equals(deleteArray)) {
			editor.putString("deleteArray",rid);
		}else{
			editor.putString("deleteArray",deleteArray+","+rid);
		}
		editor.commit();
	}
	/**
	 * 初始化sharedPreferences
	 */
	public static void initSharedPreferences(){
		SharedPreferences.Editor editor1 = YaoPao01App.sharedPreferences.edit();
		editor1.clear();
		editor1.commit();
		
		SharedPreferences.Editor editor2 =YaoPao01App.isInstall.edit();
		editor2.putInt("isInstall", 1);
		editor1.commit();
		
	}

}
