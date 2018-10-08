package com.frogermcs.multimodulegithubclient.endtoend;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.frogermcs.multimodulegithubclient.SplashActivity;
import com.frogermcs.multimodulegithubclient.repositories.RepositoriesListActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppFlowTest {
    @Rule
    public IntentsTestRule<SplashActivity> splashActivityRule = new IntentsTestRule<>(SplashActivity.class);

    @Test
    public void goThroughAllScreens_HappyPath() {

        //
        // ===== Main Screen =====
        //
        onView(withHint("username")).perform(typeText("frogermcs"));
        onView(withText("Show repositories")).perform(click());

        // Dirty 'Tread.sleep()' replacement. The solution can be way more elegant here. :)
        onView(isRoot()).perform(waitFor(2000));

        //
        // ===== Repositories list screen =====
        //

        // Is Activity started ?
        //Don't use if this is end-to-end test. In theory you shouldn't have knowledge about classes and implementation
        intended(hasComponent(RepositoriesListActivity.class.getName()));

        //Assert screen title
        onView(
                allOf(
                        isAssignableFrom(TextView.class),
                        withParent(isAssignableFrom(Toolbar.class))
                ))
                .check(matches(withText("Repositories list")));

        onView(instanceOf(RecyclerView.class)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        onView(isRoot()).perform(waitFor(500));

        //
        // ===== Repository details =====
        //

        // Is Activity started? This time Activity isn't a direct part of app module,
        // so class cannot be pointed directly.
        //Plus again, you shouldn't use this in end-to-end tests...
        intended(hasComponent("com.frogermcs.multimodulegithubclient.repository.RepositoryDetailsActivity"));

        //Assert screen title
        onView(
                allOf(
                        isAssignableFrom(TextView.class),
                        withParent(isAssignableFrom(Toolbar.class))
                ))
                .check(matches(withText("Repository details")));
    }

    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}
