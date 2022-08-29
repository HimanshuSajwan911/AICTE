package com.himanshu.aicteadmin.statistics;

import static com.himanshu.aicte.common.database.Constant.STATISTICS_COLLECTION_REFERENCE;
import static com.himanshu.aicte.common.statistics.CommonStatistics.dummyStatistics;
import static com.himanshu.aicte.common.statistics.Constants.INSTITUTION_TYPE_ARRAY;
import static com.himanshu.aicte.common.statistics.Constants.LEVEL_ARRAY;
import static com.himanshu.aicte.common.statistics.Constants.PROGRAM_ARRAY;
import static com.himanshu.aicte.common.statistics.Constants.STATE_ARRAY;
import static com.himanshu.aicte.common.statistics.Constants.YEAR_ARRAY;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.himanshu.aicte.common.statistics.CommonStatistics;
import com.himanshu.aicte.common.statistics.OnFilterSubmitListener;
import com.himanshu.aicte.common.statistics.Statistics;
import com.himanshu.aicte.common.statistics.StatisticsFilterDialog;
import com.himanshu.aicteadmin.MainActivity;
import com.himanshu.aicteadmin.R;

public class StatisticsFragment extends Fragment {

    private static final String TAG = "StatisticsFragment";

    private Statistics mStatistics;
    private ProgressBar mProgressBar;

    private View rootView;

    public StatisticsFragment() {
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
        rootView = inflater.inflate(com.himanshu.aicte.common.R.layout.fragment_statistics, container, false);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Statistics");
        }

        mProgressBar = rootView.findViewById(com.himanshu.aicte.common.R.id.progressBar_common_fragment_statistics);

        CommonStatistics.displayStatistics(dummyStatistics, rootView);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_statistics, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuItem_fragment_statistics_filter) {

            filterStatistics();

        } else if (id == R.id.menuItem_fragment_statistics_update) {

            StatisticsWriterFragment statisticsWriterFragment = new StatisticsWriterFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ((MainActivity) getActivity()).addToNavigationStack(com.himanshu.aicte.common.R.id.menuItem_activity_main_statistics);
            fragmentTransaction.replace(R.id.frameLayout_activity_main, statisticsWriterFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

        return super.onOptionsItemSelected(item);
    }

    private void filterStatistics() {

        StatisticsFilterDialog statisticsFilterDialog = new StatisticsFilterDialog(YEAR_ARRAY, STATE_ARRAY, INSTITUTION_TYPE_ARRAY, LEVEL_ARRAY, PROGRAM_ARRAY);

        statisticsFilterDialog.show(getChildFragmentManager(), "Statistics Writer");

        statisticsFilterDialog.setOnFilterSubmitListener(new OnFilterSubmitListener() {
            @Override
            public void onFilterSubmit(String year, String state, String institutionType, String level, String program) {
                mProgressBar.setVisibility(View.VISIBLE);
                String path = level + '.' + program;

                String displayPath = year + ", " + state + ", " + institutionType + ", " + level + ", " + program;

                TextView tvPath = rootView.findViewById(com.himanshu.aicte.common.R.id.textView_common_fragment_statistics_path);
                tvPath.setText("Path: " + displayPath);

                STATISTICS_COLLECTION_REFERENCE.document(year).collection(state).document(institutionType)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                mProgressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    mStatistics = task.getResult().get(path, Statistics.class);
                                    if (mStatistics != null) {
                                        CommonStatistics.displayStatistics(mStatistics, rootView);
                                    } else {
                                        Toast.makeText(getContext(), "Data not found!", Toast.LENGTH_SHORT).show();
                                        CommonStatistics.displayStatistics(dummyStatistics, rootView);
                                    }
                                } else {
                                    Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });

            }
        });
    }


}