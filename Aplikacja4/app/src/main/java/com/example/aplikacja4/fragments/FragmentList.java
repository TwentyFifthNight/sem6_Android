package com.example.aplikacja4.fragments;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikacja4.Image;
import com.example.aplikacja4.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentList extends Fragment{
    private ImageListAdapter mAdapter;

    private List<Image> mImageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // przypisujemy layout do fragmentu
        View view = inflater.inflate(R.layout.image_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        mAdapter = new ImageListAdapter(view.getContext());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mImageList = new ArrayList<>();
        getFileList();

        return view;
    }

    //fragment z listą – pobieranie listy obrazków z bazy (MediaStore)
    protected void getFileList() {
        Uri collection;
        String[] projection;
        String selection;
        String[] selectionArgs;
        //na Androidzie 10+ pobieramy z bazy tylko obrazki, które sami zapisaliśmy (dostęp
        //bez uprawnień
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.OWNER_PACKAGE_NAME
            };
            selection = MediaStore.Images.Media.OWNER_PACKAGE_NAME + " = ?";
            selectionArgs = new String[]{requireContext().getPackageName()};
            //na Androidzie 9- nie ma informacji o właścicielu
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
            };
            selection = null;
            selectionArgs = null;
        }
        String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";
        try (Cursor cursor = requireContext().getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            //zamieniamy nazwy kolumn na numery kolumn
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int packageColumn = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                packageColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.OWNER_PACKAGE_NAME);
            }
            mImageList.clear();
            while (cursor.moveToNext()) {
                //odczytujemy wartości z kolumn w poszczególnych rekordach
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                String packageName = cursor.getString(packageColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                //dodajemy informację o kolejnym obrazku do listy
                Image image = new Image(id, name);
                mImageList.add(image);
            }
            mAdapter.setImageList(mImageList);
        }
    }
}
