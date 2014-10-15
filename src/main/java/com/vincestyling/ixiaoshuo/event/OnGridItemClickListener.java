package com.vincestyling.ixiaoshuo.event;

public abstract class OnGridItemClickListener {
    private int gridItemId;

    public OnGridItemClickListener(int itemId) {
        gridItemId = itemId;
    }

    public int getGridItemId() {
        return gridItemId;
    }

    public abstract void onGridItemClick();
}
