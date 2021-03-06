package net.yaopao.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//参考：http://blog.csdn.net/liuhe688/article/details/6715983

public class DatabaseHelper extends SQLiteOpenHelper// 继承OrmLiteSqliteOpenHelper类
{

	// 数据库版本号
	public static final int DATABASE_VERSION = 2;
	// 数据库名
	private static final String DATABASE_NAME = "YaoPao.db";

	// 数据表名，一个数据库中可以有多个表（虽然本例中只建立了一个表）
	public static final String SPORTDATA_TABLE = "SportDataTable";
	public static final String SPORTPARAM_TABLE = "SportParamTable";

	// public static final String USERINFO_TABLE = "UserInfoTable";

	// 构造函数，调用父类SQLiteOpenHelper的构造函数
/*	@SuppressLint("NewApi")
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);

	}
*/
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// SQLiteOpenHelper的构造函数参数：
		// context：上下文环境
		// name：数据库名字
		// factory：游标工厂（可选）
		// version：数据库模型版本号
	}

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		// 数据库实际被创建是在getWritableDatabase()或getReadableDatabase()方法调用时
		Log.d("wydb", "DatabaseHelper Constructor");
		// CursorFactory设置为null,使用系统默认的工厂类
	}

	// 继承SQLiteOpenHelper类,必须要覆写的三个方法：onCreate(),onUpgrade(),onOpen()
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 调用时间：数据库第一次创建时onCreate()方法会被调用

		// onCreate方法有一个 SQLiteDatabase对象作为参数，根据需要对这个对象填充表和初始化数据
		// 这个方法中主要完成创建数据库后对数据库的操作

		Log.d("wydb", "DatabaseHelper onCreate");

		// 构建创建表的SQL语句（可以从SQLite Expert工具的DDL粘贴过来加进StringBuffer中）

		// 执行创建表的SQL语句
		db.execSQL(getSportDataTableSql());
		db.execSQL(getSportParamTableSql());
//		db.execSQL("CREATE UNIQUE INDEX uid_index ON " + SPORTPARAM_TABLE
//				+ " (uid);");

		// 即便程序修改重新运行，只要数据库已经创建过，就不会再进入这个onCreate方法
		Log.d("wydb", "DatabaseHelper onCreate end");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 调用时间：如果DATABASE_VERSION值被改为别的数,系统发现现有数据库版本不同,即会调用onUpgrade

		// onUpgrade方法的三个参数，一个 SQLiteDatabase对象，一个旧的版本号和一个新的版本号
		// 这样就可以把一个数据库从旧的模型转变到新的模型
		// 这个方法中主要完成更改数据库版本的操作

		Log.d("wydb", "DatabaseHelper onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS " + SPORTDATA_TABLE);
		onCreate(db);
		// 上述做法简单来说就是，通过检查常量值来决定如何，升级时删除旧表，然后调用onCreate来创建新表
		// 一般在实际项目中是不能这么做的，正确的做法是在更新数据表结构时，还要考虑用户存放于数据库中的数据不丢失

	}
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		// 每次打开数据库之后首先被执行

		Log.d("wydb", "DatabaseHelper onOpen");
	}

	public String getSportDataTableSql() {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + SPORTDATA_TABLE + "] (");
		sBuffer.append("  [id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
		sBuffer.append("  [averageHeart] INTEGER,  ");
		sBuffer.append("  [clientImagePathsSmall] TEXT,  ");
		sBuffer.append("  [clientImagePaths] TEXT,  ");
		sBuffer.append("  [clientBinaryFilePath] TEXT,  ");
		sBuffer.append("  [distance] INTEGER,  ");
		sBuffer.append("  [gpsCount] INTEGER,  ");
		sBuffer.append("  [heat] INTEGER,  ");
		sBuffer.append("  [isMatch] INTEGER,  ");
		sBuffer.append("  [jsonParam] TEXT,  ");
		sBuffer.append("  [kmCount] INTEGER,  ");
		sBuffer.append("  [maxHeart] INTEGER,  ");
		sBuffer.append("  [mileCount] INTEGER,  ");
		sBuffer.append("  [minCount] INTEGER,  ");
		sBuffer.append("  [feeling] INTEGER,  ");
		sBuffer.append("  [secondPerKm] INTEGER,  ");
		sBuffer.append("  [remark] TEXT,  ");
		sBuffer.append("  [rid] TEXT,  ");
		sBuffer.append("  [targetType] INTEGER, ");
		sBuffer.append("  [gpsString] TEXT, ");
		sBuffer.append("  [howToMove] INTEGER, ");
		sBuffer.append("  [runway] INTEGER, ");
		sBuffer.append("  [serverImagePathsSmall] TEXT, ");
		sBuffer.append("  [score] INTEGER, ");
		sBuffer.append("  [serverImagePaths] TEXT, ");
		sBuffer.append("  [startTime] BIGINT, ");
		sBuffer.append("  [serverBinaryFilePath] TEXT, ");
		sBuffer.append("  [temp] INTEGER, ");
		sBuffer.append("  [uid] TEXT, ");
		sBuffer.append("  [updateTime] BIGINT, ");
		sBuffer.append("  [duration] BIGINT, ");
		sBuffer.append("  [dbVersion] INTEGER, ");
		sBuffer.append("  [weather] INTEGER, ");
		sBuffer.append("  [targetValue] INTEGER, ");
		sBuffer.append("  [generateTime] BIGINT); ");
		return sBuffer.toString();
	}

	public String getSportParamTableSql() {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE IF NOT EXISTS [" + SPORTPARAM_TABLE + "] (");
		sBuffer.append("  [id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
		sBuffer.append("  [uid] INTEGER NOT NULL,  ");
		sBuffer.append("  [countDown] INTEGER,  ");// 0 关，1开
		sBuffer.append("  [vioce] INTEGER,  ");// 0 关，1开
		sBuffer.append("  [targetdis] INTEGER,  ");//目标距离
		sBuffer.append("  [targettime] INTEGER,  ");//目标时间
		sBuffer.append("  [typeIndex] INTEGER,  ");
		sBuffer.append("  [targetIndex] INTEGER); ");
		Log.v("wydb", sBuffer.toString());
		return sBuffer.toString();
	}

//	@Override
//	public void onCreate(SQLiteDatabase db,ConnectionSource connectionSource) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db,ConnectionSource connectionSource, int oldVersion, int newVersion) {
//		// TODO Auto-generated method stub
//		
//	}
}