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

/**
 * Custom Adapter To Display Lists
 */
public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder> {
    private ArrayList<IdData> mData;
    private ItemClickListener mClickListener;
    private int selectedPosition = -1;
    private int bgColor;

    /**
     * Constructor initialising an empty list.
     */
    public ListRecyclerAdapter(){
        this.mData = new ArrayList<>();
        this.bgColor = Color.WHITE;
    }

    /**
     * Constructor initialising a list.
     * @param map assigned to the field mData
     */
    public ListRecyclerAdapter(@NonNull ArrayList<IdData> map) {
        this.mData = new ArrayList<>(map);
        this.bgColor = Color.WHITE;
    }

    /**
     * Constructor initialising a list and setting a custom background colour.
     * @param map assigned to the field mData
     * @param bgColor assigned to the field bgColor
     */
    public ListRecyclerAdapter(@NonNull ArrayList<IdData> map, int bgColor) {
        this.mData = new ArrayList<>(map);
        this.bgColor = bgColor;
    }

    /**
     * Method called when ViewHolder created.
     * @param parent group to find view
     * @param viewType
     * @return created ViewHolder from resource layout recycle_list
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list, parent, false);
        return new ViewHolder(view);
}

    /**
     * Method called on ViewHolder updates.
     * @param holder to update
     * @param position in ViewHolder to update
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (selectedPosition == position){
            holder.changeToSelect(true);
        }
        else holder.changeToSelect(false);
        String value = getItem(position).getData();
        holder.myTextView.setText(value);
    }

    /**
     *
     * @param position in ArrayList
     * @return item at position
     */
    public IdData getItem(int position) {
        return mData.get(position);
    }

    /**
     *
     * @return number of pairs in list
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Inner ViewHolder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout background;
        TextView myTextView;

        /**
         * Constructor
         * @param itemView
         */
        ViewHolder(View itemView){
            super(itemView);
            myTextView = itemView.findViewById(R.id.text1);
            background = itemView.findViewById(R.id.holder_background);
            itemView.setOnClickListener(this);
        }

        /**
         * OnClick method of list. Sets selected position to the position of the list.
         * @param view
         */
        @Override
        public void onClick(View view) {
            if (mClickListener != null){
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

        /**
         * Method to change the background colour of the currently selected item, to provide feedback to the user.
         * @param setColor
         */
        public void changeToSelect(boolean setColor) {
            if (setColor) background.setBackgroundColor(bgColor);
            else background.setBackgroundResource(0);
        }
    }

    /**
     * Method to check if a specific item is selected
     * @param position to be compared to currently selected position
     * @return true if position is the same as the currently selected
     */
    public boolean isItemChecked(int position) {
        return (position == selectedPosition);
    }


    /**
     * Method to allow click events to be caught
     * @param itemClickListener
     */
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    /**
     * This interface will be implemented by parent to handle click events
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * Method to add an item to the list
     * @param position in list to insert item
     * @param data to be inserted
     */
    public void itemAdded(int position, IdData data){
        this.mData.add(data);
        this.notifyItemInserted(position);
    }

    /**
     * Method to remove an item from the list
     * @param index to be removed
     */
    public void itemRemoved(int index){
        this.mData.remove(index);
        this.notifyItemRemoved(index);
    }

    /**
     * Method to return whether an item is selected
     * @return true if an item is selected, false if item no item selectd.
     */
    public boolean itemSelected(){
        return (selectedPosition != RecyclerView.NO_POSITION);
    }

    /**
     * Method to deselect an item, setting currently selected item to a default no position
     */
    public void deselect(){
        selectedPosition = RecyclerView.NO_POSITION;
    }

    /**
     * Method to refresh the data set with a fresh data set
     * @param data to refresh list
     */
    public void refresh(@NonNull ArrayList<IdData> data)
    {
        this.mData = new ArrayList<>(data);
        notifyDataSetChanged();
    }

}
