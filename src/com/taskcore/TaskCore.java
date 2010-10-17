/*
 * Copyright (C) 2010 James Cox 
 */

package com.taskcore;


import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class TaskCore extends ListActivity {
	public static final int INSERT_ID = Menu.FIRST;
	
    private int mTaskNumber = 1;
    private TasksDbAdapter mDbHelper;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_list);
        mDbHelper = new TasksDbAdapter(this);
        mDbHelper.open();
        fillData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
    	return result;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case INSERT_ID:
            createTask();
            return true;
        }
       
        return super.onOptionsItemSelected(item);
    }
    
    private void createTask() {
        String noteName = "Task " + mTaskNumber++;
        mDbHelper.createTask(noteName);
        fillData();
    }
    
    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.fetchAllTasks();
        startManagingCursor(c);

        String[] from = new String[] { TasksDbAdapter.KEY_NAME };
        int[] to = new int[] { R.id.text1 };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter tasks =
            new SimpleCursorAdapter(this, R.layout.tasks_row, c, from, to);
        setListAdapter(tasks);
    }
}