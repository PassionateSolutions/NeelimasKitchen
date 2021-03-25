package com.deepak.neelimaskitchen.Common;

import com.deepak.neelimaskitchen.Model.Request;
import com.deepak.neelimaskitchen.Model.User;

public class Common {
    //Variable to save current logged in user
    public static User currentUser;
    public static Request currentRequest;

    //Used for deleting cart item
    public static String UPDATE = "Update";
    public static String DELETE = "Delete";

    public static String USER_KEY = "User";
    public static String PW_KEY = "Password";

    public static String convertCodeToStatus (String code) {

        if (code.equals("0"))
            return "Placed";

        switch (code) {
            case "1":
                return "Cancel Order...";
            case "2":
                return "Order being made...";
            case "3":
                return "On my way...";
        }

        return code;
    }

}
