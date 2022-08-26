package com.himanshu.aicteadmin.statistics;

import static com.himanshu.aicte.common.database.Constant.STATISTICS_COLLECTION_REFERENCE;
import static com.himanshu.aicte.common.statistics.CommonStatistics.dummyStatistics;
import static com.himanshu.aicte.common.statistics.Constants.INSTITUTION_TYPE_ARRAY;
import static com.himanshu.aicte.common.statistics.Constants.LEVEL_ARRAY;
import static com.himanshu.aicte.common.statistics.Constants.PROGRAM_ARRAY;
import static com.himanshu.aicte.common.statistics.Constants.STATE_ARRAY;
import static com.himanshu.aicte.common.statistics.Constants.YEAR_ARRAY;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.himanshu.aicte.common.statistics.OnFilterSubmitListener;
import com.himanshu.aicte.common.statistics.Statistics;
import com.himanshu.aicte.common.statistics.StatisticsFilterDialog;
import com.himanshu.aicteadmin.R;

import java.util.HashMap;
import java.util.Map;

public class StatisticsWriterFragment extends Fragment {

    private static final String TAG = "StatisticsWriterFrag";

    private View rootView;

    private EditText etTotalInstitutionsData, etNewInstitutionsData, etClosedInstitutionsData,
            etTotalIntakeData, etFemaleEnrolmentData, etMaleEnrolmentData, etFacultiesData,
            etStudentsPassedData, etPlacementData;

    private String mYear, mState, mInstitutionType, mLevel, mProgram;

    private Statistics mOldStatistics;
    private ProgressBar mProgressBar;

