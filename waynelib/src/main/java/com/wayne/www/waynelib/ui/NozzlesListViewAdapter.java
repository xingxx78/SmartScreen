package com.wayne.www.waynelib.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Think on 4/14/2016.
 */
public class NozzlesListViewAdapter extends BaseAdapter {
    private final Context context;
    private List<NozzleView> nozzleViews;
    private int rowHeight = -99;
    private int rowWidth = -101;

    public NozzlesListViewAdapter(Context context, List<NozzleView> nozzleViews, int rowWidth, int rowHeight) {
        this.context = context;
        this.rowWidth = rowWidth;
        this.rowHeight = rowHeight;
        this.nozzleViews = nozzleViews;
    }

    @Override
    public int getCount() {
        return this.nozzleViews.size();
    }

    @Override
    public com.wayne.www.waynelib.fdc.model.Nozzle getItem(int position) {
        return this.nozzleViews.get(position).getBindNozzle();
    }

    @Override
    public long getItemId(int position) {
        return this.nozzleViews.get(position).getBindNozzle().NozzleNo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            NozzleView nozzleView = this.nozzleViews.get(position);
            nozzleView.setLayoutParams(new ViewGroup.LayoutParams(this.rowWidth, this.rowHeight));
            return nozzleView;
        } else
            return convertView;
    }
}

