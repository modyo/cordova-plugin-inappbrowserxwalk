package com.example.plugin.InAppBrowserXwalk;

import com.example.plugin.InAppBrowserXwalk.BrowserDialog;

import android.content.res.Resources;
import org.apache.cordova.*;
import org.apache.cordova.PluginManager;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.internal.XWalkViewInternal;
import org.xwalk.core.XWalkCookieManager;

import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Typeface;
import android.widget.Toast;

import android.text.InputType;
import android.widget.EditText;
import android.view.KeyEvent;

import android.webkit.WebResourceResponse;

public class InAppBrowserXwalk extends CordovaPlugin {

    private BrowserDialog dialog;
    private XWalkView xWalkWebView;
    private CallbackContext callbackContext;

    private TextView locationUrlButton;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if(action.equals("open")) {
            this.callbackContext = callbackContext;
            this.openBrowser(data);
        }

        if(action.equals("loadFromManifest")) {
            this.callbackContext = callbackContext;
            this.loadBrowser(data);
        }

        if(action.equals("close")) {
            this.closeBrowser();
        }

        if(action.equals("show")) {
            this.showBrowser();
        }

        if(action.equals("hide")) {
            this.hideBrowser();
        }

        if(action.equals("executeScript")) {
            this.callbackContext = callbackContext;
            this.executeScript(data);
        }

        if(action.equals("injectStyleCode")) {
            this.callbackContext = callbackContext;
            this.injectStyleCode(data);
        }

