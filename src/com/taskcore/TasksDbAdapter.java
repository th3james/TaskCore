/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.taskcore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple tasks database access helper class. Defines the basic CRUD operations
 * for tasks, and gives the ability to list all tasks as well as
 * retrieve or modify a specific tasks.
 * 
 */
public class TasksDbAdapter {

    public static final String KEY_NAME = "name";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "TasksDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table tasks (_id integer primary key autoincrement, "
        + "name text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "tasks";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS tasks");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public TasksDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the tasks database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TasksDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new task using the title and body provided. If the task is
     * successfully created return the new rowId for that task, otherwise return
     * a -1 to indicate failure.
     * 
     * @param name the name of the task
     * @return rowId or -1 if failed
     */
    public long createTask(String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the task with the given rowId
     * 
     * @param rowId id of task to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTask(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all tasks in the database
     * 
     * @return Cursor over all tasks
     */
    public Cursor fetchAllTasks() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the task that matches the given rowId
     * 
     * @param rowId id of task to retrieve
     * @return Cursor positioned to matching task, if found
     * @throws SQLException if task could not be found/retrieved
     */
    public Cursor fetchTasks(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the task using the details provided. The task to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of task to update
     * @param title value to set task title to
     * @return true if the task was successfully updated, false otherwise
     */
    public boolean updateTask(long rowId, String name) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
