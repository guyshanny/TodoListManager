package com.example.guy.ex2;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> _jobs;
    private ArrayAdapter<String> _jobsAdapter;

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

        // Sets the ListView long click's event listener for multiple choice
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_main, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId())
                {
                    case R.id.menu_delete:
                    {
                        Log.d("TAG1", "deleteSelectedItems");
                        SparseBooleanArray checkedItems = lv.getCheckedItemPositions();

                        if (null == checkedItems)
                        {
                            return false;
                        }

                        ArrayList<String> updatedList = new ArrayList<String>();
                        for (int i = 0 ; i < lv.getCount() ; i++)
                        {
                            if (!(checkedItems.get(i))) // This item has to stay
                            {
                                updatedList.add(MainActivity.this._jobs.get(i));
                            }
                        }

                        MainActivity.this._jobsAdapter.clear();
                        MainActivity.this._jobsAdapter.addAll(updatedList);

                        // Repeat initial background color
                        for (int i = 0 ; i < lv.getChildCount() ; i++)
                        {
                            View itemView = lv.getChildAt(i);
                            itemView.setBackgroundColor(Color.TRANSPARENT);
                        }

                        mode.finish();  // Action picked, so close the CAB
                        return true;
                    }
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB

                View rowView = lv.getChildAt(position);
                if (checked)
                {
                    rowView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.checkedItemsBackground));
                }
                else
                {
                    rowView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
    }

    private void _populateJobsListView()
    {
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
