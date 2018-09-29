package com.diff.provider.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Helper.URLHelper;
import com.diff.provider.Models.Response;
import com.diff.provider.R;
import com.diff.provider.Retrofit.ApiInterface;
import com.diff.provider.Retrofit.RetrofitClient;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

public class ReloadActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_cash)
    TextView tvCash;
    @BindView(R.id.img_card)
    ImageView imgCard;
    @BindView(R.id.lblCash)
    TextView lblCash;
    @BindView(R.id.tv_transfer)
    TextView tvTransfer;
    @BindView(R.id.tv_paid_credit)
    TextView tvPaidCredit;
    @BindView(R.id.img_paid)
    ImageView imgPaid;
    @BindView(R.id.lblPaidCredit)
    TextView lblPaidCredit;
    @BindView(R.id.tv_enter_amount)
    TextView tvEnterAmount;
    @BindView(R.id.guideline)
    Guideline guideline;
    @BindView(R.id.rd20)
    RadioButton rd20;
    @BindView(R.id.rd50)
    RadioButton rd50;
    @BindView(R.id.rd100)
    RadioButton rd100;
    @BindView(R.id.rd200)
    RadioButton rd200;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.tv_value)
    TextView tvValue;
    @BindView(R.id.txtReloadAmount)
    EditText txtReloadAmount;
    @BindView(R.id.guideline1)
    Guideline guideline1;
    @BindView(R.id.tv_summary)
    TextView tvSummary;
    @BindView(R.id.tv_current_balances)
    TextView tvCurrentBalances;
    @BindView(R.id.lblCurrentBalance)
    TextView lblCurrentBalance;
    @BindView(R.id.tv_withdrawal)
    TextView tvWithdrawal;
    @BindView(R.id.lblReloadValue)
    TextView lblReloadValue;
    @BindView(R.id.tv_withdrawal_changes)
    TextView tvWithdrawalChanges;
    @BindView(R.id.lblNewPaidCredit)
    TextView lblNewPaidCredit;
    @BindView(R.id.tv_balances)
    TextView tvBalances;
    @BindView(R.id.lblNewBalance)
    TextView lblNewBalance;
    @BindView(R.id.chkConfirm)
    CheckBox chkConfirm;
    @BindView(R.id.chkAgree)
    CheckBox chkAgree;
    @BindView(R.id.btnReload)
    Button btnReload;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.lblTerms)
    TextView lblTerms;

    int cash_balance = 0, credit_balance = 0, cash_reload = 0, new_cash_balance = 0, new_credit_balance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkEnable();


        txtReloadAmount.addTextChangedListener(new TextWatcher() {
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
                    radioGroup.setOnCheckedChangeListener(null);
                    radioGroup.clearCheck();
                    checkEnable();

                }


                if (editable.length() == 0) {
                    lblReloadValue.setText("0");
                    cash_reload = 0;

                    new_credit_balance  = credit_balance + cash_reload;
                    lblNewPaidCredit.setText(new_credit_balance+"");

                    new_cash_balance = cash_balance - cash_reload;
                    lblNewBalance.setText(new_cash_balance+"");

                } else {
                    lblReloadValue.setText(editable.toString());
                    cash_reload = Integer.parseInt(editable.toString());

                    new_credit_balance  = credit_balance + cash_reload;
                    lblNewPaidCredit.setText(new_credit_balance+"");

                    new_cash_balance = cash_balance - cash_reload;
                    lblNewBalance.setText(new_cash_balance+"");

                }


            }
        });


        lblCash.setText(SharedHelper.getKey(ReloadActivity.this, "wallet_balance"));
        lblPaidCredit.setText(SharedHelper.getKey(ReloadActivity.this, "credit_balance"));
        lblCurrentBalance.setText(SharedHelper.getKey(ReloadActivity.this, "wallet_balance"));

        cash_balance = Integer.valueOf(SharedHelper.getKey(ReloadActivity.this, "wallet_balance"));
        credit_balance = Integer.valueOf(SharedHelper.getKey(ReloadActivity.this, "credit_balance"));

        lblTerms.setPaintFlags(lblTerms.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);


    }

    @OnClick(R.id.btnReload)
    public void onViewClicked() {

        if (txtReloadAmount.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please Enter the Reload amount", Toast.LENGTH_SHORT).show();
        }
        else if (Integer.parseInt(txtReloadAmount.getText().toString()) < 20) {
            Toast.makeText(getApplicationContext(), "Enter Reload amount min 20", Toast.LENGTH_SHORT).show();
        }

        else if (new_cash_balance < 30) {
            Toast.makeText(getApplicationContext(), "Please maintaine Cash balance 30RM!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!chkConfirm.isChecked()) {
            Toast.makeText(getApplicationContext(), "Please Select to Confirm details", Toast.LENGTH_SHORT).show();
        }
        else if (!chkAgree.isChecked()) {
            Toast.makeText(getApplicationContext(), "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show();
        } else {


            if (Integer.parseInt(txtReloadAmount.getText().toString()) <= cash_balance) {
                HashMap<String, Object> map = new HashMap();
                map.put("amount", txtReloadAmount.getText().toString());
                reload(map);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Sorry Reload amount exceed Wallet Cash Balance, please enter the amount within the Wallet Cash amount", Toast.LENGTH_LONG).show();

            }

        }

    }

    @OnClick(R.id.lblTerms)
    public void onClicked(){
        Intent viewIntent =new Intent("android.intent.action.VIEW",Uri.parse(URLHelper.TERMS));
        startActivity(viewIntent);
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

    void checkEnable()
    {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = group.findViewById(checkedId);
                int index = group.indexOfChild(radioButton);

                switch (index) {
                    case 0:
                        txtReloadAmount.setText("20");
                        txtReloadAmount.setSelection(txtReloadAmount.getText().length());
                        break;
                    case 1:
                        txtReloadAmount.setText("50");
                        txtReloadAmount.setSelection(txtReloadAmount.getText().length());
                        break;
                    case 2:
                        txtReloadAmount.setText("100");
                        txtReloadAmount.setSelection(txtReloadAmount.getText().length());
                        break;
                    case 3:
                        txtReloadAmount.setText("200");
                        txtReloadAmount.setSelection(txtReloadAmount.getText().length());
                        break;


                }

            }
        });
    }


    private void reload(HashMap<String, Object> map) {
        ApiInterface mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<Response> call = mApiInterface.reloadWallet("XMLHttpRequest", "Bearer " + SharedHelper.getKey(getApplicationContext(), "access_token"), map);
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
}
