package com.example.sentinel.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sentinel.Model.MessageModel;
import com.example.sentinel.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    List<MessageModel> messageModelList;

    public MessageAdapter(List<MessageModel> messageModelList) {
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel message = messageModelList.get(position);

        if (message.getSentBy().equals(MessageModel.SENT_BY_ME)) {
            holder.top_chat_layout.setVisibility(View.VISIBLE);
            holder.top_text.setText(message.getMessage());
            holder.bottom_chat_layout.setVisibility(View.GONE);
            holder.username.setText("Me");
            holder.sener_image.setImageResource(R.drawable.ic_person);
        } else {
            holder.top_chat_layout.setVisibility(View.GONE);
            holder.bottom_text.setText(message.getMessage());  // Fixed typo here
            holder.bottom_chat_layout.setVisibility(View.VISIBLE);
            holder.username.setText("BayMax");
            holder.sener_image.setImageResource(R.drawable.bbmax);
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout top_chat_layout, bottom_chat_layout;
        TextView top_text, bottom_text, username;  // Fixed typo here
        ImageView sener_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            top_chat_layout = itemView.findViewById(R.id.top_chatview);
            bottom_chat_layout = itemView.findViewById(R.id.bottom_chatview);
            top_text = itemView.findViewById(R.id.top_text);
            bottom_text = itemView.findViewById(R.id.bottom_text);  // Fixed typo here
            username = itemView.findViewById(R.id.txt_name);
            sener_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
