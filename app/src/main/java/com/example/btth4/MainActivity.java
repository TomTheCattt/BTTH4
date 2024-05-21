package com.example.btth4;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText edtMaLop, edtTenLop, edtSiSo;
    Button btnInsert, btnDelete, btnUpdate;
    ListView lv;
    ArrayList<String> myList;
    ArrayAdapter<String> myAdapter;
    SQLiteDatabase myDatabase;

    protected void getData() {
        myList.clear();
        Cursor c = myDatabase.query("tbllop", null, null, null, null, null, null);
        c.moveToNext();
        String data = "";
        while (c.isAfterLast() == false) {
            data = c.getString(0) + " - " + c.getString(1) + " - " + c.getString(2);
            c.moveToNext();
            myList.add(data);
        }
        c.close();
        myAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        edtMaLop = findViewById(R.id.edtMaLop);
        edtTenLop = findViewById(R.id.edtTenLop);
        edtSiSo = findViewById(R.id.edtSiSo);
        btnInsert = findViewById(R.id.btnInsert);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        lv = findViewById(R.id.lv);
        myList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        lv.setAdapter(myAdapter);
        myDatabase = openOrCreateDatabase("qlSinhVien.db", MODE_PRIVATE, null);
        try {
            String sql = "Create table tbllop(malop TEXT primary key, tenlop TEXT, siso INTEGER)";
            myDatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("Error", "Table da ton tai");
        }

        getData();

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String malop = edtMaLop.getText().toString();
                String tenlop = edtTenLop.getText().toString();
                int siso = Integer.parseInt(edtSiSo.getText().toString());
                ContentValues myValue = new ContentValues();
                myValue.put("malop", malop);
                myValue.put("tenlop", tenlop);
                myValue.put("siso", siso);
                String msg = "";
                if(myDatabase.insert("tbllop", null, myValue) == -1) {
                    msg = "Failed to insert record!";
                } else {
                    msg = "Insert record successfully!";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                edtMaLop.setText("");
                edtTenLop.setText("");
                edtSiSo.setText("");
                getData();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String malop = edtMaLop.getText().toString();
                int n = myDatabase.delete("tbllop", "malop = ?", new String[]{malop});
                String msg = "";
                if(n == 0) {
                    msg = "No record to delete";
                } else {
                    msg = n + " record deleted";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                edtMaLop.setText("");
                edtTenLop.setText("");
                edtSiSo.setText("");
                getData();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String malop = edtMaLop.getText().toString();
                int siso = Integer.parseInt(edtSiSo.getText().toString());
                ContentValues myValue = new ContentValues();
                myValue.put("siso", siso);
                int n = myDatabase.update("tbllop", myValue, "malop = ?", new String[]{malop});
                String msg = "";
                if(n == 0) {
                    msg = "No record to update";
                } else {
                    msg = n + " record updated";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                edtMaLop.setText("");
                edtTenLop.setText("");
                edtSiSo.setText("");
                getData();
            }
        });
    }
}