package com.risnysuha.saiket.quiz;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

/**
 * Task 1 · Simple Quiz App.
 * A TextView shows the question, RadioButtons hold the answers and the Submit button's
 * OnClickListener checks the choice. After submitting, the options are disabled and the
 * result ("Correct!" or "Incorrect") is shown.
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_STATE = "quiz_state";

    private QuizEngine engine;

    private ScrollView scroll;
    private TextView progressLabel;
    private TextView scoreLabel;
    private LinearProgressIndicator progressBar;
    private View quizSection;
    private TextView questionText;
    private RadioGroup optionsGroup;
    private RadioButton[] optionButtons;
    private MaterialCardView resultCard;
    private ImageView resultIcon;
    private TextView resultTitle;
    private TextView resultBody;
    private MaterialCardView scoreCard;
    private TextView finalScore;
    private TextView finalPercent;
    private TextView finalVerdict;
    private MaterialButton primaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeInsets.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeInsets.apply(findViewById(R.id.root), findViewById(R.id.header));

        bindViews();

        // Step 2: the questions and answers are defined in code (QuizBank).
        engine = new QuizEngine(QuizBank.questions());
        if (savedInstanceState != null) {
            engine.restore(savedInstanceState.getIntArray(KEY_STATE));
        }

        // Step 3: the Submit button's OnClickListener checks the selected answer.
        primaryButton.setOnClickListener(v -> onPrimaryClicked());

        render(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(KEY_STATE, engine.snapshot());
    }

    private void bindViews() {
        scroll = findViewById(R.id.scroll);
        progressLabel = findViewById(R.id.progress_label);
        scoreLabel = findViewById(R.id.score_label);
        progressBar = findViewById(R.id.progress_bar);
        quizSection = findViewById(R.id.quiz_section);
        questionText = findViewById(R.id.question_text);
        optionsGroup = findViewById(R.id.options_group);
        optionButtons = new RadioButton[]{
                findViewById(R.id.option_1),
                findViewById(R.id.option_2),
                findViewById(R.id.option_3),
                findViewById(R.id.option_4)
        };
        resultCard = findViewById(R.id.result_card);
        resultIcon = findViewById(R.id.result_icon);
        resultTitle = findViewById(R.id.result_title);
        resultBody = findViewById(R.id.result_body);
        scoreCard = findViewById(R.id.score_card);
        finalScore = findViewById(R.id.final_score);
        finalPercent = findViewById(R.id.final_percent);
        finalVerdict = findViewById(R.id.final_verdict);
        primaryButton = findViewById(R.id.primary_button);
    }

    private void onPrimaryClicked() {
        if (engine.isFinished()) {
            engine.restart();
            optionsGroup.clearCheck();
            render(true);
            return;
        }
        if (engine.isAnswered()) {
            engine.next();
            optionsGroup.clearCheck();
            render(true);
            scroll.smoothScrollTo(0, 0);
            return;
        }
        int selected = selectedOption();
        if (selected == QuizEngine.NO_SELECTION) {
            optionsGroup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            Toast.makeText(this, R.string.pick_first, Toast.LENGTH_SHORT).show();
            return;
        }
        engine.submit(selected);
        render(true);
        scroll.post(() -> scroll.smoothScrollTo(0, resultCard.getBottom()));
    }

    private int selectedOption() {
        int checkedId = optionsGroup.getCheckedRadioButtonId();
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].getId() == checkedId) {
                return i;
            }
        }
        return QuizEngine.NO_SELECTION;
    }

    /** Step 4: update the UI for the current state of the quiz. */
    private void render(boolean animate) {
        scoreLabel.setText(getString(R.string.score_format, engine.getScore()));
        progressBar.setProgressCompat(engine.getProgressPercent(), animate);

        if (engine.isFinished()) {
            showScore(animate);
            return;
        }

        quizSection.setVisibility(View.VISIBLE);
        scoreCard.setVisibility(View.GONE);
        progressLabel.setText(getString(R.string.progress_format,
                engine.getCurrentNumber(), engine.getTotal()));

        QuizQuestion question = engine.current();
        questionText.setText(question.getText());

        List<String> options = question.getOptions();
        boolean answered = engine.isAnswered();
        for (int i = 0; i < optionButtons.length; i++) {
            RadioButton button = optionButtons[i];
            if (i >= options.size()) {
                button.setVisibility(View.GONE);
                continue;
            }
            button.setVisibility(View.VISIBLE);
            button.setText(options.get(i));
            button.setEnabled(!answered); // disable the RadioButtons after submitting
            styleOption(button, i, question, answered);
        }

        if (answered) {
            showResult(question, animate);
            primaryButton.setText(engine.isLastQuestion() ? R.string.see_score : R.string.next_question);
        } else {
            resultCard.setVisibility(View.GONE);
            primaryButton.setText(R.string.submit);
        }
    }

    private void styleOption(RadioButton button, int index, QuizQuestion question, boolean answered) {
        Drawable endIcon = null;
        float alpha = 1f;
        if (!answered) {
            button.setBackgroundResource(R.drawable.bg_option);
        } else if (question.isCorrect(index)) {
            button.setBackgroundResource(R.drawable.bg_option_correct);
            endIcon = tinted(R.drawable.ic_check_circle, R.color.success);
        } else if (index == engine.getSelectedIndex()) {
            button.setBackgroundResource(R.drawable.bg_option_wrong);
            endIcon = tinted(R.drawable.ic_cancel, R.color.error);
        } else {
            button.setBackgroundResource(R.drawable.bg_option);
            alpha = 0.55f;
        }
        button.setAlpha(alpha);
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, endIcon, null);
        if (answered) {
            button.setChecked(index == engine.getSelectedIndex());
        }
    }

    private void showResult(QuizQuestion question, boolean animate) {
        boolean correct = engine.wasCorrect();
        int accent = ContextCompat.getColor(this, correct ? R.color.success : R.color.error);
        int background = ContextCompat.getColor(this, correct ? R.color.success_bg : R.color.error_bg);

        resultCard.setCardBackgroundColor(background);
        resultCard.setStrokeColor(accent);
        resultIcon.setImageResource(correct ? R.drawable.ic_check_circle : R.drawable.ic_cancel);
        resultIcon.setImageTintList(ColorStateList.valueOf(accent));
        resultTitle.setText(correct ? R.string.correct : R.string.incorrect);
        resultTitle.setTextColor(accent);
        String body = correct
                ? question.getExplanation()
                : getString(R.string.right_answer_format, question.getCorrectAnswer()) + " "
                + question.getExplanation();
        resultBody.setText(body);

        resultCard.setVisibility(View.VISIBLE);
        if (animate) {
            resultCard.setAlpha(0f);
            resultCard.setTranslationY(24f);
            resultCard.animate().alpha(1f).translationY(0f).setDuration(260).start();
        }
    }

    private void showScore(boolean animate) {
        quizSection.setVisibility(View.GONE);
        scoreCard.setVisibility(View.VISIBLE);
        progressLabel.setText(R.string.quiz_complete);

        int score = engine.getScore();
        int total = engine.getTotal();
        finalScore.setText(getString(R.string.final_score_format, score, total));
        finalPercent.setText(getString(R.string.final_percent_format, Math.round(score * 100f / total)));
        finalVerdict.setText(QuizEngine.verdict(score, total));
        primaryButton.setText(R.string.play_again);

        if (animate) {
            scoreCard.setScaleX(0.92f);
            scoreCard.setScaleY(0.92f);
            scoreCard.setAlpha(0f);
            scoreCard.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(320).start();
        }
    }

    private Drawable tinted(int drawableRes, int colorRes) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableRes);
        if (drawable == null) {
            return null;
        }
        drawable = drawable.mutate();
        drawable.setTint(ContextCompat.getColor(this, colorRes));
        return drawable;
    }
}
