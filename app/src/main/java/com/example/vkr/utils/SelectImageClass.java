package com.example.vkr.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SelectImageClass {
    public final static int CAMERA = 0, GALLERY = 1;
    public final static int REQUEST_CODE = 200;

    public static void showMenu(@NonNull Activity activity, @NonNull Boolean multitouch) {

        final CharSequence[] options = { "Камера", "Галерея", "Отмена" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Добавить фото");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Камера")) {
                activity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA);
            }
            else if (options[item].equals("Галерея")) {
                    activity.startActivityForResult(multitouch ?
                            Intent.createChooser(new Intent()
                                    .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                    .setType("image/*")
                                    .setAction(Intent.ACTION_GET_CONTENT), "Выберите фотографии")
                            :
                            new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY);
            }
            else if (options[item].equals("Отмена")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showMenu(@NonNull FragmentActivity activity, @NonNull Fragment fragment, @NonNull Boolean multitouch) {

        final CharSequence[] options = { "Камера", "Галерея", "Отмена" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Добавить фото");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Камера")) {
                fragment.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA);
            }
            else if (options[item].equals("Галерея")) {
                fragment.startActivityForResult(multitouch ?
                        Intent.createChooser(new Intent()
                                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                .setType("image/*")
                                .setAction(Intent.ACTION_GET_CONTENT), "Выберите фотографии")
                        :
                        new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY);
            }
            else if (options[item].equals("Отмена")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
