package com.lovejoy.baishi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.lovejoy.baishi.R.drawable;
import com.lovejoy.baishi.R.id;
import com.lovejoy.baishi.R.layout;
import com.lovejoy.baishi.R.menu;
import com.lovejoy.baishi.utils.*;
import com.lovejoy.baishi.utils.MyRecyclerAdapter.OnRecyclerViewItemClickListener;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 30;
    private int mMaxScrollSize;
    private boolean mIsAvatarShown = true;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private CircleImageView mProfileImage;
    private TextView mProfileName;
    private TextView mProfilePhone;
    private Button mChangeProfile;
    private Button mLogout;
    private TagView mTagView;
    private RecyclerView mRecyclerView;
    private Bitmap bitmap;
    private String photoPath;

    private ArrayList<MyItem> uploadDishList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_profile);

        //绑定组件并显示本地存储的用户信息-----------------------------------------------------
        this.bindActivity(this);

        //工具栏------------------------------------------------------------------
        this.mToolbar.setTitle("");
        this.mToolbar.inflateMenu(menu.menu_profile);
        this.setSupportActionBar(this.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.mToolbar.setNavigationIcon(drawable.ic_arrow_back_white_24dp);
        this.setSupportActionBar(this.mToolbar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        this.mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Avatar动画用到的----------------------------------------------------------
        this.mAppBarLayout.addOnOffsetChangedListener(this);
        this.mMaxScrollSize = this.mAppBarLayout.getTotalScrollRange();

        //更改资料和注销登录------------------------------------------------------------
        this.mLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = v.getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
                pref.edit().clear().commit();
                MainActivity.isUserInfoLoaded = false;
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        //加载数据并填充-------------------------------------------------------------
        this.mProfileName.setText(UserInfo.getName(this));
        this.mProfilePhone.setText(UserInfo.getPhone(this));
        AsyncImageTask task = new AsyncImageTask(this.mProfileImage, UserInfo.getImgUrl(this));
        task.execute(UserInfo.getImgUrl(this));

        String[] tagArray = UserInfo.getLabels(this);
        if (tagArray != null && tagArray.length > 0) {
            for (int i = 0; i < tagArray.length; i++) {
                Tag tag = new Tag(tagArray[i]);
                tag.layoutColor = Color.parseColor("#82D900");//已有标签用绿色
                tag.isDeletable = true;
                mTagView.addTag(tag);
            }
        }
        Tag addtag = new Tag("添加新标签");
        addtag.layoutColor = Color.parseColor("#FF9224");//添加按钮标签用黄色
        addtag.isDeletable = false;
        mTagView.addTag(addtag);
        mTagView.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {


                //点击添加按钮标签-----------------------------------------------------
                if (tag.text.equals("添加新标签")) {
                    mTagView.remove(position);
                    //展示所有没有添加的标签
                    String[] lastArray = {"新口味1", "新口味2", "新口味3", "新口味4"};
                    for (int i = 0; i < lastArray.length; i++) {
                        Tag newtag = new Tag(lastArray[i]);
                        newtag.layoutColor = Color.parseColor("#FF9224");//未添加标签用黄色
                        newtag.isDeletable = false;
                        mTagView.addTag(newtag);
                    }
                    Tag donetag = new Tag("完成");
                    donetag.layoutColor = Color.parseColor("#82D900");//完成按钮标签用绿色
                    donetag.isDeletable = false;
                    mTagView.addTag(donetag);
                }


                //点击完成按钮标签-----------------------------------------------------
                else if (tag.text.equals("完成")) {
                    //收集所有绿色的不是完成按钮的即要保留的标签
                    List<Tag> alltags = mTagView.getTags();
                    List<Tag> remaintags = new ArrayList<Tag>();
                    for (int i = 0; i < alltags.size(); i++) {
                        Tag itemtag = alltags.get(i);
                        if (itemtag.layoutColor == Color.parseColor("#82D900") && itemtag.text != "完成") {
                            remaintags.add(itemtag);
                        }
                    }
                    //然后remove所有的标签 添加remaintags中的标签
                    //mTagView.removeAll();//removeAll()用不了 改为一个个删除
                    for (int i = position; i >= 0; i--) {
                        mTagView.remove(i);
                    }
                    for (int i = 0; i < remaintags.size(); i++) {
                        Tag itemtag = remaintags.get(i);
                        mTagView.addTag(itemtag);
                    }
                    //添加添加新标签按钮标签
                    Tag addtag = new Tag("添加新标签");
                    addtag.layoutColor = Color.parseColor("#FF9224");//添加按钮标签用黄色
                    addtag.isDeletable = false;
                    mTagView.addTag(addtag);

                    //这只是显示上的 要连接服务器
                    //
                    //
                }


                //点击其他标签 如果黄色就变成绿色---------------------------------------------
                else {
                    if (tag.layoutColor == Color.parseColor("#FF9224")) {
                        tag.layoutColor = Color.parseColor("#82D900");
                        tag.isDeletable = true;
                        mTagView.invalidate();
                    }
                }
            }
        });
        this.mTagView.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(TagView view, Tag tag, int position) {
                view.remove(position);
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 使用Intent触发选择Action
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                // 打开系统提供的图片选择界面
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                // 传参以在返回本界面时触发加载图片的功能
                startActivityForResult(intent, 0x1);


            }
        });
        this.initData("http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/get_all_dishes");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {

                Uri selectedImage = data.getData();

                // 这里开始的第二部分，获取图片的路径：
                String[] proj = { MediaStore.Images.Media.DATA };
                // 将URI转换为路径：
                Cursor cursor = managedQuery(selectedImage, proj, null, null, null);
                // 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();
                // 最后根据索引值获取图片路径
                photoPath = cursor.getString(column_index);

                String file_extn = photoPath.substring(photoPath.lastIndexOf(".")+1);

                // 压缩成800*480
                bitmap = BitmapUtils.decodeSampledBitmapFromFd(photoPath, 480, 480);

                String url = "http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/upload_personal_imgurl";
                upload(url);
                try {

                    if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {

                    }
                    else{
                        //NOT IN REQUIRED FORMAT
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void upload(final String url) {

        // 将bitmap转为string，并使用BASE64加密
        final String photo = BitmapUtils.BitmapToString(bitmap);
        // 获取到图片的名字
        final String name = photoPath.substring(photoPath.lastIndexOf("/")).substring(1);

        final int id = UserInfo.getId(getBaseContext());


        new Thread() {
            public void run() {
                try {
                    UserPhotoService.getMyItems(url,id,photo,name,getBaseContext());
                    AsyncImageTask task = new AsyncImageTask(mProfileImage, UserInfo.getImgUrl(getBaseContext()));
                    task.execute(UserInfo.getImgUrl(getBaseContext()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (this.mMaxScrollSize == 0)
            this.mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = Math.abs(i) * 50 / this.mMaxScrollSize;

        if (percentage >= ProfileActivity.PERCENTAGE_TO_ANIMATE_AVATAR && this.mIsAvatarShown) {
            this.mIsAvatarShown = false;
            this.mProfileImage.animate().scaleY(0).scaleX(0).start();
        }

        if (percentage <= ProfileActivity.PERCENTAGE_TO_ANIMATE_AVATAR && !this.mIsAvatarShown) {
            this.mIsAvatarShown = true;

            this.mProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    private void bindActivity(Context context) {
        this.mToolbar = (Toolbar) this.findViewById(id.main_toolbar);
        //CircleImageView中的图片分辨率过高会只显示黑色
        //所以让它的父组件禁用硬件加速
        //https://github.com/hdodenhof/CircleImageView/issues/31
        this.mAppBarLayout = (AppBarLayout) this.findViewById(id.main_appbar);
        this.mAppBarLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.mProfileImage = (CircleImageView) this.findViewById(id.profile_avatar);
        this.mProfileName = (TextView) this.findViewById(id.name);
        this.mProfilePhone = (TextView) this.findViewById(id.phone);
        this.mChangeProfile = (Button) this.findViewById(id.button_change);
        this.mLogout = (Button) this.findViewById(id.button_logout);
        this.mTagView = (TagView) this.findViewById(id.tag_view);
        this.mRecyclerView = (RecyclerView) this.findViewById(id.recycler_view);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //分享个人信息
        if (id == R.id.action_share) {
            //
            //
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void initData(String path) {
        final String paths = path;
        // 开一个线程来加载数据
        new Thread() {
            @Override
            public void run() {
                try {
                    ProfileActivity.this.uploadDishList = MainService.getMyItems(null, paths);
                    ProfileActivity.this.handler.sendMessage(ProfileActivity.this.handler.obtainMessage(100, ProfileActivity.this.uploadDishList));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 创建Adapter
            MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter(ProfileActivity.this.uploadDishList);
            // 填充Adapter
            ProfileActivity.this.mRecyclerView.setAdapter(myRecyclerAdapter);

            MyLayoutManager layoutManager = new MyLayoutManager(getBaseContext());
            ProfileActivity.this.mRecyclerView.setLayoutManager(layoutManager);

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
}
