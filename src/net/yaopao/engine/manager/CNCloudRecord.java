package net.yaopao.engine.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CNCloudRecord {
	
	private List<String> fileArray;
	private List<SportBean> addRecordArray;
	private List<SportBean> downLoadRecordArray;
	private long synTimeNew;
//	@property (nonatomic, strong) GCDAsyncUdpSocket *udpSocket;
	private long startRequestTime;
	private long endRequestTime;
	private List<Integer> deltaTimeArray;
	private int synTimeIndex;
	private int deltaMiliSecond;
	public boolean isSynServerTime;
	private int forCloud;
	private int fileCount;
	public boolean userCancel;
	public String stepDes;
	public int newprogress;
	
	public HashMap<String,String> params = null;

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
	public static void deletePlistRecord(int distance ,int utime,int score,int secondPerKm){
		DataTool.deleteOneSportRecord(distance, utime, score, secondPerKm);
		//删除文件
	
	}
	public static void addPlistRecord(int distance ,int utime,int score,int secondPerKm){
		DataTool.saveTotalData(distance, utime, score, secondPerKm);
	}
	public static void deleteAllRecordWhenFirstInstall(){
		//needwy
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
			responseJson = NetworkHandler.zcHttpPost("", CNCloudRecord.this.params, null);
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

	public void cloud_step3(){
		 if(this.userCancel){
		        cloudFailed("用户取消");
		        return;
		 }
		    Log.v("zc","------------------第三步：上传文件");
		    //首先过滤哪些才是新纪录（时间大于generateTime，或者时间为0，离线下跑步生成时间为0）
		    long synTime = YaoPao01App.cloudDiary.getLong("synTime", 0);
		    //查询generateTime>synTime||generateTime==0的所有记录needwy，将查询出来的数组赋值给addRecordArray
		    if (this.addRecordArray == nil||this.addRecordArray.size() == 0) {
		        Log.v("zc","没有新增记录，跳过第三步上传文件步骤");
		        cloud_step4();
		    }else{
		        this.stepDes = "正在查找要上传的文件";
		        Log.v("zc","新增记录个数："+this.addRecordArray.size());
		        for(int i = 0;i<this.addRecordArray.size();i++){
		        	SportBean runclass = this.addRecordArray.get(i);
		            if(runclass.serverBinaryFilePath] && ![runclass.clientBinaryFilePath isEqualToString:@""]){//未上传服务器，并且二进制客户端路径有值
		                this.fileArray.add(String.format("%i,%s,3,%s", i,runclass.clientBinaryFilePath,runclass.getRid()));//记录index_二进制文件路径_文件类型_rid
		            }
		            if([runclass.serverImagePaths isEqualToString:@""] && ![runclass.clientImagePaths isEqualToString:@""]){
		            	this.fileArray.add(String.format("%i,%s,2,%s", i,i,runclass.clientImagePaths,runclass.getRid()));//记录index_二进制文件路径_文件类型
		            }
		            if(runclass.generateTime == 0){//当时离线保存
		                runclass.generateTime = System.currentTimeMillis()+kApp.cloudManager.deltaMiliSecond;
		                if(runclass.updateTime == 0){//当时离线保存
		                    runclass.updateTime = System.currentTimeMillis()+kApp.cloudManager.deltaMiliSecond;
		                }
		            }
		        }
		        this.fileCount = this.fileArray.size();
		        Log.v("zc","需上传文件个数："+this.fileCount);
		        if(this.fileArray.size()>0){
		            uploadOneFile();
		        }else{
		            Log.v("zc","有新增文件，但是相关文件都已上传，到第四步");
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
	    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask, YES);
	    NSString *filePath = [[paths objectAtIndex:0] stringByAppendingPathComponent:[tempArray objectAtIndex:1]];
	    BOOL blHave=[[NSFileManager defaultManager] fileExistsAtPath:filePath];
	    NSData* binaryData;
	    if(blHave){
	        binaryData = [NSData dataWithContentsOfFile:filePath];
	    }else{
	        binaryData = nil;
	    }
	    NSMutableDictionary* params = [[NSMutableDictionary alloc]init];
	    [params setObject:uid forKey:@"uid"];
	    [params setObject:[tempArray objectAtIndex:2] forKey:@"type"];
	    [params setObject:[NSString stringWithFormat:@"%@_%@",uid,[tempArray objectAtIndex:3]] forKey:@"rid"];
	    [params setObject:binaryData forKey:@"avatar"];
	    kApp.networkHandler.delegate_cloudData = self;
	    [kApp.networkHandler doRequest_cloudData:params];
	    self.stepDes = [NSString stringWithFormat:@"正在上传记录相关文件%i/%i",(self.fileCount-[self.fileArray count]+1),self.fileCount];
	}
	public void cloud_step4(){
		
	}
	public void cloudSuccess(){
		
	}
	public void cloudFailed(String error){
		Log.v("zc",error);
		this.stepDes = "同步失败，原因是:"+error;
	}
}
