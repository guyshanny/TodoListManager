package com.example.guy.ex2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.guy.ex2.actions.Action;

/**
 * Created by GUY on 4/4/2017.
 */

/**
 * Represents a reminder's actions' dialog box
 * It is a regular AlertDialog with a list
 * see https://developer.android.com/guide/topics/ui/dialogs.html#AddingAList
 */
public class ReminderOptionsDialog extends DialogFragment
{
    // The job for which we handling it's reminder's jobs
    private Job _job;
    // A handle to main activity context (for further use in running reminder's actions)
    private Context _mainActivityContext;

    /**
     * Getter for the reminder's dialog's instance
     * @param job the job we handling
     * @param mainActivityContext the main activity context
     * @return an instance of the dialog
     */
    public static ReminderOptionsDialog newInstance(Job job, Context mainActivityContext)
    {
        ReminderOptionsDialog frag = new ReminderOptionsDialog();
        frag.setJobObj(job);
        frag._mainActivityContext = mainActivityContext;

        return frag;
    }

    /**
     * Setter for the job's obj
     * @param job job's obj
     */
    public void setJobObj(Job job)
    {
        this._job = job;
    }

    /**
     * {@inheritDoc}
     *
     * Note it is different from 'AddReminderDialog' dialog.
     * Here, we don't have a custom dialog view, so we don't use the 'onCreateView'
     */
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final String[] actionsNames = this._job.getJobReminder().getActionsNames();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(this._job.getJobReminder().getDescription())
                .setItems(actionsNames, new DialogInterface.OnClickListener() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Action action = ReminderOptionsDialog.this._job.getJobReminder().getActions().get(which);
                        switch (actionsNames[which])
                        {
                            case "cancel":
                            {
                                ReminderOptionsDialog.this._job.removeReminder();

                                // Refresh ListView (to update job's reminder's date)
                                MainActivity mainActivity = (MainActivity)ReminderOptionsDialog.this._mainActivityContext;
                                mainActivity.updateJobs(null);
                            }
                            break;
                            case "call":
                            {
                                action.run(ReminderOptionsDialog.this._mainActivityContext);
                            }
                            break;
                            default:
                                break;
                        }


                        //dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
