package com.example.nagasudhir.debtonator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Nagasudhir on 8/26/2017.
 */

public class PersonSummaryAdapter extends ArrayAdapter<PersonSummaryListItem> {
    Context mContext;
    ViewHolder mViewHolder;
    List<PersonSummaryListItem> personSummaryItemsList;

    public PersonSummaryAdapter(Context context, int textViewResourceId,
                                List<PersonSummaryListItem> personSummaryObjects) {
        super(context, textViewResourceId, personSummaryObjects);
        this.mContext = context;
        this.personSummaryItemsList = personSummaryObjects;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView transactionNameTxtView;
        TextView consumptionTxtView;
        TextView contributionTxtView;
        TextView transactionWorthTxtView;
        TextView numTransactionPeopleTxtView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mViewHolder = null;
        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_person_summary_cons_list_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.transactionNameTxtView = (TextView) convertView.findViewById(R.id.person_summary_transaction_name);
            mViewHolder.consumptionTxtView = (TextView) convertView.findViewById(R.id.person_summary_transaction_consumption);
            mViewHolder.contributionTxtView = (TextView) convertView.findViewById(R.id.person_summary_transaction_contribution);
            mViewHolder.transactionWorthTxtView = (TextView) convertView.findViewById(R.id.person_summary_transaction_worth);
            mViewHolder.numTransactionPeopleTxtView = (TextView) convertView.findViewById(R.id.person_summary_transaction_people);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        // Fill EditText with the value you have in data source
        DecimalFormat df = new DecimalFormat("#.##");
        mViewHolder.transactionNameTxtView.setText(personSummaryItemsList.get(position).getTransactionName());
        mViewHolder.consumptionTxtView.setText(df.format(personSummaryItemsList.get(position).getPersonConsumption()));
        mViewHolder.contributionTxtView.setText(df.format(personSummaryItemsList.get(position).getPersonContribution()));
        mViewHolder.transactionWorthTxtView.setText(df.format(personSummaryItemsList.get(position).getTransactionWorth()));
        mViewHolder.numTransactionPeopleTxtView.setText(personSummaryItemsList.get(position).getNumTransactionPeople() + "");
        mViewHolder.transactionNameTxtView.setTextColor(Color.HSVToColor(new float[]{(360 * position) / personSummaryItemsList.size(), 0.6f, 0.96f}));
        return convertView;
    }
}
