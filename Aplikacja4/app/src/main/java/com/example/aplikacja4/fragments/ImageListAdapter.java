package com.example.aplikacja4.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikacja4.Image;
import com.example.aplikacja4.R;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {
    public interface OnItemClickListener{
        @SuppressLint("NotConstructor")
        void OnItemClickListener(Image image);
    }
    private OnItemClickListener mOnItemClickListener;
    private final LayoutInflater mLayoutInflater;
    private List<Image> mImageList;

    public ImageListAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        this.mImageList = null;
        try {
            mOnItemClickListener = (OnItemClickListener) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = mLayoutInflater.inflate(R.layout.image_row, parent, false);
        return new ImageViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image image = mImageList.get(position);
        holder.id.setText(String.valueOf(image.getId()));
        holder.name.setText(image.getName());
        holder.position = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        if (mImageList != null)
            return mImageList.size();
        return 0;
    }

    public void setImageList(List<Image> imageList){
        this.mImageList = imageList;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView id;
        TextView name;
        int position;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.rowId);
            name = itemView.findViewById(R.id.rowName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.OnItemClickListener(mImageList.get(position));
        }
    }

}
