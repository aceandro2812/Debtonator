package com.example.nagasudhir.debtonator;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nagasudhir on 8/17/2017.
 */

public class TransactionContributionAdapter extends ArrayAdapter<TransactionContributionListItem> implements CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {

    Context mContext;
    ViewHolder mViewHolder;
    List<TransactionContributionListItem> transactionContributionItemsList;
    HashSet<Integer> mDirtyRowPositions = new HashSet<Integer>();

    public TransactionContributionAdapter(Context context, int textViewResourceId,
                                          List<TransactionContributionListItem> contributionObjects) {
        super(context, textViewResourceId, contributionObjects);
        this.mContext = context;
        this.transactionContributionItemsList = contributionObjects;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mViewHolder = null;
        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_transaction_edit_contribution_list_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.contributionEditText = (EditText) convertView.findViewById(R.id.tran_contribution);
            mViewHolder.personNameTxtView = (TextView) convertView.findViewById(R.id.tran_person_name);
            mViewHolder.isConsumerChkBox = (CheckBox) convertView.findViewById(R.id.tran_is_consumer);
            mViewHolder.personIdTextView = (TextView) convertView.findViewById(R.id.tran_person_id);
            mViewHolder.idTextView = (TextView) convertView.findViewById(R.id.tran_contr_id);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        // Fill EditText with the value you have in data source
        mViewHolder.contributionEditText.setText(transactionContributionItemsList.get(position).getContribution());
        mViewHolder.personNameTxtView.setText(transactionContributionItemsList.get(position).getPersonName());
        mViewHolder.isConsumerChkBox.setChecked(transactionContributionItemsList.get(position).isConsumer());
        mViewHolder.personIdTextView.setText(transactionContributionItemsList.get(position).getPersonId());
        mViewHolder.idTextView.setText(transactionContributionItemsList.get(position).getId());

        mViewHolder.isConsumerChkBox.setOnCheckedChangeListener(this);
        mViewHolder.isConsumerChkBox.setTag(R.id.tran_is_consumer, position);

        //mViewHolder.contributionEditText.setOnFocusChangeListener(this);
        mViewHolder.contributionEditText.setTag(R.id.tran_contribution, position);
        //mViewHolder.contributionEditText.setOnEditorActionListener(this);
        mViewHolder.contributionEditText.addTextChangedListener(new MyTextWatcher(mViewHolder.contributionEditText));
        mViewHolder.contributionEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    // Hide the soft Keyboard
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // Evaluate the infix value of the contribution edit text
                    final int position = (int) v.getTag(R.id.tran_contribution);
                    String str = ((EditText) v).getText().toString();
                    Double contributionValue = 0.0;
                    try {
                        contributionValue = Infix.infix(str);
                    } catch (Exception e) {
                        // The infix evaluation is not successful so don't do anything
                        Toast.makeText(mContext, "Invalid Expression", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    String contributionText = contributionValue + "";
                    if ((contributionValue == Math.floor(contributionValue))) {
                        // contributionValue is integer type
                        contributionText = contributionValue.intValue() + "";
                    }

                    // update the objects list
                    transactionContributionItemsList.get(position).setContribution(contributionText);

                    // Change the Edit Text
                    ((EditText) v).setText(contributionText);
                    ((EditText) v).setSelection(contributionText.length());
                    return true;
                }
                return false;
            }
        });
        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton chkBox, boolean isChecked) {
        final int position = (int) chkBox.getTag(R.id.tran_is_consumer);
        transactionContributionItemsList.get(position).setConsumer(isChecked);
        // declare the list item as dirty
        mDirtyRowPositions.add(position);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    private class MyTextWatcher implements TextWatcher {

        private EditText contributionEditTextView;

        private MyTextWatcher(EditText view) {
            this.contributionEditTextView = view;

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        public void afterTextChanged(Editable s) {
            if (contributionEditTextView.hasFocus()) {
                final int position = (int) contributionEditTextView.getTag(R.id.tran_contribution);
                // Do infix evaluation and save the contribution data
                Double contributionValue = 0.0;
                try {
                    contributionValue = Infix.infix(contributionEditTextView.getText().toString());
                } catch (Exception e) {
                    // The infix evaluation is not successful so don't do anything
                    return;
                }
                String contributionText = contributionValue + "";
                if ((contributionValue == Math.floor(contributionValue))) {
                    // contributionValue is integer type
                    contributionText = contributionValue.intValue() + "";
                }
                transactionContributionItemsList.get(position).setContribution(contributionText);
                // declare the list item as dirty
                mDirtyRowPositions.add(position);
                // updateObjectInDB(position);
            }
        }

    }


    private static class ViewHolder {
        EditText contributionEditText;
        TextView personNameTxtView;
        CheckBox isConsumerChkBox;
        TextView personIdTextView;
        TextView idTextView;
    }

    private void updateObjectInDB(int position) {
        TransactionContributionListItem transactionContributionListItem = transactionContributionItemsList.get(position);

        String contribution = transactionContributionListItem.getContribution();
        if (contribution == null) {
            contribution = "0";
        }
        if (contribution.equals("") || contribution.equals("0.0")) {
            contribution = "0";
        }

        if (contribution.equals("0") && !transactionContributionListItem.isConsumer()) {
            // delete the row if contribution is zero and isConsumer is false
            int numRowsDeleted = mContext.getContentResolver().delete(Uri.parse(TransactionContributionProvider.CONTENT_URI + "/upsert"), null, new String[]{transactionContributionListItem.getTransactionsDetailsId(), transactionContributionListItem.getPersonId()});
        } else {
            // upsert the transaction contribution row
            String isConsumerText = (transactionContributionListItem.isConsumer() ? "1" : "0");
            Cursor transactionCursor = mContext.getContentResolver().query(Uri.parse(TransactionContributionProvider.CONTENT_URI + "/upsert"), null, null, new String[]{transactionContributionListItem.getTransactionsDetailsId(), transactionContributionListItem.getPersonId(), contribution, isConsumerText}, null);
        }
    }

    public void updateAllDirtyContributionItemsToDB() {
        // Iterate through all dirty rows for saving changes
        Iterator<Integer> itr = mDirtyRowPositions.iterator();
        while (itr.hasNext()) {
            updateObjectInDB(itr.next());
        }
    }
}
