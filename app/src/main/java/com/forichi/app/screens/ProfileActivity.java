package com.forichi.app.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.forichi.app.theme.ForiTheme;

public class ProfileActivity extends Activity {

    LinearLayout root;

    int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(
                w == -1 ? -1 : dp(w),
                h == -1 ? -1 : dp(h)
        );
    }

    LinearLayout.LayoutParams margin(
            int w, int h,
            int l, int t, int r, int b) {

        LinearLayout.LayoutParams p = lp(w, h);
        p.setMargins(
                dp(l), dp(t),
                dp(r), dp(b)
        );
        return p;
    }

    TextView txt(
            String s,
            float size,
            int color,
            boolean bold) {

        TextView t = new TextView(this);

        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);

        t.setTypeface(
                Typeface.create(
                        "sans",
                        bold
                                ? Typeface.BOLD
                                : Typeface.NORMAL
                )
        );

        t.setIncludeFontPadding(false);
        t.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        return t;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(
                ForiTheme.NAVY
        );

        getWindow().setNavigationBarColor(
                ForiTheme.NAVY
        );

        root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(10)
        );

        ForiTheme.page(root);

        header();
        profileCard();
        menu();

        String city = getSharedPreferences(
                "forichi_location",
                MODE_PRIVATE
        ).getString("city", "انتخاب نشده");

        TextView cityView = txt(
                "📍  شهر من: " + city,
                14,
                ForiTheme.WHITE,
                true
        );

        cityView.setPadding(dp(18), 0, dp(18), 0);
        ForiTheme.card(cityView);

        cityView.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ProfileActivity.this,
                    CityActivity.class
            );
            startActivity(intent);
        });

        root.addView(
                cityView,
                margin(-1, 58, 0, 10, 0, 0)
        );

        setContentView(root);
    }

    void header() {

        LinearLayout h = new LinearLayout(this);

        h.setGravity(
                Gravity.CENTER_VERTICAL
        );

        h.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        TextView back = txt(
                "‹",
                34,
                ForiTheme.WHITE,
                false
        );

        back.setGravity(Gravity.CENTER);

        back.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(18)
                )
        );

        back.setOnClickListener(
                v -> finish()
        );

        ForiTheme.press(back);

        h.addView(
                back,
                lp(48, 52)
        );

        TextView title =
                ForiTheme.title(
                        this,
                        "پروفایل من"
                );

        h.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        1
                )
        );

        root.addView(
                h,
                margin(
                        -1,
                        52,
                        0,
                        0,
                        0,
                        18
                )
        );
    }

    void profileCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                dp(18),
                0,
                dp(18),
                0
        );

        card.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        ForiTheme.card(card);

        TextView avatar = txt(
                "◉",
                32,
                ForiTheme.TURQUOISE,
                true
        );

        avatar.setGravity(Gravity.CENTER);

        avatar.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD2,
                        dp(50)
                )
        );

        card.addView(
                avatar,
                lp(68, 68)
        );

        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        android.content.SharedPreferences account =
                getSharedPreferences(
                        "forichi_account",
                        MODE_PRIVATE
                );

        boolean loggedIn =
                account.getBoolean(
                        "logged_in",
                        false
                );

        String name =
                account.getString(
                        "name",
                        "مهمان ForiChi"
                );

        String phone =
                account.getString(
                        "phone",
                        ""
                );

        TextView nameText = txt(
                loggedIn
                        ? name
                        : "مهمان ForiChi",
                17,
                ForiTheme.WHITE,
                true
        );

        TextView sub = txt(
                loggedIn
                        ? (
                            phone.isEmpty()
                                ? "حساب ForiChi"
                                : phone
                        )
                        : "برای استفاده کامل وارد حساب شو",
                12,
                ForiTheme.MUTED,
                false
        );

        info.addView(
                nameText,
                lp(-1, 30)
        );

        info.addView(
                sub,
                lp(-1, 25)
        );

        card.addView(
                info,
                new LinearLayout.LayoutParams(
                        0,
                        dp(70),
                        1
                )
        );

        root.addView(
                card,
                margin(
                        -1,
                        92,
                        0,
                        0,
                        0,
                        16
                )
        );
    }

    void menu() {

        addMenu(
                "حساب کاربری",
                "›",
                v -> openLogin()
        );

        addMenu(
                "خدمات من",
                "›",
                v -> Toast.makeText(
                        this,
                        "بخش خدمات من به‌زودی فعال می‌شود",
                        Toast.LENGTH_SHORT
                ).show()
        );

        addMenu(
                "کیف پول",
                "›",
                v -> openWallet()
        );

        addMenu(
                "اعلان‌ها",
                "›",
                v -> openNotifications()
        );

        addMenu(
                "تنظیمات",
                "›",
                v -> openSettings()
        );

        android.content.SharedPreferences account =
                getSharedPreferences(
                        "forichi_account",
                        MODE_PRIVATE
                );

        boolean loggedIn =
                account.getBoolean(
                        "logged_in",
                        false
                );

        TextView login = txt(
                loggedIn
                        ? "خروج از حساب"
                        : "ورود / ثبت‌نام",
                14,
                ForiTheme.WHITE,
                true
        );

        login.setGravity(Gravity.CENTER);

        ForiTheme.primary(login);

        login.setOnClickListener(v -> {

            if (loggedIn) {

                account.edit()
                        .putBoolean(
                                "logged_in",
                                false
                        )
                        .apply();

                Toast.makeText(
                        this,
                        "از حساب خارج شدی",
                        Toast.LENGTH_SHORT
                ).show();

                recreate();

            } else {

                openLogin();
            }
        });

        root.addView(
                login,
                margin(
                        -1,
                        56,
                        0,
                        15,
                        0,
                        0
                )
        );
    }

    void addMenu(
            String title,
            String arrow,
            View.OnClickListener listener) {

        LinearLayout item =
                new LinearLayout(this);

        item.setGravity(
                Gravity.CENTER_VERTICAL
        );

        item.setPadding(
                dp(18),
                0,
                dp(18),
                0
        );

        item.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        ForiTheme.card2(item);

        TextView t = txt(
                title,
                14,
                ForiTheme.WHITE,
                true
        );

        item.addView(
                t,
                new LinearLayout.LayoutParams(
                        0,
                        dp(54),
                        1
                )
        );

        TextView a = txt(
                arrow,
                24,
                ForiTheme.TURQUOISE,
                true
        );

        a.setGravity(Gravity.CENTER);

        item.addView(
                a,
                lp(35, 54)
        );

        item.setOnClickListener(listener);

        ForiTheme.press(item);

        root.addView(
                item,
                margin(
                        -1,
                        54,
                        0,
                        0,
                        0,
                        9
                )
        );
    }

    void openLogin() {

        startActivity(
                new android.content.Intent(
                        this,
                        LoginActivity.class
                )
        );
    }

    void openWallet() {

        android.content.SharedPreferences account =
                getSharedPreferences(
                        "forichi_account",
                        MODE_PRIVATE
                );

        boolean loggedIn =
                account.getBoolean(
                        "logged_in",
                        false
                );

        if (!loggedIn) {

            Toast.makeText(
                    this,
                    "برای استفاده از کیف پول ابتدا وارد حساب شو",
                    Toast.LENGTH_SHORT
            ).show();

            openLogin();
            return;
        }

        startActivity(
                new android.content.Intent(
                        this,
                        WalletActivity.class
                )
        );
    }

    void openNotifications() {

        startActivity(
                new android.content.Intent(
                        this,
                        NotificationsActivity.class
                )
        );
    }

    void openSettings() {

        startActivity(
                new android.content.Intent(
                        this,
                        SettingsActivity.class
                )
        );
    }
}
