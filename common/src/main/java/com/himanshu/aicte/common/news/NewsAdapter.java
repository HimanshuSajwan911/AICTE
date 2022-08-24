package com.himanshu.aicte.common.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsAdapter extends FirestoreRecyclerAdapter<News, NewsAdapter.NewsHolder> {


    public NewsAdapter(@NonNull FirestoreRecyclerOptions<News> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NewsHolder holder, int position, @NonNull News model) {

        Date date = model.getTimestamp().toDate();
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy, HH:mm");
        String dateTime = format.format(date);

        holder.tvTimestamp.setText(dateTime + ",");
        holder.tvAuthor.setText(model.getAuthor());
        holder.tvHeadline.setText(model.getHeadline());
        holder.tvBody.setText(model.getBody());

    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(com.himanshu.aicte.common.R.layout.news_item, parent, false);

        return new NewsHolder(view);
    }


    static class NewsHolder extends RecyclerView.ViewHolder{

        private final TextView tvTimestamp, tvHeadline, tvBody, tvAuthor;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);

            tvTimestamp = itemView.findViewById(com.himanshu.aicte.common.R.id.textView_news_item_timestamp);
            tvAuthor = itemView.findViewById(com.himanshu.aicte.common.R.id.textView_news_item_author);
            tvHeadline = itemView.findViewById(com.himanshu.aicte.common.R.id.textView_news_item_headline);
            tvBody = itemView.findViewById(com.himanshu.aicte.common.R.id.textView_news_item_body);

        }
    }

}
