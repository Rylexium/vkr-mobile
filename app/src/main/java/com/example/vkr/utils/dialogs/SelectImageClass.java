package com.example.vkr.utils.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
                .setOnFirstItem(() -> activity.startActivityForResult(takePhotoGallery(multitouch), GALLERY))
                .setOnSecondItem(() -> activity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                                .putExtra(MediaStore.EXTRA_OUTPUT, takePhotoCamera(activity)), CAMERA));
    }

    public static void showMenu(@NonNull FragmentActivity activity, @NonNull Fragment fragment, @NonNull Boolean multitouch) {
        new ShowBottomDialog().showDialog(activity, "Откуда загрузить?",
            activity.getDrawable(R.drawable.ic_baseline_photo_24), "Галерея",
            activity.getDrawable(R.drawable.ic_baseline_photo_camera_24), "Камера")
            .setOnFirstItem(()-> fragment.startActivityForResult(takePhotoGallery(multitouch), GALLERY))
            .setOnSecondItem(()-> fragment.startActivityForResult(takePhotoCamera(activity), CAMERA));
    }

    @Nullable
    private static Intent takePhotoCamera(Context context){
        String fileName = "photo";
        try {
            File imageFile = File.createTempFile(fileName, ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            currentPhotoPath = imageFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(context,
                    "com.example.vkr.fileprovider", imageFile);
            return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Intent takePhotoGallery(Boolean multitouch) {
        return multitouch ?
                Intent.createChooser(new Intent()
                        .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        .setType("image/*")
                        .setAction(Intent.ACTION_GET_CONTENT), "Выберите фотографии")
                :
                new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

}
