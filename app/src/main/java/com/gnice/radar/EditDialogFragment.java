package com.gnice.radar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gnice.radar.util.PersonItem;
import com.gnice.radar.util.Util;


public class EditDialogFragment extends DialogFragment {
    // 三种模式  查看 新增 修改
    public static final int MODE_VIEW = 0;
    public static final int MODE_ADD = 1;
    public static final int MODE_EDIT = 2;
    //    public static final int RESPONSE_CODE = 0x011;


    private int position = 0;
    private int mode = MODE_VIEW;  // 默认模式
    private int requestFrom;

    private TextInputEditText editName;
    private TextInputEditText editPhoneNum;
    private TextView showLatitude;
    private TextView showLongitude;
    private TextView showUpdatetime;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutPhoneNum;
    private LinearLayout editorTitle;
    private PersonItem personItem;

    private boolean isNameOk = false;
    private boolean isPhoneNumOk = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取点击位置(弃用)
        //        position = Integer.parseInt(getTag());
        //        Log.i("editor", "get Tag " + position);

        // 获取模式参数  获取点击位置
        if (getArguments() != null) {
            this.mode = getArguments().getInt("mode");
            this.position = getArguments().getInt("position");
        }
        Log.i("editor get mode", "" + mode);
        Log.i("editor get position", "" + position);

