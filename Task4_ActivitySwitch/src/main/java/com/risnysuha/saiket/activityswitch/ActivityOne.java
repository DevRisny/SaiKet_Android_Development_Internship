package com.risnysuha.saiket.activityswitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Task 4 · Simple Switch Between Activities — ActivityOne.
 * The button builds an Intent, puts the message in it as an extra and opens ActivityTwo.
 * When ActivityTwo sends a reply back, it is shown here.
 */
public class ActivityOne extends AppCompatActivity {

    private static final String KEY_REPLY = "last_reply";

    private TextInputLayout nameLayout;
    private TextInputLayout messageLayout;
    private TextInputEditText nameInput;
    private TextInputEditText messageInput;
    private MaterialCardView replyCard;
    private TextView replyText;
    private String lastReply;

    private final ActivityResultLauncher<Intent> activityTwoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    showReply(result.getData().getStringExtra(IntentKeys.EXTRA_REPLY), true);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeInsets.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        EdgeInsets.apply(findViewById(R.id.root), findViewById(R.id.header));

        nameLayout = findViewById(R.id.name_layout);
        messageLayout = findViewById(R.id.message_layout);
        nameInput = findViewById(R.id.name_input);
        messageInput = findViewById(R.id.message_input);
        replyCard = findViewById(R.id.reply_card);
        replyText = findViewById(R.id.reply_text);
        messageLayout.setCounterMaxLength(MessageUtils.MAX_LENGTH);

        ChipGroup suggestions = findViewById(R.id.suggestions);
        for (int i = 0; i < suggestions.getChildCount(); i++) {
            View child = suggestions.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setOnClickListener(v -> {
                    messageInput.setText(chip.getText());
                    messageInput.setSelection(messageInput.length());
                    messageLayout.setError(null);
                });
            }
        }

        MaterialButton send = findViewById(R.id.send_button);
        send.setOnClickListener(v -> openActivityTwo());

        if (savedInstanceState != null) {
            showReply(savedInstanceState.getString(KEY_REPLY), false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_REPLY, lastReply);
    }

    /** Step 2: pass the data to ActivityTwo with an Intent. */
    private void openActivityTwo() {
        String message = text(messageInput);
        switch (MessageUtils.validate(message)) {
            case EMPTY:
                messageLayout.setError(getString(R.string.error_empty));
                messageInput.requestFocus();
                return;
            case TOO_LONG:
                messageLayout.setError(getString(R.string.error_long, MessageUtils.MAX_LENGTH));
                return;
            default:
                messageLayout.setError(null);
        }

        Intent intent = new Intent(this, ActivityTwo.class);
        intent.putExtra(IntentKeys.EXTRA_MESSAGE, message.trim());
        intent.putExtra(IntentKeys.EXTRA_SENDER, MessageUtils.senderOrDefault(text(nameInput)));
        intent.putExtra(IntentKeys.EXTRA_SENT_AT, System.currentTimeMillis());
        activityTwoLauncher.launch(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showReply(String reply, boolean animate) {
        if (reply == null || reply.trim().isEmpty()) {
            return;
        }
        lastReply = reply;
        replyText.setText(reply);
        replyCard.setVisibility(View.VISIBLE);
        if (animate) {
            replyCard.setAlpha(0f);
            replyCard.setTranslationY(30f);
            replyCard.animate().alpha(1f).translationY(0f).setDuration(300).setStartDelay(200).start();
        }
    }

    private static String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString();
    }
}
