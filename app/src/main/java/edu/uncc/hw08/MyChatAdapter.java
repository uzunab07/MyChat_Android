package edu.uncc.hw08;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;

public class MyChatAdapter extends ArrayAdapter<Chat> {
    public MyChatAdapter(@NonNull Context context, int resource, @NonNull List<Chat> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_chats_list_item, parent, false);
        }

        Chat chat = getItem(position);

        TextView recipient = convertView.findViewById(R.id.textViewMsgBy);
        TextView messageMain = convertView.findViewById(R.id.textViewMsgText);
        TextView date = convertView.findViewById(R.id.textViewMsgOn);

        recipient.setText(chat.getName());
        messageMain.setText(chat.getMessage());
        if(chat.getTime() != null) {
            date.setText(chat.getTime().toDate().toString());
        }
        return convertView;
        //return super.getView(position, convertView, parent);
    }
}
