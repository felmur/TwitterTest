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

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.List;

import retrofit2.Call;

public class HomeTimeLineActivity extends ListActivity {
    private static String TAG="TwitterTest-HomeTimeLine";
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_time_line);

        TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();
        long uid = session.getUserId();

        TwitterAuthConfig authConfig =  new TwitterAuthConfig(MainActivity.TWITTER_KEY, MainActivity.TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(bt_click);

        Log.d(TAG,"UserID: "+uid);
        if (uid != 0) {
            final StatusesService statusesService = Twitter.getApiClient().getStatusesService();
            Call<List<Tweet>> lista = statusesService.homeTimeline(100,null,null,false,false,false,false);
            lista.enqueue(new Callback<List<Tweet>>() {

                @Override
                public void success(Result<List<Tweet>> listResult) {
                    List<Tweet> tweets = listResult.data;
                    final FixedTweetTimeline userTimeline = new FixedTweetTimeline.Builder()
                            .setTweets(tweets)
                            .build();
                    CustomTweetTimelineListAdapter adapter = new CustomTweetTimelineListAdapter(HomeTimeLineActivity.this, userTimeline);
                    setListAdapter(adapter);
                }

                @Override
                public void failure(TwitterException e) {
                    Log.d("Twitter","twitter " + e );
                }
            });
        }
    }

    View.OnClickListener bt_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.fab:
                    Log.d(TAG,"Add Tweet clicked");
                    TweetComposer.Builder builder = new TweetComposer.Builder(HomeTimeLineActivity.this);
                    builder.show();
                    break;
            }
        }
    };



    /**
     * Custom Adapter to overrides view onClickListener
     */
    class CustomTweetTimelineListAdapter extends TweetTimelineListAdapter {

        public CustomTweetTimelineListAdapter(Context context, Timeline<Tweet> timeline) {
            super(context, timeline);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            //disable subviews
            if (view instanceof ViewGroup) {
                disableViewAndSubViews((ViewGroup) view);
            }

            //enable root view and attach custom listener
            view.setEnabled(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long tweetId = getItemId(position);
                    Toast.makeText(context, "Click tweetId: "+tweetId, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(HomeTimeLineActivity.this,ShowTweetActivity.class);
                    i.putExtra("tweetId",tweetId);
                    startActivity(i);
                }
            });
            return view;
        }

        private void disableViewAndSubViews(ViewGroup layout) {
            layout.setEnabled(false);
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                if (child instanceof ViewGroup) {
                    disableViewAndSubViews((ViewGroup) child);
                } else {
                    child.setEnabled(false);
                    child.setClickable(false);
                    child.setLongClickable(false);
                }
            }
        }

    }

}
