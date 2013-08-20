package com.jl.crm.android.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.jl.crm.android.CrmConnectionState;
import com.jl.crm.android.R;
import com.jl.crm.android.activities.fragments.*;
import com.jl.crm.android.activities.secure.AuthenticatedFragment;
import com.jl.crm.android.utils.DaggerInjectionUtils;
import com.jl.crm.client.CrmConnectionFactory;
import com.jl.crm.client.CrmOperations;
import com.jl.crm.client.User;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A new beginning.
 *
 * @author Josh Long
 */
public class MainActivity extends SherlockFragmentActivity {

    FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return allFragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Fragment fragment = getItem(position);
            if (fragment instanceof NamedFragment) {
                return ((NamedFragment) fragment).getTitle();
            }
            return null;
        }

        @Override
        public int getCount() {
            return allFragments.size();
        }
    };
    List<AuthenticatedFragment> securedFragments = new ArrayList<AuthenticatedFragment>();
    List<Fragment> unsecuredFragments = new ArrayList<Fragment>();
    List<Fragment> allFragments = new ArrayList<Fragment>();
    ViewPager viewPager;
    @Inject
    SQLiteConnectionRepository sqLiteConnectionRepository;
    @Inject
    SQLiteConnectionRepositoryHelper repositoryHelper;
    @Inject
    CrmConnectionFactory connectionFactory;
    @Inject
    Provider<CrmOperations> crmOperationsProvider;
    CrmConnectionState crmConnectionState;
    Runnable connectionNotEstablishedRunnable = new Runnable() {
        @Override
        public void run() {
            String tag = MainActivity.class.getName();
            Log.d(tag, "a connection either exists and is invalid or it doesn't exist." +
                    " To reset everything, we're removing existing (stale) connection information");
            crmConnectionState.resetLocalConnections();
            Log.d(tag, "loading the authentication fragment.");
            show(signInFragment);
        }
    };
    Runnable connectionEstablishedRunnable = new Runnable() {
        @Override
        public void run() {
            User currentUser = crmOperationsProvider.get().currentUser();
            signin(currentUser);
        }
    };
    // fragment s
    SignInFragment signInFragment;
    CustomerSearchFragment searchFragment;
    SignOutFragment signOutFragment;
    WelcomeFragment welcomeFragment;
    UserProfileFragment  userProfileFragment;

    // todo
    public void signout() {
        runOnUiThread(this.connectionNotEstablishedRunnable);
    }

    public void signin(final User user) {

        for (AuthenticatedFragment f : securedFragments) {
            f.setCurrentUser(user);
        }
        show(searchFragment);
    }

    private void addFragments(Fragment... fragments) {
        Collections.addAll(this.unsecuredFragments, fragments);
        addToAllFragments(fragments);
    }

    public <T extends Fragment & AuthenticatedFragment> void addSecuredFragments(T... ts) {
        Collections.addAll(this.securedFragments, ts);
        addToAllFragments(ts);
    }

    private void addToAllFragments(Fragment... ts) {
        for (Fragment f : ts)
            f.setHasOptionsMenu(true);
        Collections.addAll(this.allFragments, ts);
    }

    protected void setupCommonInfrastructure() {
        this.crmConnectionState = new CrmConnectionState(this,
                connectionFactory,
                repositoryHelper,
                sqLiteConnectionRepository,
                this.connectionEstablishedRunnable,
                this.connectionNotEstablishedRunnable,
                getString(R.string.oauth_access_token_callback_uri));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjectionUtils.inject(this);
        setContentView(R.layout.main_activity);

        Log.d(MainActivity.class.getName(), "onCreate()");
        setupCommonInfrastructure();

        setupFragments();

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(fragmentPagerAdapter);

    }

    public void setupFragments() {

        this.signInFragment = new SignInFragment(this, this.crmConnectionState, this.connectionEstablishedRunnable, getString(R.string.sign_in));
        this.welcomeFragment = new WelcomeFragment();

        addFragments(this.welcomeFragment, this.signInFragment);

        this.userProfileFragment =new UserProfileFragment( this,getString(R.string.user_account));
        this.searchFragment = new CustomerSearchFragment(this, this.crmOperationsProvider, getString(R.string.search), getString(R.string.search_hint));
        this.signOutFragment = new SignOutFragment(this, getString(R.string.sign_out));
        addSecuredFragments(this.searchFragment, this.signOutFragment);


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        for (Fragment f : this.allFragments) {
            if (f instanceof MenuContributingFragment) {
                MenuContributingFragment menuContributingFragment = (MenuContributingFragment) f;
                menuContributingFragment.contributeToMenu(menu);
            }
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(MainActivity.class.getName(), "onStart()");
        show(this.welcomeFragment); // we want this displaying no matter what...
        this.crmConnectionState.start();
    }

    public void show(Fragment f) {
        int position = this.allFragments.indexOf(f);
        viewPager.setCurrentItem(position, true);
    }


}
