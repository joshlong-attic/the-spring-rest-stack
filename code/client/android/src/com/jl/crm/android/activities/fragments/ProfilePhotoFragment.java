package com.jl.crm.android.activities.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.jl.crm.android.R;
import com.jl.crm.android.activities.MainActivity;
import com.jl.crm.android.utils.IoUtils;
import com.jl.crm.client.CrmOperations;
import com.jl.crm.client.ProfilePhoto;
import com.jl.crm.client.User;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import javax.inject.Provider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.jl.crm.android.utils.IoUtils.writableFile;

/**
 * @author Josh Long
 */
public class ProfilePhotoFragment extends SecuredCrmFragment {

    public static final int REQUEST_CAMERA_CODE = 1;
    public static final int REQUEST_GALLERY_CODE = 2;
    private Provider<CrmOperations> crmOperationsProvider;
    private ImageView userProfileImageView;

    public ProfilePhotoFragment(MainActivity mainActivity, Provider<CrmOperations> crmOperationsProvider, String title) {
        super(mainActivity, title);
        this.crmOperationsProvider = crmOperationsProvider;
    }

    protected File profilePhotoFile() {
        return writableFile("profile.jpg");
    }

    protected void profilePhotoFileChanged(InputStream i) {

        try {
            byte[] bytesForInputStream = IoUtils.readFully(i);
            Bitmap bm = BitmapFactory.decodeByteArray(bytesForInputStream, 0, bytesForInputStream.length);
            userProfileImageView.setImageBitmap(bm);
            getMainActivity().showUserAccount();
            this.transmitProfilePhoto(bytesForInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream imageStream = null;
        try {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == REQUEST_CAMERA_CODE) {
                        File profilePhotoFile = profilePhotoFile();
                        if (profilePhotoFile != null && profilePhotoFile.exists()) {
                            imageStream = new FileInputStream(profilePhotoFile);
                            profilePhotoFileChanged(imageStream);
                        }
                    } else if (requestCode == REQUEST_GALLERY_CODE) {
                        Uri selectedImage = data.getData();
                        imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                        profilePhotoFileChanged(imageStream);
                    }
                }
            } finally {
                if (imageStream != null) {
                    imageStream.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected void transmitProfilePhoto(byte data[]) {
        final CrmOperations crmOperations = crmOperationsProvider.get();
        Assert.isTrue(data != null && data.length > 0);
        crmOperations.setProfilePhoto(data, MediaType.IMAGE_JPEG);
    }

    @Override
    public void setCurrentUser(User currentUser) {
        super.setCurrentUser(currentUser);
        User user = this.getCurrentUser();
        if (null != user) {
            CrmOperations crmOperations = crmOperationsProvider.get();
            if (user.isProfilePhotoImported()) {
                ProfilePhoto profilePhoto = crmOperations.getUserProfilePhoto();
                byte[] profilePhotoBytes = profilePhoto.getBytes();
                Bitmap bm = BitmapFactory.decodeByteArray(profilePhotoBytes, 0, profilePhotoBytes.length);
                userProfileImageView.setImageBitmap(bm);

                String editYourProfilePhoto
                        = getActivity().getString(R.string.edit_profile_photo);
                changeProfilePhotoButton.setText(String.format(editYourProfilePhoto, user.getFirstName()));
            }
        }
    }

    protected void searchCustomers() {
        this.getMainActivity().showSearch();
    }

    protected void capturePhoto() {
        final String takePhoto = getActivity().getString(R.string.take_photo),
                chooseFromLibrary = getActivity().getString(R.string.choose_from_library),
                cancel = getActivity().getString(R.string.cancel),
                selectProfilePhoto = getActivity().getString(R.string.select_profile_photo);

        final String[] items = {takePhoto, chooseFromLibrary, cancel};

        AlertDialog.Builder builder = new AlertDialog.Builder(getMainActivity());
        builder.setTitle(selectProfilePhoto);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                String menuItemSelected = items[item];
                if (menuItemSelected.equals(takePhoto)) {



                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(profilePhotoFile()));
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else if (menuItemSelected.equals(chooseFromLibrary)) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_GALLERY_CODE);
                } else if (menuItemSelected.equals(cancel)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_photo_fragment, container, false);

        // set what happens
        View.OnClickListener onClickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        capturePhoto();
                    }
                };
        userProfileImageView = (ImageView) view.findViewById(R.id.profile_photo);
        userProfileImageView.setScaleType(ImageView.ScaleType.FIT_START);
        userProfileImageView.setOnClickListener(onClickListener);

        // wire up the button
        changeProfilePhotoButton = (Button) view.findViewById(R.id.change_profile_photo);
        changeProfilePhotoButton.setOnClickListener(onClickListener);

        // wire up search customers button

        this.searchCustomersButton = (Button) view.findViewById(R.id.search);
        this.searchCustomersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCustomers();
            }
        });


        return view;
    }

    private Button changeProfilePhotoButton, searchCustomersButton;
}
