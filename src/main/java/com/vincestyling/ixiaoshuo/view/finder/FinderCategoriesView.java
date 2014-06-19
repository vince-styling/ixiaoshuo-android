package com.vincestyling.ixiaoshuo.view.finder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.view.BaseFragment;

// TODO : is any way to extending the FinderBaseListView ?
public class FinderCategoriesView extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return getActivity().getLayoutInflater().inflate(R.layout.finder_book_categories, null);
	}

}
