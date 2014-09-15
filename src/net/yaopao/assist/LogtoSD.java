package net.yaopao.assist;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;

import android.os.Environment;
import android.util.Log;

public class LogtoSD {

	public final String PIC_PATH = "/mnt/sdcard/zcrrr/";

	public void writeFileToSD(String XYString, String filename) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		try {
			File path = new File(PIC_PATH);
			File file = new File(PIC_PATH + filename + ".txt");
			if (!path.exists()) {
				path.mkdir();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			Writer out = null;
			out = new FileWriter(file, true);
			out.write("\r\n" + getTime() + "    " + XYString + "\r\n");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTime() {
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = tempDate.format(new java.util.Date());
		return datetime;
	}

}
