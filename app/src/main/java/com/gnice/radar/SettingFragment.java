package com.gnice.radar;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gnice.radar.UI.RevealFollowButton;
import com.gnice.radar.util.Util;


public class SettingFragment extends Fragment {
    //public class SettingFragment extends PreferenceFragment {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";


    long[] mHits = new long[3];
    Toolbar mToolbar;
    RevealFollowButton revealFollowButton;

    //        public static SettingFragment newInstance(String param1) {
    //            SettingFragment fragment = new SettingFragment();
    //            Bundle args = new Bundle();
    //            args.putString("agrs1", param1);
    //            fragment.setArguments(args);
    //            return fragment;
    //        }

    public SettingFragment() {
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
        Log.i("Setting", "Setting onCreate");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 解决 重新启动 fragment 重叠问题
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);


        //            Bundle bundle = getArguments();
        //            String agrs1 = bundle.getString("agrs1");

        TextView tv = (TextView) view.findViewById(R.id.setting_text);
        tv.setText("Setting");
        Button bt = (Button) view.findViewById(R.id.version);
        bt.setText(String.valueOf(Util.getAPPVersionCode(getActivity())) + " " + Util.getAPPVersionName(getActivity()));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //src 拷贝的源数组
                //srcPos 从源数组的那个位置开始拷贝.
                //dst 目标数组
                //dstPos 从目标数组的那个位子开始写数据
                //length 拷贝的元素的个数
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    Toast.makeText(getActivity().getApplicationContext(), "恭喜你，3次点击了。", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //        AppData data = (AppData) getActivity().getApplication();
        //        data.setString("test text");
        //        Toast.makeText(getActivity().getApplicationContext(), data.getString(), Toast.LENGTH_SHORT).show();

        //            getActivity().getFragmentManager().beginTransaction().replace(R.id.preference_fragment, new Preference()).commit();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_setting);
        getChildFragmentManager().beginTransaction().replace(R.id.preference_fragment, new Preference()).commit();

        //        initToolbar();

        revealFollowButton = (RevealFollowButton) view.findViewById(R.id.rfbutton);

        return view;
    }

    // 初始化Toolbar
    private void initToolbar() {
        mToolbar.setTitle("Setting");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        //        setSupportActionBar(mToolbar);
        //        ActionBar actionBar = getSupportActionBar();
        //        if (actionBar != null) {
        //            actionBar.setHomeAsUpIndicator(R.drawable.ic_left_back);
        //            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static class Preference extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}
