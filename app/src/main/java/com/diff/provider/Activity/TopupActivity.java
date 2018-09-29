package com.diff.provider.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.diff.provider.DiffApplication;
import com.diff.provider.Helper.CustomDialog;
import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Helper.URLHelper;
import com.diff.provider.Models.AccessDetails;
import com.diff.provider.R;
import com.diff.provider.Utilities.MyButton;
import com.google.android.gms.maps.model.LatLng;
import com.molpay.molpayxdk.MOLPayActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.diff.provider.DiffApplication.trimMessage;


public class TopupActivity extends AppCompatActivity {

    @BindView(R.id.backArrow)
    ImageView backArrow;
    @BindView(R.id.rd20)
    RadioButton rd20;
    @BindView(R.id.rd50)
    RadioButton rd50;
    @BindView(R.id.rd100)
    RadioButton rd100;
    @BindView(R.id.rd200)
    RadioButton rd200;
    @BindView(R.id.toggle)
    RadioGroup toggle;
    @BindView(R.id.chkTerms)
    CheckBox chkTerms;
    @BindView(R.id.btnSubmit)
    MyButton btnSubmit;
    @BindView(R.id.txtminRM5)
    EditText txtminRM5;
    @BindView(R.id.lblTerms)
    TextView lblTerms;

    private CustomDialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        ButterKnife.bind(this);

        checkEnable();
        lblTerms.setPaintFlags(lblTerms.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        txtminRM5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0)
                {
                    toggle.setOnCheckedChangeListener(null);
                    toggle.clearCheck();
                    checkEnable();

                }


            }
        });

    }

    @OnClick({R.id.backArrow, R.id.chkTerms, R.id.btnSubmit, R.id.lblTerms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backArrow:
                onBackPressed();
                break;
            case R.id.lblTerms:
                Intent viewIntent =new Intent("android.intent.action.VIEW",Uri.parse(URLHelper.TERMS));
                startActivity(viewIntent);
                break;
            case R.id.btnSubmit:

                if (txtminRM5.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Minimum value", Toast.LENGTH_LONG).show();
                } else if (!chkTerms.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please Accept Terms and Conditions", Toast.LENGTH_LONG).show();
                } else {
                    goToMOLPAY(txtminRM5.getText().toString().trim());

                }
                break;
        }
    }


    private void goToMOLPAY(String amount) {
        HashMap<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put(MOLPayActivity.mp_amount, amount);
        //        paymentDetails.put(MOLPayActivity.mp_username, "diffglobal@domain.com");
        paymentDetails.put(MOLPayActivity.mp_username, "api_diffglobal");
        paymentDetails.put(MOLPayActivity.mp_password, "api_HceT3583EL#");
        paymentDetails.put(MOLPayActivity.mp_merchant_ID, "diffglobal");
        paymentDetails.put(MOLPayActivity.mp_app_name, "diffglobal");
        paymentDetails.put(MOLPayActivity.mp_order_ID, "123");
        paymentDetails.put(MOLPayActivity.mp_currency, "MYR");
        paymentDetails.put(MOLPayActivity.mp_country, "MY");
        paymentDetails.put(MOLPayActivity.mp_verification_key, "59ab5d90bd9edb08f8f1506a05460fad");
        paymentDetails.put(MOLPayActivity.mp_channel, "multi");
        paymentDetails.put(MOLPayActivity.mp_bill_description, "");
        paymentDetails.put(MOLPayActivity.mp_bill_name, SharedHelper.getKey(getApplicationContext(), "first_name") + " " + SharedHelper.getKey(getApplicationContext(), "last_name"));
        paymentDetails.put(MOLPayActivity.mp_bill_email, SharedHelper.getKey(getApplicationContext(), "email"));
        paymentDetails.put(MOLPayActivity.mp_bill_mobile, SharedHelper.getKey(getApplicationContext(), "mobile"));
        paymentDetails.put(MOLPayActivity.mp_request_type, "");
        paymentDetails.put(MOLPayActivity.mp_sandbox_mode, true);

        Intent intent = new Intent(TopupActivity.this, MOLPayActivity.class);
        intent.putExtra(MOLPayActivity.MOLPayPaymentDetails, paymentDetails);
        startActivityForResult(intent, MOLPayActivity.MOLPayXDK);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOLPayActivity.MOLPayXDK && resultCode == RESULT_OK) {
            Log.d(MOLPayActivity.MOLPAY, "MOLPay result = " + data.getStringExtra(MOLPayActivity.MOLPayTransactionResult));
            JSONObject MolPay_Response;
            try {
                MolPay_Response = new JSONObject(data.getStringExtra(MOLPayActivity.MOLPayTransactionResult));

                if (MolPay_Response.optString("status_code").equalsIgnoreCase("00")) {
                    AddMoney(MolPay_Response.optString("status_code"), MolPay_Response.optString("amount"), MolPay_Response.optString("txn_ID"));
                } else {
                    Toast.makeText(TopupActivity.this, "Payment Failed", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    private void AddMoney(String status_code, String amount, String txn_id) {

        customDialog = new CustomDialog(TopupActivity.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("status", status_code);
            object.put("amount", amount);
            object.put("txn_ID", txn_id);
            Log.e("", "status" + status_code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AccessDetails.serviceurl + URLHelper.WALLET_ADD, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.e("AddMoneyResponse", response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String message = jsonObject.optString("message");

                    displayMessage(message);
                    onBackPressed();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public void displayMessage(String toastString) {
        Log.d("displayMessage", "" + toastString);

//        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        Toast.makeText(TopupActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    void checkEnable()
    {
        toggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = group.findViewById(checkedId);
                int index = group.indexOfChild(radioButton);

                switch (index) {
                    case 0:
                        txtminRM5.setText("20");
                        txtminRM5.setSelection(txtminRM5.getText().length());
                        break;
                    case 1:
                        txtminRM5.setText("50");
                        txtminRM5.setSelection(txtminRM5.getText().length());
                        break;
                    case 2:
                        txtminRM5.setText("100");
                        txtminRM5.setSelection(txtminRM5.getText().length());
                        break;
                    case 3:
                        txtminRM5.setText("200");
                        txtminRM5.setSelection(txtminRM5.getText().length());
                        break;


                }

            }
        });
    }

}
