package com.ekdibagdi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    public InterstitialAd mInterstitialAd;
    public AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.mainWebView);
        JsInterface jsInterface = new JsInterface();
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(url));
                    startActivity(intent);

                    return true;
                }

                return false;
            }
        });
        if(savedInstanceState == null)
        {
            mWebView.loadUrl("file:///android_asset/game/index.html");
        }
        else
        {
            mWebView.restoreState(savedInstanceState);
        }
        mWebView.addJavascriptInterface(jsInterface, "android");
        MobileAds.initialize(this, "...");
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("...").build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("...");
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("...").build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("...").build());
            }
        });
    }
    public void showInterstitial() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });
    }

    public void showAd() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }
    public void remAd() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                mAdView.setVisibility(View.GONE);
            }
        });
    }
    private class JsInterface{
        @JavascriptInterface
        public void ad(){
            showInterstitial();
        }

        @JavascriptInterface
        public void exitit(){finish();}

        @JavascriptInterface
        public void bAd(){
            showAd();
        }

        @JavascriptInterface
        public void bexitit(){
            remAd();
        }

        @JavascriptInterface
        public void url(String urli){
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(urli));
            startActivity(i);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    mWebView.goBack();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }
}
