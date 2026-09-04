package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.forichi.app.MainActivity;
import com.forichi.app.theme.ForiTheme;

public class RegisterActivity extends Activity {

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, float size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create("sans", Typeface.NORMAL));
        t.setIncludeFontPadding(false);
        return t;
    }

    private EditText field(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(ForiTheme.MUTED);
        e.setTextColor(Color.WHITE);
        e.setTextSize(14);
        e.setSingleLine(true);
        e.setPadding(dp(18), 0, dp(18), 0);
        ForiTheme.input(e);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(56)
                );
        lp.setMargins(0, dp(10), 0, 0);
        e.setLayoutParams(lp);

        return e;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        ForiTheme.page(scroll);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(22), dp(22), dp(30));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView back = text("‹", 34, ForiTheme.WHITE);
        back.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams backLp =
                new LinearLayout.LayoutParams(dp(48), dp(48));
        back.setLayoutParams(backLp);
        back.setBackground(ForiTheme.rounded(ForiTheme.CARD, 50));
        back.setOnClickListener(v -> finish());
        root.addView(back);

        TextView title = ForiTheme.title(this, "ساخت حساب کاربری");
        title.setTextSize(25);
        LinearLayout.LayoutParams titleLp =
                new LinearLayout.LayoutParams(-1, dp(55));
        titleLp.setMargins(0, dp(20), 0, 0);
        title.setLayoutParams(titleLp);
        root.addView(title);

        TextView sub = ForiTheme.subtitle(
                this,
                "در چند قدم ساده حساب ForiChi خودت را بساز"
        );
        root.addView(sub);

        EditText name = field("نام و نام خانوادگی");
        root.addView(name);

        EditText phone = field("شماره موبایل");
        phone.setInputType(
                android.text.InputType.TYPE_CLASS_PHONE
        );
        root.addView(phone);

        EditText password = field("رمز عبور");
        password.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        root.addView(password);

        EditText confirm = field("تکرار رمز عبور");
        confirm.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        root.addView(confirm);

        TextView register = text("ساخت حساب", 15, Color.WHITE);
        register.setGravity(Gravity.CENTER);
        register.setTypeface(
                Typeface.create("sans", Typeface.BOLD)
        );

        LinearLayout.LayoutParams registerLp =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(58)
                );
        registerLp.setMargins(0, dp(22), 0, 0);
        register.setLayoutParams(registerLp);

        ForiTheme.primary(register);

        register.setOnClickListener(v -> {

            String n = name.getText().toString().trim();
            String p = phone.getText().toString().trim();
            String pass = password.getText().toString();
            String conf = confirm.getText().toString();

            if (n.length() < 2) {
                name.setError("نام را وارد کن");
                name.requestFocus();
                return;
            }

            if (p.length() < 10) {
                phone.setError("شماره موبایل معتبر وارد کن");
                phone.requestFocus();
                return;
            }

            if (pass.length() < 4) {
                password.setError("رمز عبور حداقل ۴ کاراکتر باشد");
                password.requestFocus();
                return;
            }

            if (!pass.equals(conf)) {
                confirm.setError("رمزهای عبور یکسان نیستند");
                confirm.requestFocus();
                return;
            }

            getSharedPreferences("forichi_account", MODE_PRIVATE)
                    .edit()
                    .putString("name", n)
                    .putString("phone", p)
                    .putString("password", pass)
                    .putBoolean("logged_in", true)
                    .apply();

            Toast.makeText(
                    this,
                    "حساب با موفقیت ساخته شد",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new android.content.Intent(
                            this,
                            LoginActivity.class
                    )
            );

            finish();
        });

        root.addView(register);

        TextView login = text(
                "قبلاً حساب داری؟ ورود به حساب",
                14,
                ForiTheme.TURQUOISE
        );
        login.setGravity(Gravity.CENTER);
        login.setPadding(0, dp(18), 0, dp(18));
        login.setOnClickListener(v -> finish());
        root.addView(login);

        scroll.addView(root);
        setContentView(scroll);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);
    }
}
