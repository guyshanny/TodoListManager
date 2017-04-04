package com.example.guy.ex2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.guy.ex2.actions.CallAction;
import com.example.guy.ex2.actions.CancelAction;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * The MainActivity class of our program
 * It extends the AppComat activity
 * Also, it implements the 'AddReminderDialog.AddItemReminderDialogListener'
 * as a contract with 'AddReminderDialog' about handling a new reminder
 */
public class MainActivity extends AppCompatActivity implements AddReminderDialog.AddItemReminderDialogListener {

    private ArrayList<Job> _jobs;
    private ArrayAdapter<String> _jobsAdapter;
    private EditText _jobDescription;

    /**
     * This function handles saving the current state of the program
     * (it is called after 'onDestory' is being called. E.g after changing orientation
     * @param outState the current state (handle to a bulk of memory to save in)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("_jobs", _jobs);
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
            _jobs = new ArrayList<Job>();
        }
        else
        {
            _jobs = savedInstanceState.getParcelableArrayList("_jobs");
        }

        // Job description handle
        this._jobDescription = (EditText)findViewById(R.id.itemText);

        // Sets the button event listener
        final Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick(View v)
            {
                // Show add item's dialog
                FragmentManager fm = getSupportFragmentManager();
                AddReminderDialog addItemDialog = AddReminderDialog.newInstance("Add item's reminder");
                addItemDialog.show(fm, "add_reminder_dialog");
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

                        ArrayList<Job> updatedList = new ArrayList<Job>();
                        for (int i = 0 ; i < lv.getCount() ; i++)
                        {
                            if (!(checkedItems.get(i))) // This item has to stay
                            {
                                updatedList.add(MainActivity.this._jobs.get(i));
                            }
                        }
                        MainActivity.this.updateJobs(updatedList);

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

        // Sets single short click listener - single-choice AlertDialog
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * {@inheritDoc}
             *
             * Note it gets the clicked job and show it's
             * reminder's dialog using it's reminder's actions
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Job jobClicked = MainActivity.this._jobs.get(position);

                if (null == jobClicked.getJobReminder())
                {
                    MainActivity.this.notifyMessage("This job doesn't have a reminder");
                    return;
                }

                ReminderOptionsDialog reminderDialog = ReminderOptionsDialog.newInstance(jobClicked, MainActivity.this);
                reminderDialog.show(getSupportFragmentManager(), "reminderDialog");
            }
        });
    }

    /**
     * Handles populating the jobs ListView (builds an adapter between the data source
     * and the view)
     */
    private void _populateJobsListView()
    {
        String[] items = new String[this._jobs.size()];
        for (int i = 0 ; i < this._jobs.size() ; i++)
        {
            items[i] = this._jobs.get(i).getJobDescription() + "           " + this._jobs.get(i).getJobReminderDate();
        }

        // Build Adapter
        this._jobsAdapter = new ArrayAdapter<String>(
                this,                       // Context for the activity
                R.layout.jobs_list,         // Layout to use (create)
                items                       // Items to be displayed
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFinishAddItemDialog(@Nullable String reminderDescription, @Nullable Calendar reminderDate, Boolean isAddReminder)
    {
        // Dismiss adding job
        if (null == reminderDescription)
        {
            return;
        }

        Job job;
        if (isAddReminder)
        {
            job = new Job(this._jobDescription.getText().toString(), new Job.Reminder(reminderDescription, reminderDate));

            // Check if needs to add 'call' action
            try {
                // Splitting "call 97250391827" to "call" & "97250391827"
                String[] splittedJobDesc = reminderDescription.split(" ");

                if ((splittedJobDesc.length > 1) && (splittedJobDesc[0].toLowerCase().equals(getString(R.string.call_action_name).toLowerCase())))
                {
                    job.getJobReminder().addAction(new CallAction(getString(R.string.call_action_name), splittedJobDesc[1]));
                }
            }
            catch (Exception e)
            {
                MainActivity.this.notifyMessage("couldn't extract reminder name");
            }
        }
        else
        {
            job = new Job(this._jobDescription.getText().toString(), null);
        }

        // Add default cancel action
        job.getJobReminder().addAction(new CancelAction(getString(R.string.cancel_action_name)));

        // Update internals
        this._jobs.add(job);

        // Update list view
        this.updateJobs(null);
    }

    /**
     * Updates & refreshes the job's ListView
     * @param newJobs list of new jobs, or null if only needs to be refreshed
     */
    public void updateJobs(@Nullable ArrayList<Job> newJobs)
    {
        this._jobs = null != newJobs ? newJobs : this._jobs;
        this._populateJobsListView();
    }

    /**
     * Notify the user for an action
     * It notify using a small ellipse message box
     *
     * @param message the message to notify
     */
    public void notifyMessage(String message)
    {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}