        // 获取请求来源
        requestFrom = getTargetRequestCode();
    }

    //    @Nullable
    //    @Override
    //    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //        // 去掉对话框标题
    //        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    //
    //        View view = inflater.inflate(R.layout.editor_dialogfragment, container, false);
    //        return view;
    //    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.editor_dialogfragment, null);
        view.clearFocus();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // TODO: 2016/10/13 保存操作 视图更新操作
                                Log.i("Dialog PositiveButton", "click");
                                // 收起软键盘  防炸
                                //                                InputMethodManager imm =  (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                //                                if(imm != null) {
                                //                                    imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(),
                                //                                            0);
                                //                                }


                                if (mode == MODE_ADD) {
                                    backResult(String.valueOf(mode), editName.getText().toString(), editPhoneNum.getText().toString());
                                } else if (mode == MODE_EDIT) {
                                    String originalPhoneNum = personItem.getPhoneNum();
                                    personItem.setName(editName.getText().toString());
                                    personItem.setPhoneNum(editPhoneNum.getText().toString());
                                    backResult(String.valueOf(mode), originalPhoneNum);
                                }


                                dismiss();
                            }
                        })
                .setNegativeButton("Cancel", null);

        // 创建对话框
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();  // 为了使得确定按钮隐藏而自己添加  暂时不知副作用
        //        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.INVISIBLE);
        final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);


        textInputLayoutName = (TextInputLayout) view.findViewById(R.id.name_layout);
        textInputLayoutName.setErrorEnabled(true);
        textInputLayoutName.clearFocus();
        textInputLayoutName.clearChildFocus(textInputLayoutName.getEditText());

        textInputLayoutPhoneNum = (TextInputLayout) view.findViewById(R.id.phonenum_layout);
        textInputLayoutPhoneNum.setErrorEnabled(true);
        textInputLayoutPhoneNum.setCounterEnabled(true);
        // TODO: 2016/10/25 fix more than max length BOOM
        textInputLayoutPhoneNum.setCounterMaxLength(13);  // 超出会闪退
        textInputLayoutPhoneNum.clearFocus();

        editName = (TextInputEditText) view.findViewById(R.id.editor_name);
        editName.clearFocus();        //        editName.setHintTextColor(Color.parseColor("#ffffff"));
        if (mode == MODE_VIEW) {
            editName.setFocusable(false);
            editName.setInputType(InputType.TYPE_NULL);
        }

        editPhoneNum = (TextInputEditText) view.findViewById(R.id.editor_phonenum);
        editPhoneNum.clearFocus();


        // 判断输入错误
        if (mode == MODE_VIEW) {
            editPhoneNum.setFocusable(false);
            editName.setInputType(InputType.TYPE_NULL);
        } else {
            editPhoneNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    textInputLayoutPhoneNum.setError("Invalid phone number");
                    textInputLayoutPhoneNum.setErrorEnabled(true);
                    positiveButton.setEnabled(false);
                    isPhoneNumOk = false;
                    //                    Log.i("beforeTextChanged", "invoke" + s.length());
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //                    Log.i("onTextChanged", "invoke" + s.length());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        // length == 0
                        textInputLayoutPhoneNum.setError("Please type a invalid phone number");
                        textInputLayoutPhoneNum.setErrorEnabled(true);
                        positiveButton.setEnabled(false);
                        isPhoneNumOk = false;
                    } else if (s.length() == 11) {
                        if ((!personItem.getPhoneNum().equals(s.toString())) && AppData.dictionary.containsKey(s.toString())) {
                            textInputLayoutPhoneNum.setError("Phone number already exist");
                            textInputLayoutPhoneNum.setErrorEnabled(true);
                            positiveButton.setEnabled(false);
                            isPhoneNumOk = false;
                        } else if (!Util.iselPhoneNnm(s.toString())) {
                            textInputLayoutPhoneNum.setError("Invalid phone number");
                            textInputLayoutPhoneNum.setErrorEnabled(true);
                            positiveButton.setEnabled(false);
                            isPhoneNumOk = false;
                            //                            getDialog();
                        } else {
                            textInputLayoutPhoneNum.setErrorEnabled(false);
                            //                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                            isPhoneNumOk = true;
                            if (isNameOk)
                                positiveButton.setEnabled(true);
                        }
                    } else {
                        //                        (s.length() != 11 || s.length() != 0)
                        textInputLayoutPhoneNum.setError("Not a phone number");
                        textInputLayoutPhoneNum.setErrorEnabled(true);
                        positiveButton.setEnabled(false);
                        isPhoneNumOk = false;
                    }
                    //                    Log.i("afterTextChanged", "invoke" + s.length());
                }
            });

            editName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().isEmpty()) {
                        isNameOk = false;
                        textInputLayoutName.setError("Name can't be empty");
                        textInputLayoutName.setErrorEnabled(true);
                    } else if (s.length() > 32) {
                        isNameOk = false;
                        textInputLayoutName.setError("Name is too long");
                        textInputLayoutName.setErrorEnabled(true);
                    } else {
                        isNameOk = true;
                        textInputLayoutName.setErrorEnabled(false);
                        if (isPhoneNumOk) {
                            positiveButton.setEnabled(true);
                        }
                    }
                }
            });
        }

        showLongitude = (TextView) view.findViewById(R.id.editor_longitude);
        showLatitude = (TextView) view.findViewById(R.id.editor_latitude);
        showUpdatetime = (TextView) view.findViewById(R.id.editor_updatetime);

        // 查看 编辑 添加
        if (mode == MODE_VIEW || mode == MODE_EDIT)

        {
            if (requestFrom == FriendFragment.REQUEST_CODE_FRIEND) {
                personItem = AppData.friendsList.get(position);
            } else {
                personItem = AppData.enemiesList.get(position);
            }
            editName.setText(personItem.getName());
            editPhoneNum.setText(personItem.getPhoneNum());
            showLongitude.setText(String.format("%.3f °", personItem.getLongitude()));
            showLatitude.setText(String.format("%.3f °", personItem.getLatitude()));
            showUpdatetime.setText(personItem.getLastUpdate());

        }
        //        else if (mode == MODE_ADD) {
        //            if (requestFrom == FriendFragment.REQUEST_CODE_FRIEND) {
        //                personItem = new PersonItem("", "", PersonItem.FRIEND);
        //            } else {
        //                personItem = new PersonItem("", "", PersonItem.ENEMY);
        //            }


        // 动态标签颜色
        editorTitle = (LinearLayout) view.findViewById(R.id.editor_title);
        if (personItem != null) {
            editorTitle.setBackgroundColor(personItem.getPaletteColor());
        }


        return alertDialog;
    }

    private void backResult(String data, String originalPhoneNum) {

        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("mode", data);
        intent.putExtra("originalPhoneNum", originalPhoneNum);
        // 在Activity中返回数据，需要调用setResult()方法，数据会自动返回；而Fragment中则需要调用onActivityResult()
        if (requestFrom == FriendFragment.REQUEST_CODE_FRIEND) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), FriendFragment.RESULT_CODE_FRIEND, intent);
        } else {
            getTargetFragment().onActivityResult(getTargetRequestCode(), EnemyFragment.RESULT_CODE_ENEMY, intent);
        }
        getFragmentManager().popBackStack();
    }

    private void backResult(String data, String name, String phoneNum) {

        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("mode", data);
        intent.putExtra("name", name);
        intent.putExtra("phoneNum", phoneNum);
        // 在Activity中返回数据，需要调用setResult()方法，数据会自动返回；而Fragment中则需要调用onActivityResult()
        if (requestFrom == FriendFragment.REQUEST_CODE_FRIEND) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), FriendFragment.RESULT_CODE_FRIEND, intent);
        } else {
            getTargetFragment().onActivityResult(getTargetRequestCode(), EnemyFragment.RESULT_CODE_ENEMY, intent);
        }
        getFragmentManager().popBackStack();
    }

    @Override
    public void onPause() {
        dismiss();  // 消除
        super.onPause();
    }

    //    @Override
    //    public void onStart() {
    //        super.onStart();
    //        // 将背景设置为透明  显示圆角效果
    //        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    //
    //        // 宽度占比  75%
    //        Dialog dialog = getDialog();
    //        if (dialog != null) {
    //            DisplayMetrics dm = new DisplayMetrics();
    //            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    //            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
    //    }


    //    // 关闭或点击其他地方的时候触发
    //    @Override
    //    public void onDismiss(DialogInterface dialog) {
    //        super.onDismiss(dialog);
    //
    //        Log.i("editor", "dismiss");
    //    }
}

//    dismiss();  //关闭对话框，并触发onDismiss()回调函数。