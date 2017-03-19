package com.example.guy.ex2;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> _jobs;
    private ArrayAdapter<String> _jobsAdapter;
    private ActionMode _actionMode;

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("_jobs", _jobs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if first run or need to reconstruct data (e.g on orientation change)
        if (null == savedInstanceState)
        {
            _jobs = new ArrayList<String>();
        }
        else
        {
            _jobs = savedInstanceState.getStringArrayList("_jobs");
        }

        // Sets the button event listener
        final Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final EditText jobText = (EditText) findViewById(R.id.jobText);

                // Update internals
                MainActivity.this._jobs.add(jobText.getText().toString());

                // Update list view
                _jobsAdapter.notifyDataSetChanged();
            }
        });

        // Populate ListView data source
        _populateJobsListView();
        final ListView lv = (ListView)findViewById(R.id.todoList);

        /*
         * Sets the ListView long click's event listener
         * and registering a context menu (with delete button)
         */
        registerForContextMenu(lv);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //MainActivity.this._jobsAdapter.setsele


                MainActivity.this._jobsAdapter.remove(MainActivity.this._jobs.get(position));
                MainActivity.this._jobsAdapter.notifyDataSetChanged();

                return true;
            }
        });
    }


    private void _populateJobsListView()
    {
//        String[] items = this._jobs.toArray(new String[this._jobs.size()]);

        // Build Adapter
        this._jobsAdapter = new ArrayAdapter<String>(
                this,                       // Context for the activity
                R.layout.jobs_list,         // Layout to use (create)
                _jobs                       // Items to be displayed
        ) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent)
            {
                TextView textView = (TextView)super.getView(position, convertView, parent);

                // Setting alternating color
                textView.setTextColor((0 == position % 2) ? Color.RED : Color.BLUE);
                return textView;
            }
        };

        // Configure the list view
        ListView lv = (ListView)findViewById(R.id.todoList);
        lv.setAdapter(this._jobsAdapter);
    }
}
