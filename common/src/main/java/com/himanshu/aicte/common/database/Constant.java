package com.himanshu.aicte.common.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Constant {

    public static final FirebaseFirestore DB_ROOT_REFERENCE = FirebaseFirestore.getInstance();

    public static final CollectionReference NEWS_COLLECTION_REFERENCE = DB_ROOT_REFERENCE.collection(Constant.COLLECTION_NEWS);

    public static final CollectionReference STATISTICS_COLLECTION_REFERENCE = DB_ROOT_REFERENCE.collection(Constant.COLLECTION_STATISTICS);

    public static final String COLLECTION_NEWS = "News";

    public static final String COLLECTION_STATISTICS = "Statistics";

    public static final String FIELD_NEWS_TIMESTAMP = "timestamp";


}
