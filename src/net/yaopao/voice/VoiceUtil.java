package net.yaopao.voice;

import java.util.ArrayList;

public class VoiceUtil {

	private VoiceCode vc = new VoiceCode();

	public VoiceUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 生成2位整型数字的语音播放序列
	 * 
	 * @author shawndong
	 * @version 1.0 2014/9/10
	 * @param src
	 *            数字
	 * @param isLiang
	 *            是否将“二”在适当的地方转换为“两”
	 * @param isLing
	 *            是否将“一”到“九”转换为“零一”到“零九”
	 * @return 语音播放的序列，需要播放的音频文件的编码列表（也就是文件名称）
	 */
	public ArrayList<Integer> voiceOf2Digit(int src, boolean isLiang,
			boolean isLing) {
		if (src < 0 || src > 99) {
			return null;
		}

		ArrayList<Integer> r = new ArrayList<Integer>();

		if (src >= 0 && src < 60) {
			if (isLiang && src == 2) {
				r.add(110011);
				if (isLing && src >= 1 && src <= 9) {
					r.add(0, 100000);
				}
			} else {
				r.add(100000 + src);
				if (isLing && src >= 1 && src <= 9) {
					r.add(0, 100000);
				}
			}
			return r;
		}

		int[] d = new int[3];
		d[2] = src % 100 / 10;
		d[1] = src % 10;
		d[0] = src;

		if (d[1] != 0) {
			r.add(100000 + d[1]);
		}
		if (d[2] != 0) {
			r.add(0, 100010);
			r.add(0, 100000 + d[2]);
		}
		return r;
	}

	/**
	 * 生成5位整型数字的语音播放序列
	 * 
	 * @author shawndong
	 * @version 1.0 2014/9/10
	 * @param src
	 *            数字
	 * @param isLiang
	 *            是否将“二”在适当的地方转换为“两”
	 * @param isLing
	 *            是否将“一”到“九”转换为“零一”到“零九”
	 * @return 语音播放的序列，需要播放的音频文件的编码列表（也就是文件名称）
	 */
	public ArrayList<Integer> voiceOf5Digit(int src, boolean isLiang,
			boolean isLing) {
		if (src < 0 || src > 99999) {
			return null;
		}

		if (src < 100) {
			return voiceOf2Digit(src, isLiang, isLing);
		}

		int[] d = new int[6];
		d[5] = src / 10000;
		d[4] = src % 10000 / 1000;
		d[3] = src % 1000 / 100;
		d[2] = src % 100 / 10;
		d[1] = src % 10;
		d[0] = src;

		ArrayList<Integer> r;
		boolean isRoundNumber;
		if (d[1] == 0 && d[2] == 0) {
			r = new ArrayList<Integer>();
			isRoundNumber = true;
		} else {
			r = voiceOf2Digit(d[2] * 10 + d[1], false, false);
			isRoundNumber = false;
		}

		if (d[3] != 0) {
			if (!isRoundNumber && d[2] == 0)
				r.add(0, 100000);
			if (d[2] == 1)
				r.add(0, 100001);
			r.add(0, 110012);
			if (isLiang && d[3] == 2) {
				r.add(0, 110011);
			} else {
				r.add(0, 100000 + d[3]);
			}
		}

		if (d[4] != 0) {
			if (!isRoundNumber && d[3] == 0) {
				if (d[2] == 1)
					r.add(0, 100001);
				r.add(0, 100000);
			}
			r.add(0, 110013);
			if (isLiang && d[4] == 2) {
				r.add(0, 110011);
			} else {
				r.add(0, 100000 + d[4]);
			}
		}

		if (d[5] != 0) {
			if (!isRoundNumber && d[4] == 0) {
				if (d[3] == 0 && d[2] == 1)
					r.add(0, 100001);
				r.add(0, 100000);
			}
			r.add(0, 110014);
			if (isLiang && d[5] == 2) {
				r.add(0, 110011);
			} else {
				r.add(0, 100000 + d[5]);
			}
		}

		return r;
	}

	/**
	 * 生成时间的语音播放序列
	 * 
	 * @author shawndong
	 * @version 1.0 2014/9/10
	 * @param hour
	 *            小时
	 * @param minute
	 *            分钟
	 * @param second
	 *            秒钟
	 * @return 语音播放的序列，需要播放的音频文件的编码列表（也就是文件名称）
	 */
	public ArrayList<Integer> voiceOfTime(int hour, int minute, int second) {
		if (hour < 0)
			hour = 0;
		if (minute < 0)
			minute = 0;
		if (second < 0)
			second = 0;

		int ss = second % 60;
		int mm = (minute + second / 60) % 60;
		int hh = hour + (minute + second / 60) / 60;
		int day = hh / 24;
		int week = day / 7;
		hh = hh % 24;
		day = day % 7;

		ArrayList<Integer> r = new ArrayList<Integer>();
		ArrayList<Integer> temp;

		// 星期
		if (week > 0) {
			temp = voiceOf5Digit(week, true, false);
			for (int i : temp) {
				r.add(i);
			}
			r.add(110028);
		}
		// 天
		if (day > 0) {
			temp = voiceOf2Digit(day, true, false);
			for (int i : temp) {
				r.add(i);
			}
			r.add(110026);
		}
		// 小时
		if (hh > 0) {
			if (week > 0 && day == 0)
				r.add(100000);
			temp = voiceOf2Digit(hh, true, false);
			for (int i : temp) {
				r.add(i);
			}
			r.add(110025);
		}
		// 分
		if (mm > 0
				&& ((week > 0 && (day == 0 || hh == 0)) || (day > 0 && hh == 0))) {
			r.add(100000);
		}
		temp = voiceOf2Digit(mm, true, false);
		for (int i : temp) {
			r.add(i);
		}
		r.add(110022);
		// 秒
		temp = voiceOf2Digit(ss, false, true);
		for (int i : temp) {
			r.add(i);
		}
		r.add(110021);

		return r;
	}

