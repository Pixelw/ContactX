package com.pixel.mycontact.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pixel.mycontact.ContactDetailActivity;
import com.pixel.mycontact.R;
import com.pixel.mycontact.beans.People;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.mViewHolder> {

    private List<People> mPeopleList;
    private List<People> checkedPeople = new ArrayList<>();
    private Context context;

    public PeopleAdapter(List<People> peopleList,Context context) {
        mPeopleList = peopleList;
        this.context  = context;
    }

    public List<People> getCheckedPeople() {
        return checkedPeople;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_inlist, viewGroup, false);
        final mViewHolder holder = new mViewHolder(view);
        //设置item的点击事件，获取当前item对应people对象，
        holder.peopleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                People people = mPeopleList.get(position);
                Intent intent = new Intent(v.getContext(), ContactDetailActivity.class);
                intent.putExtra("people", people);
                v.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewHolder mViewHolder, int i) {
        final People people = mPeopleList.get(i);
        mViewHolder.name.setText(people.getName());
        mViewHolder.tvMsg.setText(people.getDisplayMsg());
        people.isSelected(false);
        mViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!people.isSelected()) {
                    mViewHolder.avatar.setImageResource(R.drawable.ic_done_black_24dp);
                    people.isSelected(true);
                    checkedPeople.add(people);
                } else {
                    mViewHolder.avatar.setImageResource(R.drawable.ic_account_circle_black_24dp);
                    people.isSelected(false);
                    checkedPeople.remove(people);
                }
                //notifyDataSetChanged();
            }
        });
        if (!TextUtils.isEmpty(people.getStatus())) {
            mViewHolder.llOnline.setVisibility(View.VISIBLE);
            mViewHolder.tvStatus.setText(people.getStatus());
            mViewHolder.avatar.setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary));
        } else {
            mViewHolder.llOnline.setVisibility(View.INVISIBLE);
            mViewHolder.tvStatus.setText("");
            mViewHolder.avatar.setColorFilter(ContextCompat.getColor(context,R.color.colorCommonGray));
        }

        if (people.getUnreadMsg() > 0){
            mViewHolder.tvUnread.setText(people.getUnreadMsg());
        }else {
            mViewHolder.tvUnread.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mPeopleList.size();
    }

    public void refresh(List<People> peoples){
        mPeopleList.clear();
        mPeopleList.addAll(peoples);
        notifyDataSetChanged();
    }


    static class mViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        View peopleView;
        LinearLayout llOnline;
        TextView tvStatus;
        TextView tvUnread;
        TextView tvMsg;

        mViewHolder(@NonNull View itemView) {
            super(itemView);
            peopleView = itemView;
            avatar = itemView.findViewById(R.id.item_avatar);
            name = itemView.findViewById(R.id.item_name);
            llOnline = itemView.findViewById(R.id.item_online_stats);
            tvStatus = itemView.findViewById(R.id.item_status);
            tvUnread = itemView.findViewById(R.id.item_unread);
            tvMsg  = itemView.findViewById(R.id.item_msg);
        }
    }
}
