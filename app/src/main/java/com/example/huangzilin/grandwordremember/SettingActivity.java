package com.example.huangzilin.grandwordremember;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("学霸背单词");
        actionBar.setSubtitle("系统设置");
        Bundle bundle;
        FragmentManager fManager = getFragmentManager();

        FragmentTransaction fTransaction = fManager.beginTransaction();
        //if(fg == null){
        SettingFragment fg = new SettingFragment();
        bundle=new Bundle();
        bundle.putString("data", "第一个Fragment");
        fg.setArguments(bundle);
        fTransaction.add(R.id.ly_content,fg);
        // }else{
        //   fTransaction.show(fg);
        //}
        fTransaction.commit();
    }
}
