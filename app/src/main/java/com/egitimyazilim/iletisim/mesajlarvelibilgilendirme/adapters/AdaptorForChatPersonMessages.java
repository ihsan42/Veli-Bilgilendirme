package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Chats;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdaptorForChatPersonMessages extends RecyclerView.Adapter<AdaptorForChatPersonMessages.ViewHolder>{
    private static final String TAG = "MessageAdapter";

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    String number;
    String sms;

    private Context mContext;
    private List<Chats> mChat;

    public AdaptorForChatPersonMessages(Context mContext, List<Chats> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public AdaptorForChatPersonMessages.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,viewGroup, false);
            return new AdaptorForChatPersonMessages.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,viewGroup,false);
            return new AdaptorForChatPersonMessages.ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Chats chats = mChat.get(i);
        viewHolder.show_message.setText(chats.getMessage());

        long longtime =chats.getTime()/1000;
//        long senttime=chats.getSent_time()/1000;
        // Date sentdate=new Date(TimeUnit.SECONDS.toMillis(senttime));
        Date date= new Date(TimeUnit.SECONDS.toMillis(longtime));
        SimpleDateFormat df2 = new SimpleDateFormat("kk:mm:ss   dd.MM.yyyy");
        String tarih=df2.format(date).toString();
        //String sentTarih=df2.format(sentdate).toString();
        viewHolder.show_date.setText(tarih);
        // viewHolder.show_sent_date.setText(sentTarih);

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public TextView show_date;
        // public TextView show_sent_date;

        public ViewHolder(View itemView){

            super(itemView);

            show_message = itemView.findViewById(R.id.msg_show);
            show_date = itemView.findViewById(R.id.textViewDate);
            //show_sent_date = itemView.findViewById(R.id.textViewSentDate);
        }
    }


    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    public void senderNo(String num){

        number =num;
    }

    public void sentData(String text_msg){

        sms = text_msg;
    }

    @Override
    public int getItemViewType(int position) {

        if (mChat.get(position).getFolder_name().equals("inbox")){

            return MSG_TYPE_LEFT;
        }else {
            return MSG_TYPE_RIGHT;
        }
    }
}
