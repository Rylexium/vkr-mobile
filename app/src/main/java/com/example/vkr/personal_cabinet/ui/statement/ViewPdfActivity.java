package com.example.vkr.personal_cabinet.ui.statement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.vkr.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ViewPdfActivity extends AppCompatActivity {

    public static File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pdf_activity);
        PDFView pdfView = findViewById(R.id.pdfview);
        pdfView.fromFile(file).load();
    }
}