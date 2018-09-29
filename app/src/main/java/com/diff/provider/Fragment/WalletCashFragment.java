package com.diff.provider.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.diff.provider.Adapter.WalletHistoryAdapter;
import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Models.HistoryItem;
import com.diff.provider.Models.WalletHistoryResponse;
import com.diff.provider.R;
import com.diff.provider.Retrofit.ApiInterface;
import com.diff.provider.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletCashFragment extends Fragment {

    View rootView;
    @BindView(R.id.rvHistory)
    RecyclerView rvHistory;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    Unbinder unbinder;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    List<HistoryItem> list = new ArrayList<>();

    public WalletCashFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_wallet_cash, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        init();

        return rootView;
    }

    private void init() {

        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        rvHistory.setHasFixedSize(true);

        progressBar.setVisibility(View.VISIBLE);

        getWalletHistory();
    }


    private void getWalletHistory() {

        ApiInterface mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<WalletHistoryResponse> call = mApiInterface.walletHistory("XMLHttpRequest", "Bearer " + SharedHelper.getKey(getActivity(), "access_token"));
        call.enqueue(new Callback<WalletHistoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<WalletHistoryResponse> call, @NonNull Response<WalletHistoryResponse> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                if (response.isSuccessful()) {

                    progressBar.setVisibility(View.GONE);

                    list.clear();
                    list.addAll(response.body().getHistory());

                    loadAdapter();
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<WalletHistoryResponse> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadAdapter() {
        if (list.size() > 0) {
            WalletHistoryAdapter adapter = new WalletHistoryAdapter(list, getActivity());
            rvHistory.setAdapter(adapter);
            rvHistory.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        } else {
            rvHistory.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }

    }


}
