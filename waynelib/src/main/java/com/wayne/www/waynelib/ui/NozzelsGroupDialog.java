package com.wayne.www.waynelib.ui;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Think on 5/12/2016.
 */
public class NozzelsGroupDialog extends DialogFragment {
    private NozzlesGroupView view;
    private int height;
    private int width;

    public NozzelsGroupDialog() {
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = (NozzlesGroupView) getArguments().getSerializable("view");
        this.height = getArguments().getInt("height");
        this.width = getArguments().getInt("width");
        getDialog().setCanceledOnTouchOutside(true);

        if (getArguments() != null) {
        }

        this.view.setLayoutParams(new ViewGroup.LayoutParams(this.width, this.height));
        return this.view;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = this.width;
        params.height = this.height;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.view.removeAllOnPropertyChangedListener();
    }
}
