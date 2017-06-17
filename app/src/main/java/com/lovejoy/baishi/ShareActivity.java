package com.lovejoy.baishi;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.lovejoy.baishi.utils.BitmapUtils;
import com.lovejoy.baishi.utils.ShareService;
import com.lovejoy.baishi.utils.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShareActivity extends AppCompatActivity {

    private ImageView photo;
    private Bitmap bitmap;
    private String photoPath;
    private Button upload;
    private EditText content;
    private int tag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);


        content = (EditText) findViewById(R.id.content_et);
        photo = (ImageView) findViewById(R.id.photo);
        upload = (Button) findViewById(R.id.send_btn);

        photo.setOnClickListener(new View.OnClickListener() {

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


        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = "http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/upload_dish_share";
                upload(url);

                Intent intent=new Intent(ShareActivity.this,ShowActivity.class);
                ShareActivity.this.startActivity(intent);
            }
        });
    }

    // 响应startActivityForResult，获取图片路径
    @Override
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
                //bitmap = BitmapFactory.decodeFile(photoPath);
                bitmap = BitmapUtils.decodeSampledBitmapFromFd(photoPath, 480, 800);
                // 设置imageview显示图片
                photo.setImageBitmap(bitmap);

                try {

                    if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                        //FINE
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
        final String time = getSystemTime();
        final int id = UserInfo.getId(getBaseContext());
        final String str = content.getText().toString();

        new Thread() {
            public void run() {
                try {
                   ShareService.getMyItems(url,id,time,photo,name,str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //获取系统当期时间
    public static String getSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }
}


