package com.wayne.www.waynelib.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.wayne.www.waynelib.fdc.model.Pump;

/**
 * Created by Think on 4/29/2016.
 */
public class PumpsGridViewAdapter extends BaseAdapter {
    private static String LOG_TAG = "PumpsGridViewAdapter";
    private Context context;
    private List<Pump> pumps;
    private OnDropdownControlMenuClickListener onPumpViewDropdownMenuClickedListeners;
    private ReentrantLock lock = new ReentrantLock();

    private PumpsGridViewAdapter() {
    }

    public PumpsGridViewAdapter(Context context, List<Pump> pumps, OnDropdownControlMenuClickListener listener) {
        this.context = context;
        this.pumps = pumps;
        this.onPumpViewDropdownMenuClickedListeners = listener;
    }

//    public void addOnDropdownMenuClickedListener(OnDropdownControlMenuClickListener listener) {
//        this.onPumpViewDropdownMenuClickedListeners.add(listener);
//    }

    @Override
    public int getCount() {
        if (this.pumps == null)
            return 0;

        return this.pumps.size();
    }

    @Override
    public Object getItem(int position) {
        return this.pumps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.pumps.get(position).PumpNo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            List<Tuple<String, Integer>> menus = new ArrayList<>();
//            menus.add(new Tuple<String, Integer>("auth", 1));
//            menus.add(new Tuple<String, Integer>("de-auth", 2));
            //Log.d(LOG_TAG, "####(==null)Binding pump with No: " + this.pumps.get(position).PumpNo + " to a PumpView at gridView position: " + position);
            PumpView pumpView = new PumpView(this.context, menus);
            pumpView.bindPump(this.pumps.get(position));
            pumpView.addOnDropdownMenuClickedListener(this.onPumpViewDropdownMenuClickedListeners);
            pumpView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return pumpView;
        } else {
            //Log.d(LOG_TAG, "####Binding pump with No: " + this.pumps.get(position).PumpNo + " to a PumpView at gridView position: " + position);
            ((PumpView) convertView).bindPump(this.pumps.get(position));
            return convertView;
        }
    }
}
