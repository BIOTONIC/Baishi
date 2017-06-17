package com.lovejoy.baishi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.lovejoy.baishi.R.drawable;
import com.lovejoy.baishi.R.id;
import com.lovejoy.baishi.R.layout;
import com.lovejoy.baishi.utils.MainService;
import com.lovejoy.baishi.utils.MyFragmentPagerAdapter;
import com.lovejoy.baishi.utils.MyItem;
import com.lovejoy.baishi.utils.MyRecyclerAdapter;
import com.lovejoy.baishi.utils.MyRecyclerAdapter.OnRecyclerViewItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private final List<MainFragment> fragments = new ArrayList<>();
    private MyFragmentPagerAdapter adapter;
    private ArrayList<MyItem> list1 = new ArrayList<>();
    private ArrayList<MyItem> list2 = new ArrayList<>();
    RecyclerView recyclerViewDish;
    RecyclerView recyclerViewRestaurant;
    private File cache;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_search);

        //缓存文件目录---------------------------------------------------------------
        this.cache = MainActivity.cache;


        //工具栏------------------------------------------------------------------
        Toolbar bar = (Toolbar) this.findViewById(id.toolbar);
        bar.setNavigationIcon(drawable.ic_arrow_back_white_24dp);
        this.setSupportActionBar(bar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        bar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.this.finish();
            }
        });
        this.editText = (EditText) this.findViewById(id.edit_text);

        //加载Fragment布局---------------------------------------------------------
        Bundle b = new Bundle();
        b.putString("path", null);
        this.fragments.add(SearchActivity.newInstance(b));

        b = new Bundle();
        b.putString("path", null);
        this.fragments.add(SearchActivity.newInstance(b));


        //使用适配器将ViewPager与Fragment绑定在一起
        this.viewPager = (ViewPager) this.findViewById(id.viewPager);
        this.adapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager(), this.fragments, new String[]{"美食", "商家"});
        this.viewPager.setAdapter(this.adapter);
        this.tabLayout = (TabLayout) this.findViewById(id.tabLayout);
        this.tabLayout.setupWithViewPager(this.viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //点击搜索按钮---------------------------------------------------------------
        if (id == R.id.action_search) {
            String key = this.editText.getText().toString();
            // 如果关键字为空则不进入搜索
            if (key.equals("") || key == null) {
                //initData(key,"http://123.206.44.12:8800/nourriture/1.php");
            } else {
                this.initData(key, "http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/get_target_dishes", 1);
                this.initData(key, "http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/get_target_restaurants", 2);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initData(final String keys, String path, int tag) {
        final String paths = path;
        final int tags = tag;
        // 开一个线程来加载数据
        new Thread() {
            @Override
            public void run() {
                try {
                    if (tags == 1) {
                        SearchActivity.this.list1 = MainService.getMyItems(keys, paths);
                        if (SearchActivity.this.list1.size() != 0) {
                            SearchActivity.this.handler1.sendMessage(SearchActivity.this.handler1.obtainMessage(100, SearchActivity.this.list1));
                        }
                    } else if (tags == 2) {
                        SearchActivity.this.list2 = MainService.getMyItems(keys, paths);
                        if (SearchActivity.this.list2.size() != 0) {
                            SearchActivity.this.handler2.sendMessage(SearchActivity.this.handler2.obtainMessage(100, SearchActivity.this.list2));
                        }
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
            MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter(SearchActivity.this.list1);
            // 填充Adapter
            SearchActivity.this.recyclerViewDish = SearchActivity.this.fragments.get(0).getRecyclerView();
            SearchActivity.this.recyclerViewDish.setAdapter(myRecyclerAdapter);

            myRecyclerAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
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
            MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter(SearchActivity.this.list2);
            // 填充Adapter
            SearchActivity.this.recyclerViewRestaurant = SearchActivity.this.fragments.get(1).getRecyclerView();
            SearchActivity.this.recyclerViewRestaurant.setAdapter(myRecyclerAdapter);

            myRecyclerAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, MyItem tag) {

                    Intent intent = new Intent(view.getContext(), DetailRestaurantActivity.class);
                    intent.putExtra("MyItem>id", tag.getId());
                    intent.putExtra("MyItem>name", tag.getName());
                    intent.putExtra("MyItem>image", tag.getImgUrl());
                    view.getContext().startActivity(intent);
                }


            });
        }
    };


    public static MainFragment newInstance(Bundle bundle) {
        MainFragment m = new MainFragment();
        m.setArguments(bundle);
        return m;
    }
}
