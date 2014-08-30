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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 获取list_item布局文件的视图
		convertView = listContainer.inflate(R.layout.sport_recording_adapter,
				null);
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

		// 设置控件集到convertView
		Log.v("wygps", "runtyV= " + runtyV);
		Log.v("wygps", "listItems= " + listItems);
		Log.v("wygps", "listItems.get(position)= " + listItems.get(position));
		Log.v("wygps", "type= " + listItems.get(position).get("type"));
		runtyV.setBackgroundResource((Integer) listItems.get(position).get(
				"type"));
		mindV.setBackgroundResource((Integer) listItems.get(position).get(
				"mind"));
		wayV.setBackgroundResource((Integer) listItems.get(position).get("way"));
		dateyV.setText((String) listItems.get(position).get("date"));
		disyV.setText((String) listItems.get(position).get("dis"));
		spedV.setText((String) listItems.get(position).get("speed"));
		indexV.setText(listItems.get(position).get("id") + "");

		return convertView;
	}

}
