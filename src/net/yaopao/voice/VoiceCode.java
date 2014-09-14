package net.yaopao.voice;

import java.util.HashMap;

public class VoiceCode {

	public HashMap<Integer, String> code = new HashMap<Integer, String>(150);

	public VoiceCode() {
		code.put(100000, "零");
		code.put(100001, "一");
		code.put(100002, "二");
		code.put(100003, "三");
		code.put(100004, "四");
		code.put(100005, "五");
		code.put(100006, "六");
		code.put(100007, "七");
		code.put(100008, "八");
		code.put(100009, "九");
		code.put(100010, "十");
		code.put(100011, "十一");
		code.put(100012, "十二");
		code.put(100013, "十三");
		code.put(100014, "十四");
		code.put(100015, "十五");
		code.put(100016, "十六");
		code.put(100017, "十七");
		code.put(100018, "十八");
		code.put(100019, "十九");
		code.put(100020, "二十");
		code.put(100021, "二十一");
		code.put(100022, "二十二");
		code.put(100023, "二十三");
		code.put(100024, "二十四");
		code.put(100025, "二十五");
		code.put(100026, "二十六");
		code.put(100027, "二十七");
		code.put(100028, "二十八");
		code.put(100029, "二十九");
		code.put(100030, "三十");
		code.put(100031, "三十一");
		code.put(100032, "三十二");
		code.put(100033, "三十三");
		code.put(100034, "三十四");
		code.put(100035, "三十五");
		code.put(100036, "三十六");
		code.put(100037, "三十七");
		code.put(100038, "三十八");
		code.put(100039, "三十九");
		code.put(100040, "四十");
		code.put(100041, "四十一");
		code.put(100042, "四十二");
		code.put(100043, "四十三");
		code.put(100044, "四十四");
		code.put(100045, "四十五");
		code.put(100046, "四十六");
		code.put(100047, "四十七");
		code.put(100048, "四十八");
		code.put(100049, "四十九");
		code.put(100050, "五十");
		code.put(100051, "五十一");
		code.put(100052, "五十二");
		code.put(100053, "五十三");
		code.put(100054, "五十四");
		code.put(100055, "五十五");
		code.put(100056, "五十六");
		code.put(100057, "五十七");
		code.put(100058, "五十八");
		code.put(100059, "五十九");

		code.put(101001, "零一");
		code.put(101002, "零二");
		code.put(101003, "零三");
		code.put(101004, "零四");
		code.put(101005, "零五");
		code.put(101006, "零六");
		code.put(101007, "零七");
		code.put(101008, "零八");
		code.put(101009, "零九");

		code.put(110001, "点");
		code.put(110002, "，");
		code.put(110003, "。");

		code.put(110011, "两");
		code.put(110012, "百");
		code.put(110013, "千");
		code.put(110014, "万");

		code.put(110021, "秒");
		code.put(110022, "分");
		code.put(110023, "秒钟");
		code.put(110024, "分钟");
		code.put(110025, "小时");
		code.put(110026, "天");
		code.put(110027, "周");
		code.put(110028, "星期");

		code.put(110041, "公里");
		code.put(110042, "米");

		code.put(110101, "请打开GPS功能");
		code.put(110102, "GPS信号较弱");

		code.put(110111, "请打开蜂窝移动数据");
		code.put(110112, "请启用数据流量");

		code.put(110191, "请打开后台刷新功能");

		code.put(120001, "跑步");
		code.put(120002, "步行");
		code.put(120003, "骑行");

		code.put(120101, "真棒！");
		code.put(120102, "加油！");
		code.put(120103, "恭喜你！");
		code.put(120104, "努力！");
		code.put(120105, "太好了！");
		code.put(120106, "太棒了！");
		code.put(120107, "真不错！");
		code.put(120108, "好厉害！");
		code.put(120109, "坚持住！");
		code.put(120110, "别放弃！");
		code.put(120111, "你一定能行！");
		code.put(120112, "出发吧！");
		code.put(120113, "冲刺吧！");
		code.put(120114, "飞奔吧！");

		code.put(120201, "运动开始");
		code.put(120202, "运动暂停");
		code.put(120203, "运动继续");
		code.put(120204, "运动完成");

		code.put(120211, "运动距离");
		code.put(120212, "用时");
		code.put(120213, "平均速度每公里");

		code.put(120221, "你已完成");
		code.put(120222, "还剩");
		code.put(120223, "你已完成目标的一半");
		code.put(120224, "你就快达成目标了");
		code.put(120225, "超过你的目标");

		code.put(130201, "你已偏离赛道");

		code.put(131101, "你的团队已完成");
		code.put(131102, "距离下一交接区还有");
		code.put(131103, "你已进入交接区");
		code.put(131104, "接力棒已交给队友");
		code.put(131105, "完成了本阶段赛程");
		code.put(131106, "请等待");
		code.put(131107, "你已接到了接力棒");

	}

	public static void main(String[] args) {
		VoiceCode main = new VoiceCode();
		
		System.out.println(main.code.get(131106));

	}

}
