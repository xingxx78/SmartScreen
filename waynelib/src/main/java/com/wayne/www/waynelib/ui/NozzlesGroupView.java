package com.wayne.www.waynelib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.wayne.www.waynelib.fdc.model.Nozzle;
import com.wayne.www.waynelib.OnPropertyChangedListener;
import com.wayne.www.waynelib.fdc.model.Pump;
import com.wayne.www.waynelib.fdc.model.PumpOrNozzleState;

/**
 * Created by Think on 3/30/2016.
 */
public class NozzlesGroupView extends LinearLayout implements OnPropertyChangedListener, Serializable {
    static String LOG_TAG = "MobileCashPos.NozzlesGroupView";
    private Context context;
    private Pump pump;
    private LinearLayout nozzelsViewLayout;
    private int rowHeight = -99;
    private int rowWidth = -101;
    private List<NozzleView> nozzleViews;
    // fired on a nozzle.
    private OnClickListener onNozzleClickedListener;
    // fired for click on a trxSummary in a nozzle.
    private OnTrxSummaryClickListener onTrxSummaryClickedListener;

    public NozzlesGroupView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public NozzlesGroupView(Context context, int rowWidth, int rowHeight, OnClickListener onNozzleClickedListener, OnTrxSummaryClickListener onTrxSummaryClickedListener) {
        super(context);
        this.rowWidth = rowWidth;
        this.rowHeight = rowHeight;
        this.onNozzleClickedListener = onNozzleClickedListener;
        this.onTrxSummaryClickedListener = onTrxSummaryClickedListener;
        sharedConstructing(context);
    }

    public NozzlesGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    //    public void setOnNozzleClickedListener(OnClickListener l) {
//        this.onNozzleClickedListener = l;
//    }
//
//    public void setOnTrxSummaryClickedListener(OnTrxSummaryClickListener l) {
//        this.onTrxSummaryClickedListener = l;
//    }
    private void startNotificationAnimation() {
//        this.fcmNotificationImageView.setBackgroundResource(R.drawable.fcm_icon_on_notify_animation);
//        this.fcmOnNotifyAnimation = (AnimationDrawable) this.fcmNotificationImageView.getBackground();
//        this.fcmOnNotifyAnimation.start();
    }

    private void stopNotificationAnimation() {
//        if (this.fcmOnNotifyAnimation != null)
//            this.fcmOnNotifyAnimation.stop();
    }

    public void removeAllOnPropertyChangedListener() {
        this.pump.removeOnPropertyChangedListener(this);
        for (int i = 0; i < this.pump.getNozzles().size(); i++) {
            this.pump.getNozzles().get(i).removeOnPropertyChangedListener(this);
            this.nozzleViews.get(i).removeAllOnPropertyChangedListener();
        }
    }

