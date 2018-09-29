package com.diff.provider.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.diff.provider.DiffApplication;
import com.diff.provider.Fragment.WalletCashFragment;
import com.diff.provider.Fragment.WalletCreditFragment;
import com.diff.provider.Helper.CustomDialog;
import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Helper.URLHelper;
import com.diff.provider.Models.AccessDetails;
import com.diff.provider.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.molpay.molpayxdk.MOLPayActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

import static com.diff.provider.DiffApplication.trimMessage;

public class ActivityWallet extends AppCompatActivity {

    ImageView backArrow;
    LinearLayout view_lyt;
    RelativeLayout Rview;
    TextView Wallet_balance, Credit_balance, lblFreeBalance;
    android.app.AlertDialog couponDialog;
    private CustomDialog customDialog;
    String device_token, device_UDID;
    String TAG = "ActivityWallet";

    TabLayout tabs;
    ViewPager container;
    TabPagerAdapter adapter;
    LinearLayout lnrTopup, lnrWithdraw, lnrCoupon, lnrReload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        backArrow = findViewById(R.id.backArrow);
        Rview = findViewById(R.id.Relative_lyt);
        view_lyt = findViewById(R.id.view_lyt);

        Wallet_balance = findViewById(R.id.Wallet_balance);
        Credit_balance = findViewById(R.id.Credit_balance);
        lblFreeBalance = findViewById(R.id.lblFreeBalance);

        lnrWithdraw = findViewById(R.id.lnrWithdraw);
        lnrCoupon = findViewById(R.id.lnrCoupon);
        lnrReload = findViewById(R.id.lnrReload);
        lnrTopup = findViewById(R.id.lnrTopup);

        tabs = findViewById(R.id.tabs);
        container = findViewById(R.id.container);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        lnrWithdraw.setOnClickListener(new Onclick());
        lnrCoupon.setOnClickListener(new Onclick());
        lnrReload.setOnClickListener(new Onclick());
        lnrTopup.setOnClickListener(new Onclick());



        tabs.addTab(tabs.newTab().setText("Wallet Cash"));
        tabs.addTab(tabs.newTab().setText("Wallet Credit"));

        adapter = new TabPagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        container.setAdapter(adapter);
        container.canScrollHorizontally(0);
        container.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                container.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void showCouponAlert() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ActivityWallet.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.coupon_dialog, null);

        Button submitBtn = (Button) view.findViewById(R.id.submit_btn);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        final EditText txtCoupon = (EditText) view.findViewById(R.id.txtCoupon);
        txtCoupon.setText("");

        builder.setView(view);
        couponDialog = builder.create();
        couponDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtCoupon.getText().toString().equalsIgnoreCase("")) {

                    AddCoupon(txtCoupon.getText().toString().trim());
                    couponDialog.dismiss();


                } else {
                    Toast.makeText(ActivityWallet.this, "Enter your coupon code", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                couponDialog.dismiss();
            }
        });


        couponDialog.show();
    }


//    goToMOLPAY(txtCoupon.getText().toString());


    private void addCredit(String s) {

        customDialog = new CustomDialog(ActivityWallet.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("amount", s);
            Log.e("", "status" + s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AccessDetails.serviceurl + URLHelper.CREDIT_ADD, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.e("AddMoneyResponse", response.toString());
                getProfile();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getApplicationContext().getResources().getString(R.string.something_went_wrong));
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
//                            GoToBeginActivity();
                            Toast.makeText(ActivityWallet.this, "Authorization Error", Toast.LENGTH_LONG).show();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getApplicationContext().getResources().getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getApplicationContext().getResources().getString(R.string.server_down));
                        } else {
                            displayMessage(getApplicationContext().getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getApplicationContext().getResources().getString(R.string.something_went_wrong));
                        e.printStackTrace();
                    }

                } else {
                    displayMessage(getApplicationContext().getResources().getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(ActivityWallet.this, "access_token"));
                Log.e("", "Access_Token" + SharedHelper.getKey(ActivityWallet.this, "access_token"));
                return headers;
            }
        };

        DiffApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }




    public void displayMessage(String toastString) {
        Log.d("displayMessage", "" + toastString);

//        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        Toast.makeText(ActivityWallet.this, toastString, Toast.LENGTH_LONG).show();
    }


    public void getProfile() {
        customDialog = new CustomDialog(ActivityWallet.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, AccessDetails.serviceurl + URLHelper.USER_PROFILE_API + "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "onResponse: "+response.toString());
                customDialog.dismiss();
                Wallet_balance.setText(response.optString("wallet_balance"));
                Credit_balance.setText(response.optString("credit_balance"));
                lblFreeBalance.setText(response.optString("free_balance"));


                SharedHelper.putKey(ActivityWallet.this, "wallet_balance", response.optString("wallet_balance"));
                SharedHelper.putKey(ActivityWallet.this, "credit_balance", response.optString("credit_balance"));
                SharedHelper.putKey(ActivityWallet.this, "free_balance", response.optString("free_balance"));
                SharedHelper.putKey(ActivityWallet.this, "withdrawal_charges", response.optString("withdrawal_charges"));


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            Toast.makeText(ActivityWallet.this, "Authorization Error", Toast.LENGTH_LONG).show();
                        } else if (response.statusCode == 422) {

                            json = DiffApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        }
                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getProfile();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(ActivityWallet.this, "access_token"));
                return headers;
            }
        };

        DiffApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(ActivityWallet.this, "device_token").equals("") && SharedHelper.getKey(ActivityWallet.this, "device_token") != null) {
                device_token = SharedHelper.getKey(ActivityWallet.this, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(ActivityWallet.this, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }


    private class Onclick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.lnrWithdraw:
                    startActivity(new Intent(ActivityWallet.this, WithdrawalActivity.class));
                    ;
                    break;

                case R.id.lnrCoupon:
                    //whatever
                    showCouponAlert();
                    break;

                case R.id.lnrReload:
                    //whatever
                    startActivity(new Intent(ActivityWallet.this, ReloadActivity.class));
                    break;

                case R.id.lnrTopup:
                    //whatever
                    startActivity(new Intent(ActivityWallet.this, TopupActivity.class));
                    break;

            }
        }
    }


    private void AddCoupon(String coupon) {

        customDialog = new CustomDialog(ActivityWallet.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("coupon", coupon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AccessDetails.serviceurl + URLHelper.COUPON_ADD, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.e("AddCoupon", response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String message = jsonObject.optString("message");
                    displayMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getProfile();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getApplicationContext().getResources().getString(R.string.something_went_wrong));
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
//                            GoToBeginActivity();
                            Toast.makeText(getApplicationContext(), "Authorization Error", Toast.LENGTH_LONG).show();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getApplicationContext().getResources().getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getApplicationContext().getResources().getString(R.string.server_down));
                        } else {
                            displayMessage(getApplicationContext().getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getApplicationContext().getResources().getString(R.string.something_went_wrong));
                        e.printStackTrace();
                    }

                } else {
                    displayMessage(getApplicationContext().getResources().getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                Log.e("", "Access_Token" + SharedHelper.getKey(getApplicationContext(), "access_token"));
                return headers;
            }
        };

        DiffApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GetToken();
        getProfile();
    }



    public class TabPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public TabPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new WalletCashFragment();
                case 1:
                    return new WalletCreditFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
