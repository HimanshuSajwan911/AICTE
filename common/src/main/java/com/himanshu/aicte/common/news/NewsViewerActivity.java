package com.himanshu.aicte.common.news;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.himanshu.aicte.common.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_viewer);

        Gson gson = new Gson();
        News news = gson.fromJson(getIntent().getStringExtra("newsJson"), News.class);

        displayNews(news);

        Button button = findViewById(R.id.button_activity_news_viewer_ok);

        button.setOnClickListener(v -> finish());

    }

    private void displayNews(News news){

        TextView tvHeadline = findViewById(R.id.textView_activity_news_viewer_headline);
        TextView tvTimestamp = findViewById(R.id.textView_activity_news_viewer_timestamp);
        TextView tvAuthor = findViewById(R.id.textView_activity_news_viewer_author);
        TextView tvBody= findViewById(R.id.textView_activity_news_viewer_body);
        tvBody.setMovementMethod(new ScrollingMovementMethod());

        Date date = news.getTimestamp().toDate();
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy, HH:mm");
        String dateTime = format.format(date);

        tvTimestamp.setText(dateTime + ",");
        tvAuthor.setText(news.getAuthor());
        tvHeadline.setText(news.getHeadline());
        tvBody.setText(news.getBody());

    }


}