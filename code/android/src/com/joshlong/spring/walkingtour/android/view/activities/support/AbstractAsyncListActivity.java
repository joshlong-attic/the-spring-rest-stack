package com.joshlong.spring.walkingtour.android.view.activities.support;

import android.app.*;
import android.os.Bundle;
import com.joshlong.spring.walkingtour.android.R;
import com.joshlong.spring.walkingtour.android.utils.DaggerInjectionUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * @author Roy Clarkson
 * @author Pierre-Yves Ricau
 * @author Josh Long
 */
public abstract class AbstractAsyncListActivity extends ListActivity implements AsyncActivity  {
    @Inject Bus bus;

    ProgressDialog progressDialog;
    boolean destroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjectionUtils.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.destroyed = true;
    }


    @Override protected void onResume() {
        super.onResume();
         bus.register(this);
    }

    @Override protected void onPause() {
        super.onPause();
         bus.unregister(this);
    }


    public void showLoadingProgressDialog() {
        this.showProgressDialog(getString(R.string.fetching));
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