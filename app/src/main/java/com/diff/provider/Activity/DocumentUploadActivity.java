package com.diff.provider.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.diff.provider.DiffApplication;
import com.diff.provider.Helper.AppHelper;
import com.diff.provider.Helper.CustomDialog;
import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Helper.URLHelper;
import com.diff.provider.Helper.VolleyMultipartRequest;
import com.diff.provider.Models.AccessDetails;
import com.diff.provider.R;
import com.diff.provider.Utilities.MyTextView;
import com.diff.provider.Utilities.Utilities;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.diff.provider.DiffApplication.trimMessage;

public class DocumentUploadActivity extends AppCompatActivity {
    private static final int SELECT_PHOTO = 100;
    public static int deviceHeight;
    public static int deviceWidth;
    Boolean isImageChanged = false;
    ImageView currentImageView;
    TextView currentTextView;
    Boolean isPermissionGivenAlready = false;
    private static final String TAG = "DocumentUpload";
    CustomDialog customDialog;
    Context context;
    Utilities utils = new Utilities();
    String device_token, device_UDID;
    @BindView(R.id.driving_license)
    ImageView drivingLicense;
    @BindView(R.id.driving_license_expire_date)
    MyTextView drivingLicenseExpireDate;
    @BindView(R.id.nric_upload_image_front)
    ImageView nricUploadImageFront;
    @BindView(R.id.car_grant_upload_image_front)
    ImageView carGrantUploadImageFront;
    @BindView(R.id.car_grant_upload_image_back)
    ImageView carGrantUploadImageBack;
    @BindView(R.id.nextIcon)
    FloatingActionButton nextIcon;
    @BindView(R.id.insurance_cover_note_front)
    ImageView insuranceCoverNoteFront;
    @BindView(R.id.insurance_cover_expire_at)
    MyTextView insuranceCoverExpireAt;
    @BindView(R.id.road_tax_image_front)
    ImageView roadTaxImageFront;
    @BindView(R.id.road_tax_expire_at)
    MyTextView roadTaxExpireAt;
    @BindView(R.id.driver_photo)
    ImageView driverPhoto;
    @BindView(R.id.phone_imei)
    EditText phoneImei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        ButterKnife.bind(this);
        context = this;
        GetToken();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkIMEIPermission()) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1110);
            } else {
                getIMEI(this);
            }
        } else {
            getIMEI(this);
        }
        Log.d("DD", RegisterActivity.REGISTER_REQUEST_OBJECT.toString());
    }


    private void register_API() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AccessDetails.serviceurl + URLHelper.register, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();
                utils.print("SignInResponse", response.toString());
                SharedHelper.putKey(DocumentUploadActivity.this, "email", RegisterActivity.REGISTER_REQUEST_OBJECT.get("email"));
                SharedHelper.putKey(DocumentUploadActivity.this, "password", RegisterActivity.REGISTER_REQUEST_OBJECT.get("password"));
                signIn();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.getClass().equals(TimeoutError.class)) {
                            return;
                        }
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {

                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            //  displayMessage(getString(R.string.server_down));
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    } else if (error instanceof TimeoutError) {
                        //  signupFinal();
                        register_API();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mm = RegisterActivity.REGISTER_REQUEST_OBJECT;
                mm.put("document[1][expired_at]", drivingLicenseExpireDate.getText().toString());
                mm.put("document[5][expired_at]", insuranceCoverExpireAt.getText().toString());
                mm.put("document[6][expired_at]", roadTaxExpireAt.getText().toString());
                mm.put("phone_imei", phoneImei.getText().toString());
                return mm;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();

                params.put("document[1][front]", new DataPart("1.jpg", AppHelper.getFileDataFromDrawable(drivingLicense.getDrawable()), "image/jpeg"));

                params.put("document[2][front]", new DataPart("2.jpg", AppHelper.getFileDataFromDrawable(nricUploadImageFront.getDrawable()), "image/jpeg"));

                params.put("document[4][front]", new DataPart("5.jpg", AppHelper.getFileDataFromDrawable(carGrantUploadImageFront.getDrawable()), "image/jpeg"));
                params.put("document[4][back]", new DataPart("6.jpg", AppHelper.getFileDataFromDrawable(carGrantUploadImageBack.getDrawable()), "image/jpeg"));

                params.put("document[5][front]", new DataPart("7.jpg", AppHelper.getFileDataFromDrawable(insuranceCoverNoteFront.getDrawable()), "image/jpeg"));

                params.put("document[6][front]", new DataPart("8.jpg", AppHelper.getFileDataFromDrawable(roadTaxImageFront.getDrawable()), "image/jpeg"));

                params.put("avatar", new DataPart("10.jpg", AppHelper.getFileDataFromDrawable(driverPhoto.getDrawable()), "image/jpeg"));

                /*for (Document document : documentAdapter.getServiceListModel()) {
                    if (document.getBitmap() != null) {
                        String photo = "photos[" + document.getId() + "]";
                        params.put(photo, new VolleyMultipartRequest.DataPart("doc.jpg", AppHelper.getFileDataFromDrawable(document.getDrawable()), "image/jpeg"));
                    }
                }*/


                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 2000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        DiffApplication.getInstance().addToRequestQueue(volleyMultipartRequest);

    }


    public void signIn() {

        customDialog = new CustomDialog(DocumentUploadActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", device_token);
            object.put("email", SharedHelper.getKey(DocumentUploadActivity.this, "email"));
            object.put("password", SharedHelper.getKey(DocumentUploadActivity.this, "password"));
            utils.print("InputToLoginAPI", "" + object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AccessDetails.serviceurl + URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();
                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                    SharedHelper.putKey(context, "currency", response.optString("currency"));
                else
                    SharedHelper.putKey(context, "currency", "$");
                getProfile();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        utils.print("ErrorInLoginAPI", "" + errorObj.toString());

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                            if (response.statusCode == 401) {
                                waitingDialog(errorObj.optString("error"));
                            } else if(response.statusCode == 500)
                            {
                                displayMessage(errorObj.optString("error"));
                            }
                            else {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else {
                            displayMessage(getString(R.string.please_try_again));
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
                        signIn();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        DiffApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        try {

            Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setMaxLines(3);
            snackbar.show();

//            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }


    public void getProfile() {

        customDialog = new CustomDialog(DocumentUploadActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, AccessDetails.serviceurl + URLHelper.USER_PROFILE_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();
                utils.print("GetProfile", response.toString());
                SharedHelper.putKey(DocumentUploadActivity.this, "id", response.optString("id"));
                SharedHelper.putKey(DocumentUploadActivity.this, "first_name", response.optString("first_name"));
                SharedHelper.putKey(DocumentUploadActivity.this, "last_name", response.optString("last_name"));
                SharedHelper.putKey(DocumentUploadActivity.this, "email", response.optString("email"));
                if (response.optString("avatar").startsWith("http"))
                    SharedHelper.putKey(context, "picture", response.optString("avatar"));
                else
                    SharedHelper.putKey(context, "picture", AccessDetails.serviceurl + "/storage/" + response.optString("avatar"));
                SharedHelper.putKey(DocumentUploadActivity.this, "gender", "" + response.optString("gender"));
                SharedHelper.putKey(DocumentUploadActivity.this, "mobile", response.optString("mobile"));
                SharedHelper.putKey(context, "approval_status", response.optString("status"));
                if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                    SharedHelper.putKey(context, "currency", response.optString("currency"));
                else
                    SharedHelper.putKey(context, "currency", "$");
                SharedHelper.putKey(context, "sos", response.optString("sos"));
                SharedHelper.putKey(DocumentUploadActivity.this, "loggedIn", getString(R.string.True));

                if (response.optJSONObject("service") != null) {
                    JSONObject service = response.optJSONObject("service");
                    JSONObject serviceType = service.optJSONObject("service_type");
                    SharedHelper.putKey(context, "service", serviceType.optString("name"));
                }
                SharedHelper.putKey(DocumentUploadActivity.this, "login_by", "manual");
                GoToMainActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            displayMessage(getString(R.string.something_went_wrong));
                        } else if (response.statusCode == 401) {
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else {
                            displayMessage(getString(R.string.please_try_again));
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
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(DocumentUploadActivity.this, "access_token"));
                return headers;
            }
        };

        DiffApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(DocumentUploadActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        DocumentUploadActivity.this.finish();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(this, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(this, ActivityEmail.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    @OnClick({R.id.driving_license, R.id.driving_license_expire_date, R.id.nric_upload_image_front, R.id.car_grant_upload_image_front, R.id.car_grant_upload_image_back, R.id.nextIcon, R.id.insurance_cover_note_front, R.id.insurance_cover_expire_at, R.id.road_tax_image_front, R.id.driver_photo, R.id.road_tax_expire_at, R.id.backArrow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.driving_license:
                currentImageView = drivingLicense;
                pickImage();
                break;
            case R.id.driving_license_expire_date:
                Calendar cal = Calendar.getInstance(); //Get the Calendar instance
                openDatePicker(drivingLicenseExpireDate, cal);
                break;
            case R.id.nric_upload_image_front:
                currentImageView = nricUploadImageFront;
                pickImage();
                break;
            case R.id.car_grant_upload_image_front:
                currentImageView = carGrantUploadImageFront;
                pickImage();
                break;
            case R.id.car_grant_upload_image_back:
                currentImageView = carGrantUploadImageBack;
                pickImage();
                break;
            case R.id.nextIcon:
                if (validate()) {
                    register_API();
                }
                break;
            case R.id.insurance_cover_note_front:
                currentImageView = insuranceCoverNoteFront;
                pickImage();
                break;
            case R.id.insurance_cover_expire_at:
                Calendar cal1 = Calendar.getInstance();
                cal1.add(Calendar.MONTH, 3);
                openDatePicker(insuranceCoverExpireAt, cal1);
                break;
            case R.id.road_tax_image_front:
                currentImageView = roadTaxImageFront;
                pickImage();
                break;
            case R.id.driver_photo:
                currentImageView = driverPhoto;
                pickImage();
                break;
            case R.id.road_tax_expire_at:
                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.MONTH, 3);
                openDatePicker(roadTaxExpireAt, cal2);
                break;
            case R.id.backArrow:
                onBackPressed();
                break;
        }
    }

    private boolean validate() {

        if (AppHelper.getFileDataFromDrawable(drivingLicense.getDrawable()).length == 0) {
            Toast.makeText(context, getString(R.string.invalid_driving_license), Toast.LENGTH_SHORT).show();
            return false;
        } else if (AppHelper.getFileDataFromDrawable(nricUploadImageFront.getDrawable()).length == 0) {
            Toast.makeText(context, getString(R.string.invalid_nric_image), Toast.LENGTH_SHORT).show();
            return false;
        } else if (AppHelper.getFileDataFromDrawable(carGrantUploadImageFront.getDrawable()).length == 0) {
            Toast.makeText(context, getString(R.string.invalid_car_grant_image), Toast.LENGTH_SHORT).show();
            return false;
        } else if (AppHelper.getFileDataFromDrawable(carGrantUploadImageBack.getDrawable()).length == 0) {
            Toast.makeText(context, getString(R.string.invalid_car_grant_image), Toast.LENGTH_SHORT).show();
            return false;
        } else if (AppHelper.getFileDataFromDrawable(insuranceCoverNoteFront.getDrawable()).length == 0) {
            Toast.makeText(context, getString(R.string.invalid_insurance_cover_image), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (AppHelper.getFileDataFromDrawable(roadTaxImageFront.getDrawable()).length == 0) {
            Toast.makeText(context, getString(R.string.invalid_road_taxt_photo_image), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (AppHelper.getFileDataFromDrawable(driverPhoto.getDrawable()).length == 0) {
            Toast.makeText(context, getString(R.string.invalid_driver_photo_image), Toast.LENGTH_SHORT).show();
            return false;
        } else if (drivingLicenseExpireDate.getText().toString().isEmpty()) {
            Toast.makeText(context, getString(R.string.invalid_driving_license_expire_at), Toast.LENGTH_SHORT).show();
            return false;
        } else if (insuranceCoverExpireAt.getText().toString().isEmpty()) {
            Toast.makeText(context, getString(R.string.invalid_insurance_cover_expire_at), Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (roadTaxExpireAt.getText().toString().isEmpty()) {
            Toast.makeText(context, getString(R.string.invalid_road_tax_cover_expire_at), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkIMEIPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (!isPermissionGivenAlready) {
                        goToImageIntent();
                    }
                }
            }
        }
        if (requestCode == 1110) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    getIMEI(this);
                }
            }
        }
    }

    void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkStoragePermission() || checkCameraPermission()) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                goToImageIntent();
            }
        } else {
            goToImageIntent();
        }
    }

    public void goToImageIntent() {


        isPermissionGivenAlready = true;
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);*/

        EasyImage.openChooserWithGallery(DocumentUploadActivity.this, "Select Picture", 0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Bitmap bitmap = null;

            try {
                isImageChanged = true;
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, uri));
                    currentImageView.setImageBitmap(reRotateImg);
                    //profile_Image.setImageBitmap(reRotateImg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                if (imageFiles.size() > 0) {
                    isImageChanged = true;
                    Glide.with(DocumentUploadActivity.this).load(Uri.fromFile(imageFiles.get(0))).into(currentImageView);
                }
            }
        });
    }


    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public void openDatePicker(final TextView textView, Calendar cal) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String mSelectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                textView.setText(mSelectedDate);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.show();
    }

    public void getIMEI(Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String IMEI = telephonyManager.getDeviceId();
        phoneImei.setText(IMEI);
    }

    void waitingDialog(String message) {
        new AlertDialog.Builder(this)
//                .setMessage(message)
                .setMessage("Registration successful. Kindly please login with your credentials. Thank you")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GoToBeginActivity();
                        dialog.dismiss();
                    }
                }).show();
    }
}
