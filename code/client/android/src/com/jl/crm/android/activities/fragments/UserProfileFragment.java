package com.jl.crm.android.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import com.jl.crm.android.R;
import com.jl.crm.android.activities.MainActivity;
import com.jl.crm.client.CrmOperations;
import com.jl.crm.client.ProfilePhoto;
import com.jl.crm.client.User;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Josh Long
 */
public class UserProfileFragment
        extends SecuredCrmFragment {


    // queue through which updates to the profile are serialized and written in order
    private final Queue<ProfileUpdate> writeProfileUpdateQueue = new ConcurrentLinkedQueue<ProfileUpdate>();
    private final int PICTURE_RESULT = 13232;
    private byte[] newProfilePhotoBytes;
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
            submitChanges();
        }

    };
    private Provider<CrmOperations> crmOperationsProvider;
    private ImageView userProfileImageView;

    public UserProfileFragment(MainActivity mainActivity, Provider<CrmOperations> crmOperationsProvider, String title) {
        super(mainActivity, title);
        this.crmOperationsProvider = crmOperationsProvider;
    }

    protected void submitChanges() {
        ProfileUpdate profileUpdate = new ProfileUpdate(firstNameEditText.toString(), lastNameEditText.toString(), this.newProfilePhotoBytes);
        writeProfileUpdateQueue.offer(profileUpdate);
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
                ProfilePhoto profilePhoto = crmOperations.getUserProfilePhoto();
                byte[] profilePhotoBytes = profilePhoto.getBytes();



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

        firstNameEditText = editText(view, R.id.first_name);
        lastNameEditText = editText(view, R.id.last_name);

        userProfileImageView = (ImageView) view.findViewById(R.id.profile_photo);
        userProfileImageView.setScaleType(ImageView.ScaleType.FIT_START);
        userProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });
        firstNameEditText.addTextChangedListener(this.syncNameFieldsTextWatcher);
        lastNameEditText.addTextChangedListener(this.syncNameFieldsTextWatcher);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICTURE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                Bitmap pic = (Bitmap) b.get("data");
                if (pic != null) {
                    userProfileImageView.setImageBitmap(pic);
                }
            }
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            // then we don't do anything because there's no new photo so just leave things alone!

        }

    }

    private void capturePhoto() {
        // Toast.makeText(getMainActivity(), "Showing the toast for the image selection thingy!", 100).show();
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.startActivityForResult(camera, PICTURE_RESULT);

    }

    public static class ProfileUpdate {
        private String firstName, lastName;
        private byte[] profilePhotoBytes;  // optional

        public ProfileUpdate(String firstName, String lastName, byte[] profilePhotoBytes) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.profilePhotoBytes = profilePhotoBytes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ProfileUpdate)) return false;

            ProfileUpdate that = (ProfileUpdate) o;

            return firstName.equals(that.firstName) && lastName.equals(that.lastName)
                    && Arrays.equals(profilePhotoBytes, that.profilePhotoBytes);

        }

        @Override
        public int hashCode() {
            int result = firstName.hashCode();
            result = 31 * result + lastName.hashCode();
            result = 31 * result + (profilePhotoBytes != null ? Arrays.hashCode(profilePhotoBytes) : 0);
            return result;
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
