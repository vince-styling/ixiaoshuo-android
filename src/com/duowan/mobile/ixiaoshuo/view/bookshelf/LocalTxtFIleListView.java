package com.duowan.mobile.ixiaoshuo.view.bookshelf;

import java.util.ArrayList;
import java.util.List;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

/**
 * 显示本地待添加文件列表
 * @author gaocong
 *
 */
public class LocalTxtFIleListView extends ViewBuilder implements
		OnItemLongClickListener, OnItemClickListener {
	private final static String TAG = "YYReader_LocalTxtFIleListView";
	protected BaseAdapter mAdapter;
	protected List<Book> mBookList;
	protected View mLotWithoutBooks;
	private ListView mListView;

	public LocalTxtFIleListView(MainActivity activity, ArrayList<Book> list,
			OnShowListener onShowListener) {
		mShowListener = onShowListener;
		setActivity(activity);
		mBookList = list;
		mViewId = R.id.local_list_view;
	}

	@Override
	public MainActivity getActivity() {
		return (MainActivity) super.getActivity();
	}

	protected void bringToFront() {
		Log.i(TAG, "bringToFront ");
		mListView = (ListView) mView.findViewById(R.id.local_list);
		mListView.setAdapter(initAdatper());
		mView.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.VISIBLE);
		mLotWithoutBooks.setVisibility(View.GONE);
		mIsInFront = true;
	}

	@Override
	public void resume() {
		Log.i(TAG, "resume ");
		super.resume();
	}

	public void init() {
		mLotWithoutBooks = getActivity().findViewById(R.id.lotWithoutBooks);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void build() {
		Log.i(TAG, "LocalTxtFIleListView build");
		mView = getActivity().getLayoutInflater().inflate(
				R.layout.loacal_file_list, null);
		mView.setId(mViewId);
	}

	private BaseAdapter initAdatper() {
		mAdapter = new BaseAdapter() {
			public View getView(int position, View convertView, ViewGroup parent) {
				return getAdapterView(position, convertView, parent);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return mBookList.get(arg0);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				if (mBookList == null) {
					Log.i(TAG, "mBookList : 0 " );
					return 0;
				}else{
					Log.i(TAG, "mBookList : " + mBookList.size());
					return mBookList.size();
				}
				
			}
		};
		return mAdapter;
	}
	
	
	private View getAdapterView(int position, View convertView, ViewGroup parent){
		TextView bookNameTextView;
		ImageView selector;
		if(convertView == null){
			convertView = getActivity().getLayoutInflater().inflate(R.layout.local_file_item, null);
		}
		bookNameTextView = (TextView) convertView.findViewById(R.id.book_name);
		bookNameTextView.setTextColor(Color.BLACK);
		selector = (ImageView) convertView.findViewById(R.id.selector);
		selector.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
		Book b = mBookList.get(position);
		String bookName = b.getLocaPath();
		bookName = bookName.substring(bookName.lastIndexOf("/")+1);  
		bookNameTextView.setText(bookName);
		Log.i(TAG, "book name : " + b.getLocaPath());
		return convertView;
	}

}
