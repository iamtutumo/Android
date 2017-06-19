package com.winbeelabs.schools.network;

import android.content.Context;
import android.net.ConnectivityManager;

public class Network
{
    public static final String API_URL = "http://invoicing.ahotbrew.com/index.php/api/";
    public static final String API_KEY = "qnaSMNnufSemGQran211qnaSMNnufSemGQran2kj";

    public Network()
    {
        // TODO Auto-generated constructor stub
        super();
    }

    public static boolean checkStatus(Context context)
    {
        // TODO Auto-generated method stub
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr.getActiveNetworkInfo() != null
                && connMgr.getActiveNetworkInfo().isAvailable()
                && connMgr.getActiveNetworkInfo().isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
