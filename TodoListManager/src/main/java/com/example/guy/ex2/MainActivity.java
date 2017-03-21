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


/**
 * The MainActivity class of our program
 */
public class MainActivity extends AppCompatActivity {

    private ArrayList<String> _jobs;
    private ArrayAdapter<String> _jobsAdapter;

    /**
     * This function handles saving the current state of the program
     * (it is called after 'onDestory' is being called. E.g after changing orientation
     * @param outState the current state (handle to a bulk of memory to save in)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("_jobs", _jobs);
    }

    /**
     * This function handles the activity creating (in the activity pipeline)
     * @param savedInstanceState the instance of saved bundle (see 'onSaveInstanceState')
     */
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
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL /*for multiple choice ListView*/);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_main, menu);

                return true;
            }

            /**
             * {@inheritDoc}
             *
             * Note we don't need to use it because our menu don't enter invalidated mode
             */
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId())
                {
                    case R.id.menu_delete:
                    {
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

            /**
             * {@inheritDoc}
             *
             * Note we don't have things to do or clean when the action menu is destroyed
             */
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB

                /*
                 * Our calculations is position-firstVisiblePosition because the 'position'
                 * param is the position in the dataAdapter (i.e the index in _jobs)
                 * while, the getChildAt gets the child at index i while the counting starts
                 * from the current visible index (not 0, because we've rolled down), so we'll
                 * get a null ptr which leads to nullptr reference exception.
                 */
                View rowView = lv.getChildAt(position - lv.getFirstVisiblePosition());
                if (checked)
                {
                    // Highlight background
                    rowView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.checkedItemsBackground));
                }
                else
                {
                    // If de-selected, restore original background
                    rowView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
    }

    /**
     * Handles populating the jobs ListView (builds an adapter between the data source
     * and the view)
     */
    private void _populateJobsListView()
    {
        // Build Adapter
        this._jobsAdapter = new ArrayAdapter<String>(
                this,                       // Context for the activity
                R.layout.jobs_list,         // Layout to use (create)
                _jobs                       // Items to be displayed
        ) {
            /**
             * {@inheritDoc}
             *
             * Note we override this function to be able to change the ListView's text's color
             */
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
