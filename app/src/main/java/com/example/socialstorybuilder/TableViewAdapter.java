package com.example.socialstorybuilder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TableViewAdapter extends RecyclerView.Adapter {

    private ArrayList<ArrayList<String>> data;
    ArrayList<String> headings;

    public TableViewAdapter(ArrayList<ArrayList <String>> data){
        this.data = data;
    }


    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_view, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TableViewHolder tableHolder = (TableViewHolder) holder;
        if (position == 0){
            tableHolder.col1.setBackgroundResource(R.drawable.table_header_cell);
            tableHolder.col2.setBackgroundResource(R.drawable.table_header_cell);
            tableHolder.col3.setBackgroundResource(R.drawable.table_header_cell);
            tableHolder.col4.setBackgroundResource(R.drawable.table_header_cell);
            tableHolder.col5.setBackgroundResource(R.drawable.table_header_cell);
            tableHolder.col6.setBackgroundResource(R.drawable.table_header_cell);
            tableHolder.col7.setBackgroundResource(R.drawable.table_header_cell);
            tableHolder.col8.setBackgroundResource(R.drawable.table_header_cell);

            tableHolder.col1.setTypeface(null, Typeface.BOLD);
            tableHolder.col2.setTypeface(null, Typeface.BOLD);
            tableHolder.col3.setTypeface(null, Typeface.BOLD);
            tableHolder.col4.setTypeface(null, Typeface.BOLD);
            tableHolder.col5.setTypeface(null, Typeface.BOLD);
            tableHolder.col6.setTypeface(null, Typeface.BOLD);
            tableHolder.col7.setTypeface(null, Typeface.BOLD);
            tableHolder.col8.setTypeface(null, Typeface.BOLD);
        }
        else{
            tableHolder.col1.setBackgroundResource(R.drawable.table_cell);
            tableHolder.col2.setBackgroundResource(R.drawable.table_cell);
            tableHolder.col3.setBackgroundResource(R.drawable.table_cell);
            tableHolder.col4.setBackgroundResource(R.drawable.table_cell);
            tableHolder.col5.setBackgroundResource(R.drawable.table_cell);
            tableHolder.col6.setBackgroundResource(R.drawable.table_cell);
            tableHolder.col7.setBackgroundResource(R.drawable.table_cell);
            tableHolder.col8.setBackgroundResource(R.drawable.table_cell);
        }

        tableHolder.col1.setText(data.get(position).get(0));
        tableHolder.col2.setText(data.get(position).get(1));
        tableHolder.col3.setText(data.get(position).get(2));
        tableHolder.col4.setText(data.get(position).get(3));
        tableHolder.col5.setText(data.get(position).get(4));
        tableHolder.col6.setText(data.get(position).get(5));
        tableHolder.col7.setText(data.get(position).get(6));
        tableHolder.col8.setText(data.get(position).get(7));




    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder {
        TextView col1;
        TextView col2;
        TextView col3;
        TextView col4;
        TextView col5;
        TextView col6;
        TextView col7;
        TextView col8;
        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            col1 = itemView.findViewById(R.id.storyNameHeading);
            col2 = itemView.findViewById(R.id.nameHeading);
            col3 = itemView.findViewById(R.id.totalReadHeading);
            col4 = itemView.findViewById(R.id.happyHeading);
            col5 = itemView.findViewById(R.id.sadHeading);
            col6 = itemView.findViewById(R.id.angryHeading);
            col7 = itemView.findViewById(R.id.confusedHeading);
            col8 = itemView.findViewById(R.id.noFeedbackHeading);
        }
    }
}
