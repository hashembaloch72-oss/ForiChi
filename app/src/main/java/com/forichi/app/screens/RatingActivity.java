package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.forichi.app.theme.ForiTheme;

public class RatingActivity extends Activity {

    private int dp(int v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String s, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create(
                "sans",
                bold ? Typeface.BOLD : Typeface.NORMAL
        ));
        t.setIncludeFontPadding(false);
        return t;
    }

    private LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }

    private LinearLayout.LayoutParams margin(
            int w, int h,
            int l, int t, int r, int b) {

        LinearLayout.LayoutParams p = lp(w, dp(h));

        p.setMargins(
                dp(l),
                dp(t),
                dp(r),
                dp(b)
        );

        return p;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(
                dp(18), dp(15),
                dp(18), dp(20)
        );

        ForiTheme.page(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text(
                "‹",
                34,
                ForiTheme.WHITE,
                false
        );

        back.setGravity(Gravity.CENTER);
        ForiTheme.secondary(back);
        back.setOnClickListener(v -> finish());

        header.addView(
                back,
                lp(dp(48), dp(48))
        );

        TextView title = text(
                "امتیاز و نظر",
                22,
                ForiTheme.WHITE,
                true
        );

        LinearLayout.LayoutParams titleLp =
                lp(0, dp(48));

        titleLp.weight = 1;
        titleLp.setMargins(
                dp(12), 0, 0, 0
        );

        header.addView(title, titleLp);

        root.addView(
                header,
                lp(-1, dp(58))
        );

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(
                LinearLayout.VERTICAL
        );

        // Specialist card
        LinearLayout specialist = new LinearLayout(this);
        specialist.setOrientation(
                LinearLayout.VERTICAL
        );
        specialist.setGravity(
                Gravity.CENTER_HORIZONTAL
        );
        specialist.setPadding(
                dp(20), dp(20),
                dp(20), dp(20)
        );

        ForiTheme.card(specialist);

        TextView avatar = text(
                "★",
                34,
                ForiTheme.GOLD,
                true
        );

        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(
                ForiTheme.rounded(
                        ForiTheme.INPUT,
                        dp(45)
                )
        );

        specialist.addView(
                avatar,
                lp(dp(82), dp(82))
        );

        TextView name = text(
                "متخصص ForiChi",
                20,
                ForiTheme.WHITE,
                true
        );

        name.setGravity(Gravity.CENTER);

        specialist.addView(
                name,
                lp(-1, dp(35))
        );

        TextView service = text(
                "طراحی و ساخت سایت",
                12,
                ForiTheme.MUTED,
                false
        );

        service.setGravity(Gravity.CENTER);

        specialist.addView(
                service,
                lp(-1, dp(28))
        );

        content.addView(
                specialist,
                margin(-1, 180, 0, 12, 0, 0)
        );

        TextView question = text(
                "تجربه شما چطور بود؟",
                20,
                ForiTheme.WHITE,
                true
        );

        question.setGravity(Gravity.CENTER);

        content.addView(
                question,
                margin(-1, 42, 0, 16, 0, 0)
        );

        // Stars
        LinearLayout stars = new LinearLayout(this);
        stars.setGravity(Gravity.CENTER);
        stars.setLayoutDirection(
                View.LAYOUT_DIRECTION_LTR
        );

        TextView[] starViews = new TextView[5];

        final int[] rating = {5};

        for (int i = 0; i < 5; i++) {

            final int index = i;

            TextView star = text(
                    "★",
                    38,
                    ForiTheme.GOLD,
                    true
            );

            star.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams starLp =
                    new LinearLayout.LayoutParams(
                            dp(55),
                            dp(60)
                    );

            stars.addView(star, starLp);

            starViews[i] = star;

            star.setOnClickListener(v -> {

                rating[0] = index + 1;

                for (int j = 0; j < 5; j++) {
                    starViews[j].setTextColor(
                            j < rating[0]
                                    ? ForiTheme.GOLD
                                    : ForiTheme.MUTED
                    );
                }
            });
        }

        content.addView(
                stars,
                lp(-1, dp(65))
        );

        TextView ratingText = text(
                "۵ از ۵",
                13,
                ForiTheme.GOLD,
                true
        );

        ratingText.setGravity(Gravity.CENTER);

        content.addView(
                ratingText,
                lp(-1, dp(30))
        );

        EditText comment = new EditText(this);

        comment.setHint(
                "نظر خود را درباره کیفیت کار بنویسید..."
        );

        comment.setHintTextColor(
                ForiTheme.MUTED
        );

        comment.setTextColor(
                ForiTheme.WHITE
        );

        comment.setTextSize(13);
        comment.setGravity(
                Gravity.TOP | Gravity.RIGHT
        );

        comment.setSingleLine(false);

        comment.setPadding(
                dp(18), dp(15),
                dp(18), dp(15)
        );

        ForiTheme.input(comment);

        content.addView(
                comment,
                margin(-1, 125, 0, 15, 0, 0)
        );

        TextView submit = text(
                "ثبت امتیاز و نظر  ✓",
                15,
                ForiTheme.WHITE,
                true
        );

        ForiTheme.primary(submit);

        content.addView(
                submit,
                margin(-1, 56, 0, 0, 0, 0)
        );

        submit.setOnClickListener(v -> {

            SharedPreferences sp =
                    getSharedPreferences(
                            "forichi_rating",
                            MODE_PRIVATE
                    );

            sp.edit()
                    .putInt("rating", rating[0])
                    .putString(
                            "comment",
                            comment.getText()
                                    .toString()
                    )
                    .apply();

            Toast.makeText(
                    this,
                    "نظر شما با موفقیت ثبت شد ⭐",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });

        scroll.addView(content);

        root.addView(
                scroll,
                lp(-1, 0)
        );

        setContentView(root);
    }
}
