package com.example.browsy;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class DashboardFragment extends Fragment {

    private CredentialsDatabaseHelper dbHelper;
    private CredentialsAdapter adapter;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbHelper = new CredentialsDatabaseHelper(getContext());

        EditText usernameInput = view.findViewById(R.id.username_input);
        EditText passwordInput = view.findViewById(R.id.password_input);
        Button addButton = view.findViewById(R.id.add_button);
        listView = view.findViewById(R.id.credentials_list);

        addButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (!username.isEmpty() && !password.isEmpty()) {
                dbHelper.addCredential(username, password);
                Toast.makeText(getContext(), "Credential added", Toast.LENGTH_SHORT).show();
                loadCredentials();
            } else {
                Toast.makeText(getContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
            }
        });

        loadCredentials();

        return view;
    }

    private void loadCredentials() {
        List<Credential> credentials = dbHelper.getAllCredentials();
        adapter = new CredentialsAdapter(getContext(), credentials, dbHelper);
        listView.setAdapter(adapter);
    }
}