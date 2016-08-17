/*
TwitterTest
by Felice Murolo - Salerno, Italia
eMail: linuxboy@fel.-RE-MO-VE-hopto.org

TwitterTest is a simple app designed for Android Studio with the help of the framework "Fabric" version 2.3.1.

For more details regards "Fabric", please refer to: https://fabric.io/home

To install the "Fabric" plug-in Android Studio, use the File menu -> Settings -> Plugins.

After installing the plugin "Fabric", you can import this application in Android Studio.

With this demo app, you can:

1) run your personal account log in to Twitter
2) view your HomeTimeLine (the stream of your down payment twitter)
3) put "like" to tweets in your stream
4) post a new tweet
5) click on a tweet to view it in full screen

To run your own app, You MUST configure the values of the variables in TWITTER_KEY and TWITTER_SECRET MainActivity.java file.
These values you can get after the registration of your interim https://fabric.io/home site, creating a new application from https://fabric.io/settings/apps

 */

package com.fmtec.android.twittertest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private static String TAG="TwitterTest-Main";
    private static boolean isLogged = false;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "your fabric consumer key here";
    public static final String TWITTER_SECRET = "your fabric secret key here";

    private TwitterLoginButton loginButton;
    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button);
        btn.setVisibility(View.GONE);
        btn.setOnClickListener(bt_click);


        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;

                isLogged = true;
                loginButton.setVisibility(View.GONE);
                btn.setVisibility(View.VISIBLE);

                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Log.d(TAG,msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                goStream();
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "Login with Twitter failure", exception);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    View.OnClickListener bt_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.button:
                    goStream();
                    break;
            }
        }
    };


    void goStream() {
        Intent i = new Intent(MainActivity.this,HomeTimeLineActivity.class);
        startActivity(i);
    }


}
