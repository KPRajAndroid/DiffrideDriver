package com.diff.provider.Bean;

import com.diff.provider.Listeners.ConnectionBooleanChangedListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public class Connect {
    private static boolean myBoolean;
    private static List<ConnectionBooleanChangedListener> listeners = new ArrayList<ConnectionBooleanChangedListener>();

    public static boolean getMyBoolean() {
        return myBoolean;
    }

    public static void setMyBoolean(boolean value) {
        myBoolean = value;

        for (ConnectionBooleanChangedListener l : listeners) {
            l.OnMyBooleanChanged();
        }
    }

    public static void addMyBooleanListener(ConnectionBooleanChangedListener l) {
        listeners.add(l);
    }
}
