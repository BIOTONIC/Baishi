package com.lovejoy.baishi.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;
import com.lovejoy.baishi.MainActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;

public final class AsyncImageTask extends AsyncTask<String, Integer, Uri> {
    private final File cache;
    private CircleImageView circleImageView;
    private ImageView imageView;

    public AsyncImageTask(ImageView imageView, String url) {
        this.cache = MainActivity.cache;
        this.imageView = imageView;
    }

    public AsyncImageTask(CircleImageView circleImageView, String url) {
        this.cache = new File(Environment.getExternalStorageDirectory(), "cache"); // 实例化缓存文件
        if (!this.cache.exists()) {
            this.cache.mkdirs(); // 如果文件不存在，创建
        }
        this.circleImageView = circleImageView;
    }

    @Override
    protected Uri doInBackground(String... params) {//参数0是图片的url
        try {
            if (params[0] != null) {
                return MainService.getImage(params[0], this.cache);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Uri result) {// 运行在主线程
        if (result != null && this.imageView != null) {
            this.imageView.setImageURI(null);
            this.imageView.setImageURI(result);
        } else if (result != null && this.circleImageView != null) {
            this.circleImageView.setImageURI(null);
            this.circleImageView.setImageURI(result);
        }
    }
}
