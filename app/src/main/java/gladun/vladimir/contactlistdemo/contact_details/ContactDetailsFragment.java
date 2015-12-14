package gladun.vladimir.contactlistdemo.contact_details;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import gladun.vladimir.contactlistdemo.model.Address;
import gladun.vladimir.contactlistdemo.model.Company;
import gladun.vladimir.contactlistdemo.model.Contact;
import gladun.vladimir.contactlistdemo.R;


/**
 * @author vvgladoun@gmail.com
 */
public class ContactDetailsFragment extends Fragment {

    int mContactId;
    Contact mContact;
    LinearLayout mContainer;
    public static final String EXTRA_ID_CONTACT = "ID_CONTACT";

    public static ContactDetailsFragment newInstance(int contactId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ID_CONTACT, contactId);
        ContactDetailsFragment fragment = new ContactDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContactId = getArguments().getInt(EXTRA_ID_CONTACT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.contact_details_fragment, parent, false);
        mContainer = (LinearLayout)fragment_view.findViewById(R.id.contact_details_container);

        (new DownloadContactDetails()).execute();

        return fragment_view;
    }

    private void setInformationBlocks(){
        if (mContact != null && mContainer != null) {
            addContactDetails();
            addAddressDetails();
            addCompanyDetails();
        }
    }

    private void addContactDetails(){
        addInformationBlock(mContainer, mContact.getUsername(),
                getString(R.string.contact_username_title), false);
        addInformationBlock(mContainer, mContact.getPhone(),
                getString(R.string.contact_phone_title), false);
        addInformationBlock(mContainer, mContact.getWebsite(),
                getString(R.string.contact_website_title), false);
    }

    private void addCompanyDetails(){
        Company company = mContact.getCompany();
        if (company != null) {
            addInformationBlock(mContainer, company.getName(),
                    getString(R.string.contact_company_title), true);
        }
    }

    private void addAddressDetails(){
        Address address = mContact.getAddress();
        if (address != null) {
            String addressDetails = "" + address.getSuite() + ", " + address.getStreet()
                    + ", " + address.getCity() + ", " + address.getZipcode();
            addInformationBlock(mContainer, addressDetails,
                    getString(R.string.contact_address_title), false);
        }
    }


    private void addInformationBlock(LinearLayout container, String details,
                                     String label, boolean addSeparator){

        if (details != null) {
            if (addSeparator) {
                addSeparator(container);
            }
            addTextView(container, details, android.R.style.TextAppearance_Large,
                    (int) getResources().getDimension(R.dimen.contact_details_margin_left),
                    (int) getResources().getDimension(R.dimen.contact_details_margin_top),
                    (int) getResources().getDimension(R.dimen.contact_details_margin_right),
                    (int) getResources().getDimension(R.dimen.contact_details_margin_bottom));

            addTextView(container, label, android.R.style.TextAppearance_Small,
                    (int) getResources().getDimension(R.dimen.contact_label_margin_left),
                    (int) getResources().getDimension(R.dimen.contact_label_margin_top),
                    (int) getResources().getDimension(R.dimen.contact_label_margin_right),
                    (int) getResources().getDimension(R.dimen.contact_label_margin_bottom));
        }
    }

    /**
     * Create new text view and add it to the view group
     */
    private void addTextView(ViewGroup container, String text, int textSize,
                             int marginLeft, int marginTop, int marginRight, int marginBottom){
        TextView newTextView = new TextView(getActivity());
        TableRow.LayoutParams newLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        newLayoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        newTextView.setLayoutParams(newLayoutParams);
        newTextView.setText(text);
        newTextView.setTextAppearance(getActivity(),  textSize);
        container.addView(newTextView);

    }

    /**
     * Create new separator view (horizontal line) and add it to the view group
     */
    private void addSeparator(ViewGroup container){
        View separator = new View(getActivity());
        TableRow.LayoutParams separatorLayoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.contact_separator_width));
        separatorLayoutParams.setMargins(
                (int) getResources().getDimension(R.dimen.contact_separator_margin_left),
                (int) getResources().getDimension(R.dimen.contact_separator_margin_top),
                (int) getResources().getDimension(R.dimen.contact_separator_margin_right),
                (int) getResources().getDimension(R.dimen.contact_separator_margin_bottom));
        separator.setLayoutParams(separatorLayoutParams);
        separator.setBackgroundResource(R.color.separator);
        container.addView(separator);
    }

    private class DownloadContactDetails extends AsyncTask<Void, Void, Void> {

        Contact contact;

        @Override
        protected Void doInBackground(Void... arg0) {
            contact = new ContactManager(getActivity()).getContact(mContactId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mContact = contact;
            if (contact != null) {
                getActivity().setTitle(contact.getName());
                setInformationBlocks();
            }
        }
    }

}
