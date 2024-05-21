package com.example.btth4;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText edtMaLop, edtTenLop, edtSiSo;
    Button btnInsert, btnDelete, btnUpdate;
    ListView lv;
    ArrayList<String> myList;
    ArrayAdapter<String> myAdapter;
    SQLiteDatabase myDatabase;

    protected void getData() {
        //làm mới bảng hiển thị dữ liệu
        myList.clear();
        //truy vấn để lấy dữ liệu từ bảng
        Cursor c = myDatabase.query("tbllop", null, null, null, null, null, null);
        c.moveToNext();
        String data;
        while (!c.isAfterLast()) {
            data = c.getString(0) + " - " + c.getString(1) + " - " + c.getString(2);
            c.moveToNext();
            myList.add(data);
        }
        c.close();
        //gửi thông báo có sự thay đổi dữ liệu
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
            Log.e("Error", "Bảng đã tồn tại");
        }

        getData();

        //chọn item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy dữ liệu của item được chọn
                String selectedItem = myList.get(position);
                // Tách dữ liệu thành các thành phần malop, tenlop, siso
                String[] parts = selectedItem.split(" - ");
                // Đặt các giá trị vào EditText
                edtMaLop.setText(parts[0]);
                edtTenLop.setText(parts[1]);
                edtSiSo.setText(parts[2]);
            }
        });

        btnInsert.setOnClickListener(v -> {
            //khai báo các biến để lấy dữ liệu
            String malop = edtMaLop.getText().toString();
            String tenlop = edtTenLop.getText().toString();
            String sisoStr = edtSiSo.getText().toString();
            ContentValues myValue = new ContentValues();
            int siso;
            String msg;
            //kiểm tra các dữ liệu có trống hay không
            if (malop.isEmpty() || tenlop.isEmpty() || sisoStr.isEmpty()) {
                msg = "Vui lòng điền đầy đủ thông tin.";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            //validate trường sĩ số có đúng kiểu int hay không
            try {
                siso = Integer.parseInt(sisoStr);
            } catch (NumberFormatException e) {
                msg = "Sĩ số phải là số nguyên.";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            //kiểm tra dữ liệu có trùng hay không
            Cursor cursor = myDatabase.query("tbllop", new String[]{"malop"}, "malop = ?", new String[]{malop}, null, null, null);
            if (cursor.moveToFirst()) {
                msg = "Mã lớp đã tồn tại.";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }
            cursor.close();
            //thêm dữ liệu vào myValue
            myValue.put("malop", malop);
            myValue.put("tenlop", tenlop);
            myValue.put("siso", siso);
            //thêm dữ liệu vào database
            if(myDatabase.insert("tbllop", null, myValue) == -1) {
                msg = "Thêm dữ liệu thất bại.";
            } else {
                msg = "Thêm dữ liệu thành công!";
            }
            //Hiển thị thông báo
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            //đặt lại các giá trị cho EditText
            edtMaLop.setText("");
            edtTenLop.setText("");
            edtSiSo.setText("");
            //Cập nhật lại bảng
            getData();
        });

        btnDelete.setOnClickListener(v -> {
            //khai báo các biến để lấy dữ liệu
            String malop = edtMaLop.getText().toString();
            String msg;
            if (malop.isEmpty()) {
                msg = "Vui lòng điền mã lớp.";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            int n = myDatabase.delete("tbllop", "malop = ?", new String[]{malop});
            if(n == 0) {
                msg = "Không có mã lớp cần xoá.";
            } else {
                msg = n + " lớp đã được xoá.";
            }
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            //đặt lại giá trị cho EditText
            edtMaLop.setText("");
            edtTenLop.setText("");
            edtSiSo.setText("");
            //cập nhật lại dữ liệu
            getData();
        });
        btnUpdate.setOnClickListener(v -> {
            //khai báo các biến để lấy dữ liệu
            String malop = edtMaLop.getText().toString();
            String tenlop = edtTenLop.getText().toString();
            String sisoStr = edtSiSo.getText().toString();
            int siso;
            String msg;
            //kiểm tra các dữ liệu có trống hay không
            if (malop.isEmpty() || tenlop.isEmpty() || sisoStr.isEmpty()) {
                msg = "Vui lòng điền đầy đủ thông tin.";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            //validate trường sĩ số có đúng kiểu int hay không
            try {
                siso = Integer.parseInt(sisoStr);
            } catch (NumberFormatException e) {
                msg = "Sĩ số phải là số nguyên.";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            // Kiểm tra dữ liệu có bị trùng hay không
            Cursor cursor = myDatabase.query("tbllop", new String[]{"tenlop", "siso"}, "malop = ?", new String[]{malop}, null, null, null);
            if (cursor.moveToFirst()) {
                String existingTenlop = cursor.getString(cursor.getColumnIndexOrThrow("tenlop"));
                int existingSiso = cursor.getInt(cursor.getColumnIndexOrThrow("siso"));
                if (existingTenlop.equals(tenlop) && existingSiso == siso) {
                    msg = "Dữ liệu mới giống dữ liệu cũ, không thực hiện việc cập nhật.";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    cursor.close();
                    return;
                }
            }
            cursor.close();
            //thêm dữ liệu vào database
            ContentValues myValue = new ContentValues();
            myValue.put("malop", malop);
            myValue.put("tenlop", tenlop);
            myValue.put("siso", siso);
            int n = myDatabase.update("tbllop", myValue, "malop = ?", new String[]{malop});
            if(n == 0) {
                msg = "Không có dữ liệu nào được cập nhật.";
            } else {
                msg = n + " dữ liệu đã được cập nhật.";
            }
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            //đặt lại các giá trị cho EditText
            edtMaLop.setText("");
            edtTenLop.setText("");
            edtSiSo.setText("");
            //Cập nhật lại giá trị cho bảng
            getData();
        });
    }
}