package net.yaopao.engine.manager.binaryIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.yaopao.activity.YaoPao01App;
import net.yaopao.engine.manager.GpsPoint;
import net.yaopao.engine.manager.OneKMInfo;
import net.yaopao.engine.manager.OneMileInfo;
import net.yaopao.engine.manager.OneMinuteInfo;
import net.yaopao.engine.manager.RunManager;
import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class BinaryIOManager {
	public static void writeBinary(String fileName,String dir) {
		int version = 1;
		int i = 0;
		int pointCount = YaoPao01App.runManager.GPSList.size();
		int kmCount = YaoPao01App.runManager.dataKm.size();
		int mileCount = YaoPao01App.runManager.dataMile.size();
		int minCount = YaoPao01App.runManager.dataMin.size();
		String saveRootPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		try {
			String filePath = saveRootPath + "/YaoPao/binary/"+dir + "/";
			File file = new File(filePath);
	        if (!file.exists())
	        {
	            Log.v("zc",""+file.mkdirs());
	        }
	        FileOutputStream fos = new FileOutputStream(filePath+ fileName + ".yaopao");
			final BufferedOutputStream byteTarget = new BufferedOutputStream(fos);
			final ByteOutput byteOutput = new StreamOutput(byteTarget);
			final BitOutput output = new BitOutput(byteOutput);
			output.writeUnsignedInt(8, version);// 版本号
			output.writeUnsignedInt(18, pointCount);// gps个数
			output.writeUnsignedInt(2, 1);// 指定坐标系（见二进制说明文档）
			output.writeUnsignedLong(42, YaoPao01App.runManager.GPSList.get(0)
					.getTime());// gps起始时间
			output.writeUnsignedInt(20, YaoPao01App.runManager.distance);// 总距离
			output.writeUnsignedInt(29, YaoPao01App.runManager.during());// 总距离
			output.writeUnsignedInt(20, 0);// 总步数
			output.writeUnsignedInt(17, 0);// 总卡路里
			output.writeUnsignedInt(18, (int)(YaoPao01App.runManager.altitudeAdd*10+0.5));// 总高程增加值
			output.writeUnsignedInt(18, (int)(YaoPao01App.runManager.altitudeReduce*10+0.5));// 总高程减少值
			output.writeUnsignedInt(10, kmCount);// km数
			output.writeUnsignedInt(10, mileCount);// mile数
			output.writeUnsignedInt(14, minCount);// 分钟数
			output.writeUnsignedInt(6, 0);// 预留
			for (i = 0; i < pointCount; i++) {
				GpsPoint point = YaoPao01App.runManager.GPSList.get(i);
				output.writeUnsignedInt(29,
						(int) ((point.getLon() + 180) * 1000000));// 经度
				output.writeUnsignedInt(28,
						(int) ((point.getLat() + 90) * 1000000));// 维度
				output.writeUnsignedInt(4, point.getStatus());// 点状态
				int timeIncrement = 0;
				if (i > 0) {
					GpsPoint lastPoint = YaoPao01App.runManager.GPSList
							.get(i - 1);
					timeIncrement = (int) (point.getTime() - lastPoint
							.getTime());
				}
				output.writeUnsignedInt(21, timeIncrement);// 时间增量
				output.writeUnsignedInt(9, point.getCourse());// 方向
				output.writeUnsignedInt(17, (int)((point.getAltitude()+1000)*10+0.5));// 高程
				output.writeUnsignedInt(8, point.getSpeed());// 速度
				output.writeUnsignedInt(4, 0);// 预留
			}
			
			for (i = 0; i < kmCount; i++) {
				OneKMInfo onekm = YaoPao01App.runManager.dataKm.get(i);
				output.writeUnsignedInt(29,
						(int) ((onekm.getLon() + 180) * 1000000));// 经度
				output.writeUnsignedInt(28,
						(int) ((onekm.getLat() + 90) * 1000000));// 纬度
				output.writeUnsignedInt(11, onekm.getDistance());// 距离
				output.writeUnsignedInt(25, onekm.getDuring());// 时间
				output.writeUnsignedInt(13, 0);// 步数
				output.writeUnsignedInt(10, 0);// 卡路里
				output.writeUnsignedInt(12, (int)(onekm.getAltitudeAdd()*10+0.5));// 高程增加值
				output.writeUnsignedInt(12, (int)(onekm.getAltitudeReduce()*10+0.5));// 高程降低值
				output.writeUnsignedInt(4, 0);// 预留
				
			}
			
			for (i = 0; i < mileCount; i++) {
				OneMileInfo oneMile = YaoPao01App.runManager.dataMile.get(i);
				output.writeUnsignedInt(29,
						(int) ((oneMile.getLon() + 180) * 1000000));// 经度
				output.writeUnsignedInt(28,
						(int) ((oneMile.getLat() + 90) * 1000000));// 纬度
				output.writeUnsignedInt(11, oneMile.getDistance());// 距离
				output.writeUnsignedInt(25, oneMile.getDuring());// 时间
				output.writeUnsignedInt(13, 0);// 步数
				output.writeUnsignedInt(10, 0);// 卡路里
				output.writeUnsignedInt(12, (int)(oneMile.getAltitudeAdd()*10+0.5));// 高程增加值
				output.writeUnsignedInt(12, (int)(oneMile.getAltitudeReduce()*10+0.5));// 高程降低值
				output.writeUnsignedInt(4, 0);// 预留
				
			}
			
			for (i = 0; i < minCount; i++) {
				OneMinuteInfo oneMinute = YaoPao01App.runManager.dataMin
						.get(i);
				output.writeUnsignedInt(29,
						(int) ((oneMinute.getLon() + 180) * 1000000));// 经度
				output.writeUnsignedInt(28,
						(int) ((oneMinute.getLat() + 90) * 1000000));// 维度
				output.writeUnsignedInt(12, oneMinute.getDistance());// 距离
				output.writeUnsignedInt(17, oneMinute.getDuring());// 时间
				output.writeUnsignedInt(10, 0);// 步数
				output.writeUnsignedInt(8, 0);// 卡路里
				output.writeUnsignedInt(10, (int)(oneMinute.getAltitudeAdd()*10+0.5));// 高程增加值
				output.writeUnsignedInt(10, (int)(oneMinute.getAltitudeReduce()*10+0.5));// 高程降低值
				output.writeUnsignedInt(4, 0);// 预留
			}
			byteTarget.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static RunManager readBinary(String fileName){
		RunManager manager = new RunManager();
		int i = 0;
		String saveRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		int version = 0;
		int coorSystem = 0;
		long startTimeStamp = 0;
		int during = 0;
		try{
			final InputStream byteSource = new BufferedInputStream(new FileInputStream(saveRootPath+"/YaoPao/binary/"+fileName+".yaopao"));
			final ByteInput byteInput = new StreamInput(byteSource);
			final BitInput bitInput = new BitInput(byteInput);
			version = bitInput.readUnsignedInt(8);//二进制解析版本
			if(version == 1){//版本1解析：
				long lastTimeStamp = 0;
				int pointCount = bitInput.readUnsignedInt(18);//点个数
				coorSystem = bitInput.readUnsignedInt(2);//坐标系
				startTimeStamp = bitInput.readUnsignedLong(42);//起始时间
				manager.distance = bitInput.readUnsignedInt(20);//距离
				during = bitInput.readUnsignedInt(29);//总时间
				int step = bitInput.readUnsignedInt(20);//总步数
				int calorie = bitInput.readUnsignedInt(17);//卡路里
				manager.altitudeAdd = bitInput.readUnsignedInt(18)/10.0;//高程增加值
				manager.altitudeReduce = bitInput.readUnsignedInt(18)/10.0;//高程降低值
				int kmCount = bitInput.readUnsignedInt(10);
				int mileCount = bitInput.readUnsignedInt(10);
				int fiveMinuteCount = bitInput.readUnsignedInt(14);
				bitInput.readUnsignedInt(6);//预留
				for(i=0;i<pointCount;i++){
					double lon = bitInput.readUnsignedInt(29)/1000000.0-180;
					double lat = bitInput.readUnsignedInt(28)/1000000.0-90;
					int status = bitInput.readUnsignedInt(4);
					//计算下时间
					long timeStamp = 0;
					if(i == 0){//第一个点
						timeStamp = startTimeStamp + bitInput.readUnsignedInt(21);
					}else{
						timeStamp = lastTimeStamp + bitInput.readUnsignedInt(21);
					}
					lastTimeStamp = timeStamp;
					int dir = bitInput.readUnsignedInt(9);
					double alt = bitInput.readUnsignedInt(17)/10.0-1000;
					int speed = bitInput.readUnsignedInt(8);
					bitInput.readUnsignedInt(4);//预留
					GpsPoint point = new GpsPoint(lon,lat,status,timeStamp,dir,alt,speed);
					manager.GPSList.add(point);
				}
				
				for(i=0;i<kmCount;i++){
					double lon = bitInput.readUnsignedInt(29)/1000000.0-180;
					double lat = bitInput.readUnsignedInt(28)/1000000.0-90;
					int distance = bitInput.readUnsignedInt(11);//距离
					int time = bitInput.readUnsignedInt(25);//时间
					bitInput.readUnsignedInt(13);//步数
					bitInput.readUnsignedInt(10);//卡路里
					double altitudeAdd = bitInput.readUnsignedInt(12)/10.0;
					double altitudeReduce = bitInput.readUnsignedInt(12)/10.0;
					bitInput.readUnsignedInt(4);//预留
					OneKMInfo oneKm = new OneKMInfo(i+1,lon,lat,distance,time,altitudeAdd,altitudeReduce);
					manager.dataKm.add(oneKm);
				}
				for(i=0;i<mileCount;i++){
					double lon = bitInput.readUnsignedInt(29)/1000000.0-180;
					double lat = bitInput.readUnsignedInt(28)/1000000.0-90;
					int distance = bitInput.readUnsignedInt(11);//距离
					int time = bitInput.readUnsignedInt(25);//时间
					bitInput.readUnsignedInt(13);//步数
					bitInput.readUnsignedInt(10);//卡路里
					double altitudeAdd = bitInput.readUnsignedInt(12)/10.0;
					double altitudeReduce = bitInput.readUnsignedInt(12)/10.0;
					bitInput.readUnsignedInt(4);//预留
					OneMileInfo oneMile = new OneMileInfo(i+1,lon,lat,distance,time,altitudeAdd,altitudeReduce);
					manager.dataMile.add(oneMile);
				}
				for(i=0;i<fiveMinuteCount;i++){
					double lon = bitInput.readUnsignedInt(29)/1000000.0-180;
					double lat = bitInput.readUnsignedInt(28)/1000000.0-90;
					int distance = bitInput.readUnsignedInt(12);//距离
					int time = bitInput.readUnsignedInt(17);//时间
					bitInput.readUnsignedInt(10);//步数
					bitInput.readUnsignedInt(8);//卡路里
					double altitudeAdd = bitInput.readUnsignedInt(10)/10.0;
					double altitudeReduce = bitInput.readUnsignedInt(10)/10.0;
					bitInput.readUnsignedInt(4);//预留
					OneMinuteInfo oneMinute = new OneMinuteInfo(i+1,lon,lat,distance,time,altitudeAdd,altitudeReduce);
					manager.dataMin.add(oneMinute);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//这里为了测试加了一个输出
		try {
			File file = new File(saveRootPath + "/YaoPao/binary/" + fileName
					+ ".txt");
			if (!file.exists()) {
				file.createNewFile();
				FileWriter fileWritter = new FileWriter(saveRootPath + "/YaoPao/binary/" + fileName + ".txt", false);
				BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
				StringBuilder content = new StringBuilder("");
				content.append("版本号是"+version+"\n");
				content.append("坐标系统是"+coorSystem+"\n");
				content.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss----").format(new Date(startTimeStamp))+"\n");
				content.append("总距离是"+manager.distance+"\n");
				content.append("总时间是"+BinaryIOManager.getTimeString(during/1000)+"\n");
				content.append("高程增加值："+manager.altitudeAdd+"\n");
				content.append("高程降低值："+manager.altitudeReduce+"\n");
				int pointCount = manager.GPSList.size();
				content.append("点序列个数:"+pointCount+"\n");
				for(i = 0;i<pointCount;i++){
					GpsPoint point = manager.GPSList.get(i);
					content.append(new SimpleDateFormat("HH:mm:ss----").format(new Date(point.getTime())));
					content.append(String.format("%.6f", point.getLon())+" ");
					content.append(String.format("%.6f", point.getLat())+" ");
					content.append(""+point.getStatus()+" ");
					content.append(""+point.getCourse()+" ");
					content.append(""+point.getAltitude()+" ");
					content.append(""+point.getSpeed()+"\n");
				}
				content.append("-----------------------------------------------------------------------------------");
				int kmCount = manager.dataKm.size();
				content.append("km个数:"+kmCount+"\n");
				for(i = 0;i<kmCount;i++){
					OneKMInfo oneKm = manager.dataKm.get(i);
					content.append(""+(i+1)+"----"+String.format("%.6f", oneKm.getLon())+" ");
					content.append(""+String.format("%.6f", oneKm.getLat())+" ");
					content.append(""+oneKm.getDistance()+" ");
					content.append(BinaryIOManager.getTimeString(oneKm.getDuring()/1000)+" ");
					content.append(""+String.format("%.1f", oneKm.getAltitudeAdd())+" ");
					content.append(""+String.format("%.1f", oneKm.getAltitudeReduce())+"\n");
				}
				content.append("-----------------------------------------------------------------------------------");
				int mileCount = manager.dataMile.size();
				content.append("mile个数:"+mileCount+"\n");
				for(i = 0;i<mileCount;i++){
					OneMileInfo oneMile = manager.dataMile.get(i);
					content.append(""+(i+1)+"----"+String.format("%.6f", oneMile.getLon())+" ");
					content.append(""+String.format("%.6f", oneMile.getLat())+" ");
					content.append(""+oneMile.getDistance()+" ");
					content.append(BinaryIOManager.getTimeString(oneMile.getDuring()/1000)+" ");
					content.append(""+String.format("%.1f", oneMile.getAltitudeAdd())+" ");
					content.append(""+String.format("%.1f", oneMile.getAltitudeReduce())+"\n");
				}
				content.append("-----------------------------------------------------------------------------------");
				int minuteCount = manager.dataMin.size();
				content.append("分钟个数:"+minuteCount+"\n");
				for(i = 0;i<minuteCount;i++){
					OneMinuteInfo oneMinute = manager.dataMin.get(i);
					content.append(""+(i+1)+"----"+String.format("%.6f", oneMinute.getLon())+" ");
					content.append(""+String.format("%.6f", oneMinute.getLat())+" ");
					content.append(""+oneMinute.getDistance()+" ");
					content.append(BinaryIOManager.getTimeString(oneMinute.getDuring()/1000)+" ");
					content.append(""+String.format("%.1f", oneMinute.getAltitudeAdd())+" ");
					content.append(""+String.format("%.1f", oneMinute.getAltitudeReduce())+"\n");
				}
				bufferWritter.write(content.toString());
				bufferWritter.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return manager;
	}
	public static String getTimeString(int secondValue){
		int hour = secondValue/3600;
	    int minute = (secondValue-hour*3600)/60;
	    int second = secondValue%60;
	    return String.format("%02d:%02d:%02d", hour,minute,second);
	}
}
