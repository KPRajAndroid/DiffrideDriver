package com.diff.provider.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Helper.URLHelper;
import com.diff.provider.Models.BanksItem;
import com.diff.provider.Models.GetBankResponse;
import com.diff.provider.Models.Response;
import com.diff.provider.R;
import com.diff.provider.Retrofit.ApiInterface;
import com.diff.provider.Retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class WithdrawalActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.tv_cash_balance)
    TextView tvCashBalance;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_enter_amount)
    TextView tvEnterAmount;
    @BindView(R.id.txtWidthAmount)
    EditText txtWidthAmount;
    @BindView(R.id.tv_minimum)
    TextView tvMinimum;
    @BindView(R.id.view3)
    View view3;
    @BindView(R.id.tv_wallet_number)
    TextView tvWalletNumber;
    @BindView(R.id.tv_wallet_enter_amount)
    TextView tvWalletEnterAmount;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.txtName)
    EditText txtName;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.txtAccNumber)
    EditText txtAccNumber;
    @BindView(R.id.tv_bank)
    TextView tvBank;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.checkBox)
    CheckBox checkBox;
    @BindView(R.id.view4)
    View view4;
    @BindView(R.id.tv_wallet_summary)
    TextView tvWalletSummary;
    @BindView(R.id.tv_summary)
    TextView tvSummary;
    @BindView(R.id.tv_current_balances)
    TextView tvCurrentBalances;
    @BindView(R.id.lblCurrentbalance)
    TextView lblCurrentbalance;
    @BindView(R.id.tv_withdrawal)
    TextView tvWithdrawal;
    @BindView(R.id.lbl_withdrawal_amount)
    TextView lblWithdrawalAmount;
    @BindView(R.id.tv_withdrawal_changes)
    TextView tvWithdrawalChanges;
    @BindView(R.id.lbl_withdraw_charges)
    TextView lblWithdrawCharges;
    @BindView(R.id.lbl_new_balance)
    TextView lblNewBalance;
    @BindView(R.id.tv_new_balances)
    TextView tvNewBalances;
    @BindView(R.id.chkConfirm)
    CheckBox chkConfirm;
    @BindView(R.id.chkTerms)
    CheckBox chkTerms;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.lblTerms)
    TextView lblTerms;

    int wallet_balance = 0, withdrawal_charges = 0, withdrawal_amount = 0, new_balance = 0, withdrawal_total = 0;
    int bank_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lblTerms.setPaintFlags(lblTerms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        wallet_balance = Integer.parseInt(SharedHelper.getKey(WithdrawalActivity.this, "wallet_balance"));
        withdrawal_charges = Integer.parseInt(SharedHelper.getKey(WithdrawalActivity.this, "withdrawal_charges"));


        tvAmount.setText(SharedHelper.getKey(WithdrawalActivity.this, "wallet_balance"));
        lblCurrentbalance.setText(SharedHelper.getKey(WithdrawalActivity.this, "wallet_balance"));
        lblWithdrawCharges.setText(SharedHelper.getKey(WithdrawalActivity.this, "withdrawal_charges"));
        txtName.setText(SharedHelper.getKey(getApplicationContext(), "first_name") + " " + SharedHelper.getKey(getApplicationContext(), "last_name"));

        txtWidthAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() == 0) {
                    lblWithdrawalAmount.setText("0");
                    withdrawal_amount = 0;

                    withdrawal_total = withdrawal_amount + withdrawal_charges;
                    new_balance = wallet_balance - withdrawal_total;
                    tvNewBalances.setText(new_balance + "");

                } else {
                    lblWithdrawalAmount.setText(editable.toString());
                    withdrawal_amount = Integer.parseInt(editable.toString());
                    withdrawal_total = withdrawal_amount + withdrawal_charges;


                    new_balance = wallet_balance - withdrawal_total;
                    tvNewBalances.setText(new_balance + "");
                }

            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BanksItem banksItem = (BanksItem) parent.getItemAtPosition(position);
                bank_id = banksItem.getId();
                Log.d("onItemSelected", "onItemSelected: " + bank_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getDoctors();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //do whatever
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick({R.id.chkConfirm, R.id.chkTerms, R.id.btnSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chkConfirm:
                break;
            case R.id.chkTerms:
                break;
            case R.id.btnSubmit:


                if (txtWidthAmount.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter the withdrawal amount", Toast.LENGTH_SHORT).show();
                } else if (txtName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter the name", Toast.LENGTH_SHORT).show();
                } else if (txtAccNumber.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter the account number", Toast.LENGTH_SHORT).show();

                }
                if (!chkConfirm.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please Select to Confirm details", Toast.LENGTH_SHORT).show();
                }
                if (!chkTerms.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show();
                } else {

                    if (Integer.parseInt(txtWidthAmount.getText().toString()) <= wallet_balance) {

                        HashMap<String, Object> map = new HashMap();

                        map.put("amount", txtWidthAmount.getText().toString());
                        map.put("bank_account_name", txtName.getText().toString());
                        map.put("bank_account_number", txtAccNumber.getText().toString());

                        map.put("bank_id", bank_id);
                        map.put("withdrawal_charges", withdrawal_charges);
                        map.put("withdraw_total", withdrawal_total);
                        map.put("net_balance", new_balance);

                        withdrawal(map);
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry Withdrawal amount exceed Wallet Cash Balance, please enter the amount within the Wallet Cash amount", Toast.LENGTH_LONG).show();
                    }
                }


                break;
        }

    }


    private void getDoctors() {
        ApiInterface mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<GetBankResponse> call = mApiInterface.getBank("XMLHttpRequest", "Bearer " + SharedHelper.getKey(getApplicationContext(), "access_token"));
        call.enqueue(new Callback<GetBankResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetBankResponse> call, @NonNull retrofit2.Response<GetBankResponse> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                if (response.isSuccessful()) {

                    ArrayAdapter<BanksItem> userAdapter = new ArrayAdapter<BanksItem>(WithdrawalActivity.this, R.layout.spinner, response.body().getBanks());
                    spinner.setAdapter(userAdapter);
                }
            }

            @Override
            public void onFailure(Call<GetBankResponse> call, Throwable t) {
            }
        });
    }

    private void withdrawal(HashMap<String, Object> map) {
        ApiInterface mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<Response> call = mApiInterface.withdrawWallet("XMLHttpRequest", "Bearer " + SharedHelper.getKey(getApplicationContext(), "access_token"), map);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                if (response.isSuccessful()) {

                    String message = response.body().getMessage();
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    onBackPressed();


                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
            }
        });
    }

    @OnClick(R.id.lblTerms)
    public void onClicked() {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(URLHelper.TERMS));
        startActivity(viewIntent);
    }

}
