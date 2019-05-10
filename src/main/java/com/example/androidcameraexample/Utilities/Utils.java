package com.example.androidcameraexample.Utilities;

public class Utils {

    //validate if the string isnull or empty
    public static boolean notNullNotFill(String validate) {
        if (validate != null) {
            if (!validate.trim().equals("")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}