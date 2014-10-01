/**
 *
 * file:保存个人登录数据
 * des:社区页面全部迁移到个人里面,登录
 * author:ToT
 * date:2014-08-02
*/
//页面初始化

(function(window){
	//初始化页面函数
	window.callbackInit = function(userinfo,playinfo,deviceinfo,serverurl){
		var obj = {};
		if(userinfo != ""){
			userinfo = Base.str2Json(userinfo);
		}
		if(playinfo != ""){
			playinfo = Base.str2Json(playinfo);
		}
		if(deviceinfo != ""){
			deviceinfo = Base.str2Json(deviceinfo);
		}
		obj.userinfo = userinfo;
		obj.playinfo = playinfo;
		obj.deviceinfo = deviceinfo;
		var dataStr = Base.json2Str(obj);
		//保存数据到本地
		Base.offlineStore.set("_localuserinfo",dataStr);
		//保存请求地址到本地
		Base.offlineStore.set("local_server_url",serverurl,true);
		//调用页面初始化方法
		initPage();
	};

	//回调计数,超过10次就不回调了
	var i = 0;
	function initPage(){
		if(Base.page != null){
			if(typeof Base.page.initPageManager == "function"){
				Base.page.initPageManager();
			}
			else{
				if(i < 10){
					i++;
					setTimeout(function(){
						initPage();
					},100);
				}
			}
		}
		else{
			setTimeout(function(){
				initPage();
			},100);
		}
	}
}(window));

/**/
$(function(){
	//测试数据
	var userinfo = {};
	userinfo.uid = "";
	userinfo.bid = "1";
	userinfo.gid = "1";
	userinfo.username = "";
	userinfo.nikename = "没啥意思a";
	userinfo.groupname = "爱玩跑队";
	userinfo.isleader = "1";
	userinfo.isbaton = "1";
	var playinfo = {};
	playinfo.mid = 1;
	var deviceinfo = {};
	deviceinfo.deviceid = "99000314911470";
	deviceinfo.platform = "android";
	//window.callbackInit(Base.json2Str(userinfo),Base.json2Str(playinfo),Base.json2Str(deviceinfo),"http://182.92.97.144:8080/");
	//window.callbackInit('{"bid":1,"gid":3,"groupname":"CCC","isbaton":"0","isleader":"0","nickname":"13122233308","uid":"","username":"","userphoto":"/image/20140916/120_EBFA23903D7E11E4A6869FF80F14043D.jpg"}','{"etime":"","mid":"1","stime":""}','{"deviceid":"99000314911470","platform":"android"}','http://182.92.97.144:8080/')
	//window.callbackInit('{"bid":"1","gid":"1","groupname":"CCC","isbaton":"0","isleader":"1","nickname":"13122233302","uid":"3","username":"","userphoto":"/image/20140916/120_EBFA23903D7E11E4A6869FF80F14043D.jpg"}','{"etime":"","mid":"1","stime":""}','{"deviceid":"99000314911470","platform":"android"}','http://182.92.97.144:8080/')
	//window.callbackInit('{"bid":"1","gid":"2","groupname":"BBBB","isbaton":"1","isleader":"0","nickname":"13122233306","uid":"6","username":"","userphoto":"/image/20140916/120_EBFA23903D7E11E4A6869FF80F14043D.jpg"}','{"etime":"","mid":"1","stime":""}','{"deviceid":"99000314911470","platform":"android"}','http://182.92.97.144:8080/')
	//window.callbackInit('{"bid":"","gid":"","groupname":"BBBB","isbaton":"1","isleader":"0","nickname":"13122233306","uid":"","username":"","userphoto":"/image/20140916/120_EBFA23903D7E11E4A6869FF80F14043D.jpg"}','{"etime":"","mid":"1","stime":""}','{"deviceid":"99000314911470","platform":"android"}','http://182.92.97.144:8080/')
	//window.callbackInit('{"bid":"1","gid":"2","groupname":"CCC","isbaton":"0","isleader":"1","nickname":"13122233305","uid":"5","username":"","userphoto":"/image/20140916/120_EBFA23903D7E11E4A6869FF80F14043D.jpg"}','{"etime":"","mid":"1","stime":""}','{"deviceid":"99000314911470","platform":"android"}','http://182.92.97.144:8080/')
	//window.callbackInit('{"bid":"","gid":"","groupname":"","isbaton":"0","isleader":"0","nickname":"","uid":"","username":"","userphoto":""}','{"etime":"","mid":1,"stime":""}','{"deviceid":"99000314911470","platform":"android"}','http://182.92.97.144:8080/');
	window.callbackInit('{"bid":"","gid":"","groupname":"","isbaton":"0","isleader":"0","nickname":"","uid":"","username":"","userphoto":""}','{"etime":"","mid":1,"stime":""}','{"deviceid":"99000314911470","platform":"android"}','http://appservice.yaopao.net/');
});



