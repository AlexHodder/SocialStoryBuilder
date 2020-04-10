package com.example.socialstorybuilder.storyedit;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.IdData;
import com.example.socialstorybuilder.ListRecyclerAdapter;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;

import java.util.ArrayList;

public class ConfigurePages extends AppCompatActivity {

    private String storyID;

    private ArrayList<IdData> pageNumberList;
    private ListRecyclerAdapter pageHashAdapter;
    private IdData selectedPage;
    private int selectedPagePosition = RecyclerView.NO_POSITION;

    private DecoratedRecyclerView pageView;
    private AlertDialog.Builder cancelConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_pages);
        Intent intent = getIntent();
        storyID = intent.getStringExtra("story_id");

        TextView storyTitle = findViewById(R.id.textViewStoryTitle);
        String title = ActivityHelper.getTitleFromID(getApplicationContext(), storyID);
        storyTitle.setText(title);

        pageNumberList = ActivityHelper.getPageList(getApplicationContext(), storyID);
        pageView = findViewById(R.id.page_list);
        pageHashAdapter = new ListRecyclerAdapter(pageNumberList);
        pageView.setAdapter(pageHashAdapter);
        pageHashAdapter.setClickListener(new ListRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1) {
                    selectedPage = pageHashAdapter.getItem(position);
                }
                selectedPagePosition = position;
            }
        });
    }

    public void newPage(View view){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseNameHelper.PageEntry.COLUMN_TEXT, "");
        values.put(DatabaseNameHelper.PageEntry.COLUMN_STORY_ID, storyID);
        int pageNo = pageNumberList.size() + 1;
        values.put(DatabaseNameHelper.PageEntry.COLUMN_PAGE_NO, pageNumberList.size() + 1);
        long pageIDLong = db.insert(DatabaseNameHelper.PageEntry.TABLE_NAME,null,values);
        String pageID = String.valueOf(pageIDLong);
        Intent intent = new Intent(this, PageEditor.class);
        intent.putExtra("story_id", storyID);
        intent.putExtra("page_id", pageID);
        intent.putExtra("page_no", pageNo);
        startActivity(intent);
        int insertIndex = pageNumberList.size();
        IdData data = new IdData(pageID, String.valueOf(pageNo));
        pageNumberList.add(insertIndex, data);
        pageHashAdapter.itemAdded(insertIndex, data);
    }

    public void editPage(View view){
        if (pageHashAdapter.itemSelected()) {
            Intent intent = new Intent(this, PageEditor.class);
            intent.putExtra("story_id", storyID);
            intent.putExtra("page_id", selectedPage.getId());
            int pageNo = Integer.parseInt(selectedPage.getData());
            intent.putExtra("page_no", pageNo);
            startActivity(intent);
        }
    }

    public void removePage(View view){
        if (pageHashAdapter.itemSelected()){

            String positionS = selectedPage.getId();
            Integer position = Integer.valueOf(positionS);
            for (int i = 0; i < pageNumberList.size(); i++){
                IdData data = pageNumberList.get(i);
                Integer pageNumber = Integer.valueOf(data.getData());
                if (pageNumber > position){
                    data.setData(String.valueOf(pageNumber - 1));
                    pageNumberList.set(i, data);
                }
            }
            pageNumberList.remove(selectedPage);
            pageHashAdapter.itemRemoved(selectedPagePosition);
            pageHashAdapter.notifyItemRangeChanged(selectedPagePosition, pageNumberList.size() - selectedPagePosition);
            pageHashAdapter.deselect();
            selectedPagePosition = RecyclerView.NO_POSITION;
        }
    }

    public void confirm(View view){
        flushDynamicPageChanges();
        finish();
    }

    public void cancel(View view){
        cancelConfirmDialog = new AlertDialog.Builder(ConfigurePages.this);
        cancelConfirmDialog.setTitle(R.string.cancel);
        cancelConfirmDialog.setMessage(getString(R.string.cancel_confirm));
        cancelConfirmDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        cancelConfirmDialog.setNegativeButton(R.string.back_button, null);
        cancelConfirmDialog.show();
    }

    public void flushDynamicPageChanges(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (IdData data : pageNumberList){
            String id = data.getId();
            ContentValues values = new ContentValues();
            String[] args = {id};
            String pageNumberString = data.getData();
            assert pageNumberString != null;
            Integer pageNumber = Integer.valueOf(pageNumberString);
            values.put(DatabaseNameHelper.PageEntry.COLUMN_PAGE_NO, pageNumber);
            db.update(DatabaseNameHelper.PageEntry.TABLE_NAME, values, "_id = ?", args);
        }

        StringBuilder inQuery = new StringBuilder();

        inQuery.append("(");
        boolean first = true;
        for (IdData data : pageNumberList) {
            String item = data.getId();
            if (first) {
                first = false;
                inQuery.append("'").append(item).append("'");
            } else {
                inQuery.append(", '").append(item).append("'");
            }
        }
        inQuery.append(")");

        String selection = DatabaseNameHelper.PageEntry._ID + " NOT IN " + inQuery.toString() + " AND " + DatabaseNameHelper.PageEntry.COLUMN_STORY_ID + " = ?";
        String[] selectionArgs = {storyID};
        db.delete(DatabaseNameHelper.PageEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

}
