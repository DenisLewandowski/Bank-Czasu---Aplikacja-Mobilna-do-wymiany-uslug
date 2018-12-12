package pl.denislewandowski.bankczasu.activities;

import android.view.View;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import pl.denislewandowski.bankczasu.R;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule
            = new ActivityTestRule<>(LoginActivity.class);

    private LoginActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }
    @Test
    public void isSignInButton() {
        View view = mActivity.findViewById(R.id.signInButton);
        assertNotNull(view);
    }
    @Test
    public void isGoogleSignInButton() {
        View view = mActivity.findViewById(R.id.googleSignInButton);
        assertNotNull(view);
    }
    @Test
    public void isEmailEditText() {
        View view = mActivity.findViewById(R.id.emailSignInEditText);
        assertNotNull(view);
    }
    @Test
    public void isPasswordEditText() {
        View view = mActivity.findViewById(R.id.passwordSignInEditText);
        assertNotNull(view);
    }

    @Test
    public void testUserInput() {
        Espresso.onView(withId(R.id.emailSignInEditText))
                .perform(typeText("11email#&gmail,com"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.signInButton)).perform(click());
        Espresso.onView(withId(R.id.emailSignInEditText))
                .check(matches(hasErrorText("Wpisz poprawny adres")));
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}