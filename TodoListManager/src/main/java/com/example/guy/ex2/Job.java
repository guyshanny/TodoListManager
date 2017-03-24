package com.example.guy.ex2;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntegerRes;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

public class Job implements Parcelable {
    public static class Reminder {
        public String description;
        public Calendar date;

        public Reminder(String _description, @Nullable Calendar _date) {
            this.description = _description;
            this.date = _date;
        }

        public Reminder(Reminder _reminder) {
            this(_reminder.description, _reminder.date);
        }
    }

    private String _jobDescription;
    private Reminder _reminder;

    public Job() {}

    public Job(String jobDescription, Reminder reminder)
    {
        this._jobDescription = jobDescription;
        this._reminder = reminder;
    }

    private Job(Parcel in)
    {
        this._jobDescription = in.readString();
        Calendar reminderDate = Calendar.getInstance();
        reminderDate.set(Calendar.YEAR, in.readInt());
        reminderDate.set(Calendar.MONTH, in.readInt());
        reminderDate.set(Calendar.DAY_OF_MONTH, in.readInt());
        this._reminder = new Reminder(in.readString(), reminderDate);
    }

    public void setJobDescription(String jobDescription)
    {
        this._jobDescription = jobDescription;
    }
    public void setJobReminder(Reminder reminder)
    {
        this._reminder = reminder;
    }

    public String getJobDescription()
    {
        return this._jobDescription;
    }
    public Reminder getJobReminder()
    {
        return this._reminder;
    }
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

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this._jobDescription);
        dest.writeString(this._reminder.description);
        dest.writeInt(this._reminder.date.get(Calendar.YEAR));
        dest.writeInt(this._reminder.date.get(Calendar.MONTH));
        dest.writeInt(this._reminder.date.get(Calendar.DAY_OF_MONTH));
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
