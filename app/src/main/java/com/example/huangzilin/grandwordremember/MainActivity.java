package com.example.huangzilin.grandwordremember;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ContentResolver cr;
    public static String AUTHORITY = "com.example.huangzilin.granddictionary.MyProvider";
    public static Uri mCurrentUri = Uri.parse("content://" + AUTHORITY + "/dict");

    private Context mContext;
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder dialogBuilder = null;

    private String word_color = "Red";
    private Integer test_word_num = 10;
    private Boolean saved = true;
    private SharedPreferences settings;

    //定义一个Handler用来更新页面：
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    add_word(MainActivity.this.getWindow().getDecorView());
                    break;
                case 0x002:
                    startActivity(new Intent(MainActivity.this,SettingActivity.class));
                    break;
                case 0x003:
                    Intent intent_ = new Intent();
                    intent_.setAction("com.example.startact.STAT");
                    startActivity(intent_);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("学霸背单词");
        actionBar.setSubtitle("-快速记忆法");
        cr = this.getContentResolver();
        mContext = this;
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        word_color = settings.getString("list_key", "Red");
        test_word_num = Integer.valueOf(settings.getString("edittext_key", "10"));
        saved = settings.getBoolean("checkbox_key", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support, menu);
        return true;
    }

    public void add_word(View source){
        TableLayout studentForm = (TableLayout) getLayoutInflater()
                .inflate(R.layout.add_word, null);
        dialogBuilder = new android.app.AlertDialog.Builder(mContext);
        alertDialog = dialogBuilder
                // 设置图标
                .setIcon(R.mipmap.dict)
                // 设置对话框标题
                .setTitle("增加单词")
                // 设置对话框显示的View对象
                .setView(studentForm)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText new_word = (EditText)alertDialog.findViewById(R.id.new_word);
                        EditText new_explanation = (EditText)alertDialog.findViewById(R.id.new_explanation);
                        EditText new_level = (EditText)alertDialog.findViewById(R.id.new_level);
                        CheckBox cover = (CheckBox)alertDialog.findViewById(R.id.cover);
                        String cur_word = new_word.getText().toString();
                        if(!cur_word.equals("")){
                            String cur_ex = new_explanation.getText().toString();
                            Integer cur_level = Integer.valueOf(new_level.getText().toString());
                            long time =  System.currentTimeMillis() / 1000;
                            ContentValues cv = new ContentValues();
                            cv.put("word", cur_word);
                            cv.put("explanation", cur_ex);
                            cv.put("level",cur_level);
                            cv.put("modified_time", time);
                            String[] cur_words = new String[]{cur_word};
                            Cursor cursor = cr.query(mCurrentUri, null, "word=?", cur_words, null);
                            if((cursor.getCount() == 0 || (cursor.getCount() != 0 && cover.isChecked())) ){
                                cr.insert(mCurrentUri, cv);
                                Toast.makeText(mContext, "成功添加", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(mContext, "单词已存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).create();
        alertDialog.show();
    }

    public void search_word(View source){
        TableLayout studentForm = (TableLayout) getLayoutInflater()
                .inflate(R.layout.search_word, null);
        dialogBuilder = new android.app.AlertDialog.Builder(mContext);
        alertDialog = dialogBuilder
                // 设置图标
                .setIcon(R.mipmap.dict)
                // 设置对话框标题
                .setTitle("查找单词")
                // 设置对话框显示的View对象add_word.xml
                .setView(studentForm)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText new_word = (EditText)alertDialog.findViewById(R.id.searched_word);
                        String cur_word = new_word.getText().toString();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.test:
                new Thread(){
                    @Override
                    public void run(){
                        try{
                            Intent intent = new Intent();
                            intent.putExtra("word_color", word_color);
                            intent.putExtra("test_word_num", test_word_num);
                            intent.putExtra("saved", saved);
                            intent.setAction("com.example.startact.TEST");
                            startActivity(intent);
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.menu1:
                Toast.makeText(this, "新增单词", Toast.LENGTH_SHORT).show();
                new Thread(){
                    @Override
                    public void run(){
                        try{
                            handler.sendEmptyMessage(0x001);
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.menu2:
                new Thread(){
                    @Override
                    public void run(){
                        try{
                            handler.sendEmptyMessage(0x003);
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.menu3:
                search_word(MainActivity.this.getWindow().getDecorView());
                break;
            case R.id.menu4:
                new Thread(){
                    @Override
                    public void run(){
                        try{
                            handler.sendEmptyMessage(0x002);
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }.start();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
