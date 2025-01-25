package com.example.browsy;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CredentialsAdapter extends BaseAdapter {

    private Context context;
    private List<Credential> credentials;
    private CredentialsDatabaseHelper dbHelper;

    public CredentialsAdapter(Context context, List<Credential> credentials, CredentialsDatabaseHelper dbHelper) {
        this.context = context;
        this.credentials = credentials;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return credentials.size();
    }

    @Override
    public Object getItem(int position) {
        return credentials.get(position);
    }

    @Override
    public long getItemId(int position) {
        return credentials.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.credential_item, parent, false);
        }

        TextView usernameText = convertView.findViewById(R.id.username_text);
        TextView passwordText = convertView.findViewById(R.id.password_text);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        Credential credential = credentials.get(position);
        usernameText.setText(credential.getUsername());
        passwordText.setText(credential.getPassword());

        deleteButton.setOnClickListener(v -> {
            dbHelper.deleteCredential(credential.getId());
            credentials.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }
}