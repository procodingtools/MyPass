package org.procodingtools.mypass.adapters.recycler_views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.procodingtools.mypass.R;
import org.procodingtools.mypass.interfaces.callbacks.OnItemClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WifiRecyclerAdapter extends RecyclerView.Adapter<WifiRecyclerAdapter.ViewHolder> {

    private List<Map<String,String>> list;
    private OnItemClickListener callback;

    public WifiRecyclerAdapter(List list){
        this.list = list;
    }

    public void addOnItemClickListener(OnItemClickListener callback){
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_wifi, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(list.get(i).get("ssid"));
        viewHolder.map.putAll(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Map<String,String> map;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            map = new HashMap<>();
            textView = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemClick(map.get("ssid"), map.get("key"));
                }
            });
        }
    }
}
