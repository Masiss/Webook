package com.example.webook;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webook.databinding.ActivityChapterBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChapterActivity extends AppCompatActivity {

    ActivityChapterBinding binding;
    EditText pageNum;
    Button jumpPage;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChapterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        pageNum = findViewById(R.id.page_number);
        jumpPage = findViewById(R.id.jumpPage);

        String content = getIntent().getStringExtra("content");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(content);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        final long ONE_MEGABYTE = 1024 * 1024 * 10;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                pdfView.fromBytes(bytes)
                        .onRender(new OnRenderListener() {
                            @Override
                            public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                                pdfView.fitToWidth(pdfView.getCurrentPage());
                            }
                        })
                        .load();
                pdfView.jumpTo(3, true);
            }
        });
        jumpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int page = Integer.valueOf(pageNum.getText().toString());
                pdfView.jumpTo(page);
            }
        });
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
//        binding.btnListChapter.setOnClickListener(v -> {
////            intent = new Intent(this, ListChapterActivity.class);
////            startActivity(intent);
//        });
    }


}