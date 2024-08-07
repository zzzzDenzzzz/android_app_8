package com.example.app_8;

import static com.example.app_8.DBHelper.LOG_TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_8.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private DBHelper dbHelper;
    private ArrayList<String> dataList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        binding.btnAdd.setOnClickListener(this);
        binding.btnRead.setOnClickListener(this);
        binding.btnClear.setOnClickListener(this);

        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        binding.listView.setAdapter(adapter);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                addEntry();
                break;
            case R.id.btnRead:
                readEntries();
                break;
            case R.id.btnClear:
                clearEntries();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void addEntry() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            String name = binding.etName.getText().toString();
            String email = binding.etEmail.getText().toString();

            contentValues.put("name", name);
            contentValues.put("email", email);

            long rowId = sqLiteDatabase.insert("mytable", null, contentValues);
            Log.d(LOG_TAG, "--- insert in mytable, rowId = " + rowId);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        dbHelper.close();
    }

    private void readEntries() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        try {
            Cursor cursor = sqLiteDatabase.query("mytable", null, null, null, null, null, null);
            dataList.clear();

            if (cursor.moveToFirst()) {
                int idColIndex = cursor.getColumnIndex("id");
                int nameColIndex = cursor.getColumnIndex("name");
                int emailColIndex = cursor.getColumnIndex("email");

                do {
                    String row = "ID = " + cursor.getInt(idColIndex)
                            + ", name = " + cursor.getString(nameColIndex)
                            + ", email = " + cursor.getString(emailColIndex);
                    dataList.add(row);
                } while (cursor.moveToNext());
            } else {
                Log.d(LOG_TAG, "0 rows");
            }
            cursor.close();
            adapter.notifyDataSetChanged();
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        dbHelper.close();
    }

    private void clearEntries() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        try {
            int clearCount = sqLiteDatabase.delete("mytable", null, null);
            Log.d(LOG_TAG, "deleted rows count = " + clearCount);

            dataList.clear();
            adapter.notifyDataSetChanged();
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        dbHelper.close();
    }
}