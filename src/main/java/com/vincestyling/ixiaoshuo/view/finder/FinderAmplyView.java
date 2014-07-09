package com.vincestyling.ixiaoshuo.view.finder;

import com.vincestyling.ixiaoshuo.view.FragmentCreator;

public class FinderAmplyView extends FinderSimplyView {

	@Override
	protected FragmentCreator[] buildMenus() {
		return new FragmentCreator[]{
				new FragmentCreator(FinderAmplyNewlyBookView.class),
				new FragmentCreator(FinderAmplyHottestBookView.class),
				new FragmentCreator(FinderAmplyUpdatesBookView.class),
				new FragmentCreator(FinderCategoriesView.class),
		};
	}

}
