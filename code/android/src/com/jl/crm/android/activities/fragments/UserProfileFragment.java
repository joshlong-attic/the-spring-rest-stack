package com.jl.crm.android.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.jl.crm.android.R;
import com.jl.crm.android.activities.MainActivity;

/**
 * @author Josh Long
 */
public class UserProfileFragment extends SecuredCrmFragment {

    private TextView welcomeTextView;
    private EditText firstNameEditText, lastNameEditText;
    private ImageView userProfileImageView;

    public UserProfileFragment(MainActivity mainActivity, String title) {
        super(mainActivity, title);
    }

    private EditText editText(View view, int fieldId) {
        return (EditText) view.findViewById(fieldId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_detail_fragment, container, false);
        if (view instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) view;
            gridLayout.setPadding(10,0,10,0);
            gridLayout.setOrientation(GridLayout.HORIZONTAL);
        }
        welcomeTextView = (TextView) view.findViewById(R.id.user_profile_description);
        firstNameEditText = editText(view, R.id.first_name);
        lastNameEditText = editText(view, R.id.last_name);
        userProfileImageView = (ImageView) view.findViewById(R.id.profile_photo);


        firstNameEditText.setText(getCurrentUser().getFirstName());
        lastNameEditText.setText(getCurrentUser().getLastName());

        return view;
    }

}
