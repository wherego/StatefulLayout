package com.gturedi.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Android layout to show most common state templates like loading, empty, error etc. To do that all you need to is
 * wrap the target area(view) with StatefulLayout. For more information about usage look
 * <a href="https://github.com/gturedi/StatefulLayout#usage">here</a>
 */
public class StatefulLayout
        extends LinearLayout {

    private static final String MSG_ONE_CHILD = "StatefulLayout must have one child!";

    private LinearLayout stContainer;
    private ProgressBar stProgress;
    private ImageView stImage;
    private View content;
    private TextView stMessage;
    private Button stButton;
    private Animation in;
    private Animation out;

    public StatefulLayout(Context context) {
        super(context);
    }

    public StatefulLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatefulLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) throw new IllegalStateException(MSG_ONE_CHILD);
        setOrientation(VERTICAL);
        if (isInEditMode()) return; // to initSate state views in designer
        content = getChildAt(0);
        LayoutInflater.from(getContext()).inflate(R.layout.stateful_layout, this, true);
        stContainer = (LinearLayout) findViewById(R.id.stContainer);
        stProgress = (ProgressBar) findViewById(R.id.stProgress);
        stImage = (ImageView) findViewById(R.id.stImage);
        stMessage = (TextView) findViewById(R.id.stMessage);
        stButton = (Button) findViewById(R.id.stButton);

        in = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        //in.setDuration(1000);
        //in.setFillAfter(true);
        out = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        //out.setDuration(1000);
        //out.setFillAfter(true);
    }

    // content //

    public void showContent() {
        if (stContainer.getVisibility() == VISIBLE) {
            out.setAnimationListener(new CustomAnimationListener(){
                @Override
                public void onAnimationEnd(Animation animation) {
                    stContainer.setVisibility(GONE);
                    content.setVisibility(VISIBLE);
                    in.setAnimationListener(null);
                    content.startAnimation(in);
                }
            });
            stContainer.startAnimation(out);
        }
    }

    // loading //

    public void showLoading() {
        showLoading("");
    }

    public void showLoading(@StringRes int resId) {
        showLoading(str(resId));
    }

    public void showLoading(String message) {
        initSate();
        stProgress.setVisibility(VISIBLE);
        if (!TextUtils.isEmpty(message)) {
            stMessage.setVisibility(VISIBLE);
            stMessage.setText(message);
        }
    }

    // empty //

    public void showEmpty() {
        showEmpty("");
    }

    public void showEmpty(@StringRes int resId) {
        showEmpty(str(resId));
    }

    public void showEmpty(String message) {
        showStateByType(ErrorStateType.EMPTY, message, null);
    }

    // error //

    public void showError(OnClickListener clickListener) {
        showError("", clickListener);
    }

    public void showError(@StringRes int resId, OnClickListener clickListener) {
        showError(str(resId), clickListener);
    }

    public void showError(String message, OnClickListener clickListener) {
        showStateByType(ErrorStateType.ERROR, message, clickListener);
    }

    // offline

    public void showOffline(OnClickListener clickListener) {
        showOffline("", clickListener);
    }

    public void showOffline(@StringRes int resId, OnClickListener clickListener) {
        showOffline(str(resId), clickListener);
    }

    public void showOffline(String message, OnClickListener clickListener) {
        showStateByType(ErrorStateType.OFFLINE, message, clickListener);
    }

    // location off //

    public void showLocationOff(OnClickListener clickListener) {
        showLocationOff("", clickListener);
    }

    public void showLocationOff(@StringRes int resId, OnClickListener clickListener) {
        showLocationOff(str(resId), clickListener);
    }

    public void showLocationOff(String message, OnClickListener clickListener) {
        showStateByType(ErrorStateType.LOCATION_OFF, message, clickListener);
    }

    // custom //

    /**
     * Shows custom state for given options. If you do not set buttonClickListener, the button will not be shown
     * @param options customization options
     * @see com.gturedi.views.CustomStateOptions
     */
    public void showCustom(CustomStateOptions options) {
        initSate();

        if (options.getImageRes() != 0) {
            stImage.setVisibility(VISIBLE);
            stImage.setImageResource(options.getImageRes());
        }

        if (!TextUtils.isEmpty(options.getMessage())) {
            stMessage.setVisibility(VISIBLE);
            stMessage.setText(options.getMessage());
        }

        if (options.getButtonText() != null) {
            stButton.setVisibility(VISIBLE);
            stButton.setOnClickListener(options.getClickListener());
            if (!TextUtils.isEmpty(options.getButtonText())) {
                stButton.setText(options.getButtonText());
            }
        }
    }

    // helper methods //

    private void showStateByType(ErrorStateType type, String message, OnClickListener clickListener) {
        initSate();
        stImage.setVisibility(VISIBLE);
        stImage.setImageResource(type.imageRes);

        stMessage.setVisibility(VISIBLE);
        if (TextUtils.isEmpty(message)) {
            stMessage.setText(type.messageRes);
        } else {
            stMessage.setText(message);
        }

        if (clickListener == null) {
            stButton.setVisibility(GONE);
        } else {
            stButton.setVisibility(VISIBLE);
            stButton.setOnClickListener(clickListener);
        }
    }

    private void initSate() {
        stProgress.setVisibility(GONE);
        stImage.setVisibility(GONE);
        stMessage.setVisibility(GONE);
        stButton.setVisibility(GONE);

        content.clearAnimation();
        stContainer.clearAnimation();

        if (stContainer.getVisibility() != VISIBLE) {
            out.setAnimationListener(new CustomAnimationListener(){
                @Override
                public void onAnimationEnd(Animation animation) {
                    content.setVisibility(GONE);
                    stContainer.setVisibility(VISIBLE);
                    in.setAnimationListener(null);
                    stContainer.startAnimation(in);
                }
            });
            content.startAnimation(out);
        }
    }

    private String str(@StringRes int resId) {
        return getContext().getString(resId);
    }

}
