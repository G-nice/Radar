package com.gnice.radar;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gnice.radar.util.ItemAdapter;
import com.gnice.radar.util.PersonItem;
import com.gnice.radar.util.Util;

import java.util.ArrayList;


public class EnemyFragment extends Fragment implements Toolbar.OnMenuItemClickListener {
    public static final int REQUEST_CODE_ENEMY = 0x010;  // 在enemy打开编辑对话框请求
    public static final int RESULT_CODE_ENEMY = 0x101;   // 在enemy界面打开并返回
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    private ArrayList<PersonItem> personList;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private FloatingActionButton fab;

    private int itemLongClickPosition = 0;

    //    public static EnemyFragment newInstance(String param1) {
    //        EnemyFragment fragment = new EnemyFragment();
    //        Bundle args = new Bundle();
    //        args.putString("agrs1", param1);
    //        fragment.setArguments(args);
    //        return fragment;
    //    }

    public EnemyFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 解决 重新启动 fragment 重叠问题
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
        Log.i("Enemy", "Enemy onCreate");
        personList = AppData.enemiesList;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 解决 重新启动 fragment 重叠问题
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
        super.onSaveInstanceState(outState);
        registerForContextMenu(recyclerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enemy_fragment, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_enemy);
        // // TODO: 2016/10/19 rename file friend_toolbar_menu
        toolbar.inflateMenu(R.menu.friend_toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);

