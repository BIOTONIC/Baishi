package com.lovejoy.baishi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lovejoy.baishi.utils.*;

import java.io.File;
import java.util.ArrayList;

public class DetailDishActivity extends AppCompatActivity {

    private File cache;
    private TextView heat;
    private TextView owner;
    private TextView charactertices;
    private TextView taboo;
    private TextView nurtrition;
    private TextView steps;
    private ArrayList<MyItem> uploadDishList = new ArrayList<>();

    private ArrayList<DetailDishItem> list = new ArrayList<>();

    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dish);
        bindActivity(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        //缓存文件目录---------------------------------------------------------------
        cache = MainActivity.cache;


        Intent intent=getIntent();
        final int id = intent.getIntExtra("MyItem>id",0);
        String name = intent.getStringExtra("MyItem>name");
        String image = intent.getStringExtra("MyItem>image");

        heat = (TextView) findViewById(R.id.heat);
        owner= (TextView) findViewById(R.id.owner);
        charactertices = (TextView) findViewById(R.id.charactertices);
        taboo = (TextView) findViewById(R.id.taboo);
        nurtrition = (TextView) findViewById(R.id.nurtrition);
        steps= (TextView) findViewById(R.id.Steps);

        new Thread() {
            public void run() {
                try {
                    list = DetailDishService.getMyItems(id,"http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/show_target_dish");
                    Message msg=handler1.obtainMessage();
                    //msg.obj=list;
                    msg.what=88;
                    handler1.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        ImageView imageView=(ImageView)findViewById(R.id.image);

        AsyncImageTask asyncImageTask = new AsyncImageTask(imageView);
        asyncImageTask.execute(image);

        toolBarLayout.setTitle(name);


        initData(String.valueOf(id),"http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/dish_to_restaurant");
    }

    private void bindActivity(Context context) {
        //CircleImageView中的图片分辨率过高会只显示黑色
        //所以让它的父组件禁用硬件加速
        //https://github.com/hdodenhof/CircleImageView/issues/31
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
    }

    public void initData(final String keys, String path) {
        final String paths = path;
        // 开一个线程来加载数
        new Thread() {
            public void run() {
                try {
                    uploadDishList = MainService.getMyItems(keys, paths);
                    handler2.sendMessage(handler2.obtainMessage(100, uploadDishList));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {

            int what = msg.what;
            if(what == 88)
            {
                //ArrayList<DetailDishItem> list=null;
                //list=(ArrayList<DetailDishItem>) msg.obj;
                heat.setText(list.get(0).getHeat()+"");
                owner.setText(list.get(0).getUpLoader());
                charactertices.setText(list.get(0).getSpecial());
                taboo.setText(list.get(0).getTaboo());
                nurtrition.setText(list.get(0).getNurtrition());
                steps.setText(list.get(0).getStep());
            }
        }
    };

    Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            // 创建Adapter
            MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter(uploadDishList);
            // 填充Adapter
            mRecyclerView.setAdapter(myRecyclerAdapter);

            MyLayoutManager layoutManager = new MyLayoutManager(getBaseContext());
            mRecyclerView.setLayoutManager(layoutManager);

            myRecyclerAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, MyItem tag) {

                    Intent intent = new Intent(view.getContext(), DetailRestaurantActivity.class);
                    intent.putExtra("MyItem>id", tag.getId());
                    view.getContext().startActivity(intent);
                }
            });
        }
    };

    private final class AsyncImageTask extends AsyncTask<String, Integer, Uri> {
        private ImageView imageView;

        public AsyncImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Uri doInBackground(String... params) {// 子线程中执行的
            try {
                return MainService.getImage(params[0], cache);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Uri result) {// 运行在主线程
            if (result != null && imageView != null)
                imageView.setImageURI(result);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(DetailDishActivity.this,MainActivity.class);
        DetailDishActivity.this.startActivity(intent);
    }
}
