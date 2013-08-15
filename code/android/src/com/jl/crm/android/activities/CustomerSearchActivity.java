package com.jl.crm.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.jl.crm.android.R;

import java.text.Collator;
import java.util.*;

public class CustomerSearchActivity extends SherlockListActivity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String path = intent.getStringExtra(INTENT_PATH);

		if (path == null) {
			path = "";
		} else {
			getSupportActionBar().setTitle(path);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		SimpleAdapter simpleAdapter = new  SimpleAdapter(this, getData(path), android.R.layout.simple_list_item_1, new String[]{getString(R.string.search)}, new int[]{android.R.id.text1}) ;
		setListAdapter(simpleAdapter);
		getListView().setTextFilterEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return true;
	}

	protected List<Map<String, Object>> getData(String prefix) {

		List<Map<String,Object>> data = new ArrayList<Map<String, Object>>();

		Collections.sort(data, NAME_COMPARATOR);

		return data;
	}

	private final static Comparator<Map<String, Object>> NAME_COMPARATOR =
			  new Comparator<Map<String, Object>>() {
				  private final Collator collator = Collator.getInstance();

				  public int compare(Map<String, Object> map1, Map<String, Object> map2) {
					  return collator.compare(map1.get("title"), map2.get("title"));
				  }
			  };

	protected Intent activityIntent(String pkg, String componentName) {
		Intent result = new Intent();
		result.setClassName(pkg, componentName);
		return result;
	}
/*
	protected Intent browseIntent(String path) {
		Intent result = new Intent();
		result.setClass(this, ListSamples.class);
		result.putExtra(INTENT_PATH, path);
		return result;
	}*/

	protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("title", name);
		temp.put("intent", intent);
		data.add(temp);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);

		Intent intent = (Intent) map.get("intent");
		startActivity(intent);
	}
/*	String[] columns = {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1};

	@Inject CrmOperations crmOperations;

	SearchView.OnSuggestionListener onSuggestionListener = new SearchView.OnSuggestionListener() {
		@Override
		public boolean onSuggestionSelect(int position) {
			return false;
		}

		@Override
		public boolean onSuggestionClick(int position) {
			return false;
		}
	};

	SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
		@Override
		public boolean onQueryTextSubmit(String query) {
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			return false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

//		User user = this.crmOperations.currentUser();
//
//		Log.d(CustomerSearchActivity.class.getName(), "the user is " + user.toString());


		//Create the search view
		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setQueryHint(getString(R.string.search_hint));
		searchView.setOnQueryTextListener(this.onQueryTextListener);
		searchView.setOnSuggestionListener(this.onSuggestionListener);
*//*
		if (mSuggestionsAdapter == null){

			MatrixCursor cursor = new MatrixCursor(columns);

			cursor.addRow(new String[]{"1", "'Murica"});
			cursor.addRow(new String[]{"2", "Canada"});
			cursor.addRow(new String[]{"3", "Denmark"});

			mSuggestionsAdapter = new SuggestionsAdapter(getSupportActionBar().getThemedContext(), cursor);
		}

		searchView.setSuggestionsAdapter(mSuggestionsAdapter);*//*

		menu.add(R.string.search_hint)
				  .setIcon(R.drawable.action_search)
				  .setActionView(searchView)
				  .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}*/

}