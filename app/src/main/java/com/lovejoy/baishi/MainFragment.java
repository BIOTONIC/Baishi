package com.lovejoy.baishi;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lovejoy.baishi.R.id;
import com.lovejoy.baishi.R.layout;
import com.lovejoy.baishi.utils.DividerItemDecoration;
import com.lovejoy.baishi.utils.MainService;
import com.lovejoy.baishi.utils.MyItem;
import com.lovejoy.baishi.utils.MyRecyclerAdapter;
import com.lovejoy.baishi.utils.MyRecyclerAdapter.OnRecyclerViewItemClickListener;

import java.io.File;
import java.util.ArrayList;

public class MainFragment extends Fragment {
    private ArrayList<MyItem> list1 = new ArrayList<>();
    private ArrayList<MyItem> list2 = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;
    private File cache;
    private View view;
    private String path;
    private String flag;

    public MainFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //获得path---------------------------------------------------------------
        this.path = this.getArguments().getString("path");
        this.flag = this.getArguments().getString("flag");

        //缓存文件目录---------------------------------------------------------------
        this.cache=MainActivity.cache;

        // 获取RecyclerView对象----------------------------------------------------
        this.view = inflater.inflate(layout.fragment_main_dish, container, false);
        this.recyclerView = (RecyclerView) this.view.findViewById(id.recycler_view);
        // 必须要创建线性布局管理器（默认是垂直方向）
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        // 为RecyclerView指定布局管理对象
        this.recyclerView.setLayoutManager(layoutManager);
        //每个item高度固定，提高性能
        this.recyclerView.setHasFixedSize(true);
        //分割线
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST));


        return this.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //加载数据并填充-------------------------------------------------------------
        if (this.path != "" && this.path != null) {
            this.initData(this.path, this.flag);
        }
    }

    public void initData(String path, String flag) {
        final String paths = path;
        final String flags = flag;
        // 开一个线程来加载数据
        new Thread() {
            @Override
            public void run() {
                try {
                    if (flags == "1") {
                        MainFragment.this.list1 = MainService.getMyItems(null, paths);
                        MainFragment.this.handler1.sendMessage(MainFragment.this.handler1.obtainMessage(100, MainFragment.this.list1));
                    } else if (flags == "2") {
                        MainFragment.this.list2 = MainService.getMyItems(null, paths);
                        MainFragment.this.handler2.sendMessage(MainFragment.this.handler2.obtainMessage(100, MainFragment.this.list2));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 创建Adapter
            MainFragment.this.myRecyclerAdapter = new MyRecyclerAdapter(MainFragment.this.list1);
            // 填充Adapter
            MainFragment.this.recyclerView.setAdapter(MainFragment.this.myRecyclerAdapter);

            MainFragment.this.myRecyclerAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, MyItem tag) {

                    Intent intent = new Intent(view.getContext(), DetailDishActivity.class);
                    intent.putExtra("MyItem>id", tag.getId());

                    intent.putExtra("MyItem>name", tag.getName());
                    intent.putExtra("MyItem>image", tag.getImgUrl());
                    view.getContext().startActivity(intent);
                }


            });
        }
    };

    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 创建Adapter
            MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter(MainFragment.this.list2);
            // 填充Adapter
            MainFragment.this.recyclerView.setAdapter(myRecyclerAdapter);
            myRecyclerAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, MyItem tag) {
                    Intent intent = new Intent(view.getContext(), DetailRestaurantActivity.class);
                    intent.putExtra("MyItem>id", tag.getId());
                    view.getContext().startActivity(intent);
                }


            });

        }
    };

    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }
}