        fab = (FloatingActionButton) view.findViewById(R.id.fab_enemy);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditor(-1, EditDialogFragment.MODE_ADD);  // 添加模式  不传送地址信息 传递-1
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleV_enemy);
        //创建默认的线性LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        itemAdapter = new ItemAdapter(personList);
        recyclerView.setAdapter(itemAdapter);
        //        ListView.setEmptyView(view.findViewById(R.id.list_empty));//设置内容为空时显示的视图
        // 设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        itemAdapter.setOnItemClickLitener(new ItemAdapter.OnItemClickListener() {
            //// TODO: 2016/10/12 add
            @Override
            public void onItemClick(View view, int position) {
                openEditor(position, EditDialogFragment.MODE_VIEW);
                Log.i("onClick item ", "" + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                itemLongClickPosition = position;
                Log.i("onLongClick item ", "" + position);
            }
        });
        return view;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.switch_sort) {
            Log.i("toolbar menu", "switch_sort");

        } else if (menuItemId == R.id.sortby_distance) {
            Log.i("toolbar menu", "sortby_distance");

        } else if (menuItemId == R.id.sortby_name) {
            Log.i("toolbar menu", "sortby_name");

        } else if (menuItemId == R.id.refresh) {

            Snackbar.make(getView(), "Enemy list refresh finish.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("I know", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        }
        return true;
    }

    // 长按 上下文菜单
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // 接收item位置 (使用onLongClick进行设置以及接收)
        // itemLongClickPosition

        switch (item.getItemId()) {
            case R.id.edit_item:
                openEditor(itemLongClickPosition, EditDialogFragment.MODE_EDIT);

                Log.i("contextMenu", "edit item " + itemLongClickPosition);
                break;
            case R.id.delete_item:

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getView().getContext());
                builder.setTitle("Confirm delete");
                builder.setMessage("Are you sure to delete ?");
                builder.setNegativeButton("Cancel", null);
                builder.setIcon(R.mipmap.logo);
                //                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                builder.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // delete 用于更新地图显示
                        AppData.databaseManager.delete(personList.get(itemLongClickPosition));
                        AppData.dictionary.remove(personList.get(itemLongClickPosition).getPhoneNum());
                        personList.remove(itemLongClickPosition).delete();

                        itemAdapter.notifyItemRemoved(itemLongClickPosition);
                        // 对于被删掉的位置及其后range大小范围内的view进行重新onBindViewHolder
                        if (itemLongClickPosition != personList.size())
                            itemAdapter.notifyItemRangeChanged(itemLongClickPosition, personList.size() - itemLongClickPosition);
                        Log.i("contextMenu", "delete item " + itemLongClickPosition);
                    }
                });
                builder.show();


                break;
        }
        return super.onContextItemSelected(item);
    }

    // 展示修改任务相关信息 对话框
    public void openEditor(int position, int mode) {
        // EditDialogFragment 异步
        EditDialogFragment editor = new EditDialogFragment();

        //        传递尝试  添加模式  编辑模式 查看模式
        Bundle bundle = new Bundle();
        bundle.putInt("mode", mode);
        bundle.putInt("position", position);
        editor.setArguments(bundle);
        //当一个Fragment启动另外一个Fragment时，setTargetFragment()设置该方法后，
        //目标Fragment可以将数据回调到启动的Fragment
        //        editor.setTargetFragment(EnemyFragment.this, REQUEST_CODE_ENEMY);
        editor.setTargetFragment(this, REQUEST_CODE_ENEMY);

        editor.show(getFragmentManager(), "EditDialogFragment");  // 将点击的位置当作Tag传参
    }

    //    setTargetFragment();相当于一个路标，为返回的数据提供了方向；
    //    onActivityResult()该方法与Activity中的onActivityResult()作用相同，都是用于接收返回的数据
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 参数中requestCode来自上面的setTargetFragment中
        // resultCode来自DialogFragment中的getTargetFragment().onActivityResult调用

        Log.i("onActivityResult ", "requestCode " + requestCode + " resultCode " + resultCode);

        if (data != null && requestCode == REQUEST_CODE_ENEMY && resultCode == RESULT_CODE_ENEMY) {
            // handle result data here
            // 判断添加模式 进行itemAdapter中的数据更新

            if (data.getStringExtra("mode").equals(String.valueOf(EditDialogFragment.MODE_ADD))) {
                Log.i("receive result mode", "ADD");
                PersonItem personItem = new PersonItem(data.getStringExtra("name"), data.getStringExtra("phoneNum"), PersonItem.ENEMY);
                personList.add(personItem);
                AppData.dictionary.put(personItem.getPhoneNum(), personItem);
                //                AppData appData = (AppData) getActivity().getApplication();
                Util.sortByName(personList);
                final int insertPos = personList.indexOf(personItem);


                recyclerView.smoothScrollToPosition(insertPos);

                //                AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
                //                localAlphaAnimation.setDuration(3200L);
                //                recyclerView.setAnimation(localAlphaAnimation);
                //                recyclerView.startAnimation(localAlphaAnimation);

                //                Handler handler = new Handler();

                //                Runnable runnable1 = new Runnable(){
                //                    public void run(){
                //                        recyclerView.smoothScrollToPosition(insertPos);
                //                    }
                //                };
                //                Runnable runnable2 = new Runnable(){
                //                    public void run(){
                itemAdapter.notifyItemInserted(insertPos);
                // 加入如下代码保证position的位置正确性
                if (insertPos != personList.size() - 1) {
                    itemAdapter.notifyItemRangeChanged(insertPos, personList.size() - insertPos);
                }
                //                    }
                //                };
                //                handler.postDelayed(runnable2, 1000L);

                //                itemAdapter.notifyItemInserted(insertPos);


                // 加入如下代码保证position的位置正确性
                //                if (insertPos != personList.size() - 1) {
                //                    itemAdapter.notifyItemRangeChanged(insertPos, personList.size() - insertPos);
                ////                    itemAdapter.notifyItemRangeInserted(insertPos, personList.size() - insertPos);
                //                }

                // TODO: 2016/10/14
                AppData.databaseManager.add(personItem);
                //

            } else if (data.getStringExtra("mode").equals(String.valueOf(EditDialogFragment.MODE_EDIT))) {
                Log.i("receive result mode", "EDIT");

                itemAdapter.notifyItemChanged(itemLongClickPosition);

                // TODO: 2016/10/14 修改数据库
                AppData.databaseManager.updateInfo(personList.get(itemLongClickPosition));

            }


        }
    }

    // 当人位置改变的时候自动更新列表中的距离
    // 在MainActivity中进行enemyFragment的show的时候调用
    public void refreshList() {
        // // TODO: 2016/10/19 fix 开销有点大
        itemAdapter.notifyDataSetChanged();
    }


    //    @Override
    //    public void OnPersonChangeUpdateLit(PersonItem personItem) {
    //
    //    }

}
