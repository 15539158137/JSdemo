package com.dxxx.jsdemo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

import sun.misc.BASE64Encoder;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //测试加密
        String data="需要加密的文字";
        byte[] bytes= new byte[0];
        try {
            bytes = data.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Base64.encode(bytes,Base64.NO_WRAP);
        String base642=new  BASE64Encoder().encode(bytes);
        String base641=   new String(Base64.encode(bytes,Base64.NO_WRAP));
        Log.e("base641",base641);
        Log.e("base642",base642);
        String after = null;
        try {
             after=AesUtils.encrypt(data);
            Log.e("加密成功",after+"");
        } catch (Exception e) {
            e.printStackTrace();
        Log.e("加密失败",e.getMessage()+"");
        }
        try {
            String before=AesUtils.desEncrypt(after);
            Log.e("解密成功",before+"");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("解密失败",e.getMessage()+"");
        }
        webView = findViewById(R.id.web);
        button = findViewById(R.id.button);
        WebSettings webSettings = webView.getSettings();

        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        //把当前页面给js，让js可以调用我的方法，但是4.2以下js可以调用我页面的所有方法，4.2以上加@jsinterface注解解决这个问题
        webView.addJavascriptInterface(new JsMethods(), "Android");
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("file:///android_asset/web.html");
        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Log.e("执行到", "onJsPrompt" + url + "=" + message);
                Uri uri = Uri.parse(message);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("js")) {
                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {
                        // 可以在协议上带有参数并传递到Android上
                        HashMap<String, String> params = new HashMap<>();
                        Set<String> collection = uri.getQueryParameterNames();

                        for (String s : collection) {
                            Log.e("传递的参数方法3" + s, uri.getQueryParameter(s));
                        }

                    }
                    //这是返回值的写法
                    result.confirm("android返回了");
                    return true;
                }

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.e("----", "重定向");
                // 步骤2：根据协议的参数，判断是否是所需要的url
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

                Uri uri = request.getUrl();
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("js")) {

                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {

                        //  步骤3：
                        // 执行JS所需要调用的逻辑
                        System.out.println("js调用了Android的方法");
                        // 可以在协议上带有参数并传递到Android上
                        HashMap<String, String> params = new HashMap<>();
                        Set<String> collection = uri.getQueryParameterNames();

                        for (String s : collection) {
                            Log.e("传递的参数" + s, uri.getQueryParameter(s));
                        }

                    }

                    return true;
                }

                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        getJsMethod();
                    }
                });

            }
        });

    }

    //android调用js的方法
    private void getJsMethod() {
        String data = "传过去的data";
        int androidVersion = Build.VERSION.SDK_INT;
// 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
        if (androidVersion < 18) {
            webView.loadUrl("javascript:callJS('" + data + "')");
        } else {
            webView.evaluateJavascript("javascript:callJS('" + data + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    Log.e("js返回的信息是", value);
                    //js返回的信息是: "传过去的data" 注意这里多了两个引号
                }
            });
        }
    }


    @JavascriptInterface
    public void jsCallAndroid() {
        Log.e("==", "js调用了android的方法jsCallAndroid");
    }
}
