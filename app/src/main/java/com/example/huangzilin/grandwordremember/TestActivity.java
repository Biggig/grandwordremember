package com.example.huangzilin.grandwordremember;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class TestActivity extends AppCompatActivity {
    private ContentResolver cr;
    public static String AUTHORITY = "com.example.huangzilin.granddictionary.MyProvider";
    public static Uri mCurrentUri = Uri.parse("content://" + AUTHORITY + "/dict");

    private ArrayList<String> words;
    private ArrayList<String> explanations;
    private ArrayList<String> choice;

    private ListView listView;
    private MyAdapter adapter;
    private Button commit;
    private static Context mContext;

    public static WordsCvSQL wordsCvSQL;
    private Map<Integer, String> map;

    private String word_color = "Red";
    private Integer test_word_num = 10;
    private Boolean saved = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("学霸背单词");
        actionBar.setSubtitle("单词测验");
        wordsCvSQL = new WordsCvSQL(this);
        cr = this.getContentResolver();
        mContext = this;
        listView = (ListView) findViewById(R.id.list);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saved == true)
                    changeDB();
                show_answer();
                commit.setVisibility(View.GONE);
            }
        });
        Intent intent = getIntent();
        word_color = intent.getStringExtra("word_color");
        test_word_num = intent.getIntExtra("test_word_num", 10);
        saved = intent.getBooleanExtra("saved", true);
    }

    public void changeDB(){
        map = adapter.map;
        Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Integer, String> entry = it.next();
            int position = entry.getKey();
            String choosed_ex = entry.getValue();
            String Right = explanations.get(position);
            if(Right.equals(choosed_ex)){
                String cur_word = words.get(position);
                Cursor cursor2 =  wordsCvSQL.query(null, "word=?", new String[]{cur_word},null);
                cursor2.moveToFirst();
                int times = cursor2.getInt(cursor2.getColumnIndex("correct_count"));
                times++;
                ContentValues cv = new ContentValues();
                cv.put("correct_count", times);
                wordsCvSQL.update(cv,"word=?", new String[]{cur_word});//修改答对总次数
            }

        }
    }

    public void show_answer(){
        ArrayList<Integer> color = new ArrayList();//需要变色的选项
        for(int i=0;i<test_word_num;i++){
            String right = explanations.get(i);
            for(int j=i*4;j<(i+1)*4;j++){
                String cur_choice = choice.get(j);
                if(right == cur_choice){
                    color.add(j-i*4);
                }
            }
        }
        adapter = new MyAdapter(words, choice, mContext, color, map, word_color);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support1, menu);
        return true;
    }

    public void select_word(){ //随机选择单词
        try{
            words = new ArrayList<>();
            explanations = new ArrayList<>();
            String[] columns = new String[]{"word", "explanation", "level"};
            Cursor cursor;
            Cursor cursor1;
            if(wordsCvSQL.getCount() == 0){//当数据库中无单词时，选前十个
                cursor = cr.query(mCurrentUri, columns, null, null, "word COLLATE NOCASE limit " + String.valueOf(test_word_num));
                cursor.moveToFirst();
            }
            else{//数据库中有单词，选接下来十个
                cursor1 = wordsCvSQL.query(new String[]{"word"}, null, null, "word COLLATE NOCASE desc");
                cursor1.moveToFirst();
                String last_one = cursor1.getString(cursor1.getColumnIndex("word"));
                cursor = cr.query(mCurrentUri, columns, "word >= ?", new String[]{last_one}, "word COLLATE NOCASE limit " + String.valueOf(test_word_num + 1));
                cursor.moveToFirst();
                cursor.moveToNext();
            }
            while(!cursor.isAfterLast()){
                //将测试单词插入数据库
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String explanation = cursor.getString(cursor.getColumnIndex("explanation"));

                if(saved == true){
                    Cursor cursor2 =  wordsCvSQL.query(null, "word=?", new String[]{word},null);//判断单词是否已存在，其实不必要
                    cursor2.moveToFirst();
                    if(cursor2.getCount()==0){
                        int level = cursor.getInt(cursor.getColumnIndex("explanation"));
                        long time =  System.currentTimeMillis() / 1000;
                        ContentValues cv = new ContentValues();
                        cv.put("word", word);
                        cv.put("level",level);
                        cv.put("last_test_time", time);
                        cv.put("test_count", 1);
                        wordsCvSQL.insert(cv);
                    }
                    else{
                        int times = cursor2.getInt(cursor2.getColumnIndex("test_count"));
                        times++;
                        ContentValues cv = new ContentValues();
                        cv.put("test_count", times);
                        wordsCvSQL.update(cv,"word=?", new String[]{word});//修改测试总次数
                    }
                }

                words.add(word);
                explanations.add(explanation);
                cursor.moveToNext();
            }
            int i;
            for(i=0;i<test_word_num;i++){
                int position = explanations.get(i).indexOf("]");
                String mid = explanations.get(i).substring(position + 1);
                explanations.set(i, mid);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void make_choice(){ //制作选项
        int i;
        choice = new ArrayList<>();
        Random random = new Random();
        for(i = 0;i < test_word_num;i++){
            ArrayList<Integer> choice_no = new ArrayList<>();
            choice_no.add(i);
            while(choice_no.size() < 4){
                int randomInt = random.nextInt(test_word_num);
                if(!choice_no.contains(randomInt))
                    choice_no.add(randomInt);
            }
            Iterator<Integer> iterator = choice_no.iterator();
            Collections.shuffle(choice_no); //打乱选项顺序
            while (iterator.hasNext()){
                choice.add(explanations.get(iterator.next()));
            }
        }
    }

    public void show_choice(){
        ArrayList<Integer> color = new ArrayList<>();
        map = new HashMap<Integer, String>();
        adapter = new MyAdapter(words, choice, mContext, color, map, word_color);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.test1:
                Toast.makeText(this, "开始测验", Toast.LENGTH_SHORT).show();
                select_word();
                make_choice();
                show_choice();
                commit.setVisibility(View.VISIBLE);
                break;
            case R.id.menu_1:
                Toast.makeText(this, "开始复习", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
