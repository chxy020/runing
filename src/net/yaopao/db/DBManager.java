package net.yaopao.db;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.yaopao.activity.SportRunMainActivity;
import net.yaopao.activity.YaoPao01App;
import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNGPSPoint4Match;
import net.yaopao.assist.Constants;
import net.yaopao.assist.GpsPoint_bak;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportBean;
import net.yaopao.bean.DataBean;
import net.yaopao.bean.SportParaBean;
import net.yaopao.engine.manager.binaryIO.BinaryIOManager;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;

public class DBManager {
	private DatabaseHelper helper;
	private SQLiteDatabase db;
	
	public DBManager(Context context) {
		Log.d("wydb", "DBManager --> Constructor");
		helper = new DatabaseHelper(context);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
		Log.v("wydb", "db=" + db.getPath());
	}

	/**
	 * 插入数据
	 * 
	 * @param sports
	 */
	@SuppressLint("NewApi")
	public int saveOneSport() {
		Log.d("wydb", "DBManager --> add");
	    DecimalFormat df=(DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);
//		Variables.hspeed =df.format((Variables.distance/(Variables.utime/1000))*3.6);
		// 采用事务处理，确保数据完整性
		db.beginTransaction(); // 开始事务
		try {
			long nowTime =new Date().getTime();
			db.execSQL(
					"INSERT INTO "
							+ DatabaseHelper.SPORTDATA_TABLE
							+ " (averageHeart,clientImagePathsSmall,clientImagePaths,clientBinaryFilePath,distance,gpsCount,"
							+ "heat,isMatch,jsonParam,kmCount,maxHeart,mileCount,feeling,secondPerKm,remark,rid,targetType,gpsString,howToMove,runway,"
							+ "serverImagePathsSmall,score,serverImagePaths,startTime,serverBinaryFilePath,temp,uid,updateTime,duration,dbVersion,weather,targetValue,"
							+ "generateTime)"
							+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
							+ ")",
					new Object[] { Variables.averageHeart, Variables.clientImagePathsSmall,
									Variables.clientImagePaths, Variables.clientBinaryFilePath,
									YaoPao01App.runManager.distance, YaoPao01App.runManager.GPSList.size(),
							Variables.heat,Variables.isMatch, Variables.jsonParam,
							YaoPao01App.runManager.dataKm.size(), Variables.maxHeart,
							YaoPao01App.runManager.dataMile.size(), YaoPao01App.runManager.getFeeling(), YaoPao01App.runManager.secondPerKm,
							YaoPao01App.runManager.getRemark(), Variables.getRid(), YaoPao01App.runManager.getTargetType(),
							Variables.gpsString, YaoPao01App.runManager.getHowToMove(),YaoPao01App.runManager.getRunway(),
							Variables.serverImagePathsSmall,YaoPao01App.runManager.score,Variables.serverImagePaths,YaoPao01App.runManager.GPSList.get(0).getTime(),
							Variables.serverBinaryFilePath,Variables.temp,Variables.uid,nowTime,YaoPao01App.runManager.during(),2,Variables.weather,
							YaoPao01App.runManager.getTargetValue(),nowTime});


			// 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
			// 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
			// 使用占位符有效区分了这种情况
			db.setTransactionSuccessful(); // 设置事务成功完成
			Log.v("wydb", "insert success");
			Cursor cursor = db.rawQuery("select last_insert_rowid() from "+ DatabaseHelper.SPORTDATA_TABLE, null);
			int strid=-1;
			if (cursor.moveToFirst()){
				strid = cursor.getInt(0);
			}
			return strid;
		} finally {
			db.endTransaction(); // 结束事务
		}
	}
	@SuppressLint("NewApi")
	public int saveOneSportMatch() {
		if(CNAppDelegate.match_totaldis < 1000){
			CNAppDelegate.match_score = 2;
	    }else{
	        int meter = (int)CNAppDelegate.match_totaldis % 1000;
	        if(meter > 500){
	        	CNAppDelegate.match_score += 4;
	        }
	    }
	    //计算点序列
	    StringBuffer pointString = new StringBuffer();
	    for(int i=0;i<CNAppDelegate.match_pointList.size();i++){
	        CNGPSPoint4Match point = CNAppDelegate.match_pointList.get(i);
	        if(i == CNAppDelegate.match_pointList.size()-1){
	        	pointString.append(String.format("%f %f", point.getLon(),point.getLat()));
	        }else{
	        	pointString.append(String.format("%f %f,", point.getLon(),point.getLat()));
	        }
	    }
	    
	    int speed_second = (int) (1000*(CNAppDelegate.match_historySecond/CNAppDelegate.match_totaldis));
	    
	    CNGPSPoint4Match firstPoint = CNAppDelegate.match_pointList.get(0);
	    long stamp = firstPoint.getTime();
		// 采用事务处理，确保数据完整性
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL(
					"INSERT INTO "
							+ DatabaseHelper.SPORTDATA_TABLE
							+ " (aheart,distance,rid,heat,hspeed,image_count,"
							+ "mheart,mind,pspeed,remarks,runtar,runty,runtra,runway,stamp,status_index,temp,utime,weather,points,"
							+ "sportty,sportpho,sport_pho_path,"
							+ "addtime)"
							+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
							+ ",?,?,?"
							+ ")",
					new Object[] { 0, CNAppDelegate.match_totaldis,
							Variables.getRid(), 0,
							0, 0,
							0, 0, speed_second,
							"", 1,
							2, pointString, 0,
							stamp, "", 0,
							CNAppDelegate.match_historySecond*1000, 0,CNAppDelegate.match_score,
							1,0,"",
							new Date().getTime() });


			// 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
			// 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
			// 使用占位符有效区分了这种情况
			db.setTransactionSuccessful(); // 设置事务成功完成
			Log.v("wydb", "insert success");
			Cursor cursor = db.rawQuery("select last_insert_rowid() from "+ DatabaseHelper.SPORTDATA_TABLE, null);
			int strid=-1;
			if (cursor.moveToFirst()){
				strid = cursor.getInt(0);
			}
			return strid;
		} finally {
			db.endTransaction(); // 结束事务
		}
	}
