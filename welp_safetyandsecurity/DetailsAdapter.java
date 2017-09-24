package com.abomicode.welp_safetyandsecurity;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rohan on 6/15/2017.
 */

public class DetailsAdapter extends ArrayAdapter<UserDetails> {

    Context context;

    public DetailsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<UserDetails> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.list_details,parent,false);


        TextView name = (TextView) view.findViewById(R.id.list_details_name);
        TextView email = (TextView) view.findViewById(R.id.list_details_email);
        TextView phone = (TextView) view.findViewById(R.id.list_details_phone);

        UserDetails details = getItem(position);


        name.setText(details.getFullName());
        email.setText(details.getEmail());
        phone.setText(details.getPhone());


        return view;
    }
}
