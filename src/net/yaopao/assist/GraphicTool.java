package net.yaopao.assist;

import java.util.HashMap;
import java.util.Map;

import net.yaopao.activity.R;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.widget.ImageView;

/**
 * 
 * @author wangyu
 * 
 *         数字图像加载与释放
 * 
 *         数字view更新
 * 
 *         数字bitmap与view解除绑定
 * 
 */
public class GraphicTool  {
	private  int[] btmNames = new int[] { R.drawable.w_0, R.drawable.w_1,
			R.drawable.w_2, R.drawable.w_3, R.drawable.w_4, R.drawable.w_5,
			R.drawable.w_6, R.drawable.w_7, R.drawable.w_8, R.drawable.w_9,
			R.drawable.w_dot, R.drawable.w_km, R.drawable.w_min,
			R.drawable.w_sec, R.drawable.w_colon, R.drawable.r_0,
			R.drawable.r_1, R.drawable.r_2, R.drawable.r_3, R.drawable.r_4,
			R.drawable.r_5, R.drawable.r_6, R.drawable.r_7, R.drawable.r_8,
			R.drawable.r_9, R.drawable.r_dot, R.drawable.r_km,
			R.drawable.r_min, R.drawable.r_sec, R.drawable.r_colon };
	public  Map<Integer, Bitmap> numBitmap = new HashMap<Integer, Bitmap>();
	private  Resources resources ;

	public GraphicTool(Resources resources) {
		this.resources=resources;
		initMap();
	}

	/*
	 * 初始化map
	 */
	private void initMap() {
		BitmapFactory.Options options = getBtmOptions();
		for (int i = 0; i < btmNames.length; i++) {
			numBitmap.put(btmNames[i], BitmapFactory.decodeResource(resources, btmNames[i], options));
		}
	}

