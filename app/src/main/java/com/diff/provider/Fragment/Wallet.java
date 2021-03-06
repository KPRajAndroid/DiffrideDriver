package com.diff.provider.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diff.provider.Activity.AccessKeyActivity;
import com.diff.provider.Activity.WelcomeScreenActivity;
import com.diff.provider.Helper.ConnectionHelper;
import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Models.AccessDetails;
import com.diff.provider.R;


/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */


public class Wallet extends Fragment {

    Activity activity;
    Context context;
    ConnectionHelper helper;
    Boolean isInternet;
    View rootView;

    public Wallet() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false);
        findViewByIdAndInitialize();
        return rootView;
    }

    public void findViewByIdAndInitialize() {
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();
    }

    public void displayMessage(String toastString) {
        Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent;

        if (AccessDetails.demo_build) {
            mainIntent = new Intent(activity, AccessKeyActivity.class);
        } else {
            mainIntent = new Intent(activity, WelcomeScreenActivity.class);
        }

        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
