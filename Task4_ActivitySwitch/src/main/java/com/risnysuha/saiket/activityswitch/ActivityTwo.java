package com.risnysuha.saiket.activityswitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Task 4 · ActivityTwo.
 * Step 3: reads the data out of the Intent that started it and shows it in a TextView.
 * It can also send a short reply back to ActivityOne.
 */
public class ActivityTwo extends AppCompatActivity {

    private static final String KEY_RECEIVED_AT = "received_at";

    private TextInputEditText replyInput;
    private long receivedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeInsets.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        EdgeInsets.apply(findViewById(R.id.root), findViewById(R.id.header));

        // Retrieve the data from the Intent.
        Intent intent = getIntent();
        String message = intent.getStringExtra(IntentKeys.EXTRA_MESSAGE);
        String sender = MessageUtils.senderOrDefault(intent.getStringExtra(IntentKeys.EXTRA_SENDER));
        long sentAt = intent.getLongExtra(IntentKeys.EXTRA_SENT_AT, System.currentTimeMillis());
        receivedAt = savedInstanceState != null
                ? savedInstanceState.getLong(KEY_RECEIVED_AT)
                : System.currentTimeMillis();
        if (message == null) {
            message = getString(R.string.no_message);
        }

        // Display it in TextViews.
        TextView receivedMessage = findViewById(R.id.received_message);
        receivedMessage.setText(message);
        ((TextView) findViewById(R.id.received_meta)).setText(
                getString(R.string.meta_format, sender, MessageUtils.formatTime(sentAt)));
        ((TextView) findViewById(R.id.stat_words)).setText(String.valueOf(MessageUtils.wordCount(message)));
        ((TextView) findViewById(R.id.stat_chars)).setText(String.valueOf(MessageUtils.charCount(message)));
        ((TextView) findViewById(R.id.stat_delivery)).setText(MessageUtils.deliveryTime(sentAt, receivedAt));
        ((TextView) findViewById(R.id.extra_message_value)).setText("\"" + message + "\"");
        ((TextView) findViewById(R.id.extra_sender_value)).setText("\"" + sender + "\"");
        ((TextView) findViewById(R.id.extra_time_value)).setText(sentAt + "L");

        replyInput = findViewById(R.id.reply_input);
        ((TextView) findViewById(R.id.reply_hint_label)).setText(getString(R.string.reply_to_format, sender));

        MaterialButton back = findViewById(R.id.back_button);
        MaterialButton backBottom = findViewById(R.id.back_bottom_button);
        MaterialButton sendReply = findViewById(R.id.reply_button);
        back.setOnClickListener(v -> closeWithoutReply());
        backBottom.setOnClickListener(v -> closeWithoutReply());
        sendReply.setOnClickListener(v -> sendReply());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closeWithoutReply();
            }
        });

        receivedMessage.setAlpha(0f);
        receivedMessage.animate().alpha(1f).setDuration(400).setStartDelay(150).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_RECEIVED_AT, receivedAt);
    }

    private void sendReply() {
        String reply = replyInput.getText() == null ? "" : replyInput.getText().toString().trim();
        if (reply.isEmpty()) {
            reply = getString(R.string.default_reply);
        }
        Intent result = new Intent().putExtra(IntentKeys.EXTRA_REPLY, reply);
        setResult(Activity.RESULT_OK, result);
        finishWithAnimation();
    }

    private void closeWithoutReply() {
        setResult(Activity.RESULT_CANCELED);
        finishWithAnimation();
    }

    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
