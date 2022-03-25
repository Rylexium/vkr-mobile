package com.example.vkr.personal_cabinet.ui.agreement;

import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.specialitysAbit;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vkr.R;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.personal_cabinet.ui.result_egu.ResultEguFragment;
import com.example.vkr.utils.OpenActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AgreementFragment extends Fragment {

    private String textBodyExams = "";

    private Button downloadAgreement;
    private Button loadAgreement;
    private View binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bodyExams();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = inflater.inflate(R.layout.fragment_agreement, container, false);
        initComponents();
        applyEvents();
        return binding.getRootView();
    }

    private void initComponents() {
        downloadAgreement = binding.findViewById(R.id.btn_download_agreement);
        loadAgreement = binding.findViewById(R.id.btn_load_agreement);
    }

    private void applyEvents(){
        downloadAgreement.setOnClickListener(view -> createPDF());
        loadAgreement.setOnClickListener(view -> {

        });
    }

    private String bodySpecialities() {
        StringBuilder res = new StringBuilder();
        specialitysAbit.sort(Comparator.comparing(
                map -> Integer.parseInt(map.get("priority") == null || Objects.equals(map.get("priority"), "null") ? "0" : map.get("priority"))));
        specialitysAbit.forEach(item -> {
            res.append("\t\t\t\t\t\t\t\t\t" + item.get("institution") + "\n" +
                    "\t\t\t\t\t\t\t\t\t" + item.get("id_spec") + " " + item.get("name_spec") + "\n" +
                    "\t\t\t\t\t\t\t\t\t" + "Форма обучения : " + item.get("typeOfStudy") + "\n" +
                    "\t\t\t\t\t\t\t\t\t" + "Условия обучения : " + PersonalCabinetActivity.listFinancing.get(
                        Integer.parseInt(item.get("id_financing") == null ? "1" : item.get("id_financing")) - 1) + "\n" +
                    "\t\t\t\t\t\t\t\t\t" + "Приоритет : " + item.get("priority"));
            res.append("\n---------------------\n");
        });
        return res.toString();
    }

    private void bodyExams(){
        new Thread(()-> {
            while (ResultEguFragment.viewModel.getExams().size() == 0 || ResultEguFragment.viewModel.getMinPointsExams().size() == 0);
            textBodyExams = String.valueOf(ResultEguFragment.viewModel.getExams());
            if(ResultEguFragment.viewModel.getExams().size() < 3) bodyExams();
        }).start();
    }

    public void createPDF(){
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1600, 1600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();
        int x = 10, y = 25, step = 25;
        textBodyExams = "\n";
        ResultEguFragment.viewModel.getExams().forEach(item -> textBodyExams +=
                "\t\t\t\t\t\t\t\t\tПредмет : " + item.get(0) +
                "\n\t\t\t\t\t\t\t\t\tКоличество баллов : " + item.get(1) +
                "\n\t\t\t\t\t\t\t\t\tГод сдачи : " + item.get(2) + "\n\n");

        StringBuilder emailPhone = new StringBuilder();
        for(String line : PersonalCabinetActivity.resEmailPhone.split("\n"))
            emailPhone.append("\t\t\t\t\t\t\t\t\t" + line + "\n");

        List<String> lineInfo = Arrays.asList(
                "1. Абитуриент : " + PersonalCabinetActivity.resFio,
                "2. Дата рождения : " + PersonalCabinetActivity.dateOfBirthday,
                "3. Специальности : \n" + bodySpecialities(),
                "4. Сведение о ЕГЭ : " + textBodyExams,
                "5. Необходимость в обжитии : _______ (да/нет)",
                "6. Контактные данные : \n" + emailPhone,
                "7. Дата заполнения : " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()),
                "8. Подпись абитуриента : __________\n\n\n\n\n");
        for(String line : lineInfo) {
            for(String line1 : line.split("\n")) {
                page.getCanvas().drawText(line1, x, y, paint);
                y += step;
            }
        }

        pdfDocument.finishPage(page);

        String filePath = Environment.getExternalStorageDirectory().getPath() + "/Согласие на зачисление.pdf";
        File file = new File(filePath);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
        Snackbar.make(PersonalCabinetActivity.fab, "Файл сформирован и скачан", Snackbar.LENGTH_SHORT)
                .setAction("Посмотреть", (view)-> OpenActivity.openPDF(getActivity(), file))
                .setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.white))
                .setActionTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.white))
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}