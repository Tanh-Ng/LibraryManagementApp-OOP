package com.librarymanagement.model;


// Quan ly cac luot dat cho truoc cua nguoi dung cho cac tai lieu
public class Reservation {
    private int reservationId;
    private User user;
    private Document document;
    private String status; // status can be "Waitng" or "Complete"

    // Tao don dat truoc cho 1 tai lieu
    public void makeReservation() {
        //To do
    }

    //Huy bo don dat truoc
    public void cancelReservation() {
        //To do
    }

    //Kiem tra trang thai don dat cho
    public void checkStatus() {
        //To do
    }
}
