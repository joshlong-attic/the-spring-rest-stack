package com.joshlong.spring.walkingtour.android.view.activities.support;

import android.app.*;
import android.os.Bundle;
import android.view.Window;
import android.webkit.*;
import com.joshlong.spring.walkingtour.android.R;


/**
 * @author Roy Clarkson
 */
public abstract class AbstractWebViewActivity extends AbstractActivity implements AsyncActivity {

    protected static final String TAG = AbstractWebViewActivity.class.getSimpleName();

    private Activity activity;

    private WebView webView;

    private ProgressDialog progressDialog = null;

    private boolean destroyed = false;

    // ***************************************
    // Activity methods
    // ***************************************


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        this.webView = new WebView(this);
        setContentView(this.webView);
        this.activity = this;

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setTitle("Loading...");
                activity.setProgress(progress * 100);
                if (progress == 100) {
                    activity.setTitle(R.string.app_name);
                }
            }
        });
    }

    // ***************************************
    // Protected methods
    // ***************************************
    protected WebView getWebView() {
        return this.webView;
    }

    // ***************************************
    // Public methods
    // ***************************************
    public void showLoadingProgressDialog() {
        showProgressDialog("Loading. Please wait...");
    }

    public void showProgressDialog(CharSequence message) {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setIndeterminate(true);
        }

        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (this.progressDialog != null && !this.destroyed) {
            this.progressDialog.dismiss();
        }
    }

}

