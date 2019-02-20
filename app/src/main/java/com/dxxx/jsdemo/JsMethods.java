package com.dxxx.jsdemo;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsMethods {
    //js调用android的方法
    @JavascriptInterface
    public void jsCallAndroid(String value) {
        Log.e("==", "js调用了android的方法jsCallAndroid"+value);
    }
}
