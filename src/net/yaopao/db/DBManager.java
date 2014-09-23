package net.yaopao.db;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.yaopao.activity.SportRecordActivity;
import net.yaopao.activity.YaoPao01App;
import net.yaopao.assist.Constants;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportBean;
import net.yaopao.bean.DataBean;
import net.yaopao.bean.SportParaBean;
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
	public void saveOneSport() {
		Log.d("wydb", "DBManager --> add");
		//YaoPao01App.lts.writeFileToSD("db 还算之前数组: " +SportRecordActivity.points, "uploadLocation");
		GpsPoint befor =null;
		for (int i = 0; i < SportRecordActivity.points.size(); i++) {
			if (i==0) {
				befor=SportRecordActivity.points.get(0);
				continue;
			}
			GpsPoint temp = SportRecordActivity.points.get(i);
			GpsPoint curr = new GpsPoint(temp.lon,temp.lat,temp.status,temp.time,temp.speed,temp.course,temp.altitude);
			curr.setLat(curr.lat-befor.lat);
			curr.setLon(curr.lon-befor.lon);
			curr.setTime(curr.time-befor.time);
			befor=SportRecordActivity.points.get(i);
			SportRecordActivity.points.set(i, curr);
			
		}
		//YaoPao01App.lts.writeFileToSD("db 还算之后数组: " +SportRecordActivity.points, "uploadLocation");
		Log.d("wysport", "DBManager SportRecordActivity.points="+SportRecordActivity.points);
		String oneSport = JSON.toJSONString(SportRecordActivity.points, true);
		String statusIndex = "";
	    DecimalFormat df=(DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);
		Variables.hspeed =df.format((Variables.distance/Variables.utime)*3.6);
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
					new Object[] { Variables.aheart, Variables.distance,
							Variables.getRid(), Variables.heat,
							Variables.hspeed, Variables.imageCount,
							Variables.mheart, Variables.mind, Variables.pspeed,
							Variables.remarks, Variables.runtar,
							Variables.runty, oneSport, Variables.runway,
							Variables.stamp, statusIndex, Variables.temp,
							Variables.utime, Variables.weather,Variables.points,
							Variables.sportty,Variables.hassportpho,Variables.sport_pho,
							new Date().getTime() });


			// 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
			// 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
			// 使用占位符有效区分了这种情况
			db.setTransactionSuccessful(); // 设置事务成功完成
			Log.v("wydb", "insert success");
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
					Variables.switchTime, Variables.switchVoice,Variables.runtarDis,Variables.runtarTime,
					Variables.runty, Variables.runtar });

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
	public void update(SportBean sport) {
		Log.d("wydb", "DBManager --> update");
		ContentValues cv = new ContentValues();
		cv.put("a", sport.getRemarks());
		db.update(DatabaseHelper.SPORTDATA_TABLE, cv, "a = ?",
				new String[] { sport.getRemarks() });
	}

	/**
	 * 删除
	 * 
	 * @param sport
	 */
	public void delete(SportBean sport) {
		Log.d("wydb", "DBManager --> deleteOldPerson");
		db.delete(DatabaseHelper.SPORTDATA_TABLE, "a >= ?",
				new String[] { String.valueOf(sport.getRemarks()) });
	}

	/**
	 * 查询全部
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
			sport.setRuntar(c.getInt(c.getColumnIndex("runtar")));
			sport.setRunty(c.getInt(c.getColumnIndex("runty")));
			sport.setMind(c.getInt(c.getColumnIndex("mind")));
			sport.setRunway(c.getInt(c.getColumnIndex("runway")));
			sport.setAddtime(c.getLong(c.getColumnIndex("addtime")));
			sport.setDistance(c.getDouble(c.getColumnIndex("distance")));
			sport.setPspeed(c.getInt(c.getColumnIndex("pspeed")));
			sport.setUtime(c.getInt(c.getColumnIndex("utime")));
			sport.setSportty(c.getInt(c.getColumnIndex("sportty")));
			sport.setSportpho(c.getInt(c.getColumnIndex("sportpho")));
			sport.setSportPhoPath(c.getString(c.getColumnIndex("sport_pho_path")));
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
		double totalDistance = 0;
		int speed = 0;// 平均配速 单位秒
		long totalTime = 0;// 秒
		int points = 0;// 秒
		while (c.moveToNext()) {
			totalDistance += c.getDouble(c.getColumnIndex("distance"));
			totalTime += c.getLong(c.getColumnIndex("utime"));
			points += c.getInt(c.getColumnIndex("points"));
		}
		c.close();
		speed = (int) ((1000 / totalDistance) * totalTime);
		data.setCount(count);
		data.setDistance(totalDistance);
		data.setPspeed(speed);
		data.setTotalTime(totalTime);
		data.setPoints(points);
		return data;
	}

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
			sport.setRuntar(c.getInt(c.getColumnIndex("runtar")));
			sport.setRunty(c.getInt(c.getColumnIndex("runty")));
			sport.setMind(c.getInt(c.getColumnIndex("mind")));
			sport.setRunway(c.getInt(c.getColumnIndex("runway")));
			sport.setAddtime(c.getLong(c.getColumnIndex("addtime")));
			sport.setDistance(c.getDouble(c.getColumnIndex("distance")));
			sport.setPspeed(c.getInt(c.getColumnIndex("pspeed")));
			sport.setPoints(c.getInt(c.getColumnIndex("points")));
			sport.setRuntra(c.getString(c.getColumnIndex("runtra")));
			sport.setUtime(c.getInt(c.getColumnIndex("utime")));
			sport.setRemarks(c.getString(c.getColumnIndex("remarks")));
			sport.setStatusIndex(c.getString(c.getColumnIndex("status_index")));
			sport.setSportpho(c.getInt(c.getColumnIndex("sportpho")));
			sport.setSportPhoPath(c.getString(c.getColumnIndex("sport_pho_path")));
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