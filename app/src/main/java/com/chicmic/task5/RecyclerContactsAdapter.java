package com.chicmic.task5;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class RecyclerContactsAdapter extends RecyclerView.Adapter<RecyclerContactsAdapter.MyViewHolder> {
    public ArrayList<Contact> selected_usersList = new ArrayList<>();
    List<Contact> contacts;
    Context context;
    int resource;

    public RecyclerContactsAdapter(ArrayList<Contact> contacts, Context context
            , int resource, ArrayList<Contact> selectedList) {
        this.contacts = contacts;
        this.context = context;
        this.resource = resource;
        this.selected_usersList = selectedList;
    }

    public void removeItem(int position) {
        contacts.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Contact item, int position) {
        contacts.add(position, item);
        notifyItemInserted(position);
    }

    public List<Contact> getData() {
        return contacts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_contact, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        final Contact contact = contacts.get(position);
        if (contact != null) {
            viewHolder.txtUserName.setText(contact.getName());
            viewHolder.txtUserPhn.setText(contact.getPhnNo());
            if (contact.getImageId() != null) {
                BitmapToString.convertStringToBitmap(contact.getImageId(), viewHolder.displayImage);
            }
        }
        if (selected_usersList.contains(contacts.get(position)))
            viewHolder.constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_selected_state));
        else
            viewHolder.constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_normal_state));

        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = ((Contact) contacts.get(position)).getTime()
                        + Utilities.FILE_EXTENSION;
                // Toast.makeText(MainActivity.this,filename,Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, AddContactActivity.class);
                intent.putExtra(Utilities.EXTRAS_NOTE_FILENAME, filename);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtUserName;
        TextView txtUserPhn;
        ImageView displayImage;
        ConstraintLayout constraintLayout;

        public MyViewHolder(@NonNull View convertView) {
            super(convertView);
            txtUserName = convertView.findViewById(R.id.txtUserName);
            txtUserPhn = convertView.findViewById(R.id.txtUserPhone);
            displayImage = convertView.findViewById(R.id.displayUserImage);
            constraintLayout = convertView.findViewById(R.id.item_contact_layout);
        }
    }

}
