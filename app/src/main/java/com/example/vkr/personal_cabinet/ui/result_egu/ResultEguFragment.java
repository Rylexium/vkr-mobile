package com.example.vkr.personal_cabinet.ui.result_egu;

import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.vkr.R;

import java.util.Objects;

public class ResultEguFragment extends Fragment {

    private View binding;
    private LinearLayout layoutOfExams;
    private ScrollView scrollView;
    private FloatingActionButton fab;

    private static ResultEguViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = inflater.inflate(R.layout.fragment_result_egu, container, false);
        layoutOfExams = binding.findViewById(R.id.layout_of_exams);
        viewModel = new ViewModelProvider(this,  new ViewModelProvider.NewInstanceFactory()).get(ResultEguViewModel.class);
        initComponents();
        applyEvents();
        return binding.getRootView();
    }
    private void onAddField(String nameExam, String pointsExam, String yearExam) {
        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field_for_sum_exams, null);
        EditText exam = rowView.findViewById(R.id.exam_noedit);
        EditText points = rowView.findViewById(R.id.text_points_of_exam_noedit);

        exam.setText("Сумма баллов ЕГЭ");
        points.setText(String.valueOf(viewModel.getExams().stream().mapToInt(e -> Integer.parseInt(e.get(1))).sum()));

        layoutOfExams.addView(rowView);
    }

    private void applyEvents(){
        scrollView = binding.findViewById(R.id.scrollview_result_egu_fragment);
        fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            //work with fab
            if (scrollY == 0 || (scrollY < oldScrollY && !fab.isShown()))
                fab.show();
            else if (scrollY > oldScrollY && fab.isShown())
                fab.hide();
        });
    }

    private void fillTable(){
        addFieldSumExams();
        for(int i=0; i < viewModel.getExams().size(); ++i)
            onAddField(viewModel.getExams().get(i).get(0), viewModel.getExams().get(i).get(1), viewModel.getExams().get(i).get(2));
    }

    private void awaitData(){
        new Thread(()->{
            while (viewModel.getExams() == null || viewModel.getMinPointsExams() == null) { }
            new Handler(Looper.getMainLooper()).post(this::fillTable);
        }).start();
    }

    public static void clearTable(){
        ResultEguViewModel.clearExams();
    }

    private void initComponents(){
        awaitData();
        scrollView = binding.findViewById(R.id.scrollview_result_egu_fragment);
        fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}