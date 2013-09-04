package com.jl.crm.android.activities.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.jl.crm.android.activities.fragments.Utils.copyStreams;
import static com.jl.crm.android.activities.fragments.Utils.writableFile;

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

    protected File fileFromUri(Uri contentUri) {
        String[] project = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, project, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return new File(cursor.getString(columnIndex));
    }

    protected void profilePhotoFileChanged(File fi) {
        Bitmap bm = BitmapFactory.decodeFile( fi.getAbsolutePath(), new BitmapFactory.Options());
        userProfileImageView.setImageBitmap(bm);
        getMainActivity().showUserAccount();
        this.transmitProfilePhoto(fi);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CODE) {
                File profilePhotoFile = profilePhotoFile();
                if (profilePhotoFile != null && profilePhotoFile.exists()) {
                    profilePhotoFileChanged(profilePhotoFile);
                }
            } else if (requestCode == REQUEST_GALLERY_CODE) {
                Uri selectedImageUri = data.getData();
                File fi = fileFromUri(selectedImageUri);
                profilePhotoFileChanged(fi);
            }

        }
    }

    protected void transmitProfilePhoto(File file) {
        final CrmOperations crmOperations = crmOperationsProvider.get();
        Assert.isTrue(file != null && file.exists() && file.length() > 0,
                "the file '" + (file == null ? "" : file.getAbsolutePath()) + "' must exist and be readable.");
        try {
            byte[] profilePhotoBytes;
            InputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            copyStreams(inputStream, outputStream);
            profilePhotoBytes = outputStream.toByteArray();
            crmOperations.setUserProfilePhoto(profilePhotoBytes, MediaType.IMAGE_JPEG);    // we know that we asked for jpg back from the camera
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setCurrentUser(User currentUser) {
        super.setCurrentUser(currentUser);
        User user = getCurrentUser();
        if (null != user) {
            CrmOperations crmOperations = crmOperationsProvider.get();
            if (user.isProfilePhotoImported()) {
                ProfilePhoto profilePhoto = crmOperations.getUserProfilePhoto();
                byte[] profilePhotoBytes = profilePhoto.getBytes();
                Bitmap bm = BitmapFactory.decodeByteArray(profilePhotoBytes, 0, profilePhotoBytes.length);
                userProfileImageView.setImageBitmap(bm);
            }
        }
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
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, chooseFromLibrary), REQUEST_GALLERY_CODE);
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
        userProfileImageView = (ImageView) view.findViewById(R.id.profile_photo);
        userProfileImageView.setScaleType(ImageView.ScaleType.FIT_START);
        userProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });
        return view;
    }
}

abstract class Utils {
    public static File writableFile(String fileName) {
        File tmpFile = new File(new File(Environment.getExternalStorageDirectory(), "spring-crm"), fileName),
                parent = tmpFile.getParentFile();
        String tmpFilePath = tmpFile.getAbsolutePath();
        Assert.isTrue(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()), "the external SD card must be writable to write to '" + tmpFilePath + "'.");
        Assert.isTrue(parent.exists() || parent.mkdirs(), "the parent directory required to write to the SD card ('" + tmpFilePath + "') does not exist and could not be created.");
        return tmpFile;
    }

    public static void copyStreams(InputStream is, OutputStream os) throws IOException {
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
}