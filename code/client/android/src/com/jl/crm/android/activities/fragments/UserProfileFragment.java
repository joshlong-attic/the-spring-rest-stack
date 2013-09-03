package com.jl.crm.android.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import javax.inject.Provider;
import java.io.*;

/**
 * @author Josh Long
 */
public class UserProfileFragment
        extends SecuredCrmFragment {

    private final int PICTURE_RESULT = 13232;
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
            // todo  submitChanges();
        }

    };
    private Provider<CrmOperations> crmOperationsProvider;
    private ImageView userProfileImageView;
    private File tmpProfilePhotoFile = writableFile("profile.jpg");

    public UserProfileFragment(MainActivity mainActivity, Provider<CrmOperations> crmOperationsProvider, String title) {
        super(mainActivity, title);
        this.crmOperationsProvider = crmOperationsProvider;
    }

    private static File writableFile(String fileName) {
        File tmpFile = new File(new File(Environment.getExternalStorageDirectory(), "spring-crm"), fileName),
                parent = tmpFile.getParentFile();
        String tmpFilePath = tmpFile.getAbsolutePath();
        Assert.isTrue(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()), "the external SD card must be writable to write to '" + tmpFilePath + "'.");
        Assert.isTrue(parent.exists() || parent.mkdirs(), "the parent directory required to write to the SD card ('" + tmpFilePath + "') does not exist and could not be created.");
        return tmpFile;
    }

    private static void copyStreams(InputStream is, OutputStream os)
            throws IOException {
        Assert.notNull(is);
        Assert.notNull(os);
        byte[] b = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(b)) != -1) {
            os.write(b, 0, bytesRead);
        }
        try {
            is.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            os.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    protected void submitChanges() {
        CrmOperations crmOperations = crmOperationsProvider.get();
        if (tmpProfilePhotoFile != null && tmpProfilePhotoFile.exists() && tmpProfilePhotoFile.length() > 0) {
            try {
                byte[] profilePhotoBytes;
                InputStream inputStream = new FileInputStream(tmpProfilePhotoFile);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                copyStreams(inputStream, outputStream);
                profilePhotoBytes = outputStream.toByteArray();
                crmOperations.setUserProfilePhoto(profilePhotoBytes, MediaType.IMAGE_JPEG);    // we know that we asked for jpg back from the camera
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String fN = firstNameEditText.toString(), lN = lastNameEditText.toString();
        Assert.hasText(fN, "you can't set the first name to an empty string");
        Assert.hasText(lN, "you can't set the last name to an empty string");
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
        // todo
        if (requestCode == PICTURE_RESULT) {
            // then make sure were looking at the profile fragment
            getMainActivity().showUserAccount(); // JUST in case!
            if (resultCode == Activity.RESULT_OK) {   // make sure the action wasn't cancelled
              /*  Bundle b = data.getExtras();
                Bitmap pic = (Bitmap) b.get("data");
                if (pic != null) {
                    userProfileImageView.destroyDrawingCache();
                    userProfileImageView.setImageBitmap(pic);
                }*/


                submitChanges();
            }
        }


    }

    protected void capturePhoto() {
        try {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            Uri uri = Uri.fromFile(tmpProfilePhotoFile);

            camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            camera.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            camera.putExtra("return-data", false);

            this.startActivityForResult(camera, PICTURE_RESULT);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

   /* public static class ProfileUpdate {
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
    }*/


}
