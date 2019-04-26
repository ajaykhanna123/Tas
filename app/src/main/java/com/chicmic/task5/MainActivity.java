package com.chicmic.task5;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private FloatingActionButton addContact;
    static boolean isInActionMode = false;
    public RecyclerContactsAdapter contactsAdapter;
    public boolean isConfirmSwipe;
    SwipeToDeleteCallback swipeToDeleteCallback;
    ArrayList<Contact> selectedList = new ArrayList<>();
    RecyclerView contactList;
    AddContactActivity addContactActivity;
    LinearLayout constraintLayout;
    TextView counterTextView;
    Toolbar toolbar;
    int counter = 0;
    ArrayList<Contact> contacts = new ArrayList<>(0);
    public boolean isConfirmDelete = false;
    AlertDialog.Builder alert;
    AlertDialog alertDialog;
    RecyclerContactsAdapter recyclerContactsAdapter;

    public boolean isConfirmDelete() {
        return isConfirmDelete;
    }

    public void setConfirmDelete(boolean confirmDelete) {
        isConfirmDelete = confirmDelete;
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
            contactsAdapter = new RecyclerContactsAdapter(contacts, this, R.layout.item_contact);
            contactList.setHasFixedSize(true);
            contactList.setLayoutManager(new LinearLayoutManager(this));
            contactList.setAdapter(contactsAdapter);


        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addContact = findViewById(R.id.add_contact);
        contactList = findViewById(R.id.listContacts);
        addContactActivity = new AddContactActivity();
        constraintLayout = findViewById(R.id.coordinator_layout);
        toolbar = findViewById(R.id.app_toolbar);


        setSupportActionBar(toolbar);


        counter = 0;
        contacts = Utilities.getAllSavedContacts(this);

        counterTextView = findViewById(R.id.counter_tetx_view);
        counterTextView.setVisibility(View.GONE);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddContactActivity();
                clearActionMode();

            }
        });


        enableSwipeToDeleteAndUndo();


    }

    public void setToolbarVisible() {
        toolbar.setVisibility(View.VISIBLE);
    }

    public void setToolbarInvisible() {
        toolbar.setVisibility(View.GONE);
    }

    public void updateCounter(int counter) {
        if (counter == 0) {

            counterTextView.setText("0 item Selected");

        } else {
            counterTextView.setText(counter + " items selected");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void prepareSelection(View view, int position) {
        setToolbarVisible();


        if (((CheckBox) view).isChecked()) {
            selectedList.add(contacts.get(position));
            counter = counter + 1;
            updateCounter(counter);
        } else {
            selectedList.remove(contacts.get(position));
            counter = counter - 1;


            updateCounter(counter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            recyclerContactsAdapter = (RecyclerContactsAdapter) contactsAdapter;
            if (selectedList.isEmpty()) {
                Toast.makeText(this, " Do Select some items to delete", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            isInActionMode = false;

            //deleteDialogMultiple(selectedList);
            recyclerContactsAdapter.updateAdapter(selectedList);
            clearActionMode();

        }
        if (item.getItemId() == R.id.menu_clear) {
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    public void clearActionMode() {
        isInActionMode = false;
        toolbar.getMenu().clear();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        counterTextView.setVisibility(View.GONE);

        counterTextView.setText("0 item Selected");
        counter = 0;
        selectedList.clear();
        setToolbarInvisible();
    }

    @Override
    public void onBackPressed() {
        enableSwipeToDeleteAndUndo();
        if (isInActionMode) {

            clearActionMode();
            contactsAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }

    }


    private void enableSwipeToDeleteAndUndo() {


        swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                clearActionMode();
                final int position = viewHolder.getAdapterPosition();
                final Contact item = contactsAdapter.getData().get(position);
                deleteDialog(item, position);

                contactsAdapter.removeItem(position);
                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);


            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                if (counterTextView.getVisibility() == View.GONE) {
                    return true;
                }
                return false;
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

    public void deleteDialogMultiple(final Contact item, final int position) {

        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }

        alert = new AlertDialog.Builder(this);
        alertDialog = alert.create();


        alert.setTitle("Delete Note")
                .setMessage("Are you sure you want to delete")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

//                            setConfirmDelete(true);
//                            recyclerContactsAdapter.updateAdapter();
                        deleteFromFile(item);


                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contactsAdapter.restoreItem(item, position);
                        contactList.scrollToPosition(position);
                        clearActionMode();
                    }
                })
                .setCancelable(false);

        alert.show();
    }








}





