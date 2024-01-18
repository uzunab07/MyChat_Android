package edu.uncc.hw08;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UsersListAdapter extends ArrayAdapter<User> {

    public UsersListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_row_item, parent, false);
        }

        User user = getItem(position);

        TextView name = convertView.findViewById(R.id.textViewName);
        ImageView online = convertView.findViewById(R.id.imageViewOnline);

        name.setText(user.getName());

        if (user.getOnline() == false){
            online.setVisibility(View.INVISIBLE);
        } else {
            online.setVisibility(View.VISIBLE);
        }

        return convertView;
        //return super.getView(position, convertView, parent);
    }
}