	/**
	 * 生成时间的语音播放序列
	 * 
	 * @author shawndong
	 * @version 1.0 2014/9/10
	 * @param hour
	 *            小时
	 * @param minute
	 *            分钟
	 * @return 语音播放的序列，需要播放的音频文件的编码列表（也就是文件名称）
	 */
	public ArrayList<Integer> voiceOfTime(int hour, int minute) {
		if (hour < 0)
			hour = 0;
		if (minute < 0)
			minute = 0;

		int mm = minute % 60;
		int hh = hour + minute / 60;
		int day = hh / 24;
		int week = day / 7;
		hh = hh % 24;
		day = day % 7;

		ArrayList<Integer> r = new ArrayList<Integer>();
		ArrayList<Integer> temp;

		// 星期
		if (week > 0) {
			temp = voiceOf5Digit(week, true, false);
			for (int i : temp) {
				r.add(i);
			}
			r.add(110028);
		}
		// 天
		if (day > 0) {
			temp = voiceOf2Digit(day, true, false);
			for (int i : temp) {
				r.add(i);
			}
			r.add(110026);
		}
		// 小时
		if (hh > 0) {
			if (week > 0 && day == 0)
				r.add(100000);
			temp = voiceOf2Digit(hh, true, false);
			for (int i : temp) {
				r.add(i);
			}
			r.add(110025);
		}
		// 分
		if (mm > 0) {
			if (week == 0 && day == 0 && hh == 0) {
				temp = voiceOf2Digit(mm, true, false);
			} else {
				temp = voiceOf2Digit(mm, true, true);
			}
			for (int i : temp) {
				r.add(i);
			}
			r.add(110024);
		} else {
			if (week == 0 && day == 0 && hh == 0) {
				r.add(100000);
				r.add(110024);
			}
		}

		return r;
	}

	/**
	 * 生成Double的语音播放序列
	 * 
	 * @author shawndong
	 * @version 1.0 2014/9/10
	 * @param number
	 *            数字
	 * @return 语音播放的序列，需要播放的音频文件的编码列表（也就是文件名称）
	 */
	public ArrayList<Integer> voiceOfDouble(double number) {
		if (number < 0)
			number = 0;

		long l = Math.round(number * 100);
		int integer = (int) (l / 100);
		int decimal = (int) (l % 100);

		ArrayList<Integer> r = new ArrayList<Integer>();
		ArrayList<Integer> temp;

		if (integer == 2 && decimal == 0) {
			r.add(110011);
		} else if (integer == 200 && decimal == 0) {
			r.add(110011);
			r.add(110012);
		} else if (integer == 2000 && decimal == 0) {
			r.add(110011);
			r.add(110013);
		} else if (integer == 20000 && decimal == 0) {
			r.add(110011);
			r.add(110014);
		} else {
			r = voiceOf5Digit(integer, false, false);
			if (decimal != 0) {
				r.add(110001);
				temp = voiceOf2Digit(decimal / 10, false, false);
				for (int i : temp) {
					r.add(i);
				}
				if (decimal % 10 != 0) {
					temp = voiceOf2Digit(decimal % 10, false, false);
					for (int i : temp) {
						r.add(i);
					}
				}
			}
		}
		return r;
	}

	public String getText(ArrayList<Integer> src) {
		if (src == null)
			return null;

		StringBuffer sb = new StringBuffer();

		for (int i : src) {
			sb.append(vc.code.get(i) + " ");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		VoiceUtil main = new VoiceUtil();

		ArrayList<Integer> vl;

		boolean isLiang = true;
		boolean isLing = true;

		for (int i = 0; i < 12; i++) {
			vl = main.voiceOf2Digit(i, isLiang, isLing);
			System.out.println(i + "\t" + main.getText(vl));
		}

		for (int i = 21999; i < 22223; i++) {
			vl = main.voiceOf5Digit(i, isLiang, isLing);
			System.out.println(i + "\t" + main.getText(vl));
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 60; j += 3) {
				vl = main.voiceOfTime(170, i, j);
				System.out.println(main.getText(vl));
			}
		}

		for (double d = 199; d < 203; d += 0.01) {
			vl = main.voiceOfDouble(d);
			System.out.println(d + "\t" + main.getText(vl));
		}
		vl = main.voiceOfDouble(2);
		System.out.println(2 + "\t" + main.getText(vl));
		vl = main.voiceOfDouble(20);
		System.out.println(20 + "\t" + main.getText(vl));
		vl = main.voiceOfDouble(200);
		System.out.println(200 + "\t" + main.getText(vl));
		vl = main.voiceOfDouble(2000);
		System.out.println(2000 + "\t" + main.getText(vl));
		vl = main.voiceOfDouble(20000);
		System.out.println(20000 + "\t" + main.getText(vl));

		for (int i = 0; i < 240; i++) {
			vl = main.voiceOfTime(0, i);
			System.out.println(main.getText(vl));
		}

	}

}
