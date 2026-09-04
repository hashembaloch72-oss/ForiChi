package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.forichi.app.theme.ForiTheme;

public class SpecialistRegisterActivity extends Activity {

    private int dp(int v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String s, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create("sans", bold ? Typeface.BOLD : Typeface.NORMAL));
        t.setIncludeFontPadding(false);
        return t;
    }

    private EditText field(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(ForiTheme.MUTED);
        e.setTextColor(ForiTheme.WHITE);
        e.setTextSize(14);
        e.setSingleLine(true);
        e.setPadding(dp(18), 0, dp(18), 0);
        e.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        ForiTheme.input(e);
        return e;
    }

    private LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(15), dp(18), dp(20));
        ForiTheme.page(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView back = text("‹", 34, ForiTheme.WHITE, false);
        back.setGravity(Gravity.CENTER);
        ForiTheme.secondary(back);
        back.setOnClickListener(v -> finish());

        header.addView(back, lp(dp(48), dp(48)));

        TextView title = text(
                "ثبت‌نام متخصص",
                22,
                ForiTheme.WHITE,
                true
        );

        LinearLayout.LayoutParams titleLp = lp(0, dp(48));
        titleLp.weight = 1;
        titleLp.setMargins(dp(12), 0, 0, 0);

        header.addView(title, titleLp);

        root.addView(header, lp(-1, dp(58)));

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, dp(10), 0, dp(20));

        TextView intro = text(
                "مهارتت را در ForiChi معرفی کن",
                21,
                ForiTheme.WHITE,
                true
        );

        content.addView(intro, lp(-1, dp(42)));

        TextView sub = text(
                "اطلاعاتت را وارد کن تا درخواست همکاری برای بررسی ارسال شود.",
                12,
                ForiTheme.MUTED,
                false
        );

        content.addView(sub, lp(-1, dp(40)));

        EditText name = field("نام و نام خانوادگی");
        content.addView(name, margin(-1, 56, 0, 12, 0, 0));

        EditText specialty = field("تخصص شما؛ مثال: طراحی سایت");
        content.addView(specialty, margin(-1, 56, 0, 12, 0, 0));

        EditText city = field("شهر؛ مثال: چابهار");
        content.addView(city, margin(-1, 56, 0, 12, 0, 0));

        EditText experience = field("سابقه کاری؛ مثال: ۳ سال");
        content.addView(experience, margin(-1, 56, 0, 12, 0, 0));

        EditText description = field("توضیح کوتاه درباره مهارت و خدمات");
        description.setSingleLine(false);
        description.setGravity(Gravity.TOP | Gravity.RIGHT);
        description.setPadding(
                dp(18), dp(15), dp(18), dp(15)
        );

        content.addView(
                description,
                margin(-1, 110, 0, 12, 0, 0)
        );

        TextView notice = text(
                "🛡️ اطلاعات شما پس از ارسال توسط مدیریت ForiChi بررسی می‌شود.",
                11,
                ForiTheme.MUTED,
                false
        );

        notice.setPadding(dp(15), dp(12), dp(15), dp(12));
        ForiTheme.card(notice);

        content.addView(
                notice,
                margin(-1, 62, 0, 15, 0, 0)
        );

        TextView submit = text(
                "ارسال درخواست همکاری  ➜",
                15,
                ForiTheme.WHITE,
                true
        );

        ForiTheme.primary(submit);

        content.addView(
                submit,
                margin(-1, 56, 0, 15, 0, 0)
        );

        submit.setOnClickListener(v -> {

            String n = name.getText().toString().trim();
            String s = specialty.getText().toString().trim();
            String c = city.getText().toString().trim();

            if (n.isEmpty() || s.isEmpty() || c.isEmpty()) {
                Toast.makeText(
                        this,
                        "نام، تخصص و شهر را وارد کنید",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            SharedPreferences sp =
                    getSharedPreferences(
                            "forichi_specialist",
                            MODE_PRIVATE
                    );

            sp.edit()
                    .putBoolean("registered", true)
                    .putString("name", n)
                    .putString("specialty", s)
                    .putString("city", c)
                    .putString(
                            "experience",
                            experience.getText().toString().trim()
                    )
                    .putString(
                            "description",
                            description.getText().toString().trim()
                    )
                    .putString("status", "در انتظار بررسی")
                    .apply();

            Toast.makeText(
                    this,
                    "درخواست شما با موفقیت ارسال شد",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        });

        TextView status = text(
                "وضعیت درخواست: در انتظار بررسی",
                12,
                ForiTheme.GOLD,
                true
        );

        status.setGravity(Gravity.CENTER);

        content.addView(
                status,
                lp(-1, dp(35))
        );

        scroll.addView(content);
        root.addView(scroll, lp(-1, 0));

        setContentView(root);
    }

    private LinearLayout.LayoutParams margin(
            int w,
            int h,
            int l,
            int t,
            int r,
            int b) {

        LinearLayout.LayoutParams p = lp(
                w,
                dp(h)
        );

        p.setMargins(
                dp(l),
                dp(t),
                dp(r),
                dp(b)
        );

        return p;
    }
}
