package com.chicmic.task5;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class RecyclerContactsAdapter extends RecyclerView.Adapter<RecyclerContactsAdapter.MyViewHolder> {

    List<Contact> contacts;
    Context context;
    int resource;


    public RecyclerContactsAdapter(ArrayList<Contact> contacts, Context context, int resource) {
        this.contacts = contacts;
        this.context = context;
        this.resource = resource;


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
        MyViewHolder viewHolder = new MyViewHolder(listItem, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final Contact contact = contacts.get(position);
        if (contact != null) {
            viewHolder.txtUserName.setText(contact.getName());
            viewHolder.txtUserPhn.setText(contact.getPhnNo());

            if (contact.getImageId() != null) {
                BitmapToString.convertStringToBitmap(contact.getImageId(), viewHolder.displayImage);
            }
        }
        if (!MainActivity.isInActionMode) {
            viewHolder.checkItemSelected.setVisibility(View.GONE);
            contacts.get(position).setSelected(false);
            //viewHolder.checkItemSelected.setChecked();

        }
        else {
            viewHolder.checkItemSelected.setVisibility(View.VISIBLE);
           //viewHolder.checkItemSelected.setChecked(false);
        }


        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!MainActivity.isInActionMode) {
                    String filename = ((Contact) contacts.get(position)).getTime()
                            + Utilities.FILE_EXTENSION;
                    // Toast.makeText(MainActivity.this,filename,Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, AddContactActivity.class);
                    intent.putExtra(Utilities.EXTRAS_NOTE_FILENAME, filename);
                    context.startActivity(intent);
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.clearActionMode();
                }
            }
        });
        viewHolder.checkItemSelected.setChecked(contacts.get(position).isSelected());
        viewHolder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.toolbar.getMenu().clear();
                mainActivity.toolbar.inflateMenu(R.menu.menu_multi_select);
                mainActivity.counterTextView.setVisibility(View.VISIBLE);
                mainActivity.isInActionMode = true;
                mainActivity.contactsAdapter.notifyDataSetChanged();
                mainActivity.setToolbarVisible();
               contacts.get(position).setSelected(true);


                mainActivity.selectedList.add(contacts.get(position));
                mainActivity.counter++;
                mainActivity.updateCounter(mainActivity.counter);



                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return contacts.size();

    }


//    public void updateAdapter() {
//        final MainActivity mainActivity = (MainActivity) context;
//
//
//        for (Contact contact : contacts) {
//
//           // mainActivity.deleteDialogMultiple(contact, contacts.indexOf(contact));
//        }
//
//        if (mainActivity.isConfirmDelete()) {
//            for (Contact contact : contacts) {
//
//                contacts.remove(contact);
//                notifyDataSetChanged();
//            }
//
//
//        }
//
//    }
    public void updateAdapter(ArrayList<Contact> arrayListContacts) {
        final MainActivity mainActivity = (MainActivity) context;


//        for (Contact contact : list) {
//            contacts.remove(contact);
//            notifyDataSetChanged();
//            mainActivity.deleteDialogMultiple(contact, list.indexOf(contact));
//        }

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(context,mainActivity.isConfirmDelete+" ",Toast.LENGTH_SHORT).show();
//                if(mainActivity.isConfirmDelete())
//                {
                    mainActivity.deleteDialogMultiple(arrayListContacts);
                    for (Contact contact:arrayListContacts)
                    {
                        contacts.remove(contact);
                        notifyDataSetChanged();
                    }
                    mainActivity.setConfirmDelete(false);
               // }
           // }
       // },4000L);





//        if(mainActivity.isConfirmDelete())
//        {
//            for (Contact contact : list) {
//
//
//            }
//
//
//        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtUserName;
        TextView txtUserPhn;
        ImageView displayImage;
        ConstraintLayout constraintLayout;
        CheckBox checkItemSelected;
        MainActivity mainActivity;


        public MyViewHolder(@NonNull View convertView, Context mainActivity) {
            super(convertView);
            txtUserName = convertView.findViewById(R.id.txtUserName);
            txtUserPhn = convertView.findViewById(R.id.txtUserPhone);
            displayImage = convertView.findViewById(R.id.displayUserImage);
            constraintLayout = convertView.findViewById(R.id.item_contact_layout);
            checkItemSelected = convertView.findViewById(R.id.checkItemSelected);
            checkItemSelected.setVisibility(View.GONE);

            //constraintLayout.setOnLongClickListener((View.OnLongClickListener) mainActivity);
            this.mainActivity = (MainActivity) mainActivity;
            checkItemSelected.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {
            mainActivity.prepareSelection(v, getAdapterPosition());

        }
    }

}
