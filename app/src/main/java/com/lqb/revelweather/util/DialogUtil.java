package com.lqb.revelweather.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.lqb.revelweather.R;

import java.lang.ref.WeakReference;

public class DialogUtil {
    private static ProgressDialog progressDialog;  // 进度对话框
    private static WeakReference<Activity> mWeakReference;

    public static void showProgressDialog(Activity activity, String message){
        showProgressDialog(activity,message,false);
    }

    private static void showProgressDialog(Activity activity, String message, boolean flag) {
        if(!isLiving(activity)){
            return;
        }

        if(mWeakReference == null){
            mWeakReference = new WeakReference(activity);
        }

        activity = mWeakReference.get();

        if (progressDialog == null) {
            if (activity.getParent() != null) {
                progressDialog = new ProgressDialog(activity.getParent());
            } else {
                progressDialog = new ProgressDialog(activity);
            }
        }

        if (!progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(flag);
            progressDialog.show();
        } else {
            progressDialog.setMessage(message);
        }

    }

    /*
     * 隐藏提示加载
     */
    public static void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && isExist_Living(mWeakReference)) {
            progressDialog.dismiss();
            progressDialog = null;
            mWeakReference.clear();
            mWeakReference = null;
        }
    }

    private static boolean isExist_Living(WeakReference<Activity> weakReference) {

        if(weakReference != null) {
            Activity activity = weakReference.get();
            return activity != null && !activity.isFinishing();

        }

        return false;
    }

    /**
     * 判断activity是否存活
     * */
    private static boolean isLiving(Activity activity) {
        return activity != null && !activity.isFinishing();
    }

//    private static void showProgressDialog(Activity activity, String message, boolean flag) {
//        if (progressDialog == null) {
//            progressDialog = new android.app.ProgressDialog(activity);
//            progressDialog.setMessage(message);
//            progressDialog.setCanceledOnTouchOutside(false);
//        } else if (progressDialog.isShowing() && isExist_Living(mWeakReference)) {
//            progressDialog.setMessage(message);
//        }
//        progressDialog.show();
//    }
//
//    public static void closeProgressDialog(boolean flag){
//        if (progressDialog != null && progressDialog.isShowing() && flag) {
//            progressDialog.dismiss();
//            progressDialog = null;
//            mWeakReference.clear();
//            mWeakReference = null;
//        }
//    }
}
