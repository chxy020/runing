package net.yaopao.engine.manager.binaryIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main().writeBinary();
//		new Main().ReadBinary();
	}
	private void writeBinary() {
		
		int pointCount = 1*3600/2;//一个小时2秒一个点
		int i = 0;
    	
        try{
        	final BufferedOutputStream byteTarget = new BufferedOutputStream(new FileOutputStream("i:\\2.zc"));
    		final ByteOutput byteOutput = new StreamOutput(byteTarget);
    		final BitOutput output = new BitOutput(byteOutput);
//        	output.writeUnsignedInt(7, 1);
//        	output.writeUnsignedInt(5, 5);
//        	output.writeUnsignedInt(4, 9);
            output.writeUnsignedInt(8, 1);//版本号
            output.writeUnsignedInt(24, pointCount);//gps个数
            output.writeUnsignedLong(48, System.currentTimeMillis());//gps起始时间
            for(i = 0;i<pointCount;i++){
            	output.writeUnsignedInt(29, 301111111);//经度
                output.writeUnsignedInt(28, 131111111);//维度
                output.writeUnsignedInt(4, 1);//点状态
                output.writeUnsignedInt(24, 2000);//时间增量
                output.writeUnsignedInt(9, 180);//方向
                output.writeUnsignedInt(10, 100);//高程
                output.writeUnsignedInt(8, 5);//速度
            }
            output.writeUnsignedInt(16, 10);//km数
            for(i=0;i<10;i++){
            	output.writeUnsignedInt(29, 301111111);//经度
                output.writeUnsignedInt(28, 131111111);//维度
            	output.writeUnsignedInt(15, 300);//速度
            }
            output.writeUnsignedInt(16, 10);//英里数
            for(i=0;i<10;i++){
            	output.writeUnsignedInt(29, 301111111);//经度
                output.writeUnsignedInt(28, 131111111);//维度
            	output.writeUnsignedInt(15, 300);//速度
            }
            output.writeUnsignedInt(16, 12);//5分钟数
            for(i=0;i<12;i++){
            	output.writeUnsignedInt(29, 301111111);//经度
                output.writeUnsignedInt(28, 131111111);//维度
                output.writeUnsignedInt(16, 1000);//距离
            	output.writeUnsignedInt(15, 300);//速度
            }
            byteTarget.flush();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
	}
	private void ReadBinary() {
		int i = 0;
		try{
			final InputStream byteSource = new BufferedInputStream(new FileInputStream("i:\\2.txt"));
			final ByteInput byteInput = new StreamInput(byteSource);
			final BitInput bitInput = new BitInput(byteInput);
			System.out.println("version is "+bitInput.readUnsignedInt(8));
			int pointCount = bitInput.readUnsignedInt(24);
			System.out.println("gps count is "+pointCount);
			long startTimeStamp = bitInput.readUnsignedLong(48);
			System.out.println("startTime is "+startTimeStamp);
			for(i=0;i<pointCount;i++){
				System.out.println("lon:"+bitInput.readUnsignedInt(29));
				System.out.println("lat:"+bitInput.readUnsignedInt(28));
				System.out.println("status:"+bitInput.readUnsignedInt(4));
				System.out.println("time:"+bitInput.readUnsignedInt(24));
				System.out.println("dir:"+bitInput.readUnsignedInt(9));
				System.out.println("alt:"+bitInput.readUnsignedInt(10));
				System.out.println("speed:"+bitInput.readUnsignedInt(8));
			}
			System.out.println("km count:"+bitInput.readUnsignedInt(16));
			for(i=0;i<10;i++){
				System.out.println("lon:"+bitInput.readUnsignedInt(29));
				System.out.println("lat:"+bitInput.readUnsignedInt(28));
				System.out.println("pace:"+bitInput.readUnsignedInt(15));
			}
			System.out.println("mile count:"+bitInput.readUnsignedInt(16));
			for(i=0;i<10;i++){
				System.out.println("lon:"+bitInput.readUnsignedInt(29));
				System.out.println("lat:"+bitInput.readUnsignedInt(28));
				System.out.println("pace:"+bitInput.readUnsignedInt(15));
			}
			System.out.println("5minute count:"+bitInput.readUnsignedInt(16));
			for(i=0;i<12;i++){
				System.out.println("lon:"+bitInput.readUnsignedInt(29));
				System.out.println("lat:"+bitInput.readUnsignedInt(28));
				System.out.println("dis:"+bitInput.readUnsignedInt(16));
				System.out.println("pace:"+bitInput.readUnsignedInt(15));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
