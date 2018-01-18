package com.wayne.www.waynelib.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import com.wayne.www.waynelib.fdc.FdcClient;
import com.wayne.www.waynelib.fdc.message.Product;
import com.wayne.www.waynelib.fdc.model.FuelSaleTransactionState;
import com.wayne.www.waynelib.fdc.model.Nozzle;
import com.wayne.www.waynelib.OnPropertyChangedListener;
import com.wayne.www.waynelib.fdc.model.PumpOrNozzleState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Think on 3/17/2016.
 */
public class NozzleView extends LinearLayout implements OnPropertyChangedListener {
    static String LOG_TAG = "MobilePos.NozzleView";
    private Logger fileLogger = LoggerFactory.getLogger(NozzleView.class);
    private Context context;
    private Nozzle nozzle;
    private ImageView moneyImageView;
    private Animation availableTrxOnNozzleAnimation;
    private OnTrxSummaryClickListener onTrxSummaryClickListener;
    private ImageView availableDotImageView;
    private AnimationDrawable onCallingAnimation;

    public NozzleView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public NozzleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public void removeAllOnPropertyChangedListener() {
        this.nozzle.removeOnPropertyChangedListener(this);
    }

    private void sharedConstructing(Context context) {
        this.context = context;
        this.setOrientation(HORIZONTAL);

        this.availableTrxOnNozzleAnimation = AnimationUtils.loadAnimation(this.context, R.anim.unpaid_nozzle_animation);
        this.availableTrxOnNozzleAnimation.setRepeatCount(100);
        this.availableTrxOnNozzleAnimation.setRepeatMode(Animation.INFINITE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addView(inflater.inflate(R.layout.nozzle_view, null, false), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setOnTrxSummaryClickListener(OnTrxSummaryClickListener l) {
        this.onTrxSummaryClickListener = l;
    }

    /*
    not good, but have no idea where to put a trigger to get notified when get displayed on UI.
     */
    public void startUnpaidAnimation() {
        this.startAnimation(this.availableTrxOnNozzleAnimation);
    }

    public void stopUnpaidAnimation() {
        this.clearAnimation();
    }

    public void bindNozzle(Nozzle nozzle) {
        this.nozzle = nozzle;
        this.nozzle.addOnPropertyChangedListener(this);
        this.availableDotImageView = (ImageView) ((FrameLayout) findViewWithTag("nozzle_view__nozzles_dot_array_" + (this.nozzle.NozzleNo - 1))).getChildAt(0);
        this.availableDotImageView.setAlpha(1);
        hiddenUnavailableNozzleRedDotImageView(nozzle);
        updateUI();

        if (nozzle.getCurrentState() == PumpOrNozzleState.Calling) {
            this.availableDotImageView.setBackgroundResource(R.drawable.nozzle_view_one_nozzle_on_calling_animation);
            this.onCallingAnimation = (AnimationDrawable) this.availableDotImageView.getBackground();
            this.onCallingAnimation.start();
        } else {
            if (this.onCallingAnimation != null)
                this.onCallingAnimation.stop();
        }
    }

    private void updateUI() {
        //Log.d(LOG_TAG, "On Nozzle " + this.nozzle.NozzleNo + ", updateUI() called");
        String productName = "UnknownProduct";
        /**
         * query the friendly name first, if missed, then show the raw value.
         * this is for the issue of typing chinese or other special char in fusion WebUI is not supported,
         * but may still want to show these friendly chars in FCM UI.
         */
        if (this.nozzle.Product != null) {
            if (Product.PRODUCT_BOOK_FRIENDLY_NAME.containsKey(this.nozzle.Product.ProductNo))
                productName = Product.PRODUCT_BOOK_FRIENDLY_NAME.get(this.nozzle.Product.ProductNo);
            else
                productName = Product.getLatestFusionLocalProductNameByProductNo(this.nozzle.Product.ProductNo) + "*";
        }

        ((TextView) findViewById(R.id.nozzle_view_productName_TextView)).setText(productName);
        LinearLayout trx_summary_LinearLayout = (LinearLayout) findViewById(R.id.trx_summary_LinearLayout);
        trx_summary_LinearLayout.removeAllViews();
        if (this.nozzle.getTransactions() != null && this.nozzle.getTransactions().size() > 0) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < this.nozzle.getTransactions().size(); i++) {
                View oneTrxSummaryView = inflater.inflate(R.layout.transaction_summary_view, null, false);
                oneTrxSummaryView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NozzleView.this.onTrxSummaryClickListener == null) return;
                        int hiddenSeqNo = Integer.parseInt(((TextView) v.findViewById(R.id.TrxSequenceNo_hidden)).getText().toString());
                        for (int i = 0; i < NozzleView.this.nozzle.getTransactions().size(); i++) {
                            if (NozzleView.this.nozzle.getTransactions().get(i).TransactionSeqNo == hiddenSeqNo)
                                NozzleView.this.onTrxSummaryClickListener.OnClick(NozzleView.this.nozzle.getTransactions().get(i));
                        }
                    }
                });

                // resolve the authed by
                //((TextView) oneTrxSummaryView.findViewById(R.id.autheddByWhoHintTextView)).setText("authedBy " + this.nozzle.getTransactions().get(i).AuthorisationApplicationSender);

