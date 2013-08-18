package com.jl.crm.android.fragments;

import android.app.*;
import android.content.Context;
import android.graphics.Color;
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
		loadAllCustomers();
	}

	private final static int ACTION_CUSTOMER_SEARCH = Menu.FIRST + 1;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


		// setup the SearchView
		SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

		SearchView searchView = new SearchView( getActivity() );
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
		searchView.setQueryHint(getString(R.string.search_hint));
		searchView.setIconified(true);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(getTag(),"query text submitted: "+ query);
				search( query);
				return true ;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d(getTag(),"query text changed: "+ newText);
				return false;
			}
		});

		TextView textView = (TextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
		textView.setTextColor(Color.WHITE);

		MenuItem menuItem = menu.add(Menu.NONE, ACTION_CUSTOMER_SEARCH, 1, getString(R.string.search)) ;
		menuItem.setIcon(android.R.drawable.ic_menu_search)
				  .setActionView(searchView)
				  .setTitle(R.string.search)
				  .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	}

	/* this is what happens by default */
	public void loadAllCustomers() {
		redrawCustomersWithNewData(crmService.loadAllUserCustomers());
	}

	public void search(String query) {
		redrawCustomersWithNewData(crmService.search(query));
	}

	private void redrawCustomersWithNewData(Collection<Customer> c) {
		assert this.currentUser != null : "the currentUser can't be null";
		List<Customer> customerCollection = new ArrayList<Customer>(c);
		CustomerArrayAdapter listAdapter = new CustomerArrayAdapter(getActivity(), customerCollection);
		setListAdapter(listAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DaggerInjectionUtils.inject(this);
		setHasOptionsMenu(true);

	}

	public static class CustomerArrayAdapter extends ArrayAdapter<Customer> {
		public CustomerArrayAdapter(Context context, List<Customer> objects) {
			super(context, R.layout.action_bar_search_item, objects);
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
