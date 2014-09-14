package net.yaopao.assist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.yaopao.activity.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SportListAdapter extends BaseAdapter {
	private Context context; // 运行上下文
	private List<Map<String, Object>> listItems; // 商品信息集合
	private LayoutInflater listContainer; // 视图容器

	public SportListAdapter(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void addItem(List<Map<String, Object>> list) {
		// TODO Auto-generated method stub
		for(int i = 0; i < list.size(); i++){
			Map<String,Object> map = list.get(i);
			this.listItems.add(map);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 获取list_item布局文件的视图
	//	try {
			convertView = listContainer.inflate(R.layout.sport_recording_adapter,null);
			// 获取控件对象
			ImageView runtyV = (ImageView) convertView
					.findViewById(R.id.list_sport_type);
			ImageView mindV = (ImageView) convertView
					.findViewById(R.id.list_sport_mind);
			ImageView wayV = (ImageView) convertView
					.findViewById(R.id.list_sport_way);
			ImageView phoV = (ImageView) convertView
					.findViewById(R.id.list_sport_pho);
			
			TextView dateyV = (TextView) convertView
					.findViewById(R.id.list_sport_date);
//			TextView disyV = (TextView) convertView
//					.findViewById(R.id.list_sport_distance);
			TextView spedV = (TextView) convertView
					.findViewById(R.id.list_sport_speed);
			TextView indexV = (TextView) convertView.findViewById(R.id.sport_index);
			//所用时间 chenxy
			TextView uTimeV = (TextView)convertView.findViewById(R.id.sport_list_time);
			
			ImageView d1v = (ImageView) convertView.findViewById(R.id.list_sport_num1);
			ImageView d2v = (ImageView) convertView.findViewById(R.id.list_sport_num2);
			ImageView d3v = (ImageView) convertView.findViewById(R.id.list_sport_dec1);
			ImageView d4v = (ImageView) convertView.findViewById(R.id.list_sport_dec2);
			initDis(listItems.get(position),new ImageView[]{d1v,d2v,d3v,d4v});
			runtyV.setBackgroundResource((Integer) listItems.get(position).get("type"));
			if (listItems.get(position).get("mind")!=null) {
				mindV.setBackgroundResource((Integer) listItems.get(position).get("mind"));
			}
			if (listItems.get(position).get("way")!=null) {
				wayV.setBackgroundResource((Integer) listItems.get(position).get("way"));
			}
			if (listItems.get(position).get("hasPho")!=null&&!"".equals(listItems.get(position).get("hasPho"))) {
				Log.v("wypho", "position = "+position+" ,has="+listItems.get(position).get("hasPho")+"-- is="+(1==(Integer)listItems.get(position).get("hasPho")));
				if (1==(Integer)listItems.get(position).get("hasPho")) {
					String simgPath = Constants.sportPho_s +listItems.get(position).get("phoName");
					Log.v("wypho", "simgPath = "+simgPath);
					Bitmap pho = getImg(simgPath);
					phoV.setImageBitmap(pho);
					phoV.setVisibility(View.VISIBLE);
				}
			}
			dateyV.setText((String) listItems.get(position).get("date"));
//			disyV.setText((String) listItems.get(position).get("dis"));
			spedV.setText((String) listItems.get(position).get("speed"));
			indexV.setText(listItems.get(position).get("id") + "");
			//修改所用时间
			uTimeV.setText(listItems.get(position).get("utime") + "");	
	//	} catch (Exception e) {
//			e.printStackTrace();
//			Log.v("wylist", "e="+e);
//		}
		
		return convertView;
	}

	private void initDis(Map<String, Object> map,ImageView[] images) {
		double distance = Double.parseDouble(map.get("dis").toString());
		//distance = 549254;
		int d1 = (int) (distance % 100000) / 10000;
		int d2 = (int) (distance % 10000) / 1000;
		int d3 = (int) (distance % 1000) / 100;
		int d4 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			images[0].setVisibility(View.VISIBLE);
		}
		update(d1, images[0]);
		update(d2, images[1]);
		update(d3, images[2]);
		update(d4, images[3]);
		
	}
	protected void update(int i, ImageView view) {
		if (i > 9) {
			i = i % 10;
		}
		switch (i) {
		case 0:
			view.setBackgroundResource(R.drawable.r_0);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.r_1);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.r_2);
			break;
		case 3:
			view.setBackgroundResource(R.drawable.r_3);
			break;
		case 4:
			view.setBackgroundResource(R.drawable.r_4);
			break;
		case 5:
			view.setBackgroundResource(R.drawable.r_5);
			break;
		case 6:
			view.setBackgroundResource(R.drawable.r_6);
			break;
		case 7:
			view.setBackgroundResource(R.drawable.r_7);
			break;
		case 8:
			view.setBackgroundResource(R.drawable.r_8);
			break;
		case 9:
			view.setBackgroundResource(R.drawable.r_9);
			break;

		default:
			break;
		}
	}

	public Bitmap getImg(String path) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			bitmap = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				fis.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return bitmap;
	}
}
