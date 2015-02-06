package net.yaopao.engine.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.yaopao.activity.LoginActivity;
import net.yaopao.activity.MatchMainActivity;
import net.yaopao.activity.UserInfoActivity;
import net.yaopao.activity.YaoPao01App;
import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNGPSPoint4Match;
import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportBean;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CNCloudRecord {
	
	private List<String> fileArray;
	private List<SportBean> addRecordArray;
	private List<SportBean> downLoadRecordArray;
	private long synTimeNew;
	private long startRequestTime;
	private long endRequestTime;
	private List<String> deltaTimeArray;
	private int synTimeIndex;
	private int deltaMiliSecond;
	public boolean isSynServerTime;
	private int forCloud;
	private int fileCount;
	public boolean userCancel;
	public String stepDes;
	public int newprogress;
	
	public HashMap<String,String> params = null;
	public byte[] binaryData = null;
	public String imageUrl = "";
	InetAddress serverAddress = null;
	DatagramSocket socket = null;
	DatagramPacket pack1 = null;
	DatagramPacket pack2 = null;
	

	public static void ClearRecordAfterUserLogin(){
	    //遍历本地所有记录，没有uid属性的将uid属性赋值为当前uid，不等于当前uid的记录删除
	    //然后查看同步记录文件，将uid赋值或者重置文件
		SharedPreferences cloudDiary = YaoPao01App.cloudDiary;
		SharedPreferences.Editor editor = cloudDiary.edit();
	    String uid_file = cloudDiary.getString("uid", "");
	    if(uid_file.equals("")){//里面的记录还尚未属于任何用户
	    	editor.putString("uid",""+Variables.uid); 
	    	editor.commit();
	    }else if(!uid_file.equals(""+Variables.uid)){//和文件中uid不相等，属于别的用户，重置文件
	    	editor.putLong("synTime",0); 
	    	editor.putString("uid",""+Variables.uid); 
	    	editor.putString("deleteArray","");
	    	editor.commit();
	    }
	}
//	public static void deletePlistRecord(int distance ,int utime,int score,int secondPerKm){
//		DataTool.deleteOneSportRecord(distance, utime, score, secondPerKm);
//		//删除文件
//	
//	}
	public static void addPlistRecord(int distance ,int utime,int score,int secondPerKm){
		DataTool.saveTotalData(distance, utime, score, secondPerKm);
	}
	public static void deleteAllRecordWhenFirstInstall(){
		//needwy调用已经写好的方法，在第一次安装之后删除所有原来的记录
		createCloudDiary(0);
	}
	public static void createCloudDiary(long synTime){
		SharedPreferences.Editor editor = YaoPao01App.cloudDiary.edit();
		editor.putLong("synTime",synTime);
		String uid;
		if(Variables.uid == 0){
			uid = "";
		}else{
			uid = ""+Variables.uid;
		}
		editor.putString("uid",uid); 
		editor.putString("deleteArray",""); 
		editor.commit();
	}
	public void startCloud(){
		if(Variables.network == 1){
			if(this.isSynServerTime){
				cloud_step12();
			}else{
				this.forCloud = 1;
				synTimeWithServer();
				this.stepDes = "正在和服务器同步时间";
			}
		}else{
			Toast.makeText(YaoPao01App.instance, "请检查网络再同步记录", Toast.LENGTH_LONG).show();
		}
	}
	public void cloud_step12(){
		//第一步
	    if(Variables.uid == 0){//是否登录
	    	Toast.makeText(YaoPao01App.instance, "请先登录再同步记录", Toast.LENGTH_LONG).show();
	        this.stepDes = "请先登录";
	        return;
	    }
	    this.downLoadRecordArray = new ArrayList<SportBean>();
	    this.addRecordArray = new ArrayList<SportBean>();
	    this.fileArray = new ArrayList<String>();
	    this.userCancel = false;
	    //第一步：将uid赋值
	    SharedPreferences.Editor editor = YaoPao01App.cloudDiary.edit();
	    editor.putString("uid",""+Variables.uid);
	    editor.commit();
	    Log.v("zc","------------------第一步：赋值uid:"+Variables.uid);
	    //第二步：删除记录
	    Log.v("zc","------------------第二步：删除记录");
	    String deleteArrayStr = YaoPao01App.cloudDiary.getString("deleteArray", "");
	    if(deleteArrayStr.equals("")){//不需要删除
	        Log.v("zc","不需要删除");
	        cloud_step3();
	    }else{
	        List<String> tempArray = new ArrayList<String>();
	        String[] deleteArray = deleteArrayStr.split(",");
	        Log.v("zc","需要删除记录个数:"+deleteArray.length);
	        for(int i=0;i<deleteArray.length;i++){
	        	tempArray.add(""+Variables.uid+"_"+deleteArray[i]);
	        }
	        String deleteArrayJSON = JSON.toJSONString(tempArray);
	        params = new HashMap<String,String>();
	        params.put("uid", ""+Variables.uid);
	        params.put("delrids", deleteArrayJSON);
	        new DeleteRecordTask().execute("");
	        this.stepDes = "正在删除"+tempArray.size()+"条记录";
	    }
	}
	private class DeleteRecordTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			responseJson = NetworkHandler.zcHttpPost(Constants.endpoints1+"/run/delrecord.htm", CNCloudRecord.this.params, null);
			if(responseJson != null && responseJson != ""){
				return true;
			}else{
				return false;
			}
		}
		protected void onProgressUpdate(String... progress) {  
            Log.v("zc","进度是"+progress[0]);  
       }  
		@Override
		protected void onPostExecute(Boolean result) {
			CNCloudRecord.this.newprogress = 1;
			if (result) {
				Log.v("zc","同步——删除记录返回:"+responseJson);
				JSONObject resultJSON = JSON.parseObject(responseJson);
				JSONObject stateDic = resultJSON.getJSONObject("state");
				if(stateDic == null)cloudFailed("删除记录失败");
				int code = stateDic.getIntValue("code");
				if(code == -7){
					Variables.islogin=3;
					DataTool.setUid(0);
					Variables.headUrl="";
					if (Variables.avatar!=null) {
						Variables.avatar=null;
					}
					Variables.userinfo = null;
					Variables.matchinfo = null;
					cloudFailed("用户在其他手机登录，请重新登录");
				}
				if(code == 0){
					Log.v("zc","删除成功");
					cloud_step3();
				}
			} else {
				cloudFailed("删除记录失败");
			}
		}
	}

	public void cloud_step3() {
		if (this.userCancel) {
			cloudFailed("用户取消");
			return;
		}
		Log.v("zc", "------------------第三步：上传文件");
		// 首先过滤哪些才是新纪录（时间大于generateTime，或者时间为0，离线下跑步生成时间为0）
		long synTime = YaoPao01App.cloudDiary.getLong("synTime", 0);
		// 查询generateTime>synTime||generateTime==0的所有记录needwy，将查询出来的数组赋值给addRecordArray
		if (this.addRecordArray == null || this.addRecordArray.size() == 0) {
			Log.v("zc", "没有新增记录，跳过第三步上传文件步骤");
			cloud_step4();
		} else {
			this.stepDes = "正在查找要上传的文件";
			Log.v("zc", "新增记录个数：" + this.addRecordArray.size());
			for (int i = 0; i < this.addRecordArray.size(); i++) {
				SportBean runclass = this.addRecordArray.get(i);
				if (runclass.getServerBinaryFilePath().equals("")
						&& !runclass.getClientBinaryFilePath().equals("")) {// 未上传服务器，并且二进制客户端路径有值
					this.fileArray.add(String.format("%i,%s,3,%s", i,
							runclass.getClientBinaryFilePath(),
							runclass.getRid()));// 记录index_二进制文件路径_文件类型_rid
				}
				if (runclass.getServerImagePaths().equals("")
						&& !runclass.getClientImagePaths().equals("")) {
					this.fileArray.add(String.format("%i,%s,2,%s", i, i,
							runclass.getClientImagePaths(), runclass.getRid()));// 记录index_二进制文件路径_文件类型
				}
				if (runclass.getGenerateTime() == 0) {// 当时离线保存
					runclass.setGenerateTime(System.currentTimeMillis()
							+ deltaMiliSecond);
					if (runclass.getUpdateTime() == 0) {// 当时离线保存
						runclass.setUpdateTime(System.currentTimeMillis()
								+ deltaMiliSecond);
					}
				}
			}
			this.fileCount = this.fileArray.size();
			Log.v("zc", "需上传文件个数：" + this.fileCount);
			if (this.fileArray.size() > 0) {
				uploadOneFile();
			} else {
				Log.v("zc", "有新增文件，但是相关文件都已上传，到第四步");
				cloud_step4();
			}
		}

	}
	public void uploadOneFile(){
	    if(this.userCancel){
	        cloudFailed("用户取消");
	        return;
	    }
	    Log.v("zc","上传一个文件");
	    String fileString = this.fileArray.get(this.fileArray.size()-1);
	    String[] tempArray = fileString.split(",");
	    String uid = ""+Variables.uid;
	    this.binaryData = getContent(tempArray[1]);
	    this.params = new HashMap<String,String>();
	    this.params.put("uid", ""+Variables.uid);
	    this.params.put("type", tempArray[2]);
	    this.params.put("rid", ""+Variables.uid+"_"+tempArray[3]);
	    new uploadFileTask().execute("");
	    this.stepDes = String.format("正在上传记录相关文件%i/%i", (this.fileCount-this.fileArray.size()+1),this.fileCount);
	}
	private class uploadFileTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			responseJson = NetworkHandler.zcHttpPost(Constants.endpoints1+"/sys/upfile.htm", CNCloudRecord.this.params, CNCloudRecord.this.binaryData);
			if(responseJson != null && responseJson != ""){
				return true;
			}else{
				return false;
			}
		}
		protected void onProgressUpdate(String... progress) {  
            Log.v("zc","进度是"+progress[0]);  
       }  
		@Override
		protected void onPostExecute(Boolean result) {
			CNCloudRecord.this.newprogress = 1;
			if (result) {
				Log.v("zc","同步——上传文件返回:"+responseJson);
				JSONObject resultJSON = JSON.parseObject(responseJson);
				JSONObject stateDic = resultJSON.getJSONObject("state");
				if(stateDic == null)cloudFailed("上传文件失败");
				int code = stateDic.getIntValue("code");
				if(code == -7){
					Variables.islogin=3;
					DataTool.setUid(0);
					Variables.headUrl="";
					if (Variables.avatar!=null) {
						Variables.avatar=null;
					}
					Variables.userinfo = null;
					Variables.matchinfo = null;
					cloudFailed("用户在其他手机登录，请重新登录");
				}
				if(code == 0){
					if(userCancel){
				        cloudFailed("用户取消");
				        return;
				    }
				    String fileString = fileArray.get(fileArray.size()-1);
				    String[] tempArray = fileString.split(",");
				    int index = Integer.parseInt(tempArray[0]);
				    int type = Integer.parseInt(tempArray[2]);;
				    SportBean runclass = addRecordArray.get(index);
				    if(type == 2){
				        runclass.setServerImagePaths(resultJSON.getString("serverImagePaths"));
				        runclass.setServerImagePathsSmall(resultJSON.getString("serverImagePathsSmall"));
				    }else if(type == 3){
				        runclass.setServerBinaryFilePath(resultJSON.getString("serverBinaryFilePath"));
				    }
				    //needwy 保存更新过的runclass，或者全部结束后一起保存
				    fileArray.remove(fileArray.size()-1);
				    if(fileArray.size()>0){
				    	new uploadFileTask().execute("");
				    }else{//文件全部上传完毕，下一步
				        Log.v("zc","文件全部上传完毕");
				       cloud_step4();
				    }
				}
			} else {
				cloudFailed("上传文件失败");
			}
		}
	}
	public void cloud_step4(){
		if(userCancel){
	        cloudFailed("用户取消");
	        return;
	    }
	    Log.v("zc","------------------第四步：上传记录");
	    String jsonString_add = getJsonStringFromArray(addRecordArray);
	    //更新的记录
	    long synTime = YaoPao01App.cloudDiary.getLong("synTime", 0);
		// 查询generateTime<synTime&&(updateTime>synTime||updateTime==0)的所有记录needwy，将查询出来的数组赋值给updateRecordArray
	    List<SportBean> updateRecordArray = null;
	    for(SportBean runclass : updateRecordArray){
	        if(runclass.getUpdateTime() == 0){
	            runclass.setUpdateTime(System.currentTimeMillis()+deltaMiliSecond);
	        }
	    }
	    //保存修改后的记录needwy
	    String jsonString_update = getJsonStringFromArray(updateRecordArray);
	    String uid = ""+Variables.uid;
	    params = new HashMap<String,String>();
	    params.put("uid", uid);
	    params.put("synTime", ""+synTime);
	    params.put("genrecords", jsonString_add);
	    params.put("uprecords", jsonString_update);
	    
	    stepDes ="正在上传记录";
	}
	private class UploadRecordTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			responseJson = NetworkHandler.zcHttpPost(Constants.endpoints1+"/run/runupdata.htm", CNCloudRecord.this.params, null);
			if(responseJson != null && responseJson != ""){
				return true;
			}else{
				return false;
			}
		}
		protected void onProgressUpdate(String... progress) {  
            Log.v("zc","进度是"+progress[0]);  
       }  
		@Override
		protected void onPostExecute(Boolean result) {
			CNCloudRecord.this.newprogress = 1;
			if (result) {
				Log.v("zc","同步——上传记录返回:"+responseJson);
				JSONObject resultJSON = JSON.parseObject(responseJson);
				JSONObject stateDic = resultJSON.getJSONObject("state");
				if(stateDic == null)cloudFailed("上传记录失败");
				int code = stateDic.getIntValue("code");
				if(code == -7){
					Variables.islogin=3;
					DataTool.setUid(0);
					Variables.headUrl="";
					if (Variables.avatar!=null) {
						Variables.avatar=null;
					}
					Variables.userinfo = null;
					Variables.matchinfo = null;
					cloudFailed("用户在其他手机登录，请重新登录");
				}
				if(code == 0){
					Log.v("zc","上传记录成功");
				    if(userCancel){
				        cloudFailed("用户取消");
				        return;
				    }
				    JSONArray delrids = resultJSON.getJSONArray("delrids");
				    JSONArray downrecordIDs = resultJSON.getJSONArray("downrecordIDs");
				    synTimeNew = resultJSON.getLongValue("synTimeNew");
				    //先删除：
				    Log.v("zc","删除文件，个数:"+delrids.size());
				    int i = 0;
				    stepDes = "正在删除本地记录:共"+delrids.size()+"条";
				    for(i=0;i<delrids.size();i++){
				        String rid = delrids.getString(i).split("_")[1];
//				        NSString* filter = [NSString stringWithFormat:@"rid=%@",rid];
//				        NSMutableArray* deleteRecordArray = [self getRecordsByFilter:filter];
//				        for(RunClass* runclass in deleteRecordArray){
//				            [kApp.managedObjectContext deleteObject:runclass];
//				            [CNCloudRecord deletePlistRecord:runclass];
//				        }
//				        if ([kApp.managedObjectContext save:&error]) {
//				            NSLog(@"Error:%@,%@",error,[error userInfo]);
//				        }
				        //删除数据库中rid为rid的记录，同时更新用户的统计，同时删除文件needwy
				    }
				    //再下载
				    Log.v("zc","下载文件，个数:"+downrecordIDs.size());
				    if(downrecordIDs.size()>0){
				        String jsonString = downrecordIDs.toJSONString();
				        String uid = ""+Variables.uid;
				        params = new HashMap<String,String>();
				        params.put("uid", uid);
				        params.put("downrecordIDs", jsonString);
				        new DownloadRecordTask().execute("");
				        stepDes = "正在下载记录";
				    }else{
				        cloudSuccess();
				    }
				}
			} else {
				cloudFailed("删除记录失败");
			}
		}
	}
	private class DownloadRecordTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			responseJson = NetworkHandler.zcHttpPost(Constants.endpoints1+"/run/rundowndata.htm", CNCloudRecord.this.params, null);
			if(responseJson != null && responseJson != ""){
				return true;
			}else{
				return false;
			}
		}
		protected void onProgressUpdate(String... progress) {  
            Log.v("zc","进度是"+progress[0]);  
       }  
		@Override
		protected void onPostExecute(Boolean result) {
			CNCloudRecord.this.newprogress = 1;
			if (result) {
				Log.v("zc","同步——下载记录返回:"+responseJson);
				JSONObject resultJSON = JSON.parseObject(responseJson);
				JSONObject stateDic = resultJSON.getJSONObject("state");
				if(stateDic == null)cloudFailed("下载记录失败");
				int code = stateDic.getIntValue("code");
				if(code == -7){
					Variables.islogin=3;
					DataTool.setUid(0);
					Variables.headUrl="";
					if (Variables.avatar!=null) {
						Variables.avatar=null;
					}
					Variables.userinfo = null;
					Variables.matchinfo = null;
					cloudFailed("用户在其他手机登录，请重新登录");
				}
				if(code == 0){
					Log.v("zc","下载记录成功");
					if(userCancel){
				        cloudFailed("用户取消");
				        return;
				    }
				    JSONArray records = resultJSON.getJSONArray("downrecords");
				    for(int i=0;i<records.size();i++){
				        SportBean runClass  = new SportBean();
				        JSONObject dic = records.getJSONObject(i);
				        runClass.setAverageHeart(dic.getIntValue("averageHeart"));
				        runClass.setClientBinaryFilePath("");
				        runClass.setClientImagePaths("");
				        runClass.setClientImagePathsSmall("");
				        runClass.setDbVersion(dic.getIntValue("dbVersion"));
				        runClass.setDistance(dic.getIntValue("distance"));
				        runClass.setDuration(dic.getIntValue("duration"));
				        runClass.setFeeling(dic.getIntValue("feeling"));
				        runClass.setGenerateTime(dic.getLongValue("generateTime"));
				        runClass.setGpsCount(dic.getIntValue("gpsCount"));
				        runClass.setGpsString(dic.getString("gpsString"));
				        runClass.setHeat(dic.getIntValue("heat"));
				        runClass.setHowToMove(dic.getIntValue("howToMove"));
				        runClass.setIsMatch(dic.getIntValue("isMatch"));
				        runClass.setJsonParam(dic.getString("jsonParam"));
				        runClass.setKmCount(dic.getIntValue("kmCount"));
				        runClass.setMaxHeart(dic.getIntValue("maxHeart"));
				        runClass.setMileCount(dic.getIntValue("mileCount"));
				        runClass.setMinCount(dic.getIntValue("minCount"));
				        runClass.setRemark(dic.getString("remark"));
				        runClass.setRid(dic.getString("rid").split("_")[1]);
				        runClass.setRunway(dic.getIntValue("runway"));
				        runClass.setScore(dic.getIntValue("score"));
				        runClass.setSecondPerKm(dic.getIntValue("secondPerKm"));
				        runClass.setServerBinaryFilePath(dic.getString("serverBinaryFilePath"));
				        runClass.setServerImagePaths(dic.getString("serverImagePaths"));
				        runClass.setServerImagePathsSmall(dic.getString("serverImagePathsSmall"));
				        runClass.setStartTime(dic.getLongValue("startTime"));
				        runClass.setTargetType(dic.getIntValue("targetType"));
				        runClass.setTargetValue(dic.getIntValue("targetValue"));
				        runClass.setTemp(dic.getIntValue("temp"));
				        runClass.setUid(dic.getIntValue("uid"));
				        runClass.setUpdateTime(dic.getLongValue("updateTime"));
				        runClass.setWeather(dic.getIntValue("weather"));
				        downLoadRecordArray.add(runClass);
				        if(!runClass.getServerBinaryFilePath().equals("")){
				            fileArray.add(String.format("%i,%s,%s,3", i,runClass.getRid(),runClass.getServerBinaryFilePath()));
				        }
				        if(!runClass.getServerImagePaths().equals("")){
				        	fileArray.add(String.format("%i,%s,%s,21", i,runClass.getRid(),runClass.getServerImagePaths()));
				        }
				        if(!runClass.getServerImagePathsSmall().equals("")){
				        	fileArray.add(String.format("%i,%s,%s,22", i,runClass.getRid(),runClass.getServerImagePathsSmall()));
				        }
				    }
				    fileCount = fileArray.size();    
				    downloadfile();
				}
			} else {
				cloudFailed("删除记录失败");
			}
		}
	}
	public void downloadfile(){
	    if(userCancel){
	        cloudFailed("用户取消");
	        return;
	    }
	    String fileString = fileArray.get(fileArray.size()-1);
	    imageUrl = fileString.split(",")[2];
	    new downLoadTask().execute("");
	    stepDes = String.format("正在下载记录相关文件%i/%i", fileCount-fileArray.size()+1,fileCount);
	}
	private class downLoadTask extends AsyncTask<String, Void, Boolean> {
		private Bitmap bitmap;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try{
				bitmap = BitmapFactory.decodeStream(getImageStream(Constants.endpoints2+imageUrl));
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(bitmap != null){
				return true;
			}else{
				return false;
			}
		}
		protected void onProgressUpdate(String... progress) {  
            Log.v("zc","进度是"+progress[0]);  
       }  
		@Override
		protected void onPostExecute(Boolean result) {
			CNCloudRecord.this.newprogress = 1;
			if (result) {
				if(userCancel){
			        cloudFailed("用户取消");
			        return;
			    }
			    String fileString = fileArray.get(fileArray.size()-1);
			    long time = Long.parseLong(fileString.split(",")[1])/1000;
			    int index = Integer.parseInt(fileString.split(",")[0]);
			    int type = Integer.parseInt(fileString.split(",")[3]);
			    //同步下载文件并保存
			    
			    SportBean runClass = downLoadRecordArray.get(index);
			    if(type == 21){//大图
			        //大图
//			        String bigImageFile = String.format("", args)[NSString stringWithFormat:@"%@/%lli_big.jpg",path,time];
//			        [data writeToFile:bigImageFile atomically:YES];
//			        runClass.clientImagePaths = [NSString stringWithFormat:@"%@/%lli_big.jpg",[CNUtil getYearMonth:time],time];
			    	//通过time得到保存路径needwy
			    	runClass.setClientImagePaths("");//将runClass客户端属性赋值needwy
			    }else if(type == 22){
			        //小图
//			        NSString *smallImageFile = [NSString stringWithFormat:@"%@/%lli_small.jpg",path,time];
//			        [data writeToFile:smallImageFile atomically:YES];
//			        runClass.clientImagePathsSmall = [NSString stringWithFormat:@"%@/%lli_small.jpg",[CNUtil getYearMonth:time],time];
			    	//通过time得到保存路径needwy
			    	runClass.setClientImagePathsSmall("");//将runClass客户端属性赋值needwy
			    }else if(type == 3){
			        //二进制文件
//			        NSString *binarayFile = [NSString stringWithFormat:@"%@/%lli.yaopao",path,time];
//			        [data writeToFile:binarayFile atomically:YES];
//			        runClass.clientBinaryFilePath = [NSString stringWithFormat:@"%@/%lli.yaopao",[CNUtil getYearMonth:time],time];
			    	//通过time得到保存路径needwy
			    	runClass.setClientBinaryFilePath("");//将runClass客户端属性赋值needwy
			    }
			    fileArray.remove(fileArray.size()-1);
			    if(fileArray.size() > 0){
			        downloadfile();
			    }else{
//			        NSError *error = nil;
//			        if ([kApp.managedObjectContext save:&error]) {
//			            for(int i=0;i<[self.downLoadRecordArray count];i++){
//			                RunClass* oneRunClass = [self.downLoadRecordArray objectAtIndex:i];
//			                [CNCloudRecord addPlistRecord:oneRunClass];
//			            }
//			        }else{
//			            [self cloudFailed:@"存储下载记录失败"];
//			            return;
//			        }
			    	//将downLoadRecordArray里的sportBean一一存储，同时更新用户统计needwy
			        cloudSuccess();
			    }
			} else {
				cloudFailed("下载记录文件失败");
			}
		}
	}
	public String getJsonStringFromArray(List<SportBean> array){
	    List<HashMap<String,Object>> jsonArray = new ArrayList<HashMap<String,Object>>();
	    String uid = ""+Variables.uid;
	    for(int i=0;i<array.size();i++){
	    	SportBean runclass = array.get(i);
	        HashMap<String,Object> recordData = new HashMap<String,Object>();
	        recordData.put("averageHeart", runclass.getAverageHeart());
	        recordData.put("dbVersion", runclass.getDbVersion());
	        recordData.put("distance", runclass.getDistance());
	        recordData.put("duration", runclass.getDuration());
	        recordData.put("feeling", runclass.getFeeling());
	        recordData.put("generateTime", runclass.getGenerateTime());
	        recordData.put("gpsCount", runclass.getGpsCount());
	        recordData.put("gpsString", runclass.getGpsString());
	        recordData.put("heat", runclass.getHeat());
	        recordData.put("howToMove", runclass.getHowToMove());
	        recordData.put("isMatch", runclass.getIsMatch());
	        recordData.put("jsonParam", runclass.getJsonParam());
	        recordData.put("kmCount", runclass.getKmCount());
	        recordData.put("maxHeart", runclass.getMaxHeart());
	        recordData.put("mileCount", runclass.getMileCount());
	        recordData.put("minCount", runclass.getMinCount());
	        recordData.put("remark", runclass.getRemark());
	        recordData.put("rid", uid+"_"+runclass.getUid());
	        recordData.put("runway", runclass.getRunway());
	        recordData.put("score", runclass.getScore());
	        recordData.put("secondPerKm", runclass.getSecondPerKm());
	        recordData.put("serverBinaryFilePath", runclass.getServerBinaryFilePath());
	        recordData.put("serverImagePaths", runclass.getServerImagePaths());
	        recordData.put("serverImagePathsSmall", runclass.getServerImagePathsSmall());
	        recordData.put("startTime", runclass.getStartTime());
	        recordData.put("targetType", runclass.getTargetType());
	        recordData.put("targetValue", runclass.getTargetValue());
	        recordData.put("temp", runclass.getTemp());
	        recordData.put("uid", runclass.getUid());
	        recordData.put("updateTime", runclass.getUpdateTime());
	        recordData.put("weather", runclass.getWeather());
	        jsonArray.add(recordData);
	    }
	    String jsonString = JSON.toJSONString(jsonArray);
	    return jsonString;
	}
	public void cloudSuccess(){
		Log.v("zc","同步全部完成");
	    stepDes = "同步完毕！";
	    //重置cloudDiray
	    createCloudDiary(synTimeNew);
	}
	public void cloudFailed(String error){
		Log.v("zc",error);
		this.stepDes = "同步失败，原因是:"+error;
	}
	public byte[] getContent(String filePath){ 
		try{
			File file = new File(filePath);  
	        long fileSize = file.length();  
	        Log.v("zc","fileSize is "+fileSize);
	        if (!file.exists()) {  
	            throw new FileNotFoundException(filePath);  
	        } 
	        FileInputStream fi = new FileInputStream(file);  
	        byte[] buffer = new byte[(int) fileSize];  
	        int offset = 0;  
	        int numRead = 0;  
	        while (offset < buffer.length  
	        && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {  
	            offset += numRead;  
	        }  
	        // 确保所有数据均被读取  
	         
	        fi.close();  
	        return buffer;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
          
    } 
	public InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}
	public void synTimeWithServer(){
		synTimeIndex = 1;
		deltaTimeArray = new ArrayList<String>();
		try{
			if(socket==null){
				socket = new DatagramSocket(null);
				socket.setReuseAddress(true);
				socket.bind(new InetSocketAddress(8011));
			}
			serverAddress = InetAddress.getByName("time.yaopao.net"); 
			
		}catch(Exception e){
			e.printStackTrace();
		}
		new SynTimeTask().execute("");
	}
	private class SynTimeTask extends AsyncTask<String, Void, Boolean> {
		long serverTime; 
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try{
				//send
				String str = "TIME:"+synTimeIndex;
				byte data[] = str.getBytes();
				pack1 = new DatagramPacket (data , data.length , serverAddress , 8011);
				socket.send(pack1);
				startRequestTime = System.currentTimeMillis();
				//receive
				byte [] data2 = new byte[30];  
				pack2 = new DatagramPacket(data2, data2.length); 
				socket.receive(pack2);  
				endRequestTime = System.currentTimeMillis();
				String receiveString = new String(pack2.getData());
				receiveString = receiveString.substring(7, 20);
				serverTime = Long.parseLong(receiveString);
				Log.v("zc","receiveString is "+receiveString);
				Log.v("zc","时间间隔是"+(endRequestTime-startRequestTime));
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } 
			
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			Log.v("zc","请求完毕"+synTimeIndex);
			int deltaTime = (int)(serverTime-(startRequestTime+endRequestTime)/2);//取得毫秒数
	        Log.v("zc","deltaTime is "+deltaTime);
	        Log.v("zc","endRequestTime is "+endRequestTime);
	        if(endRequestTime - startRequestTime < 1000){//间隔小于800毫秒,就直接使用算出来的deltaTime
	            deltaMiliSecond = deltaTime;
	            isSynServerTime = true;
	            if(forCloud == 1){
	                cloud_step12();
	                forCloud = 0;
	            }
	        }else{//请求时间过长，则存入数组
	            deltaTimeArray.add(String.format("%i,%i",endRequestTime-startRequestTime,deltaTime));
	            if(deltaTimeArray.size() == 10){//已经同步了10次了，都请求时间有点长，则取平均值
	                int min = 10000000;
	                int minIndex = 0;
	                for(int i = 0 ;i < 10 ;i++){
	                    int oneRequestTime = Integer.parseInt(deltaTimeArray.get(i).split(",")[0]);
	                    if(oneRequestTime < min){
	                        min = oneRequestTime;
	                        minIndex = i;
	                    }
	                }
	                Log.v("zc","最小的index是"+minIndex);
	                deltaMiliSecond = Integer.parseInt(deltaTimeArray.get(minIndex).split(",")[1]);
	                Log.v("zc","取得的deltaMiliSecond是"+deltaMiliSecond);
	                isSynServerTime = true;
	                if(forCloud == 1){
	                	cloud_step12();
	                    forCloud = 0;
	                }
	            }else{//没到十次继续请求
	                synTimeIndex ++ ;
	                new SynTimeTask().execute("");
	            }
	        }
		}
	}
}
