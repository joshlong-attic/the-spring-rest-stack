package com.jl.crm.android.activities;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
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
import org.springframework.util.StringUtils;

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
            return "No Title Given for " + fragment.getClass().getSimpleName();
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
    // fragments
    SignInFragment signInFragment;
    CustomerSearchFragment searchFragment;
    SignOutFragment signOutFragment;
    WelcomeFragment welcomeFragment;
    ProfilePhotoFragment userAccountFragment;
    TextView searchTextView;
    Fragment selectedFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // todo there's a major NPE being caused here on shutdown.
        // super.onSaveInstanceState(outState);
        // getSherlock().dispatchSaveInstanceState(outState);
    }

    public void signout() {
        runOnUiThread(this.connectionNotEstablishedRunnable);
    }

    public void signin(final User user) {
        restoreSelectedFragment();

        for (AuthenticatedFragment f : securedFragments) {
            f.setCurrentUser(user);
        }

    }

    protected void showDefaultSearch() {
        showSearch();
        searchFragment.loadAllCustomers();
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
                getString(R.string.oauth_access_token_callback_uri));
    }

    public void showSearch() {
        show(this.searchFragment);
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
        viewPager.setOffscreenPageLimit(this.allFragments.size());

        show(this.welcomeFragment);
    }

    public void setupFragments() {

        this.signInFragment = new SignInFragment(this, this.crmConnectionState, this.connectionEstablishedRunnable, getString(R.string.sign_in));
        this.welcomeFragment = new WelcomeFragment();

        addFragments(this.welcomeFragment, this.signInFragment);

        this.userAccountFragment = new ProfilePhotoFragment(this, this.crmOperationsProvider, getString(R.string.user_account));
        this.searchFragment = new CustomerSearchFragment(this, this.crmOperationsProvider, getString(R.string.search), getString(R.string.search_hint));
        this.signOutFragment = new SignOutFragment(this, getString(R.string.sign_out));
        addSecuredFragments(this.searchFragment, this.userAccountFragment, this.signOutFragment);
    }


    protected void runQuery(String query) {
        if (!StringUtils.hasText(query)) {
            searchFragment.loadAllCustomers();
        } else {
            searchFragment.search(query);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        final Activity activity = this;
        final String tag = getClass().getSimpleName();

        // search
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = new SearchView(activity);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setIconified(true);
        searchManager.setOnCancelListener(new SearchManager.OnCancelListener() {
            @Override
            public void onCancel() {
                runQuery(searchTextView.getText().toString());
            }
        });
        searchManager.setOnDismissListener(new SearchManager.OnDismissListener() {
            @Override
            public void onDismiss() {
                runQuery(searchTextView.getText().toString());
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(tag, "query text submitted: " + query);
                runQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(tag, "query text changed: " + newText);
                if (searchFragment.isAdded() && searchFragment.isResumed()) {
                    runQuery(newText);
                }
                return true;
            }
        });

        searchTextView = (TextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        searchTextView.setTextColor(Color.WHITE);

        // menu items

        MenuItem searchMenuItem = MenuItemUtils.menuItem(menu, this, getString(R.string.search), searchView, null, MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem signOutMenuItem = MenuItemUtils.menuItem(menu, this, getString(R.string.sign_out), null, new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                signout();
                return true;
            }
        });
        MenuItem userAccountMenuItem = MenuItemUtils.menuItem(menu, this, getString(R.string.user_account), null, new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showUserAccount();
                return true;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void showUserAccount() {
        show(this.userAccountFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(MainActivity.class.getName(), "onStart()");
        this.crmConnectionState.start(this.connectionEstablishedRunnable, this.connectionNotEstablishedRunnable);
    }


    private void restoreSelectedFragment() {
        Fragment current = this.selectedFragment;
        if (null != current) {
            show(current);
        } else {
            showUserAccount();
        }
    }

    public void show(Fragment f) {
        int position = this.allFragments.indexOf(f);
        viewPager.setCurrentItem(position, true);

        this.selectedFragment = f;

    }


}
