package com.gnice.radar.util;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnice.radar.R;

import java.util.ArrayList;


public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    protected ArrayList<PersonItem> persionList;
    private ItemViewHolder holder;
    private OnItemClickListener mOnItemClickListener = null;
    // contextMenu 相关
    private int position;


    //创建新View，被LayoutManager所调用

    public ItemAdapter(ArrayList<PersonItem> persionList) {
        this.persionList = persionList;
    }


    //将数据与界面进行绑定的操作

    /**
     * 渲染具体的ViewHolder
     *
     * @param parent   ViewHolder的容器
     * @param viewType 一个标志，我们根据该标志可以实现渲染不同类型的ViewHolder
     * @return
     */
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        holder = new ItemViewHolder(view);
        //        MyViewHolder holder = LayoutInflater.from(parent.getContext()).inflate(R.id.list_item, parent,false);

        // 设置回调函数
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(this);
            holder.itemView.setOnLongClickListener(this);
        }
        return holder;
    }

    /**
     * 绑定ViewHolder的数据。
     *
     * @param holder   存储视图数据
     * @param position 数据源list的下标
     */
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        PersonItem personItem = persionList.get(position);
        holder.ItemName.setText(personItem.getName());
        holder.ItemPhoneNum.setText(personItem.getPhoneNum());
        holder.ItemTitle.setText(personItem.getName().substring(0, 1));
        holder.ItemDistance.setText(personItem.getDistanceStr());
        // 动态标签颜色
        GradientDrawable bgShape = (GradientDrawable) holder.ItemTitle.getBackground();
        bgShape.setColor(personItem.getPaletteColor());

        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return persionList.size();
    }

    /**
     * 决定元素的布局使用哪种类型
     *
     * @param position 数据源List的下标
     * @return 一个int型标志，传递给onCreateViewHolder的第二个参数
     */
    // 暂时无用
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //    接口回调函数
    @Override
    public void onClick(View v) {
        mOnItemClickListener.onItemClick(v, (int) v.getTag());
    }

    @Override
    public boolean onLongClick(View v) {
        mOnItemClickListener.onItemLongClick(v, (int) v.getTag());
        //        return true;  // 阻止继续调用onClick
        return false;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    // 点击事件接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }


    // 写法有误  不可用
/*
    // 相关操作函数
    public void addItem(PersonItem personItem) {
        persionList.add(personItem);
    }

    public void addItem(PersonItem personItem, int position) {
        persionList.add(position, personItem);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        persionList.remove(position);
    }

    public void removeItem(PersonItem personItem) {
        int position = persionList.indexOf(personItem);
        persionList.remove(position);
        notifyItemRemoved(position);//Attention!
    }
    //        notifyItemChanged();
    */


}
