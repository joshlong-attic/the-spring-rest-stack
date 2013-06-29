package com.joshlong.spring.walkingtour.android.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.joshlong.spring.walkingtour.android.R;
import com.joshlong.spring.walkingtour.android.async.AsyncCallback;
import com.joshlong.spring.walkingtour.android.model.Customer;
import com.joshlong.spring.walkingtour.android.service.CustomerService;
import com.joshlong.spring.walkingtour.android.utils.DaggerInjectionUtils;
import com.joshlong.spring.walkingtour.android.view.activities.support.*;
import com.joshlong.spring.walkingtour.android.view.support.AbstractListAdapter;

import javax.inject.Inject;
import java.util.*;

/**
 * This is the first {@link Activity } displayed to the user.
 *
 * @author Josh Long
 */
public class CustomerListActivity extends AbstractAsyncListActivity implements AsyncActivity {

    List<Customer> customers = Collections.synchronizedList(new ArrayList<Customer>());
    @Inject
    CustomerService customerService;
    EditText filterTextBox;
    CustomerListActivity self = this;
    AbstractListAdapter customerListAdapter = new AbstractListAdapter<Customer>(this.customers) {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(CustomerListActivity.this);
            Customer customer = getItem(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.customer_list_item, parent, false);
            }
            if (customer != null) {
                ((TextView) convertView.findViewById(R.id.id)).setText(Long.toString(customer.getId()));
                ((TextView) convertView.findViewById(R.id.name)).setText(customer.getFirstName() + ' ' + customer.getLastName());
            }
            return convertView;
        }

    };
    AsyncCallback<List<Customer>> asyncUiCallback = new AsyncCallback<List<Customer>>() {
        @Override
        public void methodInvocationCompleted(List<Customer> resultsFromCall) {
            self.customers.clear();
            self.customers.addAll(sortedCustomersToDisplay(resultsFromCall));
            customerListAdapter.notifyDataSetChanged();
        }
    };
    TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String query = editable.toString().trim();
            if (query != null && query.length() > 3){
                customerService.search(query, asyncUiCallback);
            }
            else {
                Log.d(getClass().getName(), "Please specify a search with more than 3 characters " +
                    "before using the #search(String q,AsyncCallback) service.");
            }
        }
    };

    List<Customer> sortedCustomersToDisplay(List<Customer> customerCollection) {
        Collections.sort(customerCollection, new Comparator<Customer>() {
            private String name(Customer c) {
                return String.format("%s %s", "" + c.getFirstName(), "" + c.getLastName());
            }

            @Override
            public int compare(Customer lhs, Customer rhs) {
                return name(lhs).compareTo(name(rhs));
            }
        });
        return customerCollection;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inject critical components
        DaggerInjectionUtils.inject(this);
        // set the view
        setContentView(R.layout.customer_list_activity);

        // then update the view itself w/ components
        setListAdapter(this.customerListAdapter);

        // update the form components, as well.
        this.filterTextBox = (EditText) findViewById(R.id.filter);
        this.filterTextBox.addTextChangedListener(this.filterTextWatcher);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Customer customer = customers.get(position);
        Intent intent = new Intent(this, CustomerDetailActivity.class);
        intent.putExtra("customerId", customer.getId());    // if this is present, then the activity will act as an editor.
        startActivity(intent);
    }


}