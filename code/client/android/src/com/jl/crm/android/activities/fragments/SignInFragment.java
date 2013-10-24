package com.jl.crm.android.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.jl.crm.android.CrmConnectionState;
import com.jl.crm.android.activities.MainActivity;
import com.jl.crm.android.activities.MenuContributingFragment;
import com.jl.crm.android.activities.NamedFragment;
import com.jl.crm.android.utils.DaggerInjectionUtils;
import com.jl.crm.android.widget.CrmOAuthFlowWebView;


public class SignInFragment extends SherlockFragment implements NamedFragment, MenuContributingFragment {

     CrmConnectionState crmConnectionState;
    CrmOAuthFlowWebView webView;
    String signInTitle;
    MainActivity mainActivity;

    public SignInFragment(MainActivity mainActivity, CrmConnectionState crmConnectionState,  String signInTitle) {
        super();
        this.mainActivity = mainActivity;
        this.crmConnectionState = crmConnectionState;
         this.signInTitle = signInTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.webView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjectionUtils.inject(this);
        setHasOptionsMenu(true);
        setWebView(crmConnectionState.webView());
    }

    public void setWebView(CrmOAuthFlowWebView wb) {
        this.webView = wb;
         signout();
    }

    public void signout(){
        if (this.webView != null) {
            this.webView.noAccessToken();
        }
    }

    @Override
    public String getTitle() {
        return signInTitle;
    }
}
