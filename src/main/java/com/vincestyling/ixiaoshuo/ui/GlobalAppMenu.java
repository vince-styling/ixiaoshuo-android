package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.OnGridItemClickListener;
import com.vincestyling.ixiaoshuo.reader.AboutActivity;
import com.vincestyling.ixiaoshuo.reader.MainActivity;

public class GlobalAppMenu extends GridView {
    public static final int MENU_SETTINGS = 10;
    public static final int MENU_LOVEIT = 20;
    public static final int MENU_SHARE = 30;
    public static final int MENU_EXIT = 40;

    public GlobalAppMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initGrid() {
        if (getItemSize() > 0) return;

        putItem(new GridItem(R.drawable.global_apps_menu_btn_settings, R.drawable.global_apps_menu_btn_settings, new OnGridItemClickListener(MENU_SETTINGS) {
            public void onGridItemClick() {
                getContext().startActivity(new Intent(getContext(), AboutActivity.class));
            }
        }));

        putItem(new GridItem(R.drawable.global_apps_menu_btn_loveit, R.drawable.global_apps_menu_btn_loveit, new OnGridItemClickListener(MENU_LOVEIT) {
            public void onGridItemClick() {
                getActivity().showToastMsg(R.string.unimplement_feature);
            }
        }));

        putItem(new GridItem(R.drawable.global_apps_menu_btn_share, R.drawable.global_apps_menu_btn_share, new OnGridItemClickListener(MENU_SHARE) {
            public void onGridItemClick() {
                getActivity().showToastMsg(R.string.unimplement_feature);
            }
        }));

        putItem(new GridItem(R.drawable.global_apps_menu_btn_exit, R.drawable.global_apps_menu_btn_exit, new OnGridItemClickListener(MENU_EXIT) {
            public void onGridItemClick() {
                getActivity().finish();
            }
        }));

        mHighlightDrawable = getResources().getDrawable(R.drawable.global_apps_menu_selected_bg);
    }

    @Override
    protected void afterEvent() {
        getActivity().hideGlobalMenu();
        selectItem(0);
    }

    public MainActivity getActivity() {
        return (MainActivity) getContext();
    }
}
