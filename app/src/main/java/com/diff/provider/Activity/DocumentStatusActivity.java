package com.diff.provider.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.diff.provider.Helper.CustomDialog;
import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Helper.URLHelper;
import com.diff.provider.Models.AccessDetails;
import com.diff.provider.Models.DocumentList;
import com.diff.provider.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentStatusActivity extends AppCompatActivity {
    CustomDialog customDialog;
    Context context;
    RecyclerView documentRv;
    DocumentListAdapter adapter;
    List<DocumentList> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_status);
        context = this;

        ImageView back = (ImageView) findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        documentRv = findViewById(R.id.document_rv);
        adapter = new DocumentListAdapter(this, list);
        documentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        documentRv.setItemAnimator(new DefaultItemAnimator());
        documentRv.setAdapter(adapter);


        load();
    }


    private void load() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, AccessDetails.serviceurl + URLHelper.DOCUMNETS_LIST, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();

                Log.d("Response", response.toString());
                JSONArray jsonArray = response.optJSONArray("documents");
                Gson gson = new Gson();
                list.clear();
                List<DocumentList> logs = gson.fromJson(jsonArray.toString(), new TypeToken<List<DocumentList>>() {
                }.getType());
                list.addAll(logs);
                adapter.notifyDataSetChanged();

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
                        Log.e("Error", errorObj.toString());
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(context, R.string.oops_connect_your_internet, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(context, R.string.oops_connect_your_internet, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(context, R.string.oops_connect_your_internet, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        DiffApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    private class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.MyViewHolder> {

        private List<DocumentList> list;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView name, status;

            MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                status = (TextView) view.findViewById(R.id.status);
                //itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
            }
        }


        private DocumentListAdapter(Context context, List<DocumentList> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_document, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            DocumentList datum = list.get(position);
            if (datum.getDocument() != null) {
                holder.name.setText(datum.getDocument().getName());
            }

            String status = datum.getStatus().equalsIgnoreCase("ACTIVE") ? "Verified" : "Pending";
            holder.status.setText(status);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
