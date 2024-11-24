package com.tdc.vlxdonline.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.tdc.vlxdonline.Model.TraGop;
import com.tdc.vlxdonline.R;
import com.tdc.vlxdonline.databinding.ItemTraGopBinding;

import java.util.ArrayList;

public class AdapterTraGop extends ArrayAdapter {

    Context context;
    int resource;
    ArrayList<TraGop> data;

    public AdapterTraGop(@NonNull Context context, int resource, ArrayList<TraGop> data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, null);

        TextView tvDot = convertView.findViewById(R.id.tv_dotTT);
        TextView tvTong = convertView.findViewById(R.id.tv_tongTT);
        TextView tvNgay = convertView.findViewById(R.id.tv_hanTT);
        TextView tvTT = convertView.findViewById(R.id.tv_tt_thanh_toan);

        TraGop temp = data.get(position);
        tvDot.setText("Đợt " + temp.getThuTu());
        tvTong.setText(temp.getSoTien());
        tvNgay.setText(temp.getHanTra());
        if (!temp.isDaTra()) {
            tvTT.setText("Chưa Thanh Toán");
            tvTT.setBackgroundColor(Color.rgb(110, 110, 110));
        }

        return convertView;
    }
}
