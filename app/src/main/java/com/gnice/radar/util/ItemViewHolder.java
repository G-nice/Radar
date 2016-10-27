package com.gnice.radar.util;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.gnice.radar.R;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    TextView ItemTitle;
    TextView ItemName;
    TextView ItemPhoneNum;
    TextView ItemDistance;

    public ItemViewHolder(View view) {
        super(view);

        ItemTitle = (TextView) view.findViewById(R.id.item_title);
        ItemName = (TextView) view.findViewById(R.id.item_name);
        ItemPhoneNum = (TextView) view.findViewById(R.id.item_phonenum);
        ItemDistance = (TextView) view.findViewById(R.id.item_distance);

        view.setOnCreateContextMenuListener(this);


        // // TODO: 2016/10/10 设置点击弹出详情监听
        //        itemView.findViewById(R.id.base_swipe_item_container).setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                showNewsDetail(getPosition());
        //            }
        //        });
        //    }

    }

    // 构造上下文菜单ContextMenu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //add => groupId, itemId, order, title
        menu.add(Menu.NONE, R.id.edit_item, Menu.NONE, "Edit");
        menu.add(Menu.NONE, R.id.delete_item, Menu.NONE, "Delete");
        menu.add(Menu.NONE, R.id.refresh_item, Menu.NONE, "Refresh");
    }
}
