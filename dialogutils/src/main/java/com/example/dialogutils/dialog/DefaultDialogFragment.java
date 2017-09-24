package com.example.dialogutils.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.dialogutils.R;

/**
 * Created by Administrator on 2017/9/20.
 */

public class DefaultDialogFragment extends DialogFragment {
    private int layoutResID = 0;
    private View view = null;
    /**
     * 默认透明背景
     */
    private int color = Color.TRANSPARENT;

    /**
     * 默认点击外面无效
     */
    private boolean onTouchOutside = false;

    /**
     * 默认的动画
     */
    private int animation = R.style.Animation_Dialog;

    public DefaultDialogFragment setAnimation(int animation) {
        this.animation = animation;
        return this;
    }

    /**
     * 设置背景色
     * @param color 色值
     * @return 色值
     */
    public DefaultDialogFragment setBackgroundColor(int color) {
        if (color != 0) {
            this.color = color;
        }
        return this;

    }

    /**
     * 设置点击外面是否有效
     * @param touchOutside boolean
     */
    public DefaultDialogFragment setTouchOutside(boolean touchOutside) {
        this.onTouchOutside = touchOutside;
        return this;
    }

    /**
     * 设置布局文件的ID
     * @param layoutResID 布局文件
     */
    public void setContentView(int layoutResID) {
        this.layoutResID = layoutResID;
    }

    /**
     * 设置布局文件的View对象
     * @param view 视图
     */
    public void setContentView(View view) {
        this.view = view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        Window window = dialog.getWindow();
        if (window != null) {
            //设置Dialog进入和退出的动画
            window.getAttributes().windowAnimations = animation;
            //去掉标题
            window.requestFeature(Window.FEATURE_NO_TITLE);
            //窗口占满整个屏幕不留任何边界
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            //设置背景色
            window.setBackgroundDrawable(new ColorDrawable(color));
        }
        //点击外界是否无效
        dialog.setCanceledOnTouchOutside(onTouchOutside);
        if (view != null) {
            dialog.setContentView(view);
        } else if (layoutResID != 0) {
            dialog.setContentView(layoutResID);
        }
        return dialog;
    }

}
