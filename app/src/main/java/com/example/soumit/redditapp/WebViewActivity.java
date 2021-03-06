package com.example.soumit.redditapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Soumit on 2/6/2018.
 */

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = "WebViewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        WebView webView = (WebView) findViewById(R.id.webview);
        final TextView webprogressText = (TextView) findViewById(R.id.webprogressText);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.webviewLoadingProgressbar);
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "onCreate: Started");

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                webprogressText.setText("");
            }
        });

    }
}











