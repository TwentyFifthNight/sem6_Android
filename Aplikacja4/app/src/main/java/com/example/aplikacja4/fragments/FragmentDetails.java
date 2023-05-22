package com.example.aplikacja4.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.aplikacja4.Image;
import com.example.aplikacja4.R;

public class FragmentDetails extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    public void setImage(Image image) {
        TextView view = requireView().findViewById(R.id.image_name);
        view.setText(image.getName());
        //Ważne – URI zawiera id obrazka a nie nazwę
        Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Long.toString(image.getId()));
        ImageView imageView = requireView().findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
    }

}