    public StatisticsWriterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_statistics_writer, container, false);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Statistics Writer");
        }

        mProgressBar = rootView.findViewById(R.id.progressBar_fragment_statistics_writer);

        displayStatistics(dummyStatistics);

        Button buttonUpdate = rootView.findViewById(R.id.button_fragment_statistics_writer_update);

        buttonUpdate.setOnClickListener(v -> {

            if (mYear != null) {
                Statistics statistics = getFormStatistics();
                updateStatistics(mYear, mState, mInstitutionType, mLevel, mProgram, statistics);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(com.himanshu.aicte.common.R.menu.statistics, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == com.himanshu.aicte.common.R.id.menuItem_fragment_statistics_filter) {
            filterStatistics();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTotalYearStatistics(String year, Statistics statistics) {
        // updating total statistics data.
        STATISTICS_COLLECTION_REFERENCE.document(year).set(statistics).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Total Statistics updated.");
                } else {
                    Log.d(TAG, "ERROR: " + task.getException().getMessage());
                    Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addStatistics(String year, String state, String institutionType, String level, String program, Statistics statistics) {

        Map<String, Statistics> programMap = new HashMap<>();
        programMap.put(program, statistics);

        Map<String, Map<String, Statistics>> levelMap = new HashMap<>();
        levelMap.put(level, programMap);

        STATISTICS_COLLECTION_REFERENCE.document(year).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot yearDocument = task.getResult();
                    if (yearDocument.exists()) {
                        Statistics retrievedStatistics = yearDocument.toObject(Statistics.class);

                        retrievedStatistics.add(statistics);

                        setTotalYearStatistics(year, retrievedStatistics);

                    } else {
                        STATISTICS_COLLECTION_REFERENCE.document(year).set(statistics).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Total Statistics added");
                                } else {
                                    Log.d(TAG, "ERROR: " + task.getException().getMessage());
                                    Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        STATISTICS_COLLECTION_REFERENCE.document(year).collection(state).document(institutionType).set(levelMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Statistics added.");
                    Toast.makeText(getContext(), "Statistics added.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "ERROR: " + task.getException().getMessage());
                }
            }
        });

    }


    private void updateStatistics(String year, String state, String institutionType, String level, String program, Statistics updatedStatistics) {

        Map<String, Statistics> programMap = new HashMap<>();
        programMap.put(program, updatedStatistics);

        Map<String, Map<String, Statistics>> levelMap = new HashMap<>();
        levelMap.put(level, programMap);


        String path = level + '.' + program;

        // checking if any data exists for given year.
        STATISTICS_COLLECTION_REFERENCE.document(year).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot yearDocumentSnapshot = task.getResult();

                    if (yearDocumentSnapshot.exists()) {

                        // checking if any data exists for given state, institution type.
                        STATISTICS_COLLECTION_REFERENCE.document(year).collection(state).document(institutionType).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot institutionTypeDocumentSnapshot = task.getResult();

                                    // data exists for updating.
                                    if (institutionTypeDocumentSnapshot.exists()) {

                                        // updating internal statistics data.
                                        STATISTICS_COLLECTION_REFERENCE.document(year).collection(state).document(institutionType).update(path, updatedStatistics)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Statistics retrievedYearTotalStatistics = yearDocumentSnapshot.toObject(Statistics.class);

                                                            if (mOldStatistics != null) {
                                                                //Log.d(TAG, "old Stat: " + mOldStatistics.toString());
                                                                retrievedYearTotalStatistics.subtract(mOldStatistics);
                                                                //Log.d(TAG, "Retrieved Stat after-: " + retrievedYearTotalStatistics.toString());
                                                            }

                                                            mOldStatistics = updatedStatistics;
                                                            retrievedYearTotalStatistics.add(updatedStatistics);
                                                            setTotalYearStatistics(year, retrievedYearTotalStatistics);

                                                            Toast.makeText(getContext(), "Statistics updated.", Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, "Statistics updated.");
                                                        } else {
                                                            Log.d(TAG, "ERROR: " + task.getException().getMessage());
                                                            Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    // data does not exists.
                                    else {
                                        addStatistics(year, state, institutionType, level, program, updatedStatistics);
                                    }
                                }
                            }
                        });
                    }
                    // document does not exist so firstly creating it and then writing data.
                    else {
                        Log.d(TAG, "Document do Not exist, creating new");
                        addStatistics(year, state, institutionType, level, program, updatedStatistics);
                    }
                } else {
                    Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "ERROR: " + task.getException().getMessage());
                }
            }
        });

    }


    @NonNull
    private Statistics getFormStatistics() {
        long totalInstitutions = Long.parseLong(etTotalInstitutionsData.getText().toString());
        long newInstitutions = Long.parseLong(etNewInstitutionsData.getText().toString());
        long closedInstitutions = Long.parseLong(etClosedInstitutionsData.getText().toString());
        long femaleEnrolment = Long.parseLong(etFemaleEnrolmentData.getText().toString());
        long maleEnrolment = Long.parseLong(etMaleEnrolmentData.getText().toString());
        long totalIntake = Long.parseLong(etTotalIntakeData.getText().toString());
        long faculties = Long.parseLong(etFacultiesData.getText().toString());
        long studentsPassed = Long.parseLong(etStudentsPassedData.getText().toString());
        long placement = Long.parseLong(etPlacementData.getText().toString());

        Statistics statistics = new Statistics();
        statistics.setTotalInstitutions(totalInstitutions);
        statistics.setNewInstitutions(newInstitutions);
        statistics.setClosedInstitutions(closedInstitutions);
        statistics.setTotalIntake(totalIntake);
        statistics.setFemaleEnrolment(femaleEnrolment);
        statistics.setMaleEnrolment(maleEnrolment);
        statistics.setFaculties(faculties);
        statistics.setStudentsPassed(studentsPassed);
        statistics.setPlacement(placement);

        return statistics;
    }

    private void filterStatistics() {

        StatisticsFilterDialog statisticsFilterDialog = new StatisticsFilterDialog(YEAR_ARRAY, STATE_ARRAY, INSTITUTION_TYPE_ARRAY, LEVEL_ARRAY, PROGRAM_ARRAY);

        statisticsFilterDialog.show(getChildFragmentManager(), "Statistics Writer");

        statisticsFilterDialog.setOnFilterSubmitListener(new OnFilterSubmitListener() {
            @Override
            public void onFilterSubmit(String year, String state, String institutionType, String level, String program) {
                mProgressBar.setVisibility(View.VISIBLE);
                mYear = year;
                mState = state;
                mInstitutionType = institutionType;
                mLevel = level;
                mProgram = program;
                String path = level + '.' + program;

                String displayPath = year + ", " + state + ", " + institutionType + ", " + level + ", " + program;
                TextView tvPath = rootView.findViewById(R.id.textView_fragment_statistics_writer_path);
                tvPath.setText("Path: " + displayPath);

                STATISTICS_COLLECTION_REFERENCE.document(year).collection(state).document(institutionType)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                mProgressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Statistics statistics = task.getResult().get(path, Statistics.class);
                                    if (statistics != null) {
                                        mOldStatistics = statistics;
                                        displayStatistics(statistics);
                                    } else {
                                        mOldStatistics = null;
                                        displayStatistics(dummyStatistics);
                                        Toast.makeText(getContext(), "Data not found!", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayStatistics(@NonNull Statistics statistics) {

        CardView cvTotalInstitutions = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_total_institutions);
        TextView tvTotalInstitutionsTitle = (TextView) cvTotalInstitutions.getChildAt(0);
        tvTotalInstitutionsTitle.setText("Total Institutions");
        etTotalInstitutionsData = (EditText) cvTotalInstitutions.getChildAt(1);
        etTotalInstitutionsData.setText("" + statistics.getTotalInstitutions());

        CardView cvNewInstitutions = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_new_institutions);
        TextView tvNewInstitutionsTitle = (TextView) cvNewInstitutions.getChildAt(0);
        tvNewInstitutionsTitle.setText("New Institutions");
        etNewInstitutionsData = (EditText) cvNewInstitutions.getChildAt(1);
        etNewInstitutionsData.setText("" + statistics.getNewInstitutions());

        CardView cvClosedInstitutions = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_closed_institutions);
        TextView tvClosedInstitutionsTitle = (TextView) cvClosedInstitutions.getChildAt(0);
        tvClosedInstitutionsTitle.setText("Closed Institutions");
        etClosedInstitutionsData = (EditText) cvClosedInstitutions.getChildAt(1);
        etClosedInstitutionsData.setText("" + statistics.getClosedInstitutions());

        CardView cvTotalIntake = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_total_intake);
        TextView tvTotalIntakeTitle = (TextView) cvTotalIntake.getChildAt(0);
        tvTotalIntakeTitle.setText("Total Intake");
        etTotalIntakeData = (EditText) cvTotalIntake.getChildAt(1);
        etTotalIntakeData.setText("" + statistics.getTotalIntake());

        CardView cvFemaleEnrolment = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_female_enrolment);
        TextView tvFemaleEnrolmentTitle = (TextView) cvFemaleEnrolment.getChildAt(0);
        tvFemaleEnrolmentTitle.setText("Female Enrolment");
        etFemaleEnrolmentData = (EditText) cvFemaleEnrolment.getChildAt(1);
        etFemaleEnrolmentData.setText("" + statistics.getFemaleEnrolment());


        CardView cvMaleEnrolment = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_male_enrolment);
        TextView tvMaleEnrolmentTitle = (TextView) cvMaleEnrolment.getChildAt(0);
        tvMaleEnrolmentTitle.setText("Male Enrolment");
        etMaleEnrolmentData = (EditText) cvMaleEnrolment.getChildAt(1);
        etMaleEnrolmentData.setText("" + statistics.getMaleEnrolment());

        CardView cvFaculties = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_faculties);
        TextView tvFacultiesTitle = (TextView) cvFaculties.getChildAt(0);
        tvFacultiesTitle.setText("Faculties");
        etFacultiesData = (EditText) cvFaculties.getChildAt(1);
        etFacultiesData.setText("" + statistics.getFaculties());

        CardView cvStudentsPassed = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_students_passed);
        TextView tvStudentsPassedTitle = (TextView) cvStudentsPassed.getChildAt(0);
        tvStudentsPassedTitle.setText("Students Passed");
        etStudentsPassedData = (EditText) cvStudentsPassed.getChildAt(1);
        etStudentsPassedData.setText("" + statistics.getStudentsPassed());

        CardView cvPlacement = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_placement);
        TextView tvPlacementTitle = (TextView) cvPlacement.getChildAt(0);
        tvPlacementTitle.setText("Placement");
        etPlacementData = (EditText) cvPlacement.getChildAt(1);
        etPlacementData.setText("" + statistics.getPlacement());
    }


}