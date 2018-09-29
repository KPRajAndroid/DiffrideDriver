package com.diff.provider.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.diff.provider.Bean.ServiceType;
import com.diff.provider.DiffApplication;
import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Helper.URLHelper;
import com.diff.provider.Models.AccessDetails;
import com.diff.provider.R;
import com.diff.provider.Utilities.MyEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.diff.provider.DiffApplication.trimMessage;

public class ServiceRequirementActivity extends AppCompatActivity {

    @BindView(R.id.backArrow)
    ImageView backArrow;
    @BindView(R.id.service_model)
    MyEditText serviceModel;
    @BindView(R.id.service_type)
    Spinner serviceType;
    @BindView(R.id.service_color)
    MyEditText serviceColor;
    @BindView(R.id.service_year)
    MyEditText serviceYear;
    @BindView(R.id.service_seater)
    MyEditText serviceSeater;
    @BindView(R.id.service_number)
    MyEditText serviceNumber;
    @BindView(R.id.nextIcon)
    FloatingActionButton nextIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_requirement);
        ButterKnife.bind(this);
        setData();

    }

    @OnClick({R.id.backArrow, R.id.nextIcon})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backArrow:
                onBackPressed();
                break;
            case R.id.nextIcon:

                if (validate()) {

                    ServiceType serviceType = (ServiceType) ( (Spinner) findViewById(R.id.service_type) ).getSelectedItem();
                    if(serviceType == null){
                        Toast.makeText(this, R.string.invlid_service_type, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    RegisterActivity.REGISTER_REQUEST_OBJECT.put("service_type", serviceType.getId());
                    RegisterActivity.REGISTER_REQUEST_OBJECT.put("service_number", serviceNumber.getText().toString());
                    RegisterActivity.REGISTER_REQUEST_OBJECT.put("service_model", serviceModel.getText().toString());
                    RegisterActivity.REGISTER_REQUEST_OBJECT.put("service_color", serviceColor.getText().toString());
                    RegisterActivity.REGISTER_REQUEST_OBJECT.put("service_year", serviceYear.getText().toString());
                    RegisterActivity.REGISTER_REQUEST_OBJECT.put("service_seater", serviceSeater.getText().toString());

                    startActivity(new Intent(ServiceRequirementActivity.this, DocumentUploadActivity.class));

                }

                break;
        }
    }

    private boolean validate() {

        if (serviceModel.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.invalid_car_model, Toast.LENGTH_SHORT).show();
            return false;
        } else if (serviceColor.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.invalid_car_color, Toast.LENGTH_SHORT).show();
            return false;
        } else if (serviceYear.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.invalid_car_manufaturing_year, Toast.LENGTH_SHORT).show();
            return false;
        } else if (serviceSeater.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.invalid_car_seater, Toast.LENGTH_SHORT).show();
            return false;
        } else if (serviceNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.invalid_car_registration_number, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void setData() {

        JSONObject objcet = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, AccessDetails.serviceurl + URLHelper.SERVICE_TYPE, objcet, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray = response.optJSONArray("service_type");
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ServiceType>>() {
                }.getType();
                List<ServiceType> posts = gson.fromJson(jsonArray.toString(), listType);
                ArrayAdapter<ServiceType> cityAdapter = new ArrayAdapter<ServiceType>(ServiceRequirementActivity.this, R.layout.spinner, posts);
                serviceType.setAdapter(cityAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;


                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Toast.makeText(ServiceRequirementActivity.this, R.string.please_try_again, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ServiceRequirementActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(ServiceRequirementActivity.this, R.string.oops_connect_your_internet, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(ServiceRequirementActivity.this, R.string.oops_connect_your_internet, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof TimeoutError) {
                        setData();
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
}
