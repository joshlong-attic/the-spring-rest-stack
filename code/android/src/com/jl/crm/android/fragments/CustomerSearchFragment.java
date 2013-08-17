package com.jl.crm.android.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.jl.crm.android.R;
import com.jl.crm.android.utils.DaggerInjectionUtils;
import com.jl.crm.client.*;

import javax.inject.Inject;
import java.util.*;

/**
 * Searches the customer records. We want this functionality to be available as part of the faster acting application or
 * as a separate, simple-to-use Activity for handling search callbacks from the Android system
 *
 * @author Josh Long
 */
public class CustomerSearchFragment extends ListFragment {

	@Inject CrmOperations crmService;
	User currentUser;

	@Override
	public void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id) {
		Log.d(CustomerSearchFragment.class.getName(), "the current user is " + this.currentUser + "");
	}


	@Override
	public void onStart() {
		super.onStart();

		this.currentUser = crmService.currentUser();
//		doSearch("Josh");
	}

	protected void doSearch(String query) {

		assert this.currentUser != null : "the currentUser can't be null";

		// get a cursor, prepare the ListAdapter, and set it
		Toast.makeText(getActivity(), "Searching for query '" + query + "'" + "and the current User is " + this.currentUser.toString(), 10);

		String[] names = "John,Doe;Jane,Doe".split(";");
		List<Customer> customerList = new ArrayList<Customer>();

		for (String n : names) {
			customerList.add(new Customer(this.currentUser, n.split(",")[0], n.split(",")[1]) {
				@Override
				public Long getDatabaseId() {
					return (long) (Math.random() * 1000L);
				}
			});
		}

		ArrayAdapter<Customer> listAdapter = new ArrayAdapter<Customer>(getActivity(), R.layout.action_bar_search_item, customerList) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				Customer customer = this.getItem(position);

				final View view = View.inflate(getActivity(), R.layout.action_bar_search_item, null);

				final TextView fn = (TextView) view.findViewById(R.id.first_name_label);
				fn.setText(customer.getFirstName());

				final TextView ln = (TextView) view.findViewById(R.id.last_name_label);
				ln.setText(customer.getLastName());

				final TextView id = (TextView) view.findViewById(R.id.customer_id);
				id.setAlpha(.7f);
				id.setText(Long.toString(customer.getDatabaseId()));

				return view;
			}
		};
		setListAdapter(listAdapter);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DaggerInjectionUtils.inject(this);
	}
/*

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// call detail activity for the clicked entry
		Object selectedObject = l.getAdapter().getItem(position);
		assert selectedObject != null : "the selected object can't be null";
		Toast.makeText(this, "Selected " + selectedObject, 10);
	}
*/

}
