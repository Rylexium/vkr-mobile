package com.example.vkr.personal_cabinet.ui.result_egu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.vkr.R;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;

import java.util.Objects;

public class ResultEguFragment extends Fragment {

    private View binding;
    private LinearLayout layoutOfExams;

    public static ResultEguViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        PersonalCabinetActivity.selectedPage = 1;
        binding = inflater.inflate(R.layout.fragment_result_egu, container, false);
        layoutOfExams = binding.findViewById(R.id.layout_of_exams);
        viewModel = new ResultEguViewModel();
        initComponents();
        return binding.getRootView();
    }
    private void onAddField(String nameExam, String pointsExam, String yearExam) {
        if(getActivity() == null) return;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field_for_exam_noedit, null);

        EditText exam = rowView.findViewById(R.id.exam_noedit);
        EditText points = rowView.findViewById(R.id.text_points_of_exam_noedit);
        EditText year = rowView.findViewById(R.id.spinner_date_exam_noedit);

        exam.setText(nameExam);
        points.setText(pointsExam);
        //pointsExams < 39 ? color.red : color.black
        points.setTextColor( Integer.parseInt(pointsExam) < Integer.parseInt(Objects.requireNonNull(viewModel.getMinPointsExams().get(nameExam)))? Color.RED : Color.BLACK);
        year.setText(yearExam);

        layoutOfExams.addView(rowView);

    }

    private void addFieldSumExams() {
        if(getActivity() == null) return;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field_for_sum_exams, null);
        EditText exam = rowView.findViewById(R.id.exam_noedit);
        EditText points = rowView.findViewById(R.id.text_points_of_exam_noedit);

        exam.setText("Сумма баллов ЕГЭ");
        points.setText(String.valueOf(viewModel.getExams().stream().mapToInt(e -> Integer.parseInt(e.get(1))).sum()));

        layoutOfExams.addView(rowView);
    }

    private void fillTable(){
        addFieldSumExams();
        for(int i=0; i < viewModel.getExams().size(); ++i)
            onAddField(viewModel.getExams().get(i).get(0), viewModel.getExams().get(i).get(1), viewModel.getExams().get(i).get(2));
    }

    private void awaitData(){
        layoutOfExams.addView(new ProgressBar(getContext()));
        layoutOfExams.getChildAt(0).setPadding(0, 30, 0, 0);
        new Thread(()-> {
            while (viewModel.getExams().size() == 0 || viewModel.getMinPointsExams().size() == 0);
            new Handler(Looper.getMainLooper()).post(() -> {
                layoutOfExams.removeViewAt(0);
                fillTable();
            });
        }).start();
    }

    public static void clearTable(){
        ResultEguViewModel.clearExams();
    }

    private void initComponents(){
        awaitData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}