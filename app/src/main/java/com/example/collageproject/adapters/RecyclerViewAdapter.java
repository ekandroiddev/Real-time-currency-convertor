package com.example.collageproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Picture;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageproject.MainActivity;
import com.example.collageproject.OnItemClickListener;
import com.example.collageproject.R;
import com.example.collageproject.models.CurrencyModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
    List<CurrencyModel> currencyModels;

    private final OnItemClickListener listener;

    public RecyclerViewAdapter(Context context, List<CurrencyModel> currencyModels, OnItemClickListener listener) {
        this.context = context;
        this.currencyModels = currencyModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        CurrencyModel currencyModel = currencyModels.get(position);
        holder.currencyCode.setText(currencyModel.getCurrencyCode());
        holder.currencyName.setText(currencyModel.getCurrencyName());
        if (currencyModel.getFlagUrl() == null || currencyModel.getFlagUrl().isEmpty()) {
            holder.flag.setBackgroundResource(R.drawable.deffaultflag);
        } else {
            Picasso.get().load(currencyModel.getFlagUrl()).into(holder.flag);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicks(currencyModel.getCurrencyCode(), currencyModel.getFlagUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencyModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView flag;
        TextView currencyCode, currencyName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flag = itemView.findViewById(R.id.flag);
            currencyCode = itemView.findViewById(R.id.currencyCode);
            currencyName = itemView.findViewById(R.id.currencyName);
        }
    }
}
