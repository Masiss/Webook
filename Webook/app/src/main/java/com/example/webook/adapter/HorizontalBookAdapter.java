package com.example.webook.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.webook.DetailBookActivity;
import com.example.webook.databinding.PortraitBookItemBinding;
import com.example.webook.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HorizontalBookAdapter extends RecyclerView.Adapter<HorizontalBookAdapter.ViewHolder> {
    private ArrayList<Book> books;
    private Context context;

    public HorizontalBookAdapter(Context context, ArrayList<Book> books) {
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PortraitBookItemBinding binding = PortraitBookItemBinding.inflate(
                LayoutInflater.from(this.context),
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(book.getImage());
        Log.d("qqqqqq", storageRef.toString());
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(context)
                            .load(task.getResult())
                            .into(holder.getBinding().bookImage);
                }

            }
        });

        holder.getBinding().bookTitle.setText(book.getTitle());
        holder.getBinding().author.setText(book.getAuthor());
        holder.getBinding().bookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailBookActivity.class);
                intent.putExtra("book", book);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private PortraitBookItemBinding binding;

        public PortraitBookItemBinding getBinding() {
            return binding;
        }

        public void setBinding(PortraitBookItemBinding binding) {
            this.binding = binding;
        }

        ViewHolder(PortraitBookItemBinding binding) {
            super((binding.getRoot()));
            setBinding(binding);
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
