package com.vincestyling.ixiaoshuo.reader;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import com.vincestyling.ixiaoshuo.view.finder.FinderAmplyCategoryBookView;
import com.vincestyling.ixiaoshuo.view.finder.FinderBaseListView;
import com.vincestyling.ixiaoshuo.view.finder.FinderSimplyCategoryBookView;
import com.vincestyling.ixiaoshuo.view.finder.FinderView;

public class CategoryBookListActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String categoryName = getIntent().getStringExtra(Const.CATEGORY_NAME);
		int categoryId = getIntent().getIntExtra(Const.CATEGORY_ID, 0);
		if (categoryId <= 0 || StringUtil.isEmpty(categoryName)) {
			finish();
			return;
		}

		FinderBaseListView finderView = FinderView.IS_ON_SIMPLY_STYLE ?
				new FinderSimplyCategoryBookView(categoryId) : new FinderAmplyCategoryBookView(categoryId);

		setContentView(R.layout.finder_category_booklist);
		getSupportFragmentManager().beginTransaction().add(R.id.lotContent, finderView).commit();

		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText(categoryName);

		findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}