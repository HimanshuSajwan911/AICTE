package com.himanshu.aicte.common.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Constant {

    public static final CollectionReference NEWS_COLLECTION_REFERENCE = FirebaseFirestore.getInstance().collection(Constant.COLLECTION_NEWS);

    public static final CollectionReference STATISTICS_COLLECTION_REFERENCE = FirebaseFirestore.getInstance().collection(Constant.COLLECTION_STATISTICS);

    public static final CollectionReference ADMIN_COLLECTION_REFERENCE = FirebaseFirestore.getInstance().collection(Constant.COLLECTION_ADMIN);

    public static final CollectionReference USER_COLLECTION_REFERENCE = FirebaseFirestore.getInstance().collection(Constant.COLLECTION_USER);

    public static final String COLLECTION_NEWS = "News";

    public static final String COLLECTION_ADMIN = "Admin";

    public static final String COLLECTION_USER = "User";

    public static final String COLLECTION_STATISTICS = "Statistics";

    public static final String FIELD_NEWS_TIMESTAMP = "timestamp";

    public static final String IMAGE_STORAGE_PATH = "image";

    public static final String USER_IMAGE_PATH = IMAGE_STORAGE_PATH + "/" + "user";

    public static final String USER_PROFILE_IMAGE_PATH = USER_IMAGE_PATH + "/" + "profile";

    public static final String ADMIN_IMAGE_PATH = IMAGE_STORAGE_PATH + "/" + "admin";

    public static final String ADMIN_PROFILE_IMAGE_PATH = ADMIN_IMAGE_PATH + "/" + "profile";




}
