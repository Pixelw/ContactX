package com.pixel.mycontact;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pixel.mycontact.beans.DetailList;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.mViewHolder> {

    private List<People> mPeopleList;

    public List<DetailList> getDetails() {
        return details;
    }

    private List<DetailList> details;

    static class mViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;

        View peopleView;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            peopleView = itemView;
            avatar = itemView.findViewById(R.id.item_avatar);
            name = itemView.findViewById(R.id.item_name);
        }
    }

    public PeopleAdapter(List<People> peopleList) {
        mPeopleList = peopleList;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_inlist, viewGroup, false);
        final mViewHolder holder = new mViewHolder(view);
        holder.peopleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                People people = mPeopleList.get(position);
                Intent intent = new Intent(v.getContext(), ContactDetailActivity.class);
                intent.putExtra("people",people);
                v.getContext().startActivity(intent);

                //Toast.makeText(v.getContext(), people.getName() + " " + people.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                People people = mPeopleList.get(position);
                Toast.makeText(v.getContext(), people.getNumber1() + " " + people.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder mViewHolder, int i) {
        People people = mPeopleList.get(i);
        mViewHolder.name.setText(people.getName());
    }


    @Override
    public int getItemCount() {
        return mPeopleList.size();
    }
}
