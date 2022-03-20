package com.example.vkr.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.vkr.R;

import java.io.File;
import java.io.IOException;

public class SelectImageClass {
    public final static int CAMERA = 0, GALLERY = 1;
    public static String currentPhotoPath;

    public static void showMenu(@NonNull Activity activity, @NonNull Boolean multitouch) {
        new ShowBottomDialog().showDialog(activity, "Откуда загрузить?",
                activity.getDrawable(R.drawable.ic_baseline_photo_24), "Галерея",
                activity.getDrawable(R.drawable.ic_baseline_photo_camera_24), "Камера")
                .setOnFirstItem(() -> activity.startActivityForResult(multitouch ?
                        Intent.createChooser(new Intent()
                                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                .setType("image/*")
                                .setAction(Intent.ACTION_GET_CONTENT), "Выберите фотографии")
                        :
                        new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY))
                .setOnSecondItem(() ->{
                    String fileName = "photo";
                    try {
                        File imageFile = File.createTempFile(fileName, ".jpg", activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                        currentPhotoPath = imageFile.getAbsolutePath();
                        Uri imageUri = FileProvider.getUriForFile(activity,
                                "com.example.vkr.fileprovider", imageFile);
                        activity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                                        .putExtra(MediaStore.EXTRA_OUTPUT, imageUri), CAMERA);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void showMenu(@NonNull FragmentActivity activity, @NonNull Fragment fragment, @NonNull Boolean multitouch) {
        new ShowBottomDialog().showDialog(activity, "Откуда загрузить?",
            activity.getDrawable(R.drawable.ic_baseline_photo_24), "Галерея",
            activity.getDrawable(R.drawable.ic_baseline_photo_camera_24), "Камера")
            .setOnFirstItem(()-> fragment.startActivityForResult(multitouch ?
                    Intent.createChooser(new Intent()
                            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                            .setType("image/*")
                            .setAction(Intent.ACTION_GET_CONTENT), "Выберите фотографии")
                    :
                    new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY))
            .setOnSecondItem(()-> {
                String fileName = "photo";
                try {
                    File imageFile = File.createTempFile(fileName, ".jpg", activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                    currentPhotoPath = imageFile.getAbsolutePath();
                    Uri imageUri = FileProvider.getUriForFile(activity,
                            "com.example.vkr.fileprovider", imageFile);
                    fragment.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            .putExtra(MediaStore.EXTRA_OUTPUT, imageUri), CAMERA);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }
}
