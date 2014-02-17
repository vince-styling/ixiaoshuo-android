package com.vincestyling.ixiaoshuo.view.search;

import com.vincestyling.ixiaoshuo.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SearchAdapter implements SpinnerAdapter{

	private Context mContext;
	private static String[] mOrders={"全部","文字","有声"};
	
	public SearchAdapter(Context context){
		mContext = context;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCount() {
		return mOrders.length;
	}

	@Override
	public String getItem(int position) {
		return mOrders[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null){
			view = LayoutInflater.from(mContext)
					.inflate(R.layout.listitem_spinner, null);
			TextView tView = (TextView)view.findViewById(R.id.type);
			tView.setText(mOrders[position]);
		}
		
		return view;
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {
		View view = convertView;
		if(view == null){
			view = LayoutInflater.from(mContext)
					.inflate(R.layout.listitem_spinner, null);
			TextView tView = (TextView) view.findViewById(R.id.type);
			tView.setText(mOrders[position]);
		}
		
		return view;
	}
	
}