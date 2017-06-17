package com.lovejoy.baishi.utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.lovejoy.baishi.R.id;
import com.lovejoy.baishi.R.layout;
import com.lovejoy.baishi.utils.MyRecyclerAdapter.ViewHolder;

import java.util.ArrayList;

public class MyRecyclerAdapter extends
        Adapter<ViewHolder> implements OnClickListener {

    private ArrayList<MyItem> list;

    public MyRecyclerAdapter(ArrayList<MyItem> list) {
        this.list = list;
    }

    private MyRecyclerAdapter.OnRecyclerViewItemClickListener mOnItemClickListener;

    //define interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, MyItem tag);
    }

    // 用于创建控件
    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parentViewGroup, int i) {
        // 从加载的列表项控件中获得第i个
        View item = LayoutInflater.from(parentViewGroup.getContext()).inflate(
                layout.list_basic_item, parentViewGroup, false);
        MyRecyclerAdapter.ViewHolder vh = new MyRecyclerAdapter.ViewHolder(item);
        //将创建的View注册点击事件
        item.setOnClickListener(this);
        return vh;
    }

    // 为控件设置数据
    @Override
    public void onBindViewHolder(MyRecyclerAdapter.ViewHolder viewHolder, int position) {
        //  获取当前item中显示的数据
        MyItem item = this.list.get(position);
        //  设置要显示的数据
        viewHolder.info.setText(item.getInfo());
        viewHolder.name.setText(item.getName());
        int num_stars_heat = Integer.parseInt(item.getInfo());
        int num_stars = num_stars_heat / 1667;
        viewHolder.ratingBar.setRating(num_stars);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        viewHolder.itemView.setTag(item);
        //显示图片
        this.asyncImageLoad(viewHolder.image, item.getImgUrl());
    }

    @Override
    public void onClick(View v) {
        if (this.mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            this.mOnItemClickListener.onItemClick(v, (MyItem) v.getTag());
        }
    }

    public void setOnItemClickListener(MyRecyclerAdapter.OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    @Override
    public int getItemCount() {
        return this.list.size();
    }

    //  删除指定的Item
    public void removeData(int position) {
        this.list.remove(position);
        //  通知RecyclerView控件某个Item已经被删除
        this.notifyItemRemoved(position);
    }

    //添加Item 不会用到

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView info;
        private final ImageView image;
        private final RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            this.name = (TextView) itemView.findViewById(id.name);
            this.info = (TextView) itemView.findViewById(id.info);
            this.image = (ImageView) itemView.findViewById(id.image);

            this.ratingBar = (RatingBar) itemView.findViewById(id.food_list_item_rate);
        }
    }


    private void asyncImageLoad(ImageView imageView, String path) {
        AsyncImageTask task = new AsyncImageTask(imageView, path);
        task.execute(path);
    }
}

