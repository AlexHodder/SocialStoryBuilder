package com.example.socialstorybuilder;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DecoratedRecyclerView extends RecyclerView {

    public DecoratedRecyclerView(@NonNull Context context) {
        super(context);
        decorate(context);
    }

    public DecoratedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        decorate(context);
    }

    public DecoratedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        decorate(context);
    }

    private void decorate(Context context){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        this.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.getContext(), layoutManager.getOrientation());
        this.addItemDecoration(dividerItemDecoration);
    }

}
