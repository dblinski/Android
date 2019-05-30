package com.example.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener, Serializable {

    private static final int EDIT_REQUEST_CODE = 1;
    private static final int NEW_NOTE_REQUEST_CODE = 2;
    private List<Note> mainNotesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesAdapter nAdapter;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Note> temp = loadFile();
        if(temp != null){
            mainNotesList.addAll(temp);
        }
        recyclerView = findViewById(R.id.recycler);
        nAdapter = new NotesAdapter(mainNotesList,this);
        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setTitle("Notes ("+mainNotesList.size()+")");
    }
    @Override
    protected void onPause(){
        saveFile();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.add_note:
                intent = new Intent(MainActivity.this, EditActivity.class);
                this.startActivityForResult(intent, NEW_NOTE_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Note n = mainNotesList.get(pos);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("NOTE_IDENTIFIER", n); //send note object to Edit Activity
        intent.putExtra("NOTE_POSITION", pos); //send note position to Edit Activity in order to send back after
        this.startActivityForResult(intent, EDIT_REQUEST_CODE);
    }
    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        final Note n = mainNotesList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note '"+n.getTitle()+"'?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainNotesList.remove(n);
                listUpdated();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == EDIT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Note updated = (Note) data.getSerializableExtra("NEW_NOTE");
                int oldPosition = data.getIntExtra("OLD_NOTE_POSITION", -1);
                mainNotesList.remove(oldPosition);
                mainNotesList.add(0, updated);
                listUpdated();
            }
        }
        else if(requestCode == NEW_NOTE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Note newNote = (Note) data.getSerializableExtra("NEW_NOTE");
                mainNotesList.add(0,newNote);
                listUpdated();
            }
        }
    }

    public void listUpdated(){
        setTitle("Notes ("+mainNotesList.size()+")");
        nAdapter.notifyDataSetChanged();
    }

    private List<Note> loadFile(){
        List<Note> tempNoteList = new ArrayList<Note>();
        try{
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            JSONArray jsonArray = new JSONArray(sb.toString());
            JSONObject tempObject;
            String title, date, content;
            for(int i = 0; i < jsonArray.length(); i++){
                tempObject = jsonArray.getJSONObject(i);
                title = tempObject.getString("title");
                date = tempObject.getString("date");
                content = tempObject.getString("content");
                tempNoteList.add(new Note(title, date, content));
            }
        }
        catch(FileNotFoundException e){
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempNoteList;
    }
    private void saveFile(){
        try{
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
            writer.setIndent("  ");
            writer.beginArray();
            for(int i = 0; i < mainNotesList.size(); i++){
                writer.beginObject();
                writer.name("title").value(mainNotesList.get(i).getTitle());
                writer.name("date").value(mainNotesList.get(i).getDate());
                writer.name("content").value(mainNotesList.get(i).getContent());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}