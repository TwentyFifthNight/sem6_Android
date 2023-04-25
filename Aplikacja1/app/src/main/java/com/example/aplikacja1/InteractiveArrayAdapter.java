package com.example.aplikacja1;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InteractiveArrayAdapter extends RecyclerView.Adapter<InteractiveArrayAdapter.GradeViewHolder> {

    private final ArrayList<GradeModel> mGradeList;
    private final LayoutInflater mInflater;

    public InteractiveArrayAdapter(Activity activity, ArrayList<GradeModel> gradeList) {
        mInflater = activity.getLayoutInflater();
        this.mGradeList = gradeList;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = mInflater.inflate(R.layout.wiersz_oceny, parent, false);
        return new GradeViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        GradeModel grade = mGradeList.get(position);
        holder.radioGroup.setTag(position);

        RadioGroup rg = holder.radioGroup;
        RadioButton rb;
        switch (mGradeList.get(position).getGrade()){
            case 2:
                rb = rg.findViewById(R.id.radioButton2);
                rb.setChecked(true);
                break;
            case 3:
                rb = rg.findViewById(R.id.radioButton3);
                rb.setChecked(true);
                break;
            case 4:
                rb = rg.findViewById(R.id.radioButton4);
                rb.setChecked(true);
                break;
            case 5:
                rb = rg.findViewById(R.id.radioButton5);
                rb.setChecked(true);
                break;
        }
        holder.name.setText(grade.getName());
    }

    @Override
    public int getItemCount() {
        return mGradeList.size();
    }

    public class GradeViewHolder extends RecyclerView.ViewHolder implements RadioGroup.OnCheckedChangeListener{
        RadioGroup radioGroup;
        TextView name;

        public GradeViewHolder(@NonNull View itemView){
            super(itemView);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(this);
            name = itemView.findViewById(R.id.subName);
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();

            if (isChecked){
                int position = Integer.parseInt(group.getTag().toString());
                mGradeList.get(position).setGrade(Integer.parseInt(checkedRadioButton.getText().toString()));
            }
        }
    }
}
