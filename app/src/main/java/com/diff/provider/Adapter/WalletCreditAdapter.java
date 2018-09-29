package com.diff.provider.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diff.provider.Models.HistoryItem;
import com.diff.provider.R;

import java.util.List;

/**
 * Created by suthakar@appoets.com on 28-06-2018.
 */
public class WalletCreditAdapter extends RecyclerView.Adapter<WalletCreditAdapter.MyViewHolder> {

    private List<HistoryItem> list;
    private Context context;

    private ClickListener clickListener;

    public WalletCreditAdapter(List<HistoryItem> list, Context con) {
        this.list = list;
        this.context = con;
    }

    public void setList(List<HistoryItem> list) {
        this.list = list;
    }

    public void setClickListener(WalletCreditAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void redirectClick(HistoryItem HistoryItem);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_history_inflate, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HistoryItem HistoryItem = list.get(position);

        holder.lblName.setText("RM "+HistoryItem.getAmount()+" "+HistoryItem.getDescription());
        holder.lblDesc.setText(HistoryItem.getCreatedAt());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView lblName;
        private TextView lblDesc;

        private MyViewHolder(View view) {
            super(view);

            lblName = view.findViewById(R.id.lblName);
            lblDesc = view.findViewById(R.id.lblDesc);

        }
    }
}