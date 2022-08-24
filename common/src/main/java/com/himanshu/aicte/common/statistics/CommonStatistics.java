package com.himanshu.aicte.common.statistics;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

public final class CommonStatistics {

    public static final Statistics dummyStatistics = new Statistics(0, 0, 0, 0, 0, 0, 0, 0, 0);

    // Don't let anyone instantiate this class.
    private CommonStatistics() {
    }

    @SuppressLint("SetTextI18n")
    public static void displayStatistics(@NonNull Statistics statistics, View rootView) {

        CardView cvTotalInstitutions = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_total_institutions);
        TextView tvTotalInstitutionsTitle = (TextView) cvTotalInstitutions.getChildAt(0);
        tvTotalInstitutionsTitle.setText("Total Institutions");
        EditText etTotalInstitutionsData = (EditText) cvTotalInstitutions.getChildAt(1);
        etTotalInstitutionsData.setText("" + statistics.getTotalInstitutions());
        etTotalInstitutionsData.setKeyListener(null);

        CardView cvNewInstitutions = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_new_institutions);
        TextView tvNewInstitutionsTitle = (TextView) cvNewInstitutions.getChildAt(0);
        tvNewInstitutionsTitle.setText("New Institutions");
        EditText etNewInstitutionsData = (EditText) cvNewInstitutions.getChildAt(1);
        etNewInstitutionsData.setText("" + statistics.getNewInstitutions());
        etNewInstitutionsData.setKeyListener(null);

        CardView cvClosedInstitutions = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_closed_institutions);
        TextView tvClosedInstitutionsTitle = (TextView) cvClosedInstitutions.getChildAt(0);
        tvClosedInstitutionsTitle.setText("Closed Institutions");
        EditText etClosedInstitutionsData = (EditText) cvClosedInstitutions.getChildAt(1);
        etClosedInstitutionsData.setText("" + statistics.getClosedInstitutions());
        etClosedInstitutionsData.setKeyListener(null);

        CardView cvTotalIntake = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_total_intake);
        TextView tvTotalIntakeTitle = (TextView) cvTotalIntake.getChildAt(0);
        tvTotalIntakeTitle.setText("Total Intake");
        EditText etTotalIntakeData = (EditText) cvTotalIntake.getChildAt(1);
        etTotalIntakeData.setText("" + statistics.getTotalIntake());
        etTotalIntakeData.setKeyListener(null);

        CardView cvFemaleEnrolment = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_female_enrolment);
        TextView tvFemaleEnrolmentTitle = (TextView) cvFemaleEnrolment.getChildAt(0);
        tvFemaleEnrolmentTitle.setText("Female Enrolment");
        EditText etFemaleEnrolmentData = (EditText) cvFemaleEnrolment.getChildAt(1);
        etFemaleEnrolmentData.setText("" + statistics.getFemaleEnrolment());
        etFemaleEnrolmentData.setKeyListener(null);

        CardView cvMaleEnrolment = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_male_enrolment);
        TextView tvMaleEnrolmentTitle = (TextView) cvMaleEnrolment.getChildAt(0);
        tvMaleEnrolmentTitle.setText("Male Enrolment");
        EditText etMaleEnrolmentData = (EditText) cvMaleEnrolment.getChildAt(1);
        etMaleEnrolmentData.setText("" + statistics.getMaleEnrolment());
        etMaleEnrolmentData.setKeyListener(null);

        CardView cvFaculties = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_faculties);
        TextView tvFacultiesTitle = (TextView) cvFaculties.getChildAt(0);
        tvFacultiesTitle.setText("Faculties");
        EditText etFacultiesData = (EditText) cvFaculties.getChildAt(1);
        etFacultiesData.setText("" + statistics.getFaculties());
        etFacultiesData.setKeyListener(null);

        CardView cvStudentsPassed = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_students_passed);
        TextView tvStudentsPassedTitle = (TextView) cvStudentsPassed.getChildAt(0);
        tvStudentsPassedTitle.setText("Students Passed");
        EditText etStudentsPassedData = (EditText) cvStudentsPassed.getChildAt(1);
        etStudentsPassedData.setText("" + statistics.getStudentsPassed());
        etStudentsPassedData.setKeyListener(null);

        CardView cvPlacement = rootView.findViewById(com.himanshu.aicte.common.R.id.stat_item_placement);
        TextView tvPlacementTitle = (TextView) cvPlacement.getChildAt(0);
        tvPlacementTitle.setText("Placement");
        EditText etPlacementData = (EditText) cvPlacement.getChildAt(1);
        etPlacementData.setText("" + statistics.getPlacement());
        etPlacementData.setKeyListener(null);
    }

}
