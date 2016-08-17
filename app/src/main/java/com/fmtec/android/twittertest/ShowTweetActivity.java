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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

public class ShowTweetActivity extends AppCompatActivity {
    private static String TAG="TwitterTest-ShowTweet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tweet);

        Intent i = getIntent();
        long tweetId = i.getLongExtra("tweetId",0);
        Log.d(TAG,"tweetId: "+tweetId);

        if (tweetId != 0) {
            final RelativeLayout lo = (RelativeLayout) findViewById(R.id.myLayout);
            TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    TweetView tweetView = new TweetView(ShowTweetActivity.this, result.data, R.style.tw__TweetLightWithActionsStyle);
                    tweetView.setOnActionCallback(actionCallback);
                    lo.addView(tweetView);
                }
                @Override
                public void failure(TwitterException exception) {
                    Log.d(TAG, "Load Tweet failure", exception);
                }
            });

        }

    }

    // launch the login activity when a guest user tries to favorite a Tweet
    final Callback<Tweet> actionCallback = new Callback<Tweet>() {
        @Override
        public void success(Result<Tweet> result) {
            // Intentionally blank
            Log.d(TAG,"Callback successful");
        }

        @Override
        public void failure(TwitterException exception) {
            if (exception instanceof TwitterAuthException) {
                // launch custom login flow
                Log.d(TAG,"Callback failed");
                //startActivity(LoginActivity.class);
            }
        }
    };

}
