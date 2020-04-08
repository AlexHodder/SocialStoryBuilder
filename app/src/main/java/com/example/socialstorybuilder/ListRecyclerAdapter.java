package com.example.socialstorybuilder;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder> {
    private ArrayList<IdData> mData;
    private ItemClickListener mClickListener;
    private int selectedPosition = -1;
    private int bgColor;

    public ListRecyclerAdapter(){
        this.mData = new ArrayList<>();
        this.bgColor = Color.WHITE;
    }

    public ListRecyclerAdapter(@NonNull ArrayList<IdData> map) {
        this.mData = new ArrayList<>(map);
        this.bgColor = Color.WHITE;
    }
    public ListRecyclerAdapter(@NonNull ArrayList<IdData> map, int bgColor) {
        this.mData = new ArrayList<>(map);
        this.bgColor = bgColor;
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
        String value = getItem(position).getData();
        holder.myTextView.setText(value);
    }

    public IdData getItem(int position) {
        return mData.get(position);
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
            if (setColor) background.setBackgroundColor(bgColor);
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

    public void itemAdded(int position, IdData data){
        this.mData.add(data);
        this.notifyItemInserted(position);
    }

    public void itemRemoved(int index){
        this.mData.remove(index);
        this.notifyItemRemoved(index);
    }

    public boolean itemSelected(){
        return (selectedPosition != RecyclerView.NO_POSITION);
    }

    public void deselect(){
        selectedPosition = RecyclerView.NO_POSITION;
    }

    public void refresh(@NonNull ArrayList<IdData> data)
    {
        this.mData = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public void sortDataOnInt(){

    }
    public void sortDataOnStringValue(){

    }

}
