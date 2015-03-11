package net.yaopao.db;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import net.yaopao.activity.YaoPao01App;
import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNGPSPoint4Match;
import net.yaopao.assist.Constants;
import net.yaopao.assist.Variables;
import net.yaopao.bean.DataBean;
import net.yaopao.bean.SportBean;
import net.yaopao.bean.SportParaBean;
import net.yaopao.engine.manager.CNCloudRecord;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

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
			long nowTime =0;
			//判断如果同步过时间，要加上与服务器的时间差值，如果未同步过时间
			if (YaoPao01App.cloudManager.isSynServerTime) {
				nowTime =new Date().getTime() +YaoPao01App.cloudManager.deltaMiliSecond;
			}
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
							Variables.serverBinaryFilePath,Variables.temp,Variables.uid,nowTime,YaoPao01App.runManager.during(),DatabaseHelper.DATABASE_VERSION,Variables.weather,
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
	/**
	 * 更新下载的记录
	 * 
	 * @param records
	 */
	public void updateDownoadRecords(List<SportBean> addRecords,List<SportBean> updateRecords){
		
		String sql ="INSERT INTO "
				+ DatabaseHelper.SPORTDATA_TABLE
				+ " (averageHeart,clientImagePathsSmall,clientImagePaths,clientBinaryFilePath,distance,gpsCount,"
				+ "heat,isMatch,jsonParam,kmCount,maxHeart,mileCount,feeling,secondPerKm,remark,rid,targetType,gpsString,howToMove,runway,"
				+ "serverImagePathsSmall,score,serverImagePaths,startTime,serverBinaryFilePath,temp,uid,updateTime,duration,dbVersion,weather,targetValue,"
				+ "generateTime)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
				+ ")";
		String uSql ="UPDATE "+DatabaseHelper.SPORTDATA_TABLE+" SET  remark =?,updateTime=? WHERE rid=?";
		SQLiteStatement stat = db.compileStatement(sql);
		db.beginTransaction();
		for (int i = 0; i < addRecords.size(); i++) {
			SportBean data = addRecords.get(i);
			stat.bindLong(1, data.getAverageHeart());
			stat.bindString(2, data.getClientImagePathsSmall());
			stat.bindString(3, data.getClientImagePaths());
			stat.bindString(4, data.getClientBinaryFilePath());
			stat.bindLong(5, data.getDistance());
			stat.bindLong(6, data.getGpsCount());
			stat.bindLong(7, data.getHeat());
			stat.bindLong(8, data.getIsMatch());
			stat.bindString(9, data.getJsonParam());
			stat.bindLong(10, data.getKmCount());
			stat.bindLong(11, data.getMaxHeart());
			stat.bindLong(12, data.getMileCount());
			stat.bindLong(13, data.getFeeling());
			stat.bindLong(14, data.getSecondPerKm());
			stat.bindString(15, data.getRemark());
			stat.bindString(16, data.getRid());
			stat.bindLong(17, data.getTargetType());
			stat.bindString(18, data.getGpsString());
			stat.bindLong(19, data.getHowToMove());
			stat.bindLong(20, data.getRunway());
			stat.bindString(21, data.getServerImagePathsSmall());
			stat.bindLong(22, data.getScore());
			stat.bindString(23, data.getServerImagePaths());
			stat.bindLong(24, data.getStartTime());
			stat.bindString(25, data.getServerBinaryFilePath());
			stat.bindLong(26, data.getTemp());
			stat.bindLong(27, data.getUid());
			stat.bindLong(28, data.getUpdateTime());
			stat.bindLong(29, data.getDuration());
			stat.bindLong(30, DatabaseHelper.DATABASE_VERSION);
			stat.bindLong(31, data.getWeather());
			stat.bindLong(32, data.getTargetValue());
			stat.bindLong(33, data.getGenerateTime());
			stat.executeInsert();
		}
		
		 stat = db.compileStatement(uSql);
		for (int i = 0; i < updateRecords.size(); i++) {
			SportBean udata = updateRecords.get(i);
			stat.bindString(1, udata.getRemark());
			stat.bindLong(2, udata.getUpdateTime());
			stat.bindString(3, udata.getRid());
			int update = stat.executeUpdateDelete();
			Log.v("zc", "update ="+update);
		}
		
		db.setTransactionSuccessful();
		db.endTransaction();
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
		//如果同步过时间，要加上时间差值，否则时间保存0
		long nowTime = 0;
		if (YaoPao01App.cloudManager.isSynServerTime) {
			nowTime =new Date().getTime() +YaoPao01App.cloudManager.deltaMiliSecond;
		}
				
		ContentValues values = new ContentValues();  
		values.put("remark", remark);  
		values.put("updateTime", nowTime);  
		String whereClause = "id=?";  
		String[] whereArgs = new String[] { id+"" };  
		db.update(DatabaseHelper.SPORTDATA_TABLE, values, whereClause, whereArgs);  
	}

	/**
	 * 删除一条记录
	 * 
	 * @param sport
	 */
	public boolean delete(int id) {
		Log.d("wydb", "DBManager --> deleteOldPerson");
		String whereClause = "id=?"; 
		String[] whereArgs = new String[] { id+"" };  
		return db.delete(DatabaseHelper.SPORTDATA_TABLE, whereClause,	whereArgs)>0;
	}
	/**
	 * 删除一条记录
	 * 
	 * @param sport
	 */
	public boolean deleteByRid(String rid) {
		Log.d("wydb", "DBManager --> deleteOldPerson");
		String whereClause = "rid=?"; 
		String[] whereArgs = new String[] { rid+"" };  
		return db.delete(DatabaseHelper.SPORTDATA_TABLE, whereClause,	whereArgs)>0;
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
			sport.setClientImagePaths(c.getString(c.getColumnIndex("clientImagePaths"))==null?"":c.getString(c.getColumnIndex("clientImagePaths")));
			sport.setClientImagePathsSmall(c.getString(c.getColumnIndex("clientImagePathsSmall"))==null?"":c.getString(c.getColumnIndex("clientImagePathsSmall")));
			sport.setClientBinaryFilePath(c.getString(c.getColumnIndex("clientBinaryFilePath"))==null?"":c.getString(c.getColumnIndex("clientBinaryFilePath")));
			sports.add(sport);
		}
		c.close();
		return sports;
	}
	/**
	 * 查询需要更新的记录
	 * @param synTime
//	 * @param action 1-查询本地更新到服务器 ，2-查询本地需要从服务器更新的
	 * @param action 1-新增记录 ，2-更新记录
	 * @return
	 */
	 
	 
	public List<SportBean> queryForPullOrPush(Long synTime,int action ) {
		Log.d("wydb", "DBManager --> query");
		ArrayList<SportBean> sports = new ArrayList<SportBean>();
		Cursor c = queryTheCursorForUpdate(synTime,action);
		while (c.moveToNext()) {
			SportBean sport = new SportBean();
			sport.setId(c.getInt(c.getColumnIndex("id")));
			sport.setUid(c.getInt(c.getColumnIndex("uid")));
			sport.setRid(c.getString(c.getColumnIndex("rid")));
			sport.setTargetType(c.getInt(c.getColumnIndex("targetType")));
			sport.setHowToMove(c.getInt(c.getColumnIndex("howToMove")));
			sport.setFeeling(c.getInt(c.getColumnIndex("feeling")));
			sport.setRunway(c.getInt(c.getColumnIndex("runway")));
			sport.setGenerateTime(c.getLong(c.getColumnIndex("generateTime")));
			sport.setUpdateTime(c.getLong(c.getColumnIndex("updateTime")));
			sport.setStartTime(c.getLong(c.getColumnIndex("startTime")));
			sport.setDistance(c.getInt(c.getColumnIndex("distance")));
			sport.setSecondPerKm(c.getInt(c.getColumnIndex("secondPerKm")));
			sport.setDuration(c.getInt(c.getColumnIndex("duration")));
			sport.setIsMatch(c.getInt(c.getColumnIndex("isMatch")));
			sport.setClientImagePaths(c.getString(c.getColumnIndex("clientImagePaths"))==null?"":c.getString(c.getColumnIndex("clientImagePaths")));
			sport.setClientImagePathsSmall(c.getString(c.getColumnIndex("clientImagePathsSmall"))==null?"":c.getString(c.getColumnIndex("clientImagePathsSmall")));
			sport.setClientBinaryFilePath(c.getString(c.getColumnIndex("clientBinaryFilePath"))==null?"":c.getString(c.getColumnIndex("clientBinaryFilePath")));
			sport.setServerImagePathsSmall(c.getString(c.getColumnIndex("serverImagePathsSmall"))==null?"":c.getString(c.getColumnIndex("serverImagePathsSmall")));
			sport.setServerImagePaths(c.getString(c.getColumnIndex("serverImagePaths"))==null?"":c.getString(c.getColumnIndex("serverImagePaths")));
			sport.setServerBinaryFilePath(c.getString(c.getColumnIndex("serverBinaryFilePath"))==null?"":c.getString(c.getColumnIndex("serverBinaryFilePath")));
			sport.setJsonParam(c.getString(c.getColumnIndex("jsonParam")));
			sport.setRemark(c.getString(c.getColumnIndex("remark")));
			sport.setGpsString(c.getString(c.getColumnIndex("gpsString")));
			sport.setAverageHeart(c.getInt(c.getColumnIndex("averageHeart")));
			sport.setMaxHeart(c.getInt(c.getColumnIndex("maxHeart")));
			sport.setHeat(c.getInt(c.getColumnIndex("heat")));
			sport.setKmCount(c.getInt(c.getColumnIndex("kmCount")));
			sport.setGpsCount(c.getInt(c.getColumnIndex("gpsCount")));
			sport.setMinCount(c.getInt(c.getColumnIndex("minCount")));
			sport.setMileCount(c.getInt(c.getColumnIndex("mileCount")));
			sport.setScore(c.getInt(c.getColumnIndex("score")));
			sport.setTemp(c.getInt(c.getColumnIndex("temp")));
			sport.setDbVersion(c.getInt(c.getColumnIndex("dbVersion")));
			sport.setWeather(c.getInt(c.getColumnIndex("weather")));
			sport.setTargetValue(c.getInt(c.getColumnIndex("targetValue")));
			sports.add(sport);
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
			sport.setClientImagePaths(c.getString(c.getColumnIndex("clientImagePaths"))==null?"":c.getString(c.getColumnIndex("clientImagePaths")));
			sport.setClientImagePathsSmall(c.getString(c.getColumnIndex("clientImagePathsSmall"))==null?"":c.getString(c.getColumnIndex("clientImagePathsSmall")));
			sport.setClientBinaryFilePath(c.getString(c.getColumnIndex("clientBinaryFilePath"))==null?"":c.getString(c.getColumnIndex("clientBinaryFilePath")));
			sport.setScore(c.getInt(c.getColumnIndex("score")));
		}
		c.close();
		return sport;
	}
	
	/**
	 * 查询一条记录
	 * 
	 * @param rid
	 * @return
	 */
	public SportBean queryForOne(String rid) {
		Log.d("wydb", "DBManager --> query");
		Cursor c = db.rawQuery("SELECT * FROM "
				+ DatabaseHelper.SPORTDATA_TABLE + " WHERE rid ='" + rid+"'", null);
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
			sport.setClientImagePaths(c.getString(c.getColumnIndex("clientImagePaths"))==null?"":c.getString(c.getColumnIndex("clientImagePaths")));
			sport.setClientImagePathsSmall(c.getString(c.getColumnIndex("clientImagePathsSmall"))==null?"":c.getString(c.getColumnIndex("clientImagePathsSmall")));
			sport.setClientBinaryFilePath(c.getString(c.getColumnIndex("clientBinaryFilePath"))==null?"":c.getString(c.getColumnIndex("clientBinaryFilePath")));
			sport.setScore(c.getInt(c.getColumnIndex("score")));
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
	 * 保存已同步至服务器的记录
	 */
	@SuppressLint("NewApi")
	public void updatePushedData(List<SportBean> dataList){
		String sql ="UPDATE "+ DatabaseHelper.SPORTDATA_TABLE+" "
				+ "SET updateTime =?,serverImagePaths=?,serverImagePathsSmall=?,serverBinaryFilePath=? where rid=?";
		SQLiteStatement stat = db.compileStatement(sql);
		db.beginTransaction();
		for (int i = 0; i < dataList.size(); i++) {
			SportBean sport = dataList.get(i);
			stat.bindLong(1, sport.getUpdateTime());
			stat.bindString(2, sport.getServerImagePaths()==null?"":sport.getServerImagePaths());
			stat.bindString(3, sport.getServerImagePathsSmall()==null?"":sport.getServerImagePathsSmall());
			stat.bindString(4, sport.getServerBinaryFilePath()==null?"":sport.getServerBinaryFilePath());
			stat.bindString(5, sport.getRid());
			stat.executeUpdateDelete();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
/**
 * 更新数据库中现有记录，如果是未登录用户的数据，修改为此用户数据，如果是其他用户数据，删除，最后统计出修改完到数据库中的总体记录
 * @param uid
 */
	@SuppressLint("NewApi")
	public int[] updateReccordToCurrentUser(int uid){
		//第一步，查询出要删除的数据，统计总数，用作更新统计数据
		String ssql = "select distance,secondPerKm,duration,score from "+ DatabaseHelper.SPORTDATA_TABLE +" WHERE  uid <> 0 AND uid <> "+uid; 
		Cursor sc = db.rawQuery(ssql, null);
		int distance =0;
		int duration=0;
		int secondPerKm=0;
		int score =0;
		int count =0;
		
		while (sc.moveToNext()) {
			distance+=sc.getInt(sc.getColumnIndex("distance"));
			duration+=sc.getInt(sc.getColumnIndex("duration"));
			secondPerKm+=sc.getInt(sc.getColumnIndex("secondPerKm"));
			score+=sc.getInt(sc.getColumnIndex("score"));
			count+=1;
		}
		
		int[] resData = new int[]{distance,duration,score,secondPerKm,count};
		sc.close();
		
		
		//第二步，删除uid!=0&&uidDB!=uid的记录
		db.beginTransaction();
				String dsql = "DELETE FROM "+ DatabaseHelper.SPORTDATA_TABLE +" WHERE  uid <> 0 AND uid <> "+uid;
				SQLiteStatement dstat = db.compileStatement(dsql);
				dstat.executeUpdateDelete();
				Log.v("zc","本地记录的其他用户记录删除完毕 " );
				
				
		// 第三步，更新数据库中uid=0的记录
		Cursor c = db.rawQuery("select id,rid from "+DatabaseHelper.SPORTDATA_TABLE+" WHERE uid=0", null);
		String sql ="UPDATE "+ DatabaseHelper.SPORTDATA_TABLE+" SET uid=?,rid=?,updateTime=?,"
				+ "generateTime=? where id =?";
		SQLiteStatement stat = db.compileStatement(sql);
		Log.v("zc","开始更新 uid=0的记录 " );
		while (c.moveToNext()) {
			int  id = c.getInt(c.getColumnIndex("id"));
			String ridDB = c.getString(c.getColumnIndex("rid"));
			String ridUser = uid+"_"+ridDB.split("_")[1];
			Log.v("zc","ridDB ="+ridDB +", ridUser="+ridUser );
			stat.bindLong(1, uid);
			stat.bindString(2, ridUser);
			Long time = new Date().getTime();
			stat.bindLong(3, time);
			stat.bindLong(4, time);
			stat.bindLong(5,id);	
			stat.executeUpdateDelete();
		}
		Log.v("zc","记录过滤完毕 " );
		
		
		db.setTransactionSuccessful();
		db.endTransaction();
		return resData;
	}
	
	/**
	 * 查询本地数据库中所有的rid
	 * @return
	 */
	public Map<String,Object>  queryRids() {
		Cursor c = db.rawQuery("SELECT rid FROM "+ DatabaseHelper.SPORTDATA_TABLE , null);
		Map<String,Object> resMap = new HashMap<String, Object>();
		while (c.moveToNext()) {
			resMap.put(c.getString(c.getColumnIndex("rid")),null);
		}
		c.close();
		return resMap;
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
//				+ DatabaseHelper.SPORTDATA_TABLE + "  ORDER BY id DESC  limit "+Constants.offset+" offset "+Constants.offset*(page-1), null);
				+ DatabaseHelper.SPORTDATA_TABLE + "  ORDER BY generateTime DESC  limit "+Constants.offset+" offset "+Constants.offset*(page-1), null);
		return c;
	}
	
	/**
	 * 查询需要更新的记录
	 * @param page
	 * @return
	 */
	public Cursor queryTheCursorForUpdate(Long synTime,int action ) {
		StringBuffer sql = new StringBuffer("SELECT * FROM "	+ DatabaseHelper.SPORTDATA_TABLE + " WHERE generateTime ");
		if (action==1) {
			sql.append(" > "+synTime +" OR generateTime =0 ");
		}else {
			sql.append(" < "+synTime +" AND (updateTime >" + synTime+" OR updateTime=0) ");
		}
		sql.append("  ORDER BY id DESC");
//		Cursor c = db.rawQuery("SELECT * FROM "	+ DatabaseHelper.SPORTDATA_TABLE + " WHERE generateTime> "+synTime+"  ORDER BY id DESC ", null);
		Cursor c = db.rawQuery(sql.toString(), null);
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