	/**
	 * 获取加载图片参数
	 * 
	 * @return Options
	 */
	private Options getBtmOptions() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
//		ALPHA_8        代表8位Alpha位图
//		ARGB_4444      代表16位ARGB位图
//		ARGB_8888     代表32位ARGB位图
//		RGB_565         代表8位RGB位图
		
//		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;// 16位图
		options.inPurgeable = true;//存储空间不足时，允许回收bitmap所占用内存
		options.inInputShareable = true;
		return options;

	}

	/**
	 * 释放bitmap资源
	 */
	public  void releaseResource() {
		for (Integer key : numBitmap.keySet()) {
			if (numBitmap.get(key) != null && !numBitmap.get(key).isRecycled()) {
				numBitmap.get(key).recycle();
			}
		}
		System.gc();
	}

	/**
	 * 接触imageview与bitmap绑定
	 */
	public  void unbindDrawables(ImageView[] views) {
		// 把历史的ImageView 图片对象（image_)释放
		if (views == null) {
			return;
		}
		for (int i = 0; i < views.length; i++) {
			if (views[i] == null) {
				continue;
			}
			if (views[i].getDrawable() != null) {
				views[i].getDrawable().setCallback(null);
			}
		}

	}

	/*
	 * 更新白色数字图片显示
	 */
	public  void updateWhiteNum(int[] values, ImageView[] views) {
		int value = 0;
		ImageView view = null;
		for (int i = 0; i < views.length; i++) {
			value = values[i];
			view = views[i];

			if (value > 9) {
				value = value % 10;
			}
			switch (value) {
			case 0:
				view.setImageBitmap(numBitmap.get(R.drawable.w_0));
				break;
			case 1:
				view.setImageBitmap(numBitmap.get(R.drawable.w_1));
				break;
			case 2:
				view.setImageBitmap(numBitmap.get(R.drawable.w_2));
				break;
			case 3:
				view.setImageBitmap(numBitmap.get(R.drawable.w_3));
				break;
			case 4:
				view.setImageBitmap(numBitmap.get(R.drawable.w_4));
				break;
			case 5:
				view.setImageBitmap(numBitmap.get(R.drawable.w_5));
				break;
			case 6:
				view.setImageBitmap(numBitmap.get(R.drawable.w_6));
				break;
			case 7:
				view.setImageBitmap(numBitmap.get(R.drawable.w_7));
				break;
			case 8:
				view.setImageBitmap(numBitmap.get(R.drawable.w_8));
				break;
			case 9:
				view.setImageBitmap(numBitmap.get(R.drawable.w_9));
				break;

			default:
				break;
			}
		}

	}
	/*
	 * 更新白色数字图片显示
	 */
	public  void updateWhiteNum(int value, ImageView view) {
			
			if (value > 9) {
				value = value % 10;
			}
			switch (value) {
			case 0:
				view.setImageBitmap(numBitmap.get(R.drawable.w_0));
				break;
			case 1:
				view.setImageBitmap(numBitmap.get(R.drawable.w_1));
				break;
			case 2:
				view.setImageBitmap(numBitmap.get(R.drawable.w_2));
				break;
			case 3:
				view.setImageBitmap(numBitmap.get(R.drawable.w_3));
				break;
			case 4:
				view.setImageBitmap(numBitmap.get(R.drawable.w_4));
				break;
			case 5:
				view.setImageBitmap(numBitmap.get(R.drawable.w_5));
				break;
			case 6:
				view.setImageBitmap(numBitmap.get(R.drawable.w_6));
				break;
			case 7:
				view.setImageBitmap(numBitmap.get(R.drawable.w_7));
				break;
			case 8:
				view.setImageBitmap(numBitmap.get(R.drawable.w_8));
				break;
			case 9:
				view.setImageBitmap(numBitmap.get(R.drawable.w_9));
				break;
				
			default:
				break;
			}
	}

	/*
	 * 更新红色数字图片显示
	 */
	public  void updateRedNum(int[] values, ImageView[] views) {
		int value = 0;
		ImageView view = null;
		for (int i = 0; i < views.length; i++) {
			value = values[i];
			view = views[i];

			if (value > 9) {
				value = value % 10;
			}
			switch (value) {
			case 0:
				view.setImageBitmap(numBitmap.get(R.drawable.r_0));
				break;
			case 1:
				view.setImageBitmap(numBitmap.get(R.drawable.r_1));
				break;
			case 2:
				view.setImageBitmap(numBitmap.get(R.drawable.r_2));
				break;
			case 3:
				view.setImageBitmap(numBitmap.get(R.drawable.r_3));
				break;
			case 4:
				view.setImageBitmap(numBitmap.get(R.drawable.r_4));
				break;
			case 5:
				view.setImageBitmap(numBitmap.get(R.drawable.r_5));
				break;
			case 6:
				view.setImageBitmap(numBitmap.get(R.drawable.r_6));
				break;
			case 7:
				view.setImageBitmap(numBitmap.get(R.drawable.r_7));
				break;
			case 8:
				view.setImageBitmap(numBitmap.get(R.drawable.r_8));
				break;
			case 9:
				view.setImageBitmap(numBitmap.get(R.drawable.r_9));
				break;

			default:
				break;
			}
		}
	}
	/*
	 * 更新红色数字图片显示
	 */
	public  void updateRedNum(int value, ImageView view) {
			
			if (value > 9) {
				value = value % 10;
			}
			switch (value) {
			case 0:
				view.setImageBitmap(numBitmap.get(R.drawable.r_0));
				break;
			case 1:
				view.setImageBitmap(numBitmap.get(R.drawable.r_1));
				break;
			case 2:
				view.setImageBitmap(numBitmap.get(R.drawable.r_2));
				break;
			case 3:
				view.setImageBitmap(numBitmap.get(R.drawable.r_3));
				break;
			case 4:
				view.setImageBitmap(numBitmap.get(R.drawable.r_4));
				break;
			case 5:
				view.setImageBitmap(numBitmap.get(R.drawable.r_5));
				break;
			case 6:
				view.setImageBitmap(numBitmap.get(R.drawable.r_6));
				break;
			case 7:
				view.setImageBitmap(numBitmap.get(R.drawable.r_7));
				break;
			case 8:
				view.setImageBitmap(numBitmap.get(R.drawable.r_8));
				break;
			case 9:
				view.setImageBitmap(numBitmap.get(R.drawable.r_9));
				break;
				
			default:
				break;
			}
	}
}
