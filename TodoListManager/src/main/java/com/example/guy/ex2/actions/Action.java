package com.example.guy.ex2.actions;

import android.app.Activity;

/**
 * Created by GUY on 4/3/2017.
 */

/**
 * An abstract class represents an action for a reminder
 */
public abstract class Action extends Activity
{
    private String _actionName;

    /**
     * C'tor for the action's obj
     * @param actionName the action's name
     */
    public Action(String actionName)
    {
        this._actionName = actionName;
    }

    /**
     * Runs the actions
     * @param args varags to the action
     */
    public abstract void run(Object... args);

    /**
     * Getter for the action's name
     * @return the action's name
     */
    public String getActionName() { return this._actionName; }
}
