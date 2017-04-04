package com.example.guy.ex2.actions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by GUY on 4/3/2017.
 */

/**
 * The calling action
 * It extends the 'Action' abstract class
 */
public class CallAction extends Action
{
    private String _phoneNum;

    /**
     * C'tor for this obj
     * @param actionName the action's name (call)
     * @param phoneNum the phone number to call to
     */
    public CallAction(String actionName, String phoneNum)
    {
        super(actionName);
        this._phoneNum = phoneNum;
    }

    /**
     * Getter for the phone number
     * @return phone num
     */
    public String getPhoneNum() { return this._phoneNum; }

    /**
     * {@inheritDoc}
     *
     * Note it uses an intent and 'MainActivity.this' (context) to start the dialler
     * To avoid asking for more permissions (android.permission.CALL_PHONE) we use
     * the 'tel' hack.
     *
     * see - http://stackoverflow.com/questions/11699819/how-do-i-get-the-dialer-to-open-with-phone-number-displayed
     */
    @Override
    public void run(Object... args)
    {
        /*
         * Start a dialer and pass it the phone
         * (pay attention, without 'tel' prefix, it will throw 'IllegalStateException' exception)
         */
        Activity mainActivityContext = (Activity)args[0];
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + this._phoneNum));
        mainActivityContext.startActivity(intent);
    }
}
