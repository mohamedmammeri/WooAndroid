package com.designwall.moosell.activity.category;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.View;

import com.designwall.moosell.R;
import com.designwall.moosell.config.Url;
import com.designwall.moosell.task.GetDataTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    // Only needed to check activity
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);


    @Before
    public void setUp() throws Exception {
        mActivity = activityActivityTestRule.getActivity();
    }

    @Test
    public void testClickOnMenuButton(){

        View menuBtn = mActivity.findViewById(R.id.menuBtn);

        // Make sure the button is not null
        assertNotNull(menuBtn);

        // perform a click on a button
        onView(withId(R.id.menuBtn)).perform(click());

        // Make sure the activity has been launched
        Activity mainActivity = getInstrumentation().waitForMonitor(monitor);
//        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 3000);
        assertNotNull(mainActivity);
        mActivity.finish();

    }

    @Test
    public void testRestApi(){
        new GetDataTask(GetDataTask.METHOD_GET){

            @Override
            protected void onPreExecute() {
                Log.d("Test", "before executing...");
            }

            @Override
            protected void onPostExecute(String[] result) {
                Log.d("Test", result.toString());

                for (String aResult : result) {
                    Log.d("Test", aResult);
                }
            }

        }.execute(Url.getProducts());
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}