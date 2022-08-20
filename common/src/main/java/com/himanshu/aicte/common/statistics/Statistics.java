package com.himanshu.aicte.common.statistics;

public class Statistics {

    private long totalInstitutions, closedInstitutions, newInstitutions, totalIntake,
            femaleEnrolment, maleEnrolment, faculties, studentsPassed, placement;

    public Statistics() {
    }

    public Statistics(long closedInstitutions, long faculties, long femaleEnrolment, long maleEnrolment, long newInstitutions, long placement, long studentsPassed, long totalInstitutions, long totalIntake) {
        this.closedInstitutions = closedInstitutions;
        this.faculties = faculties;
        this.femaleEnrolment = femaleEnrolment;
        this.maleEnrolment = maleEnrolment;
        this.newInstitutions = newInstitutions;
        this.placement = placement;
        this.studentsPassed = studentsPassed;
        this.totalInstitutions = totalInstitutions;
        this.totalIntake = totalIntake;
    }

    public long getClosedInstitutions() {
        return closedInstitutions;
    }

    public void setClosedInstitutions(long closedInstitutions) {
        this.closedInstitutions = closedInstitutions;
    }

    public long getFaculties() {
        return faculties;
    }

    public void setFaculties(long faculties) {
        this.faculties = faculties;
    }

    public long getFemaleEnrolment() {
        return femaleEnrolment;
    }

    public void setFemaleEnrolment(long femaleEnrolment) {
        this.femaleEnrolment = femaleEnrolment;
    }

    public long getMaleEnrolment() {
        return maleEnrolment;
    }

    public void setMaleEnrolment(long maleEnrolment) {
        this.maleEnrolment = maleEnrolment;
    }

    public long getNewInstitutions() {
        return newInstitutions;
    }

    public void setNewInstitutions(long newInstitutions) {
        this.newInstitutions = newInstitutions;
    }

    public long getPlacement() {
        return placement;
    }

    public void setPlacement(long placement) {
        this.placement = placement;
    }

    public long getStudentsPassed() {
        return studentsPassed;
    }

    public void setStudentsPassed(long studentsPassed) {
        this.studentsPassed = studentsPassed;
    }

    public long getTotalInstitutions() {
        return totalInstitutions;
    }

    public void setTotalInstitutions(long totalInstitutions) {
        this.totalInstitutions = totalInstitutions;
    }

    public long getTotalIntake() {
        return totalIntake;
    }

    public void setTotalIntake(long totalIntake) {
        this.totalIntake = totalIntake;
    }

    public void add(Statistics statistics) {
        this.totalInstitutions += statistics.getTotalInstitutions();
        this.closedInstitutions += statistics.getClosedInstitutions();
        this.newInstitutions += statistics.getNewInstitutions();
        this.totalIntake += statistics.getTotalIntake();
        this.femaleEnrolment += statistics.getFemaleEnrolment();
        this.maleEnrolment += statistics.getMaleEnrolment();
        this.faculties += statistics.getFaculties();
        this.studentsPassed += statistics.getStudentsPassed();
        this.placement += statistics.getPlacement();
    }

}
