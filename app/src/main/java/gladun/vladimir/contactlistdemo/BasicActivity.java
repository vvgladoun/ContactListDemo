package gladun.vladimir.contactlistdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import gladun.vladimir.contactlistdemo.R;

/**
 *
 * @author vvgladoun@gmail.com
 */
public abstract class BasicActivity extends AppCompatActivity {

    /**
     * create fragment to place in fragmentContainer
     *
     * @return android.support.v4.app.Fragment object
     */
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                placeUpButton();
            }
        });

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        } else {
            //re-create navigation button for fragments after rotation
            placeUpButton();
        }
    }


    /**
     * Find action bar in a fragment and add up navigation
     * if fragment is not first oin stack
     */
    void placeUpButton(){
        int stackHeight = getSupportFragmentManager().getBackStackEntryCount();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            if (stackHeight > 0) {
                // show navigation button
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                // hide navigation button
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setHomeButtonEnabled(false);
            }
        }
    }
}