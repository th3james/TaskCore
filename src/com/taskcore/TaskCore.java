/*
 * Copyright (C) 2010 James Cox 
 */

package com.taskcore;


import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

public class TaskCore extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
	private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
	
    private TasksDbAdapter mDbHelper;
    private Cursor mTasksCursor;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_list);
        mDbHelper = new TasksDbAdapter(this);
        mDbHelper.open();
        fillData();
        
        registerForContextMenu(getListView());
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

    //Handle creation of context menu
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
        case DELETE_ID:
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            mDbHelper.deleteTask(info.id);
            fillData();
            return true;
        }
		return super.onContextItemSelected(item);
	}
    
    private void createTask() {
        String noteName = "Task ";
        mDbHelper.createTask(noteName);
        fillData();
    }
    
    private void fillData() {
        // Get all of the notes from the database and create the item list
        mTasksCursor = mDbHelper.fetchAllTasks();
        startManagingCursor(mTasksCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { TasksDbAdapter.KEY_NAME };
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.text1 };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter tasks =
            new SimpleCursorAdapter(this, R.layout.tasks_row, mTasksCursor, from, to);
        setListAdapter(tasks);
    }
}