package com.example.vkr.personal_cabinet.ui.agreement;

import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.specialitysAbit;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vkr.R;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;

import java.util.Comparator;
import java.util.Objects;

public class AgreementFragment extends Fragment {

    private View binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = inflater.inflate(R.layout.fragment_agreement, container, false);
        Log.e("1. Абитуриент : " ,"");
        Log.e("2. Дата рождения : " ,"");
        Log.e("3. Специальности : ", bodySpecialities());
        Log.e("4. Сведение о ЕГЭ : ", "");
        return binding.getRootView();
    }


    private String bodySpecialities(){
        StringBuilder res = new StringBuilder();
        specialitysAbit.sort(Comparator.comparing(
                map -> Integer.parseInt(map.get("priority") == null || Objects.equals(map.get("priority"), "null") ? "0" : map.get("priority"))));
        specialitysAbit.forEach(item -> {
            res.append(item.get("institution")  + "\n" +
                    item.get("id_spec") + " " + item.get("name_spec") + "\n" +
                    "Форма обучения : " + item.get("typeOfStudy") + "\n" +
                    "Условия обучения : " + PersonalCabinetActivity.listFinancing.get(
                            Integer.parseInt(item.get("id_financing") == null ? "1" : item.get("id_financing")) - 1) + "\n" +
                    "Приоритет : " + item.get("priority"));
            res.append("---------------------");
        });
        return res.toString();
    }

    private String bodyExams(){
        StringBuilder res = new StringBuilder();


        return res.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}