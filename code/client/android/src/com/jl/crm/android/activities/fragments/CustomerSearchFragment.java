package com.jl.crm.android.activities.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.jl.crm.android.R;
import com.jl.crm.android.activities.MainActivity;
import com.jl.crm.android.activities.MenuContributingFragment;
import com.jl.crm.android.activities.NamedFragment;
import com.jl.crm.android.activities.secure.AuthenticatedFragment;
import com.jl.crm.client.CrmOperations;
import com.jl.crm.client.Customer;
import com.jl.crm.client.User;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomerSearchFragment extends SherlockListFragment
        implements MenuContributingFragment, AuthenticatedFragment, NamedFragment {
    Provider<CrmOperations> crmOperationsProvider;
    User currentUser;
    MainActivity mainActivity;
    String hint, title;
    CrmOperations crmOperations;
    private CustomerArrayAdapter listAdapter;
    private List<Customer> customers = new ArrayList<Customer>();

    public CustomerSearchFragment(MainActivity mainActivity,
                                  Provider<CrmOperations> crmOperationsProvider,
                                  String title,
                                  String hint) {
        super();
        this.crmOperationsProvider = crmOperationsProvider;
        this.hint = hint;
        this.title = title;
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean isAuthenticated() {
        return this.currentUser != null;
    }

    @Override
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        this.crmOperations = this.crmOperationsProvider.get();
    }

    @Override
    public void onStart() {
        super.onStart();

        listAdapter = new CustomerArrayAdapter(getSherlockActivity(), customers);
        setListAdapter(listAdapter);

       /* if (isAuthenticated())
            loadAllCustomers();
*/
    }

    /* this is what happens by default */
    public void loadAllCustomers() {
        Log.d(getClass().getName(), "loading all the CRM customers.");
        redrawCustomersWithNewData(crmOperations.loadAllUserCustomers());
    }

    public void search(String query) {
        Log.d(getClass().getName(), "loading only the CRM customers that match a particular query.");
        redrawCustomersWithNewData(crmOperations.search(query));
    }

    private void redrawCustomersWithNewData(Collection<Customer> c) {
        assert this.currentUser != null : "the currentUser can't be null";

        this.customers.clear();
        this.customers.addAll(c);

        listAdapter.notifyDataSetChanged();

        mainActivity.showSearch();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public static class CustomerArrayAdapter extends ArrayAdapter<Customer> {
        private List<Customer> records;

        public CustomerArrayAdapter(Context context, List<Customer> objects) {
            super(context, R.layout.action_bar_search_item, objects);
            this.records = objects;
        }

        @Override
        public int getCount() {
            return this.records.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Customer customer = this.getItem(position);

            final View view = View.inflate(this.getContext(), R.layout.action_bar_search_item, null);

            final TextView name = (TextView) view.findViewById(R.id.name_label);
            name.setText(customer.getFirstName() + " " + customer.getLastName());

            final TextView id = (TextView) view.findViewById(R.id.customer_id);
            id.setAlpha(.7f);
            id.setText(Long.toString(customer.getDatabaseId()));

            return view;
        }
    }

}
