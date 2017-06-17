package com.lovejoy.baishi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.lovejoy.baishi.R.id;
import com.lovejoy.baishi.R.layout;
import com.lovejoy.baishi.R.string;
import com.lovejoy.baishi.utils.AsyncImageTask;
import com.lovejoy.baishi.utils.MyFragmentPagerAdapter;
import com.lovejoy.baishi.utils.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private View headerView;
    private CircleImageView navHeaderAvatar;
    private TextView navHeaderName;
    private TextView navHeaderPhone;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private final List<Fragment> fragments = new ArrayList<>();
    private MyFragmentPagerAdapter adapter;
    private int userId;

    public static boolean isUserInfoLoaded;//用户是否登录

    private long lastBackTime = 0;//上次按下返回键的系统时间
    private long currentBackTime = 0;//当前按下返回键的系统时间

    public static File cache;

    final public static int REQUEST_CODE_ASK_STORAGE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_main);

        //缓存文件目录---------------------------------------------------------------
        cache = getCacheDir();

        //获取存储权限---------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= 23) {
            int checkStoragePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(checkStoragePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_ASK_STORAGE);
            }
        }

        //工具栏------------------------------------------------------------------
        Toolbar toolbar = (Toolbar) this.findViewById(id.toolbar);
        this.setSupportActionBar(toolbar);

        //抽屉-------------------------------------------------------------------
        DrawerLayout drawer = (DrawerLayout) this.findViewById(id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, string.navigation_drawer_open, string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //获取Nav Header的控件------------------------------------------------------
        this.navigationView = (NavigationView) this.findViewById(id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);
        //http://stackoverflow.com/questions/32246360/how-to-get-view-from-drawer-header-layout-with-binding-in-activity
        this.headerView = this.navigationView.inflateHeaderView(layout.nav_header_main);
        this.navHeaderAvatar = (CircleImageView) this.headerView.findViewById(id.nav_header_avatar);
        this.navHeaderName = (TextView) this.headerView.findViewById(id.nav_header_name);
        this.navHeaderPhone = (TextView) this.headerView.findViewById(id.nav_header_phone);

        //添加Fragments----------------------------------------------------------
        Bundle b = new Bundle();
        b.putString("path", "http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/get_all_dishes");
        b.putString("flag", "1");
        this.fragments.add(MainActivity.newInstance(b));

        b = new Bundle();
        b.putString("path", "http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/get_all_restaurants");
        b.putString("flag", "2");
        this.fragments.add(MainActivity.newInstance(b));

        //使用适配器将ViewPager与Fragment绑定在一起
        this.viewPager = (ViewPager) this.findViewById(id.viewPager);
        this.adapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager(), this.fragments, new String[]{"美食", "商家"});
        this.viewPager.setAdapter(this.adapter);
        this.tabLayout = (TabLayout) this.findViewById(id.tabLayout);
        this.tabLayout.setupWithViewPager(this.viewPager);

        //发布食谱-----------------------------------------------------------------
        FloatingActionButton fab = (FloatingActionButton) this.findViewById(id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.isUserInfoLoaded) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(view.getContext(), ShareActivity.class);
                    startActivity(intent);
                }


            }
        });


        this.navHeaderAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.isUserInfoLoaded) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    MainActivity.this.startActivity(intent);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "无法获得访问存储权限", Toast.LENGTH_SHORT)
                            .show();
                }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //动态加载用户信息-------------------------------------------------------------
        this.userId = UserInfo.getId(this);
        if (this.userId >0) {
            this.navHeaderName.setText(UserInfo.getName(this));
            this.navHeaderPhone.setText(UserInfo.getPhone(this));
            AsyncImageTask task = new AsyncImageTask(this.navHeaderAvatar, UserInfo.getImgUrl(this));
            task.execute(UserInfo.getImgUrl(this));
            MainActivity.isUserInfoLoaded = true;
        }
    }

    //点击返回 如果抽屉开着的 关闭抽屉
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) this.findViewById(id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            currentBackTime = System.currentTimeMillis();//获取当前系统时间的毫秒数
            if(currentBackTime - lastBackTime > 2 * 1000){//比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                lastBackTime = currentBackTime;
            }else{//如果两次按下的时间差小于2秒，则退出程序
                System.exit(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //点击抽屉里的Item
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_circle) {
            Intent intent = new Intent(this, ShowActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_nearby) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {

        }
        //关闭抽屉
        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static MainFragment newInstance(Bundle bundle) {
        MainFragment m = new MainFragment();
        m.setArguments(bundle);
        return m;
    }
}
