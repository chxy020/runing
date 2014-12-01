package zc.manager.binaryIO;

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

import zc.manager.FiveMinuteInfo;
import zc.manager.OneKMInfo;
import zc.manager.OneMileInfo;
import zc.manager.RunManager;
import zc.manager.ZCGPSPoint;

import android.os.Environment;
import net.yaopao.activity.YaoPao01App;

public class BinaryIOManager {
	public static void writeBinary(String fileName) {
		int version = 1;
		int i = 0;
		int pointCount = YaoPao01App.runManager.GPSList.size();
		int kmCount = YaoPao01App.runManager.dataKm.size();
		int mileCount = YaoPao01App.runManager.dataMile.size();
		int fiveMinuteCount = YaoPao01App.runManager.data5Min.size();
		String saveRootPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		try {
			final BufferedOutputStream byteTarget = new BufferedOutputStream(
					new FileOutputStream(saveRootPath + "/yaopao/binary/"
							+ fileName + ".yaopao"));
			final ByteOutput byteOutput = new StreamOutput(byteTarget);
			final BitOutput output = new BitOutput(byteOutput);
			output.writeUnsignedInt(8, version);// 版本号
			output.writeUnsignedInt(8, 1);// 指定坐标系（见二进制说明文档）
			output.writeUnsignedInt(24, pointCount);// gps个数
			output.writeUnsignedLong(48, YaoPao01App.runManager.GPSList.get(0)
					.getTime());// gps起始时间
			for (i = 0; i < pointCount; i++) {
				ZCGPSPoint point = YaoPao01App.runManager.GPSList.get(i);
				output.writeUnsignedInt(29,
						(int) ((point.getLon() + 180) * 1000000));// 经度
				output.writeUnsignedInt(28,
						(int) ((point.getLat() + 90) * 1000000));// 维度
				output.writeUnsignedInt(4, point.getStatus());// 点状态
				int timeIncrement = 0;
				if (i > 0) {
					ZCGPSPoint lastPoint = YaoPao01App.runManager.GPSList
							.get(i - 1);
					timeIncrement = (int) (point.getTime() - lastPoint
							.getTime());
				}
				output.writeUnsignedInt(24, timeIncrement);// 时间增量
				output.writeUnsignedInt(9, point.getCourse());// 方向
				output.writeUnsignedInt(10, point.getAltitude());// 高程
				output.writeUnsignedInt(8, point.getSpeed());// 速度
			}
			output.writeUnsignedInt(16, kmCount);// km数
			for (i = 0; i < kmCount; i++) {
				OneKMInfo onekm = YaoPao01App.runManager.dataKm.get(i);
				output.writeUnsignedInt(29,
						(int) ((onekm.getLon() + 180) * 1000000));// 经度
				output.writeUnsignedInt(28,
						(int) ((onekm.getLat() + 90) * 1000000));// 维度
				output.writeUnsignedInt(15, onekm.getPace());// 速度
			}
			output.writeUnsignedInt(16, mileCount);// 英里数
			for (i = 0; i < mileCount; i++) {
				OneMileInfo oneMile = YaoPao01App.runManager.dataMile.get(i);
				output.writeUnsignedInt(29,
						(int) ((oneMile.getLon() + 180) * 1000000));// 经度
				output.writeUnsignedInt(28,
						(int) ((oneMile.getLat() + 90) * 1000000));// 维度
				output.writeUnsignedInt(15, oneMile.getPace());// 速度
			}
			output.writeUnsignedInt(16, fiveMinuteCount);// 5分钟数
			for (i = 0; i < fiveMinuteCount; i++) {
				FiveMinuteInfo fiveMinute = YaoPao01App.runManager.data5Min
						.get(i);
				output.writeUnsignedInt(29,
						(int) ((fiveMinute.getLon() + 180) * 1000000));// 经度
				output.writeUnsignedInt(28,
						(int) ((fiveMinute.getLat() + 90) * 1000000));// 维度
				output.writeUnsignedInt(16, (int) fiveMinute.getDistance());// 距离
				output.writeUnsignedInt(15, fiveMinute.getPace());// 速度
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
		try{
			final InputStream byteSource = new BufferedInputStream(new FileInputStream(saveRootPath+"/yaopao/binary/"+fileName+".yaopao"));
			final ByteInput byteInput = new StreamInput(byteSource);
			final BitInput bitInput = new BitInput(byteInput);
			int version = bitInput.readUnsignedInt(8);//二进制解析版本
			if(version == 1){//版本1解析：
				long lastTimeStamp = 0;
				int coorSystem = bitInput.readUnsignedInt(8);//坐标系
				int pointCount = bitInput.readUnsignedInt(24);//点个数
				long startTimeStamp = bitInput.readUnsignedLong(48);//起始时间
				for(i=0;i<pointCount;i++){
					double lon = bitInput.readUnsignedInt(29)/1000000.0-180;
					double lat = bitInput.readUnsignedInt(28)/1000000.0-90;
					int status = bitInput.readUnsignedInt(4);
					//计算下时间
					long timeStamp = 0;
					if(i == 0){//第一个点
						timeStamp = startTimeStamp;
					}else{
						timeStamp = lastTimeStamp + bitInput.readUnsignedInt(24);
					}
					lastTimeStamp = timeStamp;
					int dir = bitInput.readUnsignedInt(9);
					int alt = bitInput.readUnsignedInt(10);
					int speed = bitInput.readUnsignedInt(8);
					ZCGPSPoint point = new ZCGPSPoint(lon,lat,status,timeStamp,dir,alt,speed);
					manager.GPSList.add(point);
				}
				int kmCount = bitInput.readUnsignedInt(16);
				for(i=0;i<kmCount;i++){
					double lon = bitInput.readUnsignedInt(29)/1000000.0-180;
					double lat = bitInput.readUnsignedInt(28)/1000000.0-90;
					int pace = bitInput.readUnsignedInt(15);
					OneKMInfo oneKm = new OneKMInfo(i+1,lon,lat,pace);
					manager.dataKm.add(oneKm);
				}
				int mileCount = bitInput.readUnsignedInt(16);
				for(i=0;i<mileCount;i++){
					double lon = bitInput.readUnsignedInt(29)/1000000.0-180;
					double lat = bitInput.readUnsignedInt(28)/1000000.0-90;
					int pace = bitInput.readUnsignedInt(15);
					OneMileInfo oneMile = new OneMileInfo(i+1,lon,lat,pace);
					manager.dataMile.add(oneMile);
				}
				int fiveMinuteCount = bitInput.readUnsignedInt(16);
				for(i=0;i<fiveMinuteCount;i++){
					double lon = bitInput.readUnsignedInt(29)/1000000.0-180;
					double lat = bitInput.readUnsignedInt(28)/1000000.0-90;
					double distance = bitInput.readUnsignedInt(16);
					int pace = bitInput.readUnsignedInt(15);
					FiveMinuteInfo fiveMinute = new FiveMinuteInfo(i+1,lon,lat,distance,pace);
					manager.data5Min.add(fiveMinute);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//这里为了测试加了一个输出
		try {
			File file = new File(saveRootPath + "/yaopao/binary/" + fileName
					+ ".txt");
			if (!file.exists()) {
				file.createNewFile();
				FileWriter fileWritter = new FileWriter(file.getName(), false);
				BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
				StringBuilder content = new StringBuilder("");
				int pointCount = manager.GPSList.size();
				content.append("点序列个数:"+pointCount+"\n");
				for(i = 0;i<pointCount;i++){
					ZCGPSPoint point = manager.GPSList.get(i);
					content.append(new SimpleDateFormat("hh:mm:ss:").format(new Date(point.getTime())));
					content.append(""+point.getLon()+" ");
					content.append(""+point.getLat()+" ");
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
					content.append(""+(i+1)+":"+oneKm.getLon()+" ");
					content.append(""+oneKm.getLat()+" ");
					content.append(BinaryIOManager.getTimeString(oneKm.getPace())+"\n");
				}
				content.append("-----------------------------------------------------------------------------------");
				int mileCount = manager.dataMile.size();
				content.append("mile个数:"+mileCount+"\n");
				for(i = 0;i<mileCount;i++){
					OneMileInfo oneMile = manager.dataMile.get(i);
					content.append(""+(i+1)+":"+oneMile.getLon()+" ");
					content.append(""+oneMile.getLat()+" ");
					content.append(BinaryIOManager.getTimeString(oneMile.getPace())+"\n");
				}
				content.append("-----------------------------------------------------------------------------------");
				int fiveMinuteCount = manager.data5Min.size();
				content.append("5分钟个数:"+fiveMinuteCount+"\n");
				for(i = 0;i<mileCount;i++){
					FiveMinuteInfo fiveMinute = manager.data5Min.get(i);
					content.append(""+(i+1)+":"+fiveMinute.getLon()+" ");
					content.append(""+fiveMinute.getLat()+" ");
					content.append(""+fiveMinute.getDistance()+" ");
					content.append(BinaryIOManager.getTimeString(fiveMinute.getPace())+"\n");
				}
				bufferWritter.write("");
				bufferWritter.close();
			}
			
		} catch (Exception e) {

		}
		return manager;
	}
	public static String getTimeString(int secondValue){
		int hour = secondValue/3600;
	    int minute = (secondValue-hour*3600)/60;
	    int second = secondValue%60;
	    return String.format("%2d:%2d:%2d", hour,minute,second);
	}
}