        return true;
    }

    class MyResourceClient extends XWalkResourceClient {
           MyResourceClient(XWalkView view) {
               super(view);
           }

           @Override
           public void onLoadStarted (XWalkView view, String url) {

               try {
                   JSONObject obj = new JSONObject();
                   obj.put("type", "loadstart");
                   obj.put("url", url);
                   PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                   result.setKeepCallback(true);
                   callbackContext.sendPluginResult(result);
               } catch (JSONException ex) {}
           }

           @Override
           public void onLoadFinished (XWalkView view, String url) {

                if (locationUrlButton!=null)
                {
                    locationUrlButton.setText(url);
                }


               try {
                   JSONObject obj = new JSONObject();
                   obj.put("type", "loadstop");
                   obj.put("url", url);
                   PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                   result.setKeepCallback(true);
                   callbackContext.sendPluginResult(result);
               } catch (JSONException ex) {}
           }


           @Override
           public WebResourceResponse shouldInterceptLoadRequest (XWalkView view, String url) {
               try {
                   JSONObject obj = new JSONObject();
                   obj.put("type", "shouldInterceptLoadRequest");
                   obj.put("url", url);
                   PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                   result.setKeepCallback(true);
                   callbackContext.sendPluginResult(result);
               } catch (JSONException ex) {}

                return super.shouldInterceptLoadRequest(view, url);
           }
    }

    private void openBrowser(final JSONArray data) throws JSONException {
        final String url = data.getString(0);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new BrowserDialog(cordova.getActivity(), android.R.style.Theme_NoTitleBar);
                xWalkWebView = new XWalkView(cordova.getActivity(), cordova.getActivity());
                XWalkCookieManager mCookieManager = new XWalkCookieManager();
                mCookieManager.setAcceptCookie(true);
                mCookieManager.setAcceptFileSchemeCookies(true);
                xWalkWebView.setResourceClient(new MyResourceClient(xWalkWebView));
                xWalkWebView.load(url, "");

                locationUrlButton = new TextView(cordova.getActivity());

                setupInAppBrowser(data, url);
            }
        });
    }

    private void loadBrowser(final JSONArray data) throws JSONException {
        final String pathManifest = data.getString(0);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new BrowserDialog(cordova.getActivity(), android.R.style.Theme_NoTitleBar);
                xWalkWebView = new XWalkView(cordova.getActivity(), cordova.getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.FILL_PARENT );
                lp.weight = 1;
                lp.height = LayoutParams.FILL_PARENT;

                xWalkWebView.setLayoutParams(lp);
                xWalkWebView.getSettings().setUseWideViewPort(true);
                xWalkWebView.setInitialScale(100);XWalkCookieManager

                mCookieManager = new XWalkCookieManager();
                mCookieManager.setAcceptCookie(true);
                mCookieManager.setAcceptFileSchemeCookies(true);

                xWalkWebView.setResourceClient(new MyResourceClient(xWalkWebView));
                xWalkWebView.loadAppFromManifest(pathManifest, null);

                locationUrlButton = new TextView(cordova.getActivity());

                setupInAppBrowser(data, "Loading ....");
            }
        });
    }

    private void setupInAppBrowser (final JSONArray data, final String locationTitle){

                android.util.Log.v("InAppBrowserXwalk", "setupInAppBrowser started");

                String toolbarTopColor = "#FFFFFF";
                String toolbarBottomColor = "#FFFFFF";

                int toolbarTopHeight = 80;
                int toolbarBottomHeight = 80;

                String closeButtonText = "< Close";
                int closeButtonSize = 25;
                String closeButtonColor = "#000000";

                int locationUrlSize = 25;
                String locationUrlColor = "#000000";

                boolean openHidden = false;

                if(data != null && data.length() > 1) {

                    android.util.Log.v("InAppBrowserXwalk", "json parsing started");

                    try {
                            JSONObject options = new JSONObject(data.getString(1));

                            if(!options.isNull("toolbarTopColor")) {
                                toolbarTopColor = options.getString("toolbarTopColor");
                                android.util.Log.v("InAppBrowserXwalk", "toolbarTopColor:" + toolbarTopColor);
                            }
                            if(!options.isNull("toolbarBottomColor")) {
                                toolbarBottomColor = options.getString("toolbarBottomColor");
                                android.util.Log.v("InAppBrowserXwalk", "toolbarBottomColor:" + toolbarBottomColor);
                            }
                            if(!options.isNull("toolbarTopHeight")) {
                                toolbarTopHeight = options.getInt("toolbarTopHeight");
                                android.util.Log.v("InAppBrowserXwalk", "toolbarTopHeight:" + toolbarTopHeight);
                            }
                            if(!options.isNull("toolbarBottomHeight")) {
                                toolbarBottomHeight = options.getInt("toolbarBottomHeight");
                                android.util.Log.v("InAppBrowserXwalk", "toolbarBottomHeight:" + toolbarBottomHeight);
                            }
                            if(!options.isNull("closeButtonText")) {
                                closeButtonText = options.getString("closeButtonText");
                                android.util.Log.v("InAppBrowserXwalk", "closeButtonText:" + closeButtonText);
                            }
                            if(!options.isNull("closeButtonSize")) {
                                closeButtonSize = options.getInt("closeButtonSize");
                                android.util.Log.v("InAppBrowserXwalk", "closeButtonSize:" + closeButtonSize);
                            }
                            if(!options.isNull("closeButtonColor")) {
                                closeButtonColor = options.getString("closeButtonColor");
                                android.util.Log.v("InAppBrowserXwalk", "closeButtonColor:" + closeButtonColor);
                            }
                            if(!options.isNull("locationUrlColor")) {
                                locationUrlColor = options.getString("locationUrlColor");
                                android.util.Log.v("InAppBrowserXwalk", "locationUrlColor:" + locationUrlColor);
                            }
                            if(!options.isNull("locationUrlSize")) {
                                locationUrlSize = options.getInt("locationUrlSize");
                                android.util.Log.v("InAppBrowserXwalk", "locationUrlSize:" + locationUrlSize);
                            }
                            if(!options.isNull("openHidden")) {
                                openHidden = options.getBoolean("openHidden");
                                android.util.Log.v("InAppBrowserXwalk", "openHidden:" + openHidden);
                            }
                        }
                    catch (JSONException ex) {
                        android.util.Log.v("InAppBrowserXwalk", "json parsing error ex:" + ex);
                    }
                }

                LinearLayout main = new LinearLayout(cordova.getActivity());
                main.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout toolbarTop = new RelativeLayout(cordova.getActivity());
                toolbarTop.setBackgroundColor(android.graphics.Color.parseColor(toolbarTopColor));
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, toolbarTopHeight);
                lp2.weight = 0;
                toolbarTop.setLayoutParams(lp2);
                toolbarTop.setPadding(30, 5, 5, 5);

                RelativeLayout toolbarBottom = new RelativeLayout(cordova.getActivity());
                toolbarBottom.setBackgroundColor(android.graphics.Color.parseColor(toolbarBottomColor));
                LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, toolbarBottomHeight);
                lp3.weight = 0;
                toolbarBottom.setLayoutParams(lp3);
                toolbarBottom.setPadding(15, 5, 5, 5);

                TextView closeButton = new TextView(cordova.getActivity());
                closeButton.setText(closeButtonText);
                closeButton.setTextSize(closeButtonSize);
                closeButton.setTextColor(android.graphics.Color.parseColor(closeButtonColor));
                closeButton.setTypeface(Typeface.create("sans-serif-thin", Typeface.BOLD));
                toolbarTop.addView(closeButton);

                closeButton.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                         closeBrowser();
                     }
                 });

                locationUrlButton.setText(locationTitle);
                locationUrlButton.setTextSize(locationUrlSize);
                locationUrlButton.setTextColor(android.graphics.Color.parseColor(locationUrlColor));
                locationUrlButton.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
                locationUrlButton.setSingleLine(true);
                locationUrlButton.setEllipsize( android.text.TextUtils.TruncateAt.END);

                locationUrlButton.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                         //do nothing
                     }
                 });

                toolbarBottom.addView(locationUrlButton);

                main.addView(toolbarTop);
                main.addView(xWalkWebView);
                main.addView(toolbarBottom);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                dialog.setCancelable(true);
                LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
                dialog.addContentView(main, layoutParams);
                if(!openHidden) {
                    dialog.show();
                }
                android.util.Log.v("InAppBrowserXwalk", "setupFinished");
    }

    public void hideBrowser() {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dialog != null) {
                    dialog.hide();
                }
            }
        });
    }

    public void showBrowser() {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dialog != null) {
                    dialog.show();
                }
            }
        });
    }

    public void closeBrowser() {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                xWalkWebView.onDestroy();
                dialog.dismiss();
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "exit");
                    PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                } catch (JSONException ex) {}
            }
        });
    }

    public void executeScript(JSONArray data) throws JSONException {

        final String javascript = data.getString(0);

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                xWalkWebView.evaluateJavascript(javascript,null); //warning: it seems that another instace is open.

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "executeScript");
                    obj.put("javascript", javascript);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                } catch (JSONException ex) {}
            }
        });
    }


    public void injectStyleCode(JSONArray data) throws JSONException {

        final String style = data.getString(0);

        String jsWrapper = "(function(d) { var c = d.createElement('style'); c.innerHTML = %s; d.body.appendChild(c); })(document)";

        android.util.Log.v("InAppBrowserXwalk", "run jsWrapper:" + jsWrapper);

        String scriptToInject;

        org.json.JSONArray jsonEsc = new org.json.JSONArray();
        jsonEsc.put(style);
        String jsonRepr = jsonEsc.toString();
        String jsonSourceString = jsonRepr.substring(1, jsonRepr.length()-1);
        scriptToInject = String.format(jsWrapper, jsonSourceString);

        final String finalScriptToInject = scriptToInject;


        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                android.util.Log.v("InAppBrowserXwalk", "run css finalScriptToInject:" + finalScriptToInject);

                //xWalkWebView.load("javascript:" + finalScriptToInject, "");
                xWalkWebView.evaluateJavascript(finalScriptToInject, null);

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "injectStyleCode");
                    obj.put("injectStyleCode", finalScriptToInject);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                } catch (JSONException ex) {}
            }
        });
    }
}
