package com.wayne.www.waynelib.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import com.wayne.www.waynelib.fdc.model.Nozzle;
import com.wayne.www.waynelib.OnPropertyChangedListener;
import com.wayne.www.waynelib.fdc.model.Pump;
import com.wayne.www.waynelib.fdc.model.PumpOrNozzleState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Think on 3/7/2016.
 */
public class PumpView extends LinearLayout implements OnPropertyChangedListener {
    static String LOG_TAG = "MobileCashPos.PumpView";
    private Logger fileLogger = LoggerFactory.getLogger(PumpView.class);
    public void addOnDropdownMenuClickedListener(OnDropdownControlMenuClickListener listener) {
        this.onDropdownControlMenuClickListeners.add(listener);
    }

    private List<Tuple<String, Integer>> dropDownControlMenuItems = new ArrayList<>();
    private boolean needRecaculatePumpImageViewHeight = true;
    private List<OnDropdownControlMenuClickListener> onDropdownControlMenuClickListeners = new ArrayList<>();
    private TextView titleTextView;
    private ImageView pumpImageView;
    // by attaching this layout to main view to implement the turn on and off effect.
    private LinearLayout dropDownControlMenuLayout;
    private Context context;
    private Pump pump;

    /*
    * @param items, ShowText:Id,  the Id will be send upon event OnDropdownControlMenuClick
    */
    public PumpView(Context context, List<Tuple<String, Integer>> items) {
        super(context);
        sharedConstructing(context);
        this.dropDownControlMenuItems = items;
    }

    public PumpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public Pump getBindPump() {
        return this.pump;
    }

    /*
    *   gets if the control Menu opened or not.
    *   when clicked on the main Icon, the submenu will be opened, the state is 'true'.
    *   when submenu is on, and clicked on the main icon, the submenu will gone, the state is 'false'
    */
    public boolean getControlMenuOpeningState() {
        View found = this.findViewById(this.dropDownControlMenuLayout.getId());
        return found != null;
    }

    public void bindPump(Pump pump) {
        this.pump = pump;
        this.updateUI();
        this.dropDownControlMenuLayout.removeAllViews();
        for (int i = 0; i < this.dropDownControlMenuItems.size(); i++) {
            final Tuple<String, Integer> oneItem = this.dropDownControlMenuItems.get(i);
            TextView _ = new TextView(this.context);
            _.setId(View.generateViewId());
            _.setTag(oneItem.second);
            _.setText(oneItem.first);
            _.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final TextView clicked = (TextView) view;
                    fileLogger.debug("ControlMenu Item " + clicked.getTag() + " get clicked at PumpView: " + PumpView.this.pump.PumpNo);
                    Handler h = new Handler();
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            for (OnDropdownControlMenuClickListener l :
                                    PumpView.this.onDropdownControlMenuClickListeners) {

                                l.onMenuItemClicked(PumpView.this, clicked.getTag(), ClickType.SingleClick);
                            }
                        }
                    });
                }
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 5, 0, 0);
            this.dropDownControlMenuLayout.addView(_, lp);
        }
        this.pump.addOnPropertyChangedListener(this);
        for (int i = 0; i < this.pump.getNozzles().size(); i++) {
            this.pump.getNozzles().get(i).addOnPropertyChangedListener(this);
        }
    }

    private void updateUI() {
        this.titleTextView.setText(Integer.toString(this.pump.PumpNo));
        setGifAnimationToImageViewByPumpState(this.pump.getCurrentState());
    }

    public void toggleDropdownMenu(boolean turnOn) {
        View found = this.findViewById(this.dropDownControlMenuLayout.getId());
        if (turnOn && found == null && this.dropDownControlMenuItems.size() > 0) {
            {
                this.addView(this.dropDownControlMenuLayout, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                this.bringToFront();
            }
        } else if (turnOn == false && found != null)
            this.removeView(this.dropDownControlMenuLayout);
    }

    private void sharedConstructing(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addView(inflater.inflate(R.layout.pump_view, null, false), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        this.context = context;
        this.setOrientation(VERTICAL);
        //this.setBackgroundColor(0xFF9CE1B0);
        this.titleTextView = (TextView) findViewById(R.id.pump_view_title_TextView);
        this.pumpImageView = (ImageView) findViewById(R.id.pump_view_pump_ImageView);
        this.pumpImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fileLogger.debug("Click on PumpView: " + PumpView.this.getBindPump().PumpNo);
                for (OnDropdownControlMenuClickListener l :
                        PumpView.this.onDropdownControlMenuClickListeners) {
                    l.onMenuItemClicked(PumpView.this, null, ClickType.SingleClick);
                }
//
                if (getControlMenuOpeningState())
                    toggleDropdownMenu(false);
                else
                    toggleDropdownMenu(true);
//                Handler h = new Handler();
//                h.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((Vibrator) PumpView.this.context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(150);
//                        for (OnDropdownControlMenuClickListener l :
//                                PumpView.this.onDropdownControlMenuClickListeners) {
//                            l.onMenuItemClicked(PumpView.this, null, ClickType.SingleClick);
//                        }
////
//                        if (getControlMenuOpeningState())
//                            toggleDropdownMenu(false);
//                        else
//                            toggleDropdownMenu(true);
//                    }
//                });
            }
        });
        this.pumpImageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fileLogger.debug("LongClick on PumpView: " + PumpView.this.getBindPump().PumpNo);
                Handler h = new Handler();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        ((Vibrator) PumpView.this.context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(150);
                        for (OnDropdownControlMenuClickListener l :
                                PumpView.this.onDropdownControlMenuClickListeners) {
                            l.onMenuItemClicked(PumpView.this, null, ClickType.LongClick);
                        }

                        if (getControlMenuOpeningState()) toggleDropdownMenu(false);
                        else toggleDropdownMenu(true);

                    }
                });
                return true;
            }
        });
        this.dropDownControlMenuLayout = new LinearLayout(context);
        this.dropDownControlMenuLayout.setId(View.generateViewId());
        this.dropDownControlMenuLayout.setOrientation(VERTICAL);
        //this.buttonsLayout.setBackgroundColor(0xFFC71585);
    }

    /**
     * Set the pump to a new state, the UI will change accordingly.
     *
     * @param newState new state
     */
    public void setPumpState(PumpOrNozzleState newState) {
        setGifAnimationToImageViewByPumpState(newState);
        this.needRecaculatePumpImageViewHeight = true;
        // possible cause Bitmap changed, so forcely trigger a re-caculation.
        layout(0, 0, 0, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /* this is the callback that all views have actual Width and Height set*/
        super.onLayout(changed, l, t, r, b);
        if (this.needRecaculatePumpImageViewHeight) {
            Bitmap currentBitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.pump_liq_estado_idle);// ((BitmapDrawable) this.pumpImageView.getDrawable()).getBitmap();
            float ratio = (float) (currentBitmap.getHeight()) / (float) (currentBitmap.getWidth());
            int newHeight = (int) (this.pumpImageView.getWidth() * ratio);
            fileLogger.debug("onLayout called, pumpImageView, for maintain radio: " + ratio + ", set new Height to: " + newHeight);
            this.pumpImageView.setLayoutParams(new FrameLayout.LayoutParams((int)
                    (this.pumpImageView.getWidth() * 0.9), (int) (newHeight * 0.9)));
            this.needRecaculatePumpImageViewHeight = false;
        }
    }

    private void setGifAnimationToImageViewByPumpState(PumpOrNozzleState state) {
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(this.pumpImageView);
        if (state == PumpOrNozzleState.Idle)
            Glide.with(this.context).load(R.drawable.pump_liq_estado_idle_group).into(imageViewTarget);
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

    @Override
    public void onPropertyChanged(Object sender, String propertyName) {
        this.updateUI();
        if ((sender instanceof Pump)) {
            Pump p = (Pump) sender;
            fileLogger.trace("On Pump " + p.PumpNo + " Property changed, propertyName: " + propertyName);
        } else if ((sender instanceof Nozzle) && !propertyName.equals("State")) {
            Nozzle n = (Nozzle) sender;
            //Log.d(LOG_TAG, "On Nozzle " + n.NozzleNo + " Property changed, propertyName: " + propertyName);
            boolean otherNozzlesHaveTrx = false;
            for (int m = 0; m < pump.getNozzles().size(); m++) {
                if (pump.getNozzles().get(m).getTransaction() != null) {
                    otherNozzlesHaveTrx = true;
                    break;
                }
            }

            if (otherNozzlesHaveTrx)
                pump.setState(PumpOrNozzleState.Unpaid);
            else
                pump.setState(PumpOrNozzleState.Idle);
        }
    }

    public enum ClickType {SingleClick, LongClick}
}
