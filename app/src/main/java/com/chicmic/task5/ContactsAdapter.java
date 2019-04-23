package com.chicmic.task5;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class ContactsAdapter extends ArrayAdapter<Contact> {


    public ContactsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contact> notes) {
        super(context, resource, notes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_contact, null);
        }

        Contact note = getItem(position);

        if(note != null)
        {
            TextView userName =  convertView.findViewById(R.id.txtUserName);
            TextView userPhone =  convertView.findViewById(R.id.txtUserPhone);
            ImageView userImage = convertView.findViewById(R.id.displayUserImage);

            userName.setText(note.getName());
            userPhone.setText(note.getPhnNo());

        }
        return convertView;
    }
}

