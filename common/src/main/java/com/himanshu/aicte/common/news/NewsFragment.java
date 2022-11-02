package com.himanshu.aicte.common.news;

import static com.himanshu.aicte.common.database.Constant.ADMIN_COLLECTION_REFERENCE;
import static com.himanshu.aicte.common.database.Constant.FIELD_NEWS_SAVED_NEWS;
import static com.himanshu.aicte.common.database.Constant.NEWS_COLLECTION_REFERENCE;
import static com.himanshu.aicte.common.database.Constant.USER_COLLECTION_REFERENCE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.himanshu.aicte.common.R;
import com.himanshu.aicte.common.database.Constant;
import com.himanshu.aicte.common.user.User;

import java.util.List;


public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference newsCollectionReference = db.collection(Constant.COLLECTION_NEWS);

    private NewsAdapter newsAdapter;
    private final String mUserType;
    private boolean newsEditorAllowed;

    public NewsFragment() {
        mUserType = User.TYPE_USER;
    }

    public NewsFragment(String userType) {
        this.mUserType = userType;
    }

    public void setNewsEditorAllowed(boolean newsEditorAllowed) {
        this.newsEditorAllowed = newsEditorAllowed;
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

        newsAdapter = new NewsAdapter(options, mUserType);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_fragment_news);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setItemAnimator(null);

        newsAdapter.setOnNewsItemClickListener(new OnNewsItemClickListener() {
            @Override
            public void onNewsItemClick(int position, int itemType, View view) {
                News news = newsAdapter.getItem(position);

                if (itemType == OnNewsItemClickListener.SAVED_NEWS) {

                    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (fbUser != null) {
                        String userId = fbUser.getUid();
                        DocumentReference documentUser;

                        if (mUserType.equals(User.TYPE_ADMIN)) {
                            documentUser = ADMIN_COLLECTION_REFERENCE.document(userId);
                        } else {
                            documentUser = USER_COLLECTION_REFERENCE.document(userId);
                        }

                        documentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                User user = documentSnapshot.toObject(User.class);

                                List<String> savedNewsList = user.getSavedNews();

                                if (savedNewsList != null && savedNewsList.contains(news.getHeadline())) {
                                    documentUser.update(FIELD_NEWS_SAVED_NEWS, FieldValue.arrayRemove(news.getHeadline()));
                                } else {
                                    documentUser.update(FIELD_NEWS_SAVED_NEWS, FieldValue.arrayUnion(news.getHeadline()));
                                }
                                newsAdapter.notifyItemChanged(position);
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Sign in to save news", Toast.LENGTH_SHORT).show();
                    }
                } else if (itemType == OnNewsItemClickListener.BODY) {
                    Intent intent = new Intent(getContext(), NewsViewerActivity.class);
                    Gson gson = new Gson();
                    String newsJson = gson.toJson(news);
                    intent.putExtra("newsJson", newsJson);
                    startActivity(intent);

                }

            }

            @Override
            public void onNewsItemLongClick(int position, int itemType, View view) {
                Log.d(TAG, "Long Click: " + position);
                News news = newsAdapter.getItem(position);
                if (newsEditorAllowed) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_news, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            int itemId = item.getItemId();
                            if (itemId == R.id.menu_news_popup_edit) {
                                //TODO start edit news activity
                                return true;
                            } else if (itemId == R.id.menu_news_popup_delete) {

                                NEWS_COLLECTION_REFERENCE.document(news.getHeadline())
                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    newsAdapter.notifyItemRemoved(position);
                                                    Toast.makeText(getContext(), "News Deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    String errMessage = task.getException().getMessage();
                                                    if (errMessage == null) {
                                                        errMessage = "could not delete news.";
                                                    }
                                                    Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                return true;
                            }

                            return false;
                        }
                    });

                    popupMenu.show();
                }

            }
        });

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
