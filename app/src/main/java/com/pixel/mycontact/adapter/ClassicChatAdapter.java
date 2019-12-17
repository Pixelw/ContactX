package com.pixel.mycontact.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixel.mycontact.R;
import com.pixel.mycontact.beans.IMMessage;

import java.util.List;

/**
 * @author Carl Su
 * @date 2019/12/14
 */
public class ClassicChatAdapter extends RecyclerView.Adapter<ClassicChatAdapter.mViewHolder> {

    private List<IMMessage> chatList;

    public ClassicChatAdapter(List<IMMessage> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation_classic, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        IMMessage imMessage = chatList.get(position);
        holder.userName.setText(imMessage.getMsgUser());
        holder.msgBody.setText(imMessage.getMsgBody());
        holder.time.setText(imMessage.getSimpleTime());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    //自定adapter操作

    /**
     * 添加消息条目
     *
     * @param imMessage 传入消息对象
     * @return 添加到的位置
     */
    public int add(IMMessage imMessage) {
        chatList.add(imMessage);
        int position = chatList.indexOf(imMessage);
        notifyItemInserted(position);
        //
        notifyItemRangeChanged(position, chatList.size());
        return position;
    }

    public void remove(int position) {
        chatList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, chatList.size());
    }


    static class mViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView time;
        TextView msgBody;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.msgUser);
            time = itemView.findViewById(R.id.msgTime);
            msgBody = itemView.findViewById(R.id.msgBody);

        }
    }
}
