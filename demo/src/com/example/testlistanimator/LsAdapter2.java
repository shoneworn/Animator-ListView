package com.example.testlistanimator;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LsAdapter2 extends BaseAdapter {

	private Context mContext;
	private List<String> mList;
	private LayoutInflater mInflater = null;
	private boolean isVisible = true;
	/** 要删除的position */
	public int remove_position = -1;
	private int[] bg = {R.drawable.a1,R.drawable.a2,R.drawable.a3,R.drawable.a4,R.drawable.a5,R.drawable.a6,R.drawable.a7};

	public LsAdapter2(Context mContext, List<String> mList) {
		this.mContext = mContext;
		this.mList = mList;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mList != null)
			return mList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_item, null);
		TextView tv = (TextView) view.findViewById(R.id.item_tv);
		tv.setBackgroundResource(bg[position]);

		tv.setText(mList.get(position));
		if (!isVisible && (position == -1 + mList.size())) {
			tv.setText("");
		}
		if (remove_position == position) {
			tv.setText("");
		}

		return view;
	}

	public void addItem(String mShua) {
		mList.add(mShua);
		notifyDataSetChanged();
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/** 设置删除的position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	/** 删除频道列表 */
	public void remove() {
//		System.out.println("list2="+mList.size()+"   remove_position ="+remove_position);

		if(remove_position>=0||remove_position<mList.size())
		mList.remove(remove_position);
		remove_position = -1;
		notifyDataSetChanged();
	}

}
