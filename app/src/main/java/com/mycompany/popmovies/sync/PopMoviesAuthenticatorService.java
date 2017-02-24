package com.mycompany.popmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Borys on 2017-02-18.
 */


public class PopMoviesAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    //com.mycompany.popmovies.sync.PopMoviesAuthenticator
    private PopMoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        //mAuthenticator = new com.mycompany.popmovies.sync.PopMoviesAuthenticator(this);
        mAuthenticator = new PopMoviesAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}