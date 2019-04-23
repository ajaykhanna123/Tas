package com.chicmic.task5;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton addContact;
    private ListView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addContact=findViewById(R.id.add_contact);
        contactList=findViewById(R.id.listContacts);
        Toast.makeText(this, System.currentTimeMillis()+"", Toast.LENGTH_SHORT).show();

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddContactActivity();
            }
        });

    }
    void goToAddContactActivity()
    {
        Intent intent=new Intent(MainActivity.this,AddContactActivity.class);
        startActivity(intent);

    }
    @Override
    protected void onResume() {
        super.onResume();
        contactList.setAdapter(null);

        ArrayList<Contact> notes = Utilities.getAllSavedContacts(this);

        if(notes == null || notes.size() == 0)
        {
            Toast.makeText(this, " No saved Contact", Toast.LENGTH_SHORT).show();
            return;
        }else
        {
            ContactsAdapter contactsAdapter = new ContactsAdapter(this, R.layout.item_contact, notes);
            contactList.setAdapter(contactsAdapter);
            contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String filename = ((Contact)contactList.getItemAtPosition(i)).getTime()
                            + Utilities.FILE_EXTENSION;
                    Toast.makeText(MainActivity.this,filename,Toast.LENGTH_SHORT).show();
                    Intent viewNoteIntent = new Intent(getApplicationContext(), AddContactActivity.class);
                    viewNoteIntent.putExtra(Utilities.EXTRAS_NOTE_FILENAME, filename);
                    startActivity(viewNoteIntent);
                }
            });
        }
    }




}
