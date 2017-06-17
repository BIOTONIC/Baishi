package com.lovejoy.baishi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.lovejoy.baishi.utils.*;

import java.io.File;
import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<ShareItem> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ShowShareAdapter myRecyclerAdapter;
    private File cache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        //缓存文件目录---------------------------------------------------------------
        cache = MainActivity.cache;


        //工具栏------------------------------------------------------------------
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ShowActivity.this,MainActivity.class);
                ShowActivity.this.startActivity(intent);
            }
        });



        recyclerView = (RecyclerView) findViewById(R.id.recycler_views);

        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        initData();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    public void initData() {

        // 开一个线程来加载数据
        new Thread() {
            public void run() {
                try {
                    list = ShowService.getMyItems("http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/download_dish_share");
                    handler.sendMessage(handler.obtainMessage(100, list));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 创建Adapter
            myRecyclerAdapter = new ShowShareAdapter(list);
            // 填充Adapter
            recyclerView.setAdapter(myRecyclerAdapter);


        }
    };

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ShowActivity.this,MainActivity.class);
        ShowActivity.this.startActivity(intent);
    }

}
