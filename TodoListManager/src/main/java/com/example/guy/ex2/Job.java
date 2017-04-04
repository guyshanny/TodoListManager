package com.example.guy.ex2;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.example.guy.ex2.actions.Action;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This obj handles a Job in the job list
 */
public class Job implements Parcelable {
    /**
     * Represents a reminder for the job
     */
    public static class Reminder {
        public String description;
        public Calendar date;
        public ArrayList<Action> actions;

        /**
         * C'tor for the reminder
         * @param _description the reminder's description
         * @param _date the reminder's date
         */
        public Reminder(String _description, @Nullable Calendar _date) {
            this.description = _description;
            this.date = _date;
            this.actions = new ArrayList<Action>();
        }

        /**
         * Copy c'tor
         * @param _reminder other obj
         */
        public Reminder(Reminder _reminder) {
            this(_reminder.description, _reminder.date);
        }

        /**
         * Adding action to the reminder's actions list
         * @param action the new action to add
         */
        public void addAction(Action action) { this.actions.add(action); }

        /**
         * Gets reminder's actions' names
         * @return actions' name as a list
         */
        public String[] getActionsNames()
        {
            String[] names = new String[this.actions.size()];
            for (int i = 0 ; i < this.actions.size() ; i++)
            {
                names[i] = this.actions.get(i).getActionName();
            }
            return names;
        }
    }

    // Job description
    private String _jobDescription;
    // Job reminder obj
    private Reminder _reminder;

    /**
     * Job's c'tor
     */
    public Job() {}

    /**
     * Job c'tor
     * @param jobDescription job's description
     * @param reminder job's reminder's obj
     */
    public Job(String jobDescription, Reminder reminder)
    {
        this._jobDescription = jobDescription;
        this._reminder = reminder;
    }

    /**
     * Job's serializable c'tor
     * @param in input stream
     */
    private Job(Parcel in)
    {
        this._jobDescription = in.readString();
        int isHasReminder = in.readInt();
        if (1 == isHasReminder)
        {
            Calendar reminderDate = Calendar.getInstance();
            reminderDate.set(Calendar.YEAR, in.readInt());
            reminderDate.set(Calendar.MONTH, in.readInt());
            reminderDate.set(Calendar.DAY_OF_MONTH, in.readInt());
            this._reminder = new Reminder(in.readString(), reminderDate);
        }
        else
        {
            this._reminder = null;
        }
    }

    /**
     * Setter for the job's description
     * @param jobDescription job's description
     */
    public void setJobDescription(String jobDescription)
    {
        this._jobDescription = jobDescription;
    }

    /**
     * Setter for the job's reminder
     * @param reminder the job's reminder
     */
    public void setJobReminder(Reminder reminder)
    {
        this._reminder = reminder;
    }

    /**
     * Getter for job's description
     * @return job's description
     */
    public String getJobDescription()
    {
        return this._jobDescription;
    }

    /**
     * Getter for job's reminder
     * @return job's reminder
     */
    public Reminder getJobReminder()
    {
        return this._reminder;
    }

    /**
     * Getter for job's reminder's date
     * @return job's reminder's date
     */
    public String getJobReminderDate()
    {
        if (null != this._reminder)
        {
            return Integer.toString(this._reminder.date.get(Calendar.YEAR)) + "/" +
                    Integer.toString(this._reminder.date.get(Calendar.MONTH)) + "/" +
                    Integer.toString(this._reminder.date.get(Calendar.DAY_OF_MONTH));

        }
        else
        {
            return "No due date";
        }
    }

    /**
     * Removes the job's reminder
     */
    public void removeReminder()
    {
        this._reminder = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() { return 0; }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this._jobDescription);
        if (null != this._reminder)
        {
            dest.writeInt(1);   // Indicator for writing date (it my be null)
            dest.writeString(this._reminder.description);
            dest.writeInt(this._reminder.date.get(Calendar.YEAR));
            dest.writeInt(this._reminder.date.get(Calendar.MONTH));
            dest.writeInt(this._reminder.date.get(Calendar.DAY_OF_MONTH));
        }
        else
        {
            dest.writeInt(0); // Indicator for not writing date (it my be null)
        }
    }

    /**
     * A creator for the loading operation that is being triggered in onCreate
     * while we reconstruct our internal params
     */
    public static final Parcelable.Creator<Job> CREATOR = new Parcelable.Creator<Job>()
    {
        /**
         * Creates the object from a given stream
         * @param in a handler to a stream we've saved our state in
         * @return a ChatMessage obj filled with the state in 'in'
         */
        public Job createFromParcel(Parcel in)
        {
            return new Job(in);
        }

        /**
         * C'tor for an array of obj. Needed as a part of the Parcelable abstraction
         * @param size of array
         * @return an array of ChatMessage obj
         */
        public Job[] newArray(int size)
        {
            return new Job[size];
        }
    };
}
