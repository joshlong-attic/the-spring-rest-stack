package com.jl.crm.android.activities.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.jl.crm.android.R;
import com.jl.crm.android.activities.MainActivity;
import com.jl.crm.client.CrmOperations;
import com.jl.crm.client.ProfilePhoto;
import com.jl.crm.client.User;
import org.springframework.http.MediaType;

import javax.inject.Provider;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Josh Long
 */
public class UserProfileFragment
        extends SecuredCrmFragment {


    // queue through which updates to the profile are serialized and written in order
    private final Queue<ProfileUpdate> writeProfileUpdateQueue = new ConcurrentLinkedQueue<ProfileUpdate>();
    private EditText firstNameEditText, lastNameEditText;
    private TextWatcher syncNameFieldsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // todo make sure that we also upload the bytes to the image
            ProfileUpdate profileUpdate = new ProfileUpdate(firstNameEditText.toString(), lastNameEditText.toString(), null);
            writeProfileUpdateQueue.offer(profileUpdate);
        }
    };
    private Provider<CrmOperations> crmOperationsProvider;
    private TextView welcomeTextView;
    private ImageView userProfileImageView;

    public UserProfileFragment(MainActivity mainActivity, Provider<CrmOperations> crmOperationsProvider, String title) {
        super(mainActivity, title);
        this.crmOperationsProvider = crmOperationsProvider;
    }

    @Override
    public void setCurrentUser(User currentUser) {
        super.setCurrentUser(currentUser);
        User user = getCurrentUser();

        if (null != user) {
            firstNameEditText.setText(user.getFirstName());
            lastNameEditText.setText(user.getLastName());
            CrmOperations crmOperations = crmOperationsProvider.get();
            if (user.isProfilePhotoImported()) {

                MediaType mediaType = user.getProfilePhotoMediaType();

                ProfilePhoto profilePhoto = crmOperations.getUserProfilePhoto();
                byte[] profilePhotoBytes = profilePhoto.getBytes();

                Log.d(getTag(), "the media type for the profile photo is '" + mediaType.toString() + "'.");

                Bitmap bitmap = BitmapFactory.decodeByteArray(profilePhotoBytes, 0, profilePhotoBytes.length);
                userProfileImageView.setImageBitmap(bitmap);

            }
        }
    }

    private EditText editText(View view, int fieldId) {
        return (EditText) view.findViewById(fieldId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_detail_fragment, container, false);
        if (view instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) view;
            gridLayout.setPadding(10, 0, 10, 0);
            gridLayout.setOrientation(GridLayout.HORIZONTAL);
        }
        welcomeTextView = (TextView) view.findViewById(R.id.user_profile_description);
        firstNameEditText = editText(view, R.id.first_name);
        lastNameEditText = editText(view, R.id.last_name);
        userProfileImageView = (ImageView) view.findViewById(R.id.profile_photo);


        firstNameEditText.addTextChangedListener(this.syncNameFieldsTextWatcher);
        lastNameEditText.addTextChangedListener(this.syncNameFieldsTextWatcher);
        return view;
    }

    public static class ProfileUpdate {
        private String firstName, lastName;
        private byte[] profilePhotoBytes;  // optional

        public ProfileUpdate(String firstName, String lastName, byte[] profilePhotoBytes) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.profilePhotoBytes = profilePhotoBytes;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public byte[] getProfilePhotoBytes() {
            return profilePhotoBytes;
        }
    }


}
