package com.example.aplikacja2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhoneListAdapter extends RecyclerView.Adapter<PhoneListAdapter.PhoneViewHolder>{
    interface OnItemClickListener{
        @SuppressLint("NotConstructor")
        void OnItemClickListener(Phone phone);
    }

    private final OnItemClickListener mOnItemClickListener;
    private final LayoutInflater mLayoutInflater;
    private List<Phone> mPhoneList;
    public PhoneListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mPhoneList = null;
        try{
            mOnItemClickListener = (OnItemClickListener) context;
        }catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = mLayoutInflater.inflate(R.layout.phone_row, parent, false);
        return new PhoneViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneViewHolder holder, int position) {
        Phone phone = mPhoneList.get(position);
        holder.model.setText(phone.getModel());
        holder.manufacturer.setText(phone.getManufacturer());
        holder.position = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        //w momencie tworzenia obiektu adaptera lista może nie
        //być dostępna
        if (mPhoneList != null)
            return mPhoneList.size();
        return 0;
    }

    public Phone getPhone(int position){
        return mPhoneList.get(position);
    }

    public void setElementList(List<Phone> phoneList) {
        this.mPhoneList = phoneList;
        notifyDataSetChanged();
    }


    public class PhoneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView model;
        TextView manufacturer;
        int position;

        public PhoneViewHolder(@NonNull View itemView) {
            super(itemView);
            model = itemView.findViewById(R.id.modelRow);
            manufacturer = itemView.findViewById(R.id.maufacturerRow);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mOnItemClickListener.OnItemClickListener(mPhoneList.get(position));
        }
    }
}