/**
 * 保存运动参数
 */
	public void saveSportParam() {
		Log.d("wydb", "DBManager --> add saveSportParam");
		// 采用事务处理，确保数据完整性
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL("REPLACE INTO " + DatabaseHelper.SPORTPARAM_TABLE
					+ " (uid,countDown,vioce,targetdis,targettime,typeIndex,targetIndex)"
					+ " VALUES (?,?,?,?,?,?,?)", new Object[] { Variables.uid,
					Variables.switchTime, Variables.switchVoice,Variables.runTargetDis,Variables.runTargetTime,
					Variables.runType, Variables.runTargetType });

			// 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
			// 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
			// 使用占位符有效区分了这种情况
			db.setTransactionSuccessful(); // 设置事务成功完成
			Log.v("wydb", "saveSportParam insert success");
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 更新数据
	 * 未实现
	 * @param sport
	 */
/*	public void update(SportBean sport) {
		Log.d("wydb", "DBManager --> update");
		ContentValues cv = new ContentValues();
		cv.put("a", sport.getRemarks());
		db.update(DatabaseHelper.SPORTDATA_TABLE, cv, "a = ?",
				new String[] { sport.getRemarks() });
	}*/
	
	/**
	 * 更新心情
	 * @param sport
	 */
	public void updateRemark(int id, String remark) {
		ContentValues values = new ContentValues();  
		values.put("remark", remark);  
		values.put("updateTime", new Date().getTime());  
		String whereClause = "id=?";  
		String[] whereArgs = new String[] { id+"" };  
		db.update(DatabaseHelper.SPORTDATA_TABLE, values, whereClause, whereArgs);  
	}

	/**
	 * 删除一条记录
	 * 
	 * @param sport
	 */
	public void delete(int id) {
		Log.d("wydb", "DBManager --> deleteOldPerson");
		String whereClause = "id=?"; 
		String[] whereArgs = new String[] { id+"" };  
		db.delete(DatabaseHelper.SPORTDATA_TABLE, whereClause,	whereArgs);
	}

	/**
	 * 运动记录分页查寻
	 * 
	 * @return
	 */
	public List<SportBean> query(int page) {
		Log.d("wydb", "DBManager --> query");
		ArrayList<SportBean> sports = new ArrayList<SportBean>();
		Cursor c = queryTheCursorForPage(page);
		while (c.moveToNext()) {

			SportBean sport = new SportBean();
			sport.setId(c.getInt(c.getColumnIndex("id")));
			sport.setRid(c.getString(c.getColumnIndex("rid")));
			sport.setTargetType(c.getInt(c.getColumnIndex("targetType")));
			sport.setHowToMove(c.getInt(c.getColumnIndex("howToMove")));
			sport.setFeeling(c.getInt(c.getColumnIndex("feeling")));
			sport.setRunway(c.getInt(c.getColumnIndex("runway")));
			sport.setGenerateTime(c.getLong(c.getColumnIndex("generateTime")));
			sport.setDistance(c.getInt(c.getColumnIndex("distance")));
			sport.setSecondPerKm(c.getInt(c.getColumnIndex("secondPerKm")));
			sport.setDuration(c.getInt(c.getColumnIndex("duration")));
			sport.setIsMatch(c.getInt(c.getColumnIndex("isMatch")));
//			sport.setSportpho(c.getInt(c.getColumnIndex("sportpho")));
			sport.setClientImagePaths(c.getString(c.getColumnIndex("clientImagePaths")));
			sport.setClientImagePathsSmall(c.getString(c.getColumnIndex("clientImagePathsSmall")));
			sports.add(sport);
//			YaoPao01App.lts.writeFileToSD(
//					"db list : id=" + c.getColumnIndex("id") + " rid="
//							+ c.getColumnIndex("rid")
//
//							+ " runtar=" + c.getColumnIndex("runtar")
//							+ " runty=" + c.getColumnIndex("runty"),
//					"uploadLocation");
		}
		c.close();
		return sports;
	}

	/**
	 * 查询总体记录数据
	 * 
	 * @return
	 */
	public DataBean queryData() {
		DataBean data = new DataBean();
		Cursor c = queryTheCursor();
		int count = c.getCount();
		Log.v("wysport", "db count="+count);
		int totalDistance = 0;
		int speed = 0;// 平均配速 单位秒
		long totalTime = 0;// 毫秒
		int points = 0;// 秒
		while (c.moveToNext()) {
			totalDistance += c.getDouble(c.getColumnIndex("distance"));
			totalTime += c.getLong(c.getColumnIndex("duration"));
			points += c.getInt(c.getColumnIndex("score"));
		}
		c.close();
//		speed = (int) (totalTime/totalDistance);
		speed = (int) Math.round(totalTime/totalDistance);
		data.setCount(count);
		data.setDistance(totalDistance);
		data.setPspeed(speed);
		data.setTotalTime(totalTime);
		data.setPoints(points);
		return data;
	}
	
//	/**
//	 * 查询总体记录数
//	 * 
//	 * @return
//	 */
//	public int queryDataCountAndToalScore() {
//		Cursor c = queryTheCursor();
//		int count = c.getCount();
//		c.close();
//		return count;
//	}

	/**
	 * 查询一条记录
	 * 
	 * @param id
	 * @return
	 */
	public SportBean queryForOne(int id) {
		Log.d("wydb", "DBManager --> query");
		ArrayList<SportBean> sports = new ArrayList<SportBean>();
		Cursor c = db.rawQuery("SELECT * FROM "
				+ DatabaseHelper.SPORTDATA_TABLE + " WHERE id =" + id, null);
//		YaoPao01App.lts.writeFileToSD("db : " + c, "uploadLocation");
		SportBean sport = new SportBean();
		while (c.moveToNext()) {
			sport.setId(c.getInt(c.getColumnIndex("id")));
			sport.setRid(c.getString(c.getColumnIndex("rid")));
			sport.setTargetType(c.getInt(c.getColumnIndex("targetType")));
			sport.setHowToMove(c.getInt(c.getColumnIndex("howToMove")));
			sport.setFeeling(c.getInt(c.getColumnIndex("feeling")));
			sport.setRunway(c.getInt(c.getColumnIndex("runway")));
			sport.setGenerateTime(c.getLong(c.getColumnIndex("generateTime")));
			sport.setDistance(c.getInt(c.getColumnIndex("distance")));
			sport.setSecondPerKm(c.getInt(c.getColumnIndex("secondPerKm")));
			sport.setDuration(c.getInt(c.getColumnIndex("duration")));
			sport.setRemark(c.getString(c.getColumnIndex("remark")));
			sport.setIsMatch(c.getInt(c.getColumnIndex("isMatch")));
			sport.setClientImagePaths(c.getString(c.getColumnIndex("clientImagePaths")));
			sport.setClientImagePathsSmall(c.getString(c.getColumnIndex("clientImagePathsSmall")));
			sport.setClientBinaryFilePath(c.getString(c.getColumnIndex("clientBinaryFilePath")));
			sport.setScore(c.getInt(c.getColumnIndex("score")));
//			sport.setRuntra(c.getString(c.getColumnIndex("runtra")));
//			sport.setStatusIndex(c.getString(c.getColumnIndex("status_index")));
			
			
			sports.add(sport);
		}
		c.close();
		return sport;
	}
	/**
	 * 查询用户的运动参数
	 * 
	 * @param id
	 * @return
	 */
	public SportParaBean querySportParam(int uid) {
		ArrayList<SportBean> sports = new ArrayList<SportBean>();
		Cursor c = db.rawQuery("SELECT * FROM "+ DatabaseHelper.SPORTPARAM_TABLE + " WHERE uid =" + uid, null);
		SportParaBean sport = new SportParaBean();
		while (c.moveToNext()) {
			sport.setId(c.getInt(c.getColumnIndex("id")));
			sport.setUid(c.getInt(c.getColumnIndex("uid")));
			sport.setCountDown(c.getInt(c.getColumnIndex("countDown")));
			sport.setVioce(c.getInt(c.getColumnIndex("vioce")));
			sport.setTargetdis(c.getInt(c.getColumnIndex("targetdis")));
			sport.setTargettime(c.getInt(c.getColumnIndex("targettime")));
			sport.setTypeIndex(c.getInt(c.getColumnIndex("typeIndex")));
			sport.setTargetIndex(c.getInt(c.getColumnIndex("targetIndex")));
		}
		c.close();
		return sport;
	}

	/**
	 * query all , return cursor
	 * 
	 * @return Cursor
	 */
	public Cursor queryTheCursor() {
		Log.d("wydb", "DBManager --> queryTheCursor");
		Cursor c = db.rawQuery("SELECT * FROM "
				+ DatabaseHelper.SPORTDATA_TABLE + " ORDER BY id DESC", null);
		return c;
	}
	/**
	 * 运动记录分页查询
	 * @param page
	 * @return
	 */
	public Cursor queryTheCursorForPage(int page) {
		Cursor c = db.rawQuery("SELECT * FROM "
				+ DatabaseHelper.SPORTDATA_TABLE + "  ORDER BY id DESC  limit "+Constants.offset+" offset "+Constants.offset*(page-1), null);
		return c;
	}

	/**
	 * close database
	 */
	public void closeDB() {
		Log.d("wydb", "DBManager --> closeDB");
		// 释放数据库资源
		db.close();
	}

}