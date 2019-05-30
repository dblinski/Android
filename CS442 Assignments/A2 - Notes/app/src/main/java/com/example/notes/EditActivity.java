package com.example.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

public class EditActivity extends AppCompatActivity
        implements Serializable {

    private EditText title, content;
    private Note n;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        title = (EditText) findViewById(R.id.editTitle);
        content = (EditText) findViewById(R.id.editContent);
        if(intent.hasExtra("NOTE_IDENTIFIER")){
            n = (Note) intent.getSerializableExtra("NOTE_IDENTIFIER");
            pos = intent.getIntExtra("NOTE_POSITION", 0);
            title.setText(n.getTitle());
            content.setText(n.getContent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.save_note){
            saveNote(null);
        }
        return true;
    }

    public void saveNote(View v){
        if(n != null) {
            if (title.getText().toString().equals(n.getTitle()) && content.getText().toString().equals(n.getContent())) {
                finish();
            }
        }
        if(title.getText().toString().matches("")){
            Toast.makeText(this,"Your note was missing a title and was not saved",Toast.LENGTH_LONG).show();
            finish();
        }
        Intent data = new Intent();
        Note updatedNote = new Note(title.getText().toString(), Note.makeDate(), content.getText().toString());
        data.putExtra("OLD_NOTE_POSITION", pos);
        data.putExtra("NEW_NOTE", updatedNote);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your note is not saved!");
        builder.setMessage("Save note '" + title.getText() + "'?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveNote(null);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
