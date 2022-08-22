package com.himanshu.aicteadmin.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.himanshu.aicte.common.database.Constant;
import com.himanshu.aicte.common.news.News;
import com.himanshu.aicte.common.news.NewsAdapter;
import com.himanshu.aicteadmin.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference newsCollectionReference = db.collection(Constant.COLLECTION_NEWS);

    private NewsAdapter newsAdapter;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("News");
        }

        Query query = newsCollectionReference.orderBy(Constant.FIELD_NEWS_TIMESTAMP, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<News> options = new FirestoreRecyclerOptions.Builder<News>()
                .setQuery(query, News.class)
                .build();

        newsAdapter = new NewsAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_fragment_news);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsAdapter);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        newsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        newsAdapter.stopListening();
    }
}
