package com.example.socialstorybuilder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import java.util.Map;


public class DragMapRecycleAdapter extends MapRecyclerAdapter{

    public DragMapRecycleAdapter(){
        super();
    }
    public DragMapRecycleAdapter(@NonNull Map<String, String> map){
        super(map);
    }

}
