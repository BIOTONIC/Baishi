package com.lovejoy.baishi.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lovejoy.baishi.R;

import java.util.ArrayList;

public class ShowShareAdapter extends
        RecyclerView.Adapter<ShowShareAdapter.ViewHolder>  {

    private ArrayList<ShareItem> list = null;

    public ShowShareAdapter(ArrayList<ShareItem> list) {
        this.list = list;
    }

    //private MyRecyclerAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
//    public static interface OnRecyclerViewItemClickListener {
//        void onItemClick(View view, MyItem tag);
//    }

    // 用于创建控件
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parentViewGroup, int i) {
        // 从加载的列表项控件中获得第i个
        View item = LayoutInflater.from(parentViewGroup.getContext()).inflate(
                R.layout.show_share_item, parentViewGroup, false);
        ViewHolder vh = new ViewHolder(item);
        //将创建的View注册点击事件
        //item.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ShareItem item = list.get(position);
        viewHolder.name.setText(item.getName());
        viewHolder.words.setText(item.getWords());
        viewHolder.time.setText(item.getTime());

        //将数据保存在itemView的Tag中，以便点击时进行获取
        //viewHolder.itemView.setTag(item);
        //显示图片
        asyncImageLoad(viewHolder.image, item.getImage());
        asyncImageLoad(viewHolder.photo, item.getPhoto());

    }


//    public void onClick(View v) {
//        if (mOnItemClickListener != null) {
//            //注意这里使用getTag方法获取数据
//            mOnItemClickListener.onItemClick(v, (MyItem) v.getTag());
//        }
//    }

//    public void setOnItemClickListener(MyRecyclerAdapter.OnRecyclerViewItemClickListener listener) {
//        this.mOnItemClickListener = listener;
//    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    //  删除指定的Item
    public void removeData(int position) {
        list.remove(position);
        //  通知RecyclerView控件某个Item已经被删除
        notifyItemRemoved(position);
    }

    //添加Item 不会用到

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView words;
        private ImageView image;
        private ImageView photo;
        private TextView time;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            words = (TextView) itemView.findViewById(R.id.words);
            image = (ImageView) itemView.findViewById(R.id.image);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            time = (TextView) itemView.findViewById(R.id.time);

        }
    }
    private void asyncImageLoad(ImageView imageView, String path) {
        AsyncImageTask task = new AsyncImageTask(imageView, path);
        task.execute(path);
    }
}

