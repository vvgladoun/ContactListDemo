package gladun.vladimir.contactlistdemo.contact_list;


import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gladun.vladimir.contactlistdemo.utilities.ActivityCallback;
import gladun.vladimir.contactlistdemo.R;
import gladun.vladimir.contactlistdemo.contact_details.ContactDetailsFragment;


/**
 *
 * @author vvgladoun@gmail.com
 */
public class ContactViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    protected TextView mNameText;
    protected TextView mEmailText;
    private int mContactId;

    public ContactViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mNameText = (TextView) itemView.findViewById(R.id.item_text_name);
        mEmailText = (TextView) itemView.findViewById(R.id.item_text_email);
    }

    public void bindContact(int contactId) {
        mContactId = contactId;
    }

    @Override
    public void onClick(View v) {
        if (mContactId > 0) {
            Fragment contactDetailsFragment = ContactDetailsFragment.newInstance(mContactId);
            if (v.getContext() instanceof ActivityCallback) {
                ActivityCallback callbackActivity = (ActivityCallback)v.getContext();
                callbackActivity.changeFragment(contactDetailsFragment);
            }


        }
    }
}
