package gladun.vladimir.contactlistdemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import gladun.vladimir.contactlistdemo.contact_list.ContactListFragment;
import gladun.vladimir.contactlistdemo.utilities.ActivityCallback;

/**
 * Main activity contains only one fragment - list of details
 *
 * @author vvgladoun@gmail.com
 */
public class MainActivity extends BasicActivity implements ActivityCallback {

    @Override
    protected android.support.v4.app.Fragment createFragment() {
        return new ContactListFragment();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * to navigate from contact details fragment back to list fragment
     */
    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void changeFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
