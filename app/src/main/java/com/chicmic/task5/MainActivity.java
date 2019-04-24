package com.chicmic.task5;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

//import com.baoyz.swipemenulistview.SwipeMenuListView;

public class MainActivity extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener {
    private FloatingActionButton addContact;
    RecyclerContactsAdapter contactsAdapter;
    ActionMode mActionMode;
    Menu context_menu;
    boolean isMultiSelect = false;
    ArrayList<Contact> multiselectList = new ArrayList<>();
    AlertDialogHelper alertDialogHelper;
    ArrayList<Contact> contacts;
    AlertDialogHelper.AlertDialogListener alertDialogListener = new AlertDialogHelper.AlertDialogListener() {
        @Override
        public void onPositiveClick(int from) {

            if (from == 1) {
                if (multiselectList.size() > 0) {
                    for (int i = 0; i < multiselectList.size(); i++) {
                        contacts.remove(multiselectList.get(i));
                        deleteFromFile(multiselectList.get(i));
                        contactsAdapter.notifyDataSetChanged();
                    }

                    if (mActionMode != null) {
                        mActionMode.finish();
                    }
                    Toast.makeText(getApplicationContext(), "Delete Click", Toast.LENGTH_SHORT).show();
                }
            } else if (from == 2) {
                if (mActionMode != null) {
                    mActionMode.finish();
                }


            }


        }

        @Override
        public void onNegativeClick(int from) {

        }

        @Override
        public void onNeutralClick(int from) {

        }
    };
    private RecyclerView contactList;
    private AddContactActivity addContactActivity;
    private ConstraintLayout constraintLayout;
    private boolean isUndo;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("",
                            "Delete Contact", "DELETE",
                            "CANCEL", 1, false);
                    return true;
                default:
                    return false;
            }
        }

//

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselectList = new ArrayList<Contact>();
            refreshAdapter();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addContact = findViewById(R.id.add_contact);
        contactList = findViewById(R.id.listContacts);
        addContactActivity = new AddContactActivity();
        constraintLayout = findViewById(R.id.coordinator_layout);

        alertDialogHelper = new AlertDialogHelper(MainActivity.this);


        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddContactActivity();
            }
        });

        enableSwipeToDeleteAndUndo();

    }

    void goToAddContactActivity() {
        Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        contactList.setAdapter(null);


        contacts = Utilities.getAllSavedContacts(this);

        if (contacts == null || contacts.size() == 0) {
            Toast.makeText(this, " No saved Contact", Toast.LENGTH_SHORT).show();
            return;
        } else {
            contactsAdapter = new RecyclerContactsAdapter(contacts, this, R.layout.item_contact
                    , multiselectList);
            contactList.setHasFixedSize(true);
            contactList.setLayoutManager(new LinearLayoutManager(this));
            contactList.setAdapter(contactsAdapter);


        }


        contactList.addOnItemTouchListener(new RecyclerItemClickListener(this,
                contactList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multiSelect(position);
                else
                    Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselectList = new ArrayList<Contact>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multiSelect(position);

            }
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void multiSelect(int position) {
        if (mActionMode != null) {
            if (multiselectList.contains(contacts.get(position)))
                multiselectList.remove(contacts.get(position));
            else
                multiselectList.add(contacts.get(position));

            if (multiselectList.size() > 0)
                mActionMode.setTitle("" + multiselectList.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }

    public void refreshAdapter() {
        contactsAdapter.selected_usersList = multiselectList;
        contactsAdapter.contacts = contacts;
        contactsAdapter.notifyDataSetChanged();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Contact item = contactsAdapter.getData().get(position);

                contactsAdapter.removeItem(position);
                deleteDialog(item, position);

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);


            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(contactList);
    }

    public void deleteFromFile(final Contact contact) {
        Utilities.deleteContact(getApplicationContext(),
                contact.getTime() + Utilities.FILE_EXTENSION);
        Toast.makeText(getApplicationContext(),
                " contact has been deleted", Toast.LENGTH_SHORT).show();
    }

    public void deleteDialog(final Contact item, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Note")
                .setMessage("Are you sure you want to delete ")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFromFile(item);

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contactsAdapter.restoreItem(item, position);
                        contactList.scrollToPosition(position);


                    }
                })
                .setCancelable(false);

        alert.show();
    }

    @Override
    public void onPositiveClick(int from) {

    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
}