    private void sharedConstructing(Context context) {
        this.context = context;
        setClickable(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addView(inflater.inflate(R.layout.nozzels_group_view, null, false), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //this.setBackgroundColor(0xFFFC3936);
        this.nozzelsViewLayout = (LinearLayout) findViewById(R.id.bigNozzles_Nozzles_Layout);
    }

    public void bindPump(Pump pump) {
        this.pump = pump;
        this.pump.addOnPropertyChangedListener(this);
        for (int i = 0; i < this.pump.getNozzles().size(); i++) {
            // ONLY highlight the available nozzles
            this.pump.getNozzles().get(i).addOnPropertyChangedListener(this);
        }

        this.nozzleViews = new ArrayList<>();
        for (int i = 0; i < this.pump.getNozzles().size(); i++) {
            NozzleView nv = new NozzleView(context);
            nv.bindNozzle(this.pump.getNozzles().get(i));
            nv.setOnTrxSummaryClickListener(NozzlesGroupView.this.onTrxSummaryClickedListener);
            nv.setOnClickListener(NozzlesGroupView.this.onNozzleClickedListener);
            nozzleViews.add(nv);
        }

        NozzlesListViewAdapter ada = new NozzlesListViewAdapter(this.context, nozzleViews, this.rowWidth, this.rowHeight);
        ListView nozzlesListView = new ListView(this.context);
        nozzlesListView.setAdapter(ada);
        this.nozzelsViewLayout.removeAllViews();
        this.nozzelsViewLayout.addView(nozzlesListView, new LayoutParams(this.rowWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.hiddenUnavailableNozzleHeaderImageView(pump);
        this.updateUI();
    }

    public Pump getBoundPump() {
        return this.pump;
    }

    private void updateUI() {
        ((TextView) this.findViewById(R.id.nozzlesGroup_Title_TextView)).setText(
                String.format(this.context.getString(R.string.nozzlesGroup_Title), this.pump.PumpNo, this.pump.getNozzles().size()));
        for (int i = 0; i < this.pump.getNozzles().size(); i++)
            setGifAnimationToImageViewByNozzleState(this.pump.getNozzles().get(i).getCurrentState(), getTitleNozzleImageViewByNozzleNo(this.pump.getNozzles().get(i).NozzleNo));
    }

    @Override
    public void onPropertyChanged(Object sender, String propertyName) {
        if ((sender instanceof Pump)) {
//            Pump p = (Pump) sender;
//            Log.d(LOG_TAG, "On Pump " + p.PumpNo + " Property changed, propertyName: " + propertyName);
//            if (propertyName.equals("State")) {
//                for (int i = 0; i < p.getNozzles().size(); i++) {
//                }
//            }
        } else if ((sender instanceof Nozzle)) {
            Nozzle changedNozzle = (Nozzle) sender;
//            Log.d(LOG_TAG, "On Pump " + this.pump.PumpNo
//                    + ", Noz: " + changedNozzle.NozzleNo
//                    + " Pro changed, proName: " + propertyName + ", curNozState: " + changedNozzle.getCurrentState());
            this.updateUI();
        }
    }

    private void setGifAnimationToImageViewByNozzleState(PumpOrNozzleState state, ImageView imageViewTarget) {
        if (state == PumpOrNozzleState.Idle)
            Glide.with(this.context).load(R.drawable.pump_liq_estado_idle).into(imageViewTarget);
        else if (state == PumpOrNozzleState.Authorized)
            Glide.with(this.context).load(R.drawable.pump_liq_estado_authorized).into(imageViewTarget);
        else if (state == PumpOrNozzleState.Starting)
            Glide.with(this.context).load(R.drawable.pump_liq_estado_starting).into(imageViewTarget);
        else if (state == PumpOrNozzleState.Fueling)
            Glide.with(this.context).load(R.drawable.pump_liq_estado_fuelling).into(imageViewTarget);
        else if (state == PumpOrNozzleState.Calling)
            Glide.with(this.context).load(R.drawable.pump_liq_estado_calling).into(imageViewTarget);
        else if (state == PumpOrNozzleState.Closed)
            Glide.with(this.context).load(R.drawable.pump_liq_estado_closed_group).into(imageViewTarget);
        else if (state == PumpOrNozzleState.Error)
            Glide.with(this.context).load(R.drawable.pump_liq_estado_error).into(imageViewTarget);
        else if (state == PumpOrNozzleState.Unpaid)
            Glide.with(this.context).load(R.drawable.pump_liq_estado_idle_unpaid_trx_rmb).into(imageViewTarget);
    }

    // nozzleNo is based on 1.
    private ImageView getTitleNozzleImageViewByNozzleNo(int nozzleNo) {
        if (nozzleNo == 1)
            return (ImageView) this.findViewById(R.id.nozzles_group_nozzels_array_0);
        else if (nozzleNo == 2)
            return (ImageView) this.findViewById(R.id.nozzles_group_nozzels_array_1);
        else if (nozzleNo == 3)
            return (ImageView) this.findViewById(R.id.nozzles_group_nozzels_array_2);
        else if (nozzleNo == 4)
            return (ImageView) this.findViewById(R.id.nozzles_group_nozzels_array_3);
        else if (nozzleNo == 5)
            return (ImageView) this.findViewById(R.id.nozzles_group_nozzels_array_4);
        return null;
    }

    private void hiddenUnavailableNozzleHeaderImageView(Pump p) {
        this.findViewById(R.id.nozzles_group_nozzels_array_0).setAlpha(0.2f);
        this.findViewById(R.id.nozzles_group_nozzels_array_1).setAlpha(0.2f);
        this.findViewById(R.id.nozzles_group_nozzels_array_2).setAlpha(0.2f);
        this.findViewById(R.id.nozzles_group_nozzels_array_3).setAlpha(0.2f);
        this.findViewById(R.id.nozzles_group_nozzels_array_4).setAlpha(0.2f);
        for (int j = 1; j <= 5; j++)
            for (int i = 0; i < p.getNozzles().size(); i++) {
                if (p.getNozzles().get(i).NozzleNo == j) {
                    if (j == 1)
                        this.findViewById(R.id.nozzles_group_nozzels_array_0).setAlpha(1f);
                    else if (j == 2)
                        this.findViewById(R.id.nozzles_group_nozzels_array_1).setAlpha(1f);
                    else if (j == 3)
                        this.findViewById(R.id.nozzles_group_nozzels_array_2).setAlpha(1f);
                    else if (j == 4)
                        this.findViewById(R.id.nozzles_group_nozzels_array_3).setAlpha(1f);
                    else if (j == 5)
                        this.findViewById(R.id.nozzles_group_nozzels_array_4).setAlpha(1f);
                }
            }
    }
}
