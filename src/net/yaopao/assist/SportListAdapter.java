package net.yaopao.assist;

import java.util.List;
import java.util.Map;

import net.yaopao.activity.R;
import android.content.Context;
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
		try {
			convertView = listContainer.inflate(R.layout.sport_recording_adapter,null);
			// 获取控件对象
			ImageView runtyV = (ImageView) convertView
					.findViewById(R.id.list_sport_type);
			ImageView mindV = (ImageView) convertView
					.findViewById(R.id.list_sport_mind);
			ImageView wayV = (ImageView) convertView
					.findViewById(R.id.list_sport_way);
			TextView dateyV = (TextView) convertView
					.findViewById(R.id.list_sport_date);
			TextView disyV = (TextView) convertView
					.findViewById(R.id.list_sport_distance);
			TextView spedV = (TextView) convertView
					.findViewById(R.id.list_sport_speed);
			TextView indexV = (TextView) convertView.findViewById(R.id.sport_index);
			//所用时间 chenxy
			TextView uTimeV = (TextView)convertView.findViewById(R.id.sport_list_time);
			
			// 设置控件集到convertView
			Log.v("wylist", "id= " + listItems.get(position).get("id"));
			Log.v("wylist", "type= " + listItems.get(position).get("type"));
			Log.v("wylist", "mind= " + listItems.get(position).get("mind"));
			Log.v("wylist", "way= " + listItems.get(position).get("way"));
			Log.v("wylist", "date= " + listItems.get(position).get("date"));
			Log.v("wylist", "dis=" + listItems.get(position).get("dis"));
			Log.v("wylist", "speed= " + listItems.get(position).get("speed"));
			
			Log.v("wylist", "utime= " + listItems.get(position).get("utime"));
			Log.v("wylist", "========================================================== " );
			runtyV.setBackgroundResource((Integer) listItems.get(position).get("type"));
			if (listItems.get(position).get("mind")!=null) {
				mindV.setBackgroundResource((Integer) listItems.get(position).get("mind"));
			}
			if (listItems.get(position).get("way")!=null) {
				wayV.setBackgroundResource((Integer) listItems.get(position).get("way"));
			}
			dateyV.setText((String) listItems.get(position).get("date"));
			disyV.setText((String) listItems.get(position).get("dis"));
			spedV.setText((String) listItems.get(position).get("speed"));
			indexV.setText(listItems.get(position).get("id") + "");
			//修改所用时间
			uTimeV.setText(listItems.get(position).get("utime") + "");	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return convertView;
	}

}
