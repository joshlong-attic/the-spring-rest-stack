package com.jl.crm.android.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import com.jl.crm.android.R;
import com.jl.crm.android.activities.MainActivity;

/**
 * @author Josh Long
 */
public class UserProfileFragment extends SecuredCrmFragment {

    private EditText username, fn, ln;
    private ImageView imageView;

    public UserProfileFragment(MainActivity mainActivity, String title) {
        super(mainActivity, title);
    }

    private EditText editText(View v, int fieldId) {
        return (EditText) v.findViewById(fieldId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_detail_fragment ,container,false);
       if(v instanceof GridLayout){
           GridLayout gridLayout  = (GridLayout) v;
           gridLayout.setOrientation(GridLayout.HORIZONTAL);
       }
        username = editText(v, R.id.username);
        fn = editText(v, R.id.first_name);
        ln = editText(v, R.id.last_name);
        imageView = (ImageView) v.findViewById(R.id.profile_photo);


        // set the current user information
        username.setText(getCurrentUser().getUsername());
        fn.setText(getCurrentUser().getFirstName());
        ln.setText(getCurrentUser().getLastName());

        return v;
    }

}
