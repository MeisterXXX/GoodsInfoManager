package com.zsj.ilovemyfamily.homework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private List<Thing> list;
    private BaseAdapter adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView)findViewById(R.id.listView);

        updateList();
        lv.setAdapter(adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final View v = getLayoutInflater().inflate(R.layout.template, null);
                TextView textView1 = (TextView) v.findViewById(R.id.textView1);
                textView1.setText(list.get(position)._id);

                TextView textView2 = (TextView) v.findViewById(R.id.textView2);
                textView2.setText(list.get(position).name);

                TextView textView3 = (TextView)v.findViewById(R.id.textView3);
                textView3.setText(list.get(position).content);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        lv.setTag(id);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("你真的确定要删除么？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String delete = "delete from things where _id=?";
                                MyDBHelper myDBHelper = new MyDBHelper(MainActivity.this);
                                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                                //Thing thing = (Thing) lv.getTag();
                                db.execSQL(delete, new String[]{list.get(position)._id});
                                myDBHelper.close();
                                updateList();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
                return v;
            }
        });

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDBHelper myDBHelper1 = new MyDBHelper(MainActivity.this);
                SQLiteDatabase db1 = myDBHelper1.getReadableDatabase();

                EditText editText = (EditText) findViewById(R.id.editText);
                String name = editText.getText().toString();

                EditText editText2 = (EditText) findViewById(R.id.editText2);
                String content = editText2.getText().toString();
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (name.equals("") || content.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("请输入完整的信息");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                } else {
                    String select = "select content from things where name=?";
                    Cursor cursor = db1.rawQuery(select, new String[]{name});

                    int i = 0;
                    while (cursor.moveToNext()) {
                        i++;
                        content = String.valueOf(Integer.parseInt(cursor.getString(0)) + Integer.parseInt(content));
                        String update = "update things set content=? where name=?";
                        db1.execSQL(update, new String[]{content, name});
                        Toast toast = Toast.makeText(MainActivity.this, "库存总数量为" + content, Toast.LENGTH_LONG);
                        toast.show();
                    }
                    if (i == 0) {
                        String insert = "insert into things (name,content) values (?,?)";
                        db1.execSQL(insert, new String[]{name, content});

                    }
                    myDBHelper1.close();
                    updateList();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        Button button1 = (Button)findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.editText);
                editText.setText("");

                EditText editText2 = (EditText)findViewById(R.id.editText2);
                editText2.setText("");
            }
        });


    }

    private void updateList() {
        final MyDBHelper myDBHelper = new MyDBHelper(MainActivity.this);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        String select = "select _id,name,content from things";
        Cursor cursor = db.rawQuery(select, null);
        list = new ArrayList<Thing>();
        while (cursor.moveToNext()){
            Thing thing = new Thing();
            thing._id=cursor.getString(0);
            thing.name=cursor.getString(1);
            thing.content=cursor.getString(2);
            list.add(thing);
        }
        myDBHelper.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        adapter.notifyDataSetChanged();
    }
}
