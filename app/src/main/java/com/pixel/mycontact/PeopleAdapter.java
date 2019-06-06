package com.pixel.mycontact;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixel.mycontact.beans.People;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.mViewHolder> {

    private List<People> mPeopleList;
    private List<People> checkedPeople = new ArrayList<>();

    PeopleAdapter(List<People> peopleList) {
        mPeopleList = peopleList;
    }

    public List<People> getCheckedPeople() {
        return checkedPeople;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_inlist, viewGroup, false);
        final mViewHolder holder = new mViewHolder(view);
        //设置item的点击事件，获取当前item对应people对象，并通过Intent序列化（Serialize）传入ContactDetailActivity
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
        mViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            /**
             * WIP * WIP * WIP
             * 实现点击头像改变的效果，实现CheckBox的功能
             * 点击头像可以添加到列表，但是显示不正常
             * “尚未完成'
             */
            @Override
            public void onClick(View v) {
                if (!people.getChecked()) {
                    mViewHolder.avatar.setImageResource(R.drawable.ic_done_black_24dp);
                    people.setChecked(true);
                    checkedPeople.add(people);
                } else {
                    mViewHolder.avatar.setImageResource(R.drawable.ic_account_circle_black_24dp);
                    people.setChecked(false);
                    checkedPeople.remove(people);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPeopleList.size();
    }

    static class mViewHolder extends RecyclerView.ViewHolder {
        //list 的item包含头像和姓名
        ImageView avatar;
        TextView name;

        View peopleView;

        mViewHolder(@NonNull View itemView) {
            super(itemView);
            peopleView = itemView;
            avatar = itemView.findViewById(R.id.item_avatar);
            name = itemView.findViewById(R.id.item_name);
        }
    }
}
