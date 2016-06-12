package com.smehsn.compassinsurance.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.adapter.FormPagerAdapter;
import com.smehsn.compassinsurance.form.AttachmentProvider;
import com.smehsn.compassinsurance.form.FormObjectProvider;
import com.smehsn.compassinsurance.form.PhotoAttachment;
import com.smehsn.compassinsurance.form.ValidationException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PhotoAttachmentFragment extends Fragment implements FormObjectProvider, AttachmentProvider {
    public static final  String TAG                   = PhotoAttachmentFragment.class.getSimpleName();
    private static final int    REQUEST_IMAGE_CAPTURE = 1;

    private Map<Integer, Uri> resIdImageUriMapping = new HashMap<>();

    private String pageTitle;
    private int    positionInViewPager;
    private String currentRequestedAction;
    private View   rootView;
    private Uri    requestedImageUri;


    @BindView(R.id.image_drivingLicensePhoto)
    ImageView imageLicense;
    @BindView(R.id.image_vinNumberPhoto)
    ImageView imageVin;
    public PhotoAttachmentFragment() {
    }


    public static PhotoAttachmentFragment newInstance(String pageTitle, int position) {
        PhotoAttachmentFragment fragment = new PhotoAttachmentFragment();
        fragment.pageTitle = pageTitle;
        fragment.positionInViewPager = position;
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("positionInViewPager", positionInViewPager);
        outState.putString("pageTitle", pageTitle);
        outState.putString("currentRequestedAction", currentRequestedAction);
        outState.putParcelable("requestedImageUri", requestedImageUri);


        if (resIdImageUriMapping != null) {
            Bundle imageData = new Bundle();
            for (Integer resId : resIdImageUriMapping.keySet()) {
                imageData.putParcelable(resId.toString(), resIdImageUriMapping.get(resId));
            }
            outState.putBundle("resIdImageUriMapping", imageData);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            pageTitle = savedInstanceState.getString("pageTitle");
            positionInViewPager = savedInstanceState.getInt("positionInViewPager");
            currentRequestedAction = savedInstanceState.getString("currentRequestedAction");
            requestedImageUri = savedInstanceState.getParcelable("requestedImageUri");

            Bundle imageData = savedInstanceState.getBundle("resIdImageUriMapping");
            if (imageData != null) {
                for (String key : imageData.keySet()) {
                    resIdImageUriMapping.put(Integer.parseInt(key), (Uri) imageData.get(key));
                }
            }

        }
    }

    private void restoreImagesState() {
        for (int resId : resIdImageUriMapping.keySet()) {
            loadImageViewFromUri(resId, resIdImageUriMapping.get(resId));
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        restoreState(savedInstanceState);
        rootView = inflater.inflate(R.layout.form_photo_attachment, container, false);
        ButterKnife.bind(this, rootView);
        restoreImagesState();
        return rootView;
    }


    @OnClick({
            R.id.image_drivingLicensePhoto,
            R.id.image_vinNumberPhoto,
            R.id.action_vinNumberPhoto,
            R.id.action_drivingLicensePhoto
    })
    public void onTakeImageAction(View v) {

        currentRequestedAction = (String) v.getTag();
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        requestedImageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, requestedImageUri);
        if (photoIntent.resolveActivity(getContext().getPackageManager()) != null) {
            getActivity().startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            int targetImageViewResId = this.getResources().getIdentifier(
                    "image_" + currentRequestedAction,
                    "id",
                    getContext().getPackageName());
            resIdImageUriMapping.put(targetImageViewResId, requestedImageUri);
            loadImageViewFromUri(targetImageViewResId, requestedImageUri);
            requestedImageUri = null;
        }
    }


    private void loadImageViewFromUri(int resId, Uri uri){
        final ImageView imageView = (ImageView) rootView.findViewById(resId);
        imageView.setVisibility(View.VISIBLE);
        Picasso.
                with(getContext()).
                load(uri).
                into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setBackgroundColor(Color.TRANSPARENT);
                    }

                    @Override
                    public void onError() {

                    }
                });

    }

    @Override
    public List<File> getAttachedFiles() {
        List<File> files = new ArrayList<>();
        for (Map.Entry<Integer, Uri> entry: resIdImageUriMapping.entrySet()){
            if (entry != null && entry.getValue() != null)
                files.add(new File(getRealPathFromURI(entry.getValue())));
        }

        return files;
    }


    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
        return null;
    }



    @Override
    public Object getFormModelObject() throws ValidationException {
        PhotoAttachment attachment = new PhotoAttachment();
        attachment.setDrivingLicensePhoto((imageLicense.getDrawable() == null) ? "blank" : "Attached to the email");
        attachment.setVinNumberPhoto((imageVin.getDrawable() == null) ? "blank" : "Attached to the email");
        return attachment;
    }

    @Override
    public int getPagePosition() {
        return positionInViewPager;
    }

    @Override
    public String getTitle() {
        return FormPagerAdapter.PAGE_TITLES[positionInViewPager];
    }


}
