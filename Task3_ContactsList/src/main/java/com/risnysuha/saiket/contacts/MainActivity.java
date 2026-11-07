package com.risnysuha.saiket.contacts;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Task 3 · Display List of Contacts.
 * Asks for READ_CONTACTS, reads names and numbers with a ContentResolver on a background
 * thread and shows them in a ListView. Tapping a contact opens the dialer.
 */
public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "contacts_prefs";
    private static final String KEY_ASKED = "asked_permission";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<Contact> allContacts = new ArrayList<>();
    private boolean loaded;

    private ContactAdapter adapter;
    private ListView listView;
    private View searchBox;
    private TextInputEditText searchInput;
    private TextView countLabel;
    private TextView headerSubtitle;
    private CircularProgressIndicator loading;
    private View stateCard;
    private android.widget.ImageView stateIcon;
    private TextView stateTitle;
    private TextView stateBody;
    private MaterialButton stateButton;
    private MaterialButton refreshButton;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    loadContacts();
                } else {
                    showPermissionState();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeInsets.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeInsets.apply(findViewById(R.id.root), findViewById(R.id.header));

        listView = findViewById(R.id.contacts_list);
        searchBox = findViewById(R.id.search_layout);
        searchInput = findViewById(R.id.search_input);
        countLabel = findViewById(R.id.count_label);
        headerSubtitle = findViewById(R.id.header_subtitle);
        loading = findViewById(R.id.loading);
        stateCard = findViewById(R.id.state_card);
        stateIcon = findViewById(R.id.state_icon);
        stateTitle = findViewById(R.id.state_title);
        stateBody = findViewById(R.id.state_body);
        stateButton = findViewById(R.id.state_button);
        refreshButton = findViewById(R.id.refresh_button);
        // Leave room for the refresh button that sits over the header.
        headerSubtitle.setPadding(0, 0, Math.round(56 * getResources().getDisplayMetrics().density), 0);

        adapter = new ContactAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> dial(adapter.getItem(position)));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        refreshButton.setOnClickListener(v -> {
            if (hasPermission()) {
                loadContacts();
            } else {
                requestOrOpenSettings();
            }
        });

        if (hasPermission()) {
            loadContacts();
        } else if (savedInstanceState == null && !wasAskedBefore()) {
            showPermissionState();
            askPermission(); // ask straight away on the first launch
        } else {
            showPermissionState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The user may have granted the permission in Settings and come back.
        if (!loaded && hasPermission()) {
            loadContacts();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean wasAskedBefore() {
        return getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEY_ASKED, false);
    }

    private void askPermission() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ASKED, true).apply();
        permissionLauncher.launch(Manifest.permission.READ_CONTACTS);
    }

    /** After a permanent "Don't allow", Android will not show the dialog again, so we open Settings. */
    private boolean isPermanentlyDenied() {
        return wasAskedBefore() && !shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
    }

    private void requestOrOpenSettings() {
        if (isPermanentlyDenied()) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
        } else {
            askPermission();
        }
    }

    private void loadContacts() {
        stateCard.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            List<Contact> result;
            try {
                result = ContactsRepository.load(getContentResolver());
            } catch (SecurityException e) {
                result = null;
            }
            final List<Contact> contacts = result;
            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                loading.setVisibility(View.GONE);
                if (contacts == null) {
                    showPermissionState();
                    return;
                }
                loaded = true;
                allContacts = contacts;
                headerSubtitle.setText(getResources().getQuantityString(
                        R.plurals.header_count, contacts.size(), contacts.size()));
                applyFilter();
            });
        });
    }

    private void applyFilter() {
        if (!loaded) {
            return;
        }
        String query = searchInput.getText() == null ? "" : searchInput.getText().toString();
        List<Contact> filtered = ContactUtils.filter(allContacts, query);
        adapter.submit(filtered);
        searchBox.setVisibility(allContacts.isEmpty() ? View.GONE : View.VISIBLE);
        countLabel.setVisibility(allContacts.isEmpty() ? View.GONE : View.VISIBLE);
        countLabel.setText(query.trim().isEmpty()
                ? getResources().getQuantityString(R.plurals.contact_count, filtered.size(), filtered.size())
                : getString(R.string.search_count_format, filtered.size(), allContacts.size()));

        if (allContacts.isEmpty()) {
            showState(R.drawable.ic_person, R.string.empty_title, R.string.empty_body, R.string.refresh, true);
            listView.setVisibility(View.GONE);
        } else if (filtered.isEmpty()) {
            showState(R.drawable.ic_search, R.string.no_match_title, R.string.no_match_body, 0, false);
            listView.setVisibility(View.GONE);
        } else {
            stateCard.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void showPermissionState() {
        loaded = false;
        listView.setVisibility(View.GONE);
        searchBox.setVisibility(View.GONE);
        countLabel.setVisibility(View.GONE);
        headerSubtitle.setText(R.string.header_subtitle);
        boolean settings = isPermanentlyDenied();
        showState(R.drawable.ic_lock, R.string.permission_title,
                settings ? R.string.permission_body_settings : R.string.permission_body,
                settings ? R.string.open_settings : R.string.allow_access, true);
    }

    private void showState(int icon, int title, int body, int buttonText, boolean actionIsRefresh) {
        stateIcon.setImageResource(icon);
        stateTitle.setText(title);
        stateBody.setText(body);
        if (buttonText == 0) {
            stateButton.setVisibility(View.GONE);
        } else {
            stateButton.setVisibility(View.VISIBLE);
            stateButton.setText(buttonText);
            stateButton.setOnClickListener(v -> {
                if (hasPermission()) {
                    loadContacts();
                } else {
                    requestOrOpenSettings();
                }
            });
        }
        stateCard.setVisibility(View.VISIBLE);
    }

    private void dial(Contact contact) {
        if (contact.getNumbers().isEmpty()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(contact.primaryNumber())));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_dialer_format, contact.primaryNumber()),
                    Toast.LENGTH_LONG).show();
        }
    }
}
