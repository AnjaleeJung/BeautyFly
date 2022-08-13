package com.android.fypmid;

import java.util.ArrayList;

public class UserBookingsFirebase {
    public ArrayList<CartItem> bookingItems;

    public UserBookingsFirebase(ArrayList<CartItem> bookingItems) {
        this.bookingItems = bookingItems;
    }
    public UserBookingsFirebase() {
    }

    public ArrayList<CartItem> getBookingItems() {
        return bookingItems;
    }

    public void setBookingItems(ArrayList<CartItem> bookingItems) {
        this.bookingItems = bookingItems;
    }
}
