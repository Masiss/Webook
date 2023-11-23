package com.example.webook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webook.databinding.ChapterBinding;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {

    private Context context;
    private List<String> chapters;
    public ChapterAdapter(Context context, List<String> chapters) {
        this.chapters=chapters;
        this.context=context;
    }

    @NonNull
    @Override
    public ChapterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChapterBinding binding=ChapterBinding.inflate(LayoutInflater.from(this.context),parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String chapter=chapters.get(position);
            holder.getChapterBinding().chapTitle.setText(chapter);
            holder.getChapterBinding().chapNo.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ChapterBinding chapterBinding;
        public ChapterBinding getChapterBinding(){
            return chapterBinding;
        }
        public void setChapterBinding(ChapterBinding chapterBinding){
            this.chapterBinding=chapterBinding;
        }
        ViewHolder(ChapterBinding chapterBinding){
            super((chapterBinding.getRoot()));
            setChapterBinding(chapterBinding);
        }
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
