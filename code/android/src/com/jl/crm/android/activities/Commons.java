package com.jl.crm.android.activities;


import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.jl.crm.android.R;
import com.jl.crm.android.utils.DaggerInjectionUtils;

/**
 * Its really hard to re-use menus across {@link Activity activity} instances in Android. Best to use the delegate
 * pattern.
 *
 * @author Josh Long
 */
public abstract class Commons {

	private final static int ACTION_CUSTOMER_SEARCH = Menu.FIRST + 1;
	private final static int ACTION_USER_ACCOUT = Menu.FIRST + 2;

	public static void onCreate(final Activity activity, final Bundle savedInstanceState) {

		DaggerInjectionUtils.inject(activity);

		Window window = activity.getWindow();
		window.requestFeature(Window.FEATURE_PROGRESS);
		window.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
	}

	// Handles the initialization of the ActionBar menus
	public static boolean onCreateOptionsMenu(final Activity activity, Menu menu) {

		SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);

		// setup the SearchView
		SearchView searchView = new SearchView(activity);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
		searchView.setQueryHint(activity.getString(R.string.search_hint));
		searchView.setIconified(true);

		TextView textView = (TextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
		textView.setTextColor(Color.WHITE);


//		AutoCompleteTextView searchText = (AutoCompleteTextView) searchView.findViewById(R.id.abs__search_src_text);
//		searchText.setHintTextColor(activity.getResources().getColor(android.R.color.primary_text_light));
//		searchText.setTextColor(activity.getResources().getColor(android.R.color.primary_text_light));

	/*	searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			*//**
		 * Called when the user submits the query. This could be due to a key press on the
		 * keyboard or due to pressing a submit button.
		 * The listener can override the standard behavior by returning true
		 * to indicate that it has handled the submit request. Otherwise return false to
		 * let the SearchView handle the submission by launching any associated intent.
		 *
		 * @param query the query text that is to be submitted
		 * @return true if the query has been handled by the listener, false to let the
		 *         SearchView perform the default action.
		 *//*
			@Override
			public boolean onQueryTextSubmit(String query) {
//				toast(activity, "Searching for query '" + query + "'");
				return false;
			}

			*//**
		 * Called when the query text is changed by the user.
		 *
		 * @param newText the new content of the query text field.
		 * @return false if the SearchView should perform the default action of showing any
		 *         suggestions if available, true if the action was handled by the listener.
		 *//*
			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d(Commons.class.getName(), "the query changed to '" + newText + "'");
				return false;
			}
		});*/
		menu.add(Menu.NONE, ACTION_CUSTOMER_SEARCH, 1, activity.getString(R.string.search))
				  .setIcon(android.R.drawable.ic_menu_search)
				  .setActionView(searchView)
				  .setTitle(R.string.search)
				  .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


		// show the user profile
		menu.add(Menu.NONE, ACTION_USER_ACCOUT, 2, activity.getString(R.string.user_account))
				  .setIcon(android.R.drawable.ic_menu_info_details)
				  .setTitle(R.string.user_account)
				  .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);


		return true;
	}

	// handles responding to events for the ActionBar menus
	public static boolean onOptionsItemSelected(Activity activity, MenuItem item) {
		switch (item.getItemId()) {
			case ACTION_CUSTOMER_SEARCH:
				launchSearchSelectedActivity(activity);
				break;
			case ACTION_USER_ACCOUT:
				launchUserProfileActivity(activity);
				break;
			// If home icon is clicked return to main Activity
			case android.R.id.home:
				launchHome(activity);
				break;
			default:
				break;
		}

		return true;
	}

	protected static void launchHome(Activity activity) {
		toast(activity, "launching home");
		launchActivity(activity, UserHomeActivity.class);
	}

	private static void launchActivity(Activity activity, Class<? extends Activity> activityClass) {
		Intent intent = new Intent(activity, activityClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}

	public static void launchSearchSelectedActivity(Activity activity) {
		toast(activity, "launching home");
		launchActivity(activity, CustomerSearchActivity.class);
	}

	public static void launchUserProfileActivity(Activity activity) {
		toast(activity, "launching user profile");
		launchActivity(activity, UserHomeActivity.class);

	}

	private static void toast(Activity activity, String message) {
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}
}