                // resolve amount
                ((TextView) oneTrxSummaryView.findViewById(R.id.totalAmountTextView)).setText("￥" + Float.toString(this.nozzle.getTransactions().get(i).Amount));

                // resolve the volumn
                ((TextView) oneTrxSummaryView.findViewById(R.id.volumnTextView)).setText(Float.toString(this.nozzle.getTransactions().get(i).Volume) + "L");

                // resolve timeHint
                String timeHint = "";
                if (this.nozzle.getTransactions().get(i).TransactionTimeStamp != null) {
                    Calendar c = Calendar.getInstance();
                    long mills = c.getTimeInMillis() - this.nozzle.getTransactions().get(i).TransactionTimeStamp.getTimeInMillis();
                    int mins = (int) (mills / (1000 * 60));
                    int hours = mins / 60;
                    if (mins == 0)
                        timeHint = this.context.getString(R.string.nozzleView_trxFinishedTimeHint0);
                    else if (mins <= 60)
                        timeHint = mins + this.context.getString(R.string.nozzleView_trxFinishedTimeHint1);
                    else if (mins > 60)
                        timeHint = hours + this.context.getString(R.string.nozzleView_trxFinishedTimeHint2);
                }

                ((TextView) oneTrxSummaryView.findViewById(R.id.timeExpiredHintTextView)).setText(timeHint);

                // resolve state imageView
                ImageView trxStateImageView = (ImageView) oneTrxSummaryView.findViewById(R.id.trxStateImageView);
                if (this.nozzle.getTransactions().get(i).State == FuelSaleTransactionState.Locked) {
                    trxStateImageView.setImageResource(R.drawable.transaction_state_locked);
                    if (FdcClient.getDefault().getDefaultWorkstationId().equals(this.nozzle.getTransactions().get(i).ReservingDeviceId))
                        ((TextView) oneTrxSummaryView.findViewById(R.id.lockedByWhoHintTextView)).setText(this.context.getString(R.string.transactionSummaryView_TrxReservedByThisPosHint));
                    else
                        ((TextView) oneTrxSummaryView.findViewById(R.id.lockedByWhoHintTextView))
                                .setText(String.format(this.context.getString(R.string.transactionSummaryView_TrxReservedByTextView), this.nozzle.getTransactions().get(i).ReservingDeviceId));
                } else if (this.nozzle.getTransactions().get(i).State == FuelSaleTransactionState.Payable) {
                    trxStateImageView.setImageResource(R.drawable.transaction_state_payable);
                } else if (this.nozzle.getTransactions().get(i).State == FuelSaleTransactionState.Cleared) {
                    trxStateImageView.setImageResource(R.drawable.transaction_state_cleared);
                }

                // put in the hidden sequence number, for reverse find the trx object.
                ((TextView) oneTrxSummaryView.findViewById(R.id.TrxSequenceNo_hidden)).setText(Integer.toString(this.nozzle.getTransactions().get(i).TransactionSeqNo));


                // vertical, and using weight, so set 0dp.
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
                lp.weight = trx_summary_LinearLayout.getWeightSum() / this.nozzle.getTransactions().size();
                trx_summary_LinearLayout.addView(oneTrxSummaryView, lp);
                fileLogger.debug("##########one trx summary added");
            }
        } else {
            TextView noTrxRemindTextView = new TextView(this.context);
            noTrxRemindTextView.setText(this.context.getString(R.string.nozzleView_defaultNoTrxHint));
            trx_summary_LinearLayout.addView(noTrxRemindTextView);
        }
    }

    public Nozzle getBindNozzle() {
        return this.nozzle;
    }

    @Override
    public void onPropertyChanged(Object sender, String propertyName) {
        if ((sender instanceof Nozzle)) {
            Nozzle n = (Nozzle) sender;
            fileLogger.debug("On nozzle " + this.nozzle.NozzleNo + " changed, PropertyName: " + propertyName);
            if (propertyName.equals("Transaction") || propertyName.equals("TransactionUpdated")) {
                updateUI();
            } else if (propertyName.equals("State")) {
                if (n.getCurrentState() == PumpOrNozzleState.Calling) {
                    this.availableDotImageView.setBackgroundResource(R.drawable.nozzle_view_one_nozzle_on_calling_animation);
                    this.onCallingAnimation = (AnimationDrawable) this.availableDotImageView.getBackground();
                    this.onCallingAnimation.start();
                } else {
                    if (this.onCallingAnimation != null) {
                        this.onCallingAnimation.stop();
                        this.availableDotImageView.setBackground(ContextCompat.getDrawable(this.context, R.drawable.red_dot));
                    }
                }
            } else if (propertyName.equals("OtherUnknown")) {

            }
        }
    }

    private void hiddenUnavailableNozzleRedDotImageView(Nozzle availableNozzle) {
        findViewWithTag("nozzle_view__nozzles_dot_array_0").setAlpha(0.2f);
        findViewWithTag("nozzle_view__nozzles_dot_array_1").setAlpha(0.2f);
        findViewWithTag("nozzle_view__nozzles_dot_array_2").setAlpha(0.2f);
        findViewWithTag("nozzle_view__nozzles_dot_array_3").setAlpha(0.2f);
        findViewWithTag("nozzle_view__nozzles_dot_array_4").setAlpha(0.2f);
        findViewWithTag("nozzle_view__nozzles_dot_array_" + (availableNozzle.NozzleNo - 1)).setAlpha(1f);
    }
}
