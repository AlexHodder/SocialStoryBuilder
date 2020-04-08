package com.example.socialstorybuilder;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapRecyclerAdapter extends RecyclerView.Adapter<MapRecyclerAdapter.ViewHolder> {
    private final ArrayList mData;
    private ItemClickListener mClickListener;
    private int selectedPosition = -1;

    public MapRecyclerAdapter(){
        this.mData = new ArrayList();
    }

    public MapRecyclerAdapter(@NonNull Map<String, String> map) {
        this.mData = new ArrayList();
        this.mData.addAll(map.entrySet());
    }
    
    public MapRecyclerAdapter(@NonNull ArrayList<Map.Entry<String, String>> map){
        this.mData = map;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list, parent, false);
        return new ViewHolder(view);
}

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (selectedPosition == position){
            holder.changeToSelect(true);
        }
        else holder.changeToSelect(false);
        String value = getValue(position);
        holder.myTextView.setText(value);
    }

    private Map.Entry<Object, Object> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    public String getKey(int position) {
        return getItem(position).getKey().toString();
    }
    public String getValue(int position) {
        return getItem(position).getValue().toString();
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout background;
        TextView myTextView;
        ViewHolder(View itemView){
            super(itemView);
            myTextView = itemView.findViewById(R.id.text1);
            background = itemView.findViewById(R.id.holder_background);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null){
//                int position = getAdapterPosition();
//                if (position != RecyclerView.NO_POSITION){
//                    mClickListener.onItemClick(view, getAdapterPosition());
//                    notifyItemChanged(selectedPosition);
//                    selectedPosition = getAdapterPosition();
//                    notifyItemChanged(selectedPosition);
//                }
                if (selectedPosition == getAdapterPosition()) {
                    selectedPosition = RecyclerView.NO_POSITION;
                    notifyDataSetChanged();
                    mClickListener.onItemClick(view, selectedPosition);
                    return;
                }
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();
                mClickListener.onItemClick(view, selectedPosition);
            }
        }
        public void changeToSelect(boolean setColor) {
            if (setColor) background.setBackgroundColor(Color.WHITE);
            else background.setBackgroundResource(0);
        }
    }

    public boolean isItemChecked(int position) {
        return (position == selectedPosition);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void refresh(@NonNull Map<String, String> map)
    {
        this.mData.clear();
        this.mData.addAll(map.entrySet());
        notifyDataSetChanged();
    }

    public void itemRemoved(int position){
        this.mData.remove(position);
        this.notifyItemRemoved(position);
    }
    public void itemAdded(String key, String value){
        AbstractMap.SimpleEntry<String, String> mapItem = new AbstractMap.SimpleEntry<>(key, value);
        this.mData.add(mapItem);
        int position = mData.indexOf(mapItem);
        this.notifyItemInserted(position);
    }

    public boolean itemSelected(){
        return (selectedPosition != RecyclerView.NO_POSITION);
    }

    public void deselect(){
        selectedPosition = RecyclerView.NO_POSITION;
    }

}
