package com.kiyotaka.jetpackdemo.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.kiyotaka.jetpackdemo.http.Area;

public class CheckAdapter extends ArrayAdapter<String> {
    private static final int MAX = 3;
    private SparseArray<Integer> saveChecked = new SparseArray<>();
    private boolean isSingle;

    public CheckAdapter(@NonNull Context context, String[] objects) {
        this(context, objects, false);
    }

    public CheckAdapter(@NonNull Context context, String[] objects, boolean isSingle) {
        super(context, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, objects);
        this.isSingle = isSingle;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(android.R.id.text1);
        checkedTextView.setChecked(saveChecked.get(position) != null);
        return view;
    }

    public void toggle(int key, int value) {
        //单选
        if (isSingle) {
            saveChecked.clear();
            saveChecked.put(key, value);
        }
        //多选
        else {
            if (saveChecked.get(key) == null) {
                if (saveChecked.size() < MAX) {
                    saveChecked.put(key, value);
                } else {
                    ToastUtils.showShort("最多选取3个！");
                }
            } else
                saveChecked.remove(key);
        }
        notifyDataSetChanged();
    }

    public SparseArray<Integer> getSaveChecked() {
        return saveChecked;
    }
}