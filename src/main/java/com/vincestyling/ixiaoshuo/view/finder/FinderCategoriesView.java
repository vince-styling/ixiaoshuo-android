package com.vincestyling.ixiaoshuo.view.finder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Category;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.reader.CategoryBookListActivity;
import com.vincestyling.ixiaoshuo.view.BaseFragment;
import com.vincestyling.ixiaoshuo.view.EndlessListAdapter;

import java.util.List;

public class FinderCategoriesView extends BaseFragment implements AdapterView.OnItemClickListener {
    private EndlessListAdapter<Category> mAdapter;
    private View mLotNetworkUnavaliable;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.finder_book_categories, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (ListView) view.findViewById(R.id.lsvCategories);

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
                } else {
                    holder = (Holder) convertView.getTag();
                }

                Category category = getItem(position);
                holder.txvCategoryName.setText(category.getName());
                holder.txvCategoryBookCount.setText(String.format(
                        getResources().getString(R.string.finder_categories_amount), category.getBookCount()));

                return convertView;
            }

            class Holder {
                TextView txvCategoryName,
                        txvCategoryBookCount;
            }

            @Override
            protected View initProgressView() {
                return getActivity().getLayoutInflater().inflate(R.layout.contents_loading, null);
            }
        };

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mLotNetworkUnavaliable = view.findViewById(R.id.lotNetworkUnavaliable);
        mLotNetworkUnavaliable.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    @Override
    public void onResume() {
        if (mAdapter.getItemCount() == 0) loadData();
        super.onResume();
    }

    private void loadData() {
        Netroid.getCategories(new Listener<List<Category>>() {
            @Override
            public void onPreExecute() {
                mLotNetworkUnavaliable.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mAdapter.setIsLoadingData(true);
            }

            @Override
            public void onFinish() {
                mAdapter.setIsLoadingData(false);
            }

            @Override
            public void onSuccess(List<Category> categories) {
                mAdapter.setData(categories);
            }

            @Override
            public void onError(NetroidError error) {
                mLotNetworkUnavaliable.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category category = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), CategoryBookListActivity.class);
        intent.putExtra(Const.CATEGORY_NAME, category.getName());
        intent.putExtra(Const.CATEGORY_ID, category.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }
}
