package com.jl.crm.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import com.jl.crm.android.CrmConnectionState;
import com.jl.crm.android.utils.DaggerInjectionUtils;
import com.jl.crm.android.widget.CrmOAuthFlowWebView;

/** @author Josh Long */
public class AuthenticationFragment extends Fragment {


	Runnable connectionEstablishedRunnable;
	CrmConnectionState crmConnectionState;
	CrmOAuthFlowWebView webView;
	boolean debug = false;

	public AuthenticationFragment(CrmConnectionState crmConnectionState, Runnable connectionEstablishedRunnable) {
		super();
		this.crmConnectionState = crmConnectionState;
		this.connectionEstablishedRunnable = connectionEstablishedRunnable;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return this.webView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DaggerInjectionUtils.inject(this);

		this.webView = crmConnectionState.webView();

		if (debug){
			crmConnectionState.resetLocalConnections();
		}

		this.webView.noAccessToken();


	}

}
