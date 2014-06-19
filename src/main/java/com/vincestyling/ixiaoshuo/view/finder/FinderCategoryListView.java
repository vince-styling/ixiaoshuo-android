package com.vincestyling.ixiaoshuo.view.finder;

import android.view.Display;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Category;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.view.EndlessListAdapter;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

import java.util.List;

public class FinderCategoryListView extends ViewBuilder implements OnItemClickListener {
	private View mLotNetworkUnavaliable;
	private EndlessListAdapter<Category> mAdapter;
	private FinderCategoriesView mCategoriesView;

	public FinderCategoryListView(MainActivity activity, FinderCategoriesView categoriesView) {
		mViewId = R.id.lsvFinderBookCategories;
		mCategoriesView = categoriesView;
		setActivity(activity);
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.finder_book_categories_listview, null);
	}

	@Override
	public void init() {
		if (mLotNetworkUnavaliable != null) return;
		mLotNetworkUnavaliable = getActivity().findViewById(R.id.lotFinderNetworkUnavaliable);

		mAdapter = new EndlessListAdapter<Category>() {
			@Override
			protected View getView(int position, View convertView) {
				Holder holder;
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.finder_book_category_item, null);

					holder = new Holder();
					holder.txvCategoryName = (TextView) convertView.findViewById(R.id.txvCategoryName);
					holder.txvCategoryBookCount = (TextView) convertView.findViewById(R.id.txvCategoryBookCount);
					convertView.setTag(holder);

					convertView.setLayoutParams(new AbsListView.LayoutParams(
							getListView().getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
				} else {
					holder = (Holder) convertView.getTag();
				}

				Category category = getItem(position);
				holder.txvCategoryName.setText(category.getName());
				holder.txvCategoryBookCount.setText("共" + category.getBookCount() + "部");

				return convertView;
			}

			@Override
			protected View initProgressView() {
				View progressView = getActivity().getLayoutInflater().inflate(R.layout.contents_loading, null);
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				progressView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
				return progressView;
			}
		};
		getListView().setAdapter(mAdapter);
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void resume() {
		Button btnFinderRetry = (Button) mLotNetworkUnavaliable.findViewById(R.id.btnFinderRetry);
		btnFinderRetry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadData();
			}
		});
		super.resume();
		loadData();
	}

	private void loadData() {
		mLotNetworkUnavaliable.setVisibility(View.GONE);
		if (mAdapter.getItemCount() > 0) return;

		Netroid.getCategories(new Listener<List<Category>>() {
			@Override
			public void onPreExecute() {
				mAdapter.setIsLoadingData(true);
			}

			@Override
			public void onFinish() {
				mAdapter.setIsLoadingData(false);
			}

			@Override
			public void onSuccess(List<Category> categories) {
				mAdapter.addAll(categories);
			}

			@Override
			public void onError(NetroidError error) {
				if (isInFront()) mLotNetworkUnavaliable.setVisibility(View.VISIBLE);
			}
		});
	}

	class Holder {
		TextView txvCategoryName, txvCategoryBookCount;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		Category category = mAdapter.getItem(position);
//		if (category == null) return;
//		mCategoriesView.showView(new FinderCategoryBookListView(category.getId(), mCategoriesView.getBookType(), getActivity()));
	}

	protected ListView getListView() {
		return (ListView) mView;
	}

	@Override
	public MainActivity getActivity() {
		return (MainActivity) super.getActivity();
	}

}
