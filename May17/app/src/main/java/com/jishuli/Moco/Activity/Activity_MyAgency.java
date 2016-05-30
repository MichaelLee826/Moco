package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jishuli.Moco.Adapter.Adapter_MyAgency;
import com.jishuli.Moco.R;

import java.util.ArrayList;
import java.util.List;

public class Activity_MyAgency extends Activity {
    private ImageButton backButton;
    private Button signUpButton;
    private Button joinButton;

    private List<String> agencyList = new ArrayList<>();
    private ListAdapter listAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myagency);

        findViews();
        setListeners();
        setListView();

    }

    public void findViews(){
        listView = (ListView)findViewById(R.id.MyAgencyListView);

        //我要加入机构的按钮作为ListView的FootView
        View footView = getLayoutInflater().inflate(R.layout.myagency_footview, null);
        listView.addFooterView(footView);

        joinButton = (Button)footView.findViewById(R.id.MyAgency_FootView);

        backButton = (ImageButton)findViewById(R.id.MyAgencyBackgroundLayoutBackButton);
        signUpButton = (Button)findViewById(R.id.MyAgencySignUpButton);
    }

    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_MyAgency.this.finish();
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyAgency.this, Activity_JoinAgency.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyAgency.this, Activity_SignUpAgency.class);
                startActivity(intent);
            }
        });
    }

    public void setListView(){
        for (int i = 0; i < 10; i++){
            agencyList.add("ABC育儿机构");
        }
        listAdapter = new Adapter_MyAgency(this, agencyList);
        listView.setAdapter(listAdapter);
    }
}
