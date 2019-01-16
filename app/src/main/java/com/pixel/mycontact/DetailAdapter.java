package com.pixel.mycontact;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixel.mycontact.beans.DetailList;

import java.util.List;

/**
 * 生成详细信息列表
 *
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.mViewHolder> {
    private List<DetailList> listd;
    static class mViewHolder extends RecyclerView.ViewHolder {
        ImageView hd;
        TextView d1;
        TextView d2;
        mViewHolder(@NonNull View itemView) {
            super(itemView);
            hd = itemView.findViewById(R.id.item_dh);
            d1 = itemView.findViewById(R.id.item_d1);
            d2 = itemView.findViewById(R.id.item_d2);
        }
    }

    DetailAdapter(List<DetailList> list){
        listd = list;
    }
    @NonNull
    public DetailAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_detaillist,viewGroup,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailAdapter.mViewHolder mViewHolder, int i) {
        DetailList details = listd.get(i);
        mViewHolder.d1.setText(details.getD1());
        mViewHolder.hd.setImageResource(details.getImgSrc());
        mViewHolder.d2.setText(details.getD2());
    }

    @Override
    public int getItemCount() {
        return listd.size();
    }
}
