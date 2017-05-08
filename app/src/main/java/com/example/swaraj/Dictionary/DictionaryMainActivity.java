package com.example.swaraj.Dictionary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Handler;


public class DictionaryMainActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    public static ArrayList<DictObjectModel> data;
    DatabaseHelper db ;
    ProgressBar loader;

   public static ArrayList<String> wordcombimelist;
    ArrayList<String> meancombimelist;
    LinkedHashMap<String,String> namelist;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        loader=(ProgressBar)findViewById(R.id.loader);
        recyclerView.setHasFixedSize(true);
        db= new DatabaseHelper(this);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0,new Intent(this,MyService.class)  , 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY ,pendingIntent);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search Here");
        searchView.setQueryRefinementEnabled(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<DictObjectModel>();
       new Filldata().execute();


        // fetchData();





        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return  false; }

            @Override
            public boolean onQueryTextChange(String newText) {


                newText = newText.toLowerCase();

                final ArrayList<DictObjectModel> filteredList = new ArrayList<DictObjectModel>();

                for (int i = 0; i < wordcombimelist.size(); i++) {

                    final String text = wordcombimelist.get(i).toLowerCase();
                    if (text.contains(newText)) {

                        filteredList.add(new DictObjectModel(wordcombimelist.get(i),meancombimelist.get(i)));
                    }
                }
                adapter = new CustomAdapter(filteredList);
                recyclerView.setAdapter(adapter);


                return true;
            }
        });

       //
        // startService(new Intent(getApplicationContext(),MyService.class));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.settings:
                startActivity(new Intent(this,SettingsActivity.class));
                return true;
            case R.id.quiz:
                startActivity(new Intent(this, QuizActivty.class));
                return true;
             default:
                 return super.onOptionsItemSelected(item);






        }
    }

  /*  public void fetchData()
    {
        db =new DatabaseHelper(this);
        try {

            db.createDataBase();
            db.openDataBase();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        namelist=new LinkedHashMap<>();
        int ii;
        SQLiteDatabase sd = db.getReadableDatabase();
        Cursor cursor = sd.query("Dictionary1" ,null, null, null, null, null, null);
        ii=cursor.getColumnIndex("word");
        wordcombimelist=new ArrayList<String>();
        meancombimelist= new ArrayList<String>();
        while (cursor.moveToNext()){
            namelist.put(cursor.getString(ii), cursor.getString(cursor.getColumnIndex("definition")));
        }
        Iterator entries = namelist.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            wordcombimelist.add(String.valueOf(thisEntry.getKey()));
            meancombimelist.add("- "+String.valueOf(thisEntry.getValue()));
        }

        for (int i = 0; i < wordcombimelist.size(); i++) {
          data.add(new DictObjectModel(wordcombimelist.get(i), meancombimelist.get(i)));
          }
        adapter = new CustomAdapter(data);
        recyclerView.setAdapter(adapter);
    }*/
    class Filldata extends AsyncTask
    {private ProgressDialog pdia;
        @Override
        protected void onPreExecute() {
           // loader.setVisibility(ProgressBar.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            db =new DatabaseHelper(getApplicationContext());
            try {

                db.createDataBase();
                db.openDataBase();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            namelist=new LinkedHashMap<>();
            int ii;
            SQLiteDatabase sd = db.getReadableDatabase();
            Cursor cursor = sd.query("Dictionary1" ,null, null, null, null, null, null);
            ii=cursor.getColumnIndex("word");
            wordcombimelist=new ArrayList<String>();
            meancombimelist= new ArrayList<String>();
            while (cursor.moveToNext()){
                namelist.put(cursor.getString(ii), cursor.getString(cursor.getColumnIndex("definition")));
            }
            Iterator entries = namelist.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry thisEntry = (Map.Entry) entries.next();
                wordcombimelist.add(String.valueOf(thisEntry.getKey()));
                meancombimelist.add("- "+String.valueOf(thisEntry.getValue()));
            }

            for (int i = 0; i < wordcombimelist.size(); i++) {
                data.add(new DictObjectModel(wordcombimelist.get(i), meancombimelist.get(i)));
            }
            adapter = new CustomAdapter(data);

            return data;
        }

        @Override
        protected void onPostExecute(Object o) {
            recyclerView.setAdapter(adapter);
            new Runnable() {
                @Override
                public void run() {
                    startService(new Intent(getApplicationContext(),MyService.class));
                }
            };
            super.onPostExecute(o);
        }
    }
}
