package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.forichi.app.theme.ForiTheme;

public class LoginActivity extends Activity {

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String s, float size, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        t.setIncludeFontPadding(false);
        return t;
    }

    private LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }

    private EditText input(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(ForiTheme.MUTED);
        e.setTextColor(ForiTheme.WHITE);
        e.setTextSize(14);
        e.setSingleLine(true);
        e.setPadding(dp(18), 0, dp(18), 0);
        e.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        e.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        e.setBackground(
                ForiTheme.rounded(ForiTheme.INPUT, dp(22))
        );
        return e;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(18));
        ForiTheme.page(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView back = text("‹", 34, ForiTheme.WHITE);
        back.setGravity(Gravity.CENTER);
        back.setBackground(
                ForiTheme.rounded(ForiTheme.CARD, dp(18))
        );
        back.setOnClickListener(v -> finish());

        header.addView(back, lp(dp(48), dp(48)));

        TextView title = text(
                "ورود به ForiChi",
                21,
                ForiTheme.WHITE
        );
        title.setTypeface(Typeface.DEFAULT_BOLD);

        LinearLayout.LayoutParams titleLp =
                lp(0, dp(48));
        titleLp.weight = 1;
        titleLp.setMargins(dp(12), 0, 0, 0);

        header.addView(title, titleLp);
        root.addView(header, lp(-1, dp(58)));

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(5), dp(25), dp(5), dp(20));

        // Logo
        TextView logo = text(
                "F",
                42,
                ForiTheme.WHITE
        );
        logo.setGravity(Gravity.CENTER);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        logo.setBackground(
                ForiTheme.gradient(
                        ForiTheme.TURQUOISE,
                        Color.rgb(0, 130, 145),
                        dp(32)
                )
        );

        content.addView(logo, lp(dp(90), dp(90)));

        TextView welcome = text(
                "خوش برگشتی",
                24,
                ForiTheme.WHITE
        );
        welcome.setGravity(Gravity.CENTER);
        welcome.setTypeface(Typeface.DEFAULT_BOLD);

        LinearLayout.LayoutParams welcomeLp =
                lp(-1, dp(45));
        welcomeLp.setMargins(0, dp(18), 0, 0);

        content.addView(welcome, welcomeLp);

        TextView desc = text(
                "برای ادامه شماره موبایل خود را وارد کن.",
                13,
                ForiTheme.MUTED
        );
        desc.setGravity(Gravity.CENTER);

        content.addView(desc, lp(-1, dp(40)));

        // Phone
        EditText phone = input("شماره موبایل");

        phone.setInputType(
                InputType.TYPE_CLASS_PHONE
        );

        LinearLayout.LayoutParams phoneLp =
                lp(-1, dp(58));
        phoneLp.setMargins(0, dp(18), 0, dp(10));

        content.addView(phone, phoneLp);

        // Password
        EditText password = input("رمز عبور");

        password.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        content.addView(password, lp(-1, dp(58)));

        // Forgot
        TextView forgot = text(
                "رمز عبور را فراموش کرده‌ام",
                12,
                ForiTheme.TURQUOISE
        );
        forgot.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams forgotLp =
                lp(-1, dp(45));
        forgotLp.setMargins(0, dp(4), 0, dp(12));

        content.addView(forgot, forgotLp);

        ForiTheme.press(forgot);

        forgot.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "بازیابی رمز عبور به‌زودی فعال می‌شود",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // Login
        TextView login = text(
                "ورود به حساب",
                15,
                ForiTheme.WHITE
        );

        ForiTheme.primary(login);

        content.addView(login, lp(-1, dp(58)));

        login.setOnClickListener(v -> {

            String number = phone.getText().toString().trim();
            String pass = password.getText().toString();

            if (number.length() < 10) {
                Toast.makeText(
                        this,
                        "شماره موبایل را صحیح وارد کن",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (pass.length() < 4) {
                Toast.makeText(
                        this,
                        "رمز عبور حداقل ۴ کاراکتر باشد",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            android.content.SharedPreferences account =
                    getSharedPreferences("forichi_account", MODE_PRIVATE);

            String savedPhone = account.getString("phone", "");
            String savedPassword = account.getString("password", "");

            if (savedPhone.isEmpty()) {
                Toast.makeText(
                        this,
                        "ابتدا یک حساب بساز",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (!number.equals(savedPhone) || !pass.equals(savedPassword)) {
                Toast.makeText(
                        this,
                        "شماره موبایل یا رمز عبور اشتباه است",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            account.edit()
                    .putBoolean("logged_in", true)
                    .apply();

            Toast.makeText(
                    this,
                    "خوش آمدی 👋",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new android.content.Intent(
                            this,
                            com.forichi.app.MainActivity.class
                    )
            );

            finish();
        });

        // Register
        TextView register = text(
                "حساب نداری؟  ثبت‌نام کن",
                14,
                ForiTheme.TURQUOISE
        );
        register.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams registerLp =
                lp(-1, dp(55));
        registerLp.setMargins(0, dp(8), 0, 0);

        content.addView(register, registerLp);

        ForiTheme.press(register);

        register.setOnClickListener(v -> {
            startActivity(
                    new android.content.Intent(
                            this,
                            RegisterActivity.class
                    )
            );
        });

        scroll.addView(content);
        root.addView(scroll, lp(-1, 0));

        setContentView(root);
    }
}
