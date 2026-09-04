package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.forichi.app.theme.ForiTheme;

public class AdminActivity extends Activity {

    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);

        buildUI();
    }

    private TextView text(String value, float size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans", Typeface.BOLD));
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setIncludeFontPadding(false);
        return t;
    }

    private TextView menu(String icon, String title) {

        TextView item = text(icon + "   " + title, 14, ForiTheme.WHITE);
        item.setPadding(20, 0, 20, 0);

        ForiTheme.card(item);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(58)
                );

        lp.setMargins(0, 0, 0, dp(10));
        item.setLayoutParams(lp);

        ForiTheme.press(item);

        item.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        title + " • به‌زودی فعال می‌شود",
                        Toast.LENGTH_SHORT
                ).show()
        );

        return item;
    }

    private TextView stat(String icon, String number, String title) {

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(10, 15, 10, 15);

        ForiTheme.card(box);

        TextView i = text(icon, 24, ForiTheme.WHITE);
        i.setGravity(Gravity.CENTER);

        TextView n = text(number, 23, ForiTheme.WHITE);
        n.setGravity(Gravity.CENTER);

        TextView t = text(title, 11, ForiTheme.MUTED);
        t.setGravity(Gravity.CENTER);

        box.addView(i, new LinearLayout.LayoutParams(-1, dp(32)));
        box.addView(n, new LinearLayout.LayoutParams(-1, dp(35)));
        box.addView(t, new LinearLayout.LayoutParams(-1, dp(25)));

        returnBoxLayout(box);
        return convertToText(box);
    }

    private void returnBoxLayout(LinearLayout box) {
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        0,
                        dp(125),
                        1
                );
        lp.setMargins(dp(5), 0, dp(5), 0);
        box.setLayoutParams(lp);
    }

    private TextView convertToText(LinearLayout box) {
        TextView fake = new TextView(this);
        fake.setTag(box);
        return fake;
    }

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void addSectionTitle(String title) {

        TextView t = text(title, 18, ForiTheme.WHITE);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(45)
                );

        lp.setMargins(0, dp(14), 0, dp(4));

        root.addView(t, lp);
    }

    private void buildUI() {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(25));

        ForiTheme.page(root);

        scroll.addView(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text("‹", 34, ForiTheme.WHITE);
        back.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams backLp =
                new LinearLayout.LayoutParams(dp(48), dp(48));

        back.setLayoutParams(backLp);
        ForiTheme.secondary(back);

        back.setOnClickListener(v -> finish());

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setPadding(dp(12), 0, 0, 0);

        TextView title = text("مدیریت ForiChi", 24, ForiTheme.WHITE);
        TextView sub = text("مرکز کنترل اپلیکیشن", 11, ForiTheme.MUTED);

        titleBox.addView(title);
        titleBox.addView(sub);

        header.addView(back);
        header.addView(
                titleBox,
                new LinearLayout.LayoutParams(0, dp(60), 1)
        );

        root.addView(
                header,
                new LinearLayout.LayoutParams(-1, dp(65))
        );

        // Welcome
        LinearLayout welcome = new LinearLayout(this);
        welcome.setOrientation(LinearLayout.VERTICAL);
        welcome.setPadding(dp(20), dp(20), dp(20), dp(20));

        ForiTheme.gradient(
                Color.rgb(0, 145, 155),
                Color.rgb(18, 48, 72),
                30
        );

        welcome.setBackground(
                ForiTheme.gradient(
                        Color.rgb(0, 145, 155),
                        Color.rgb(18, 48, 72),
                        30
                )
        );

        TextView wt = text("⚡ پنل مدیریت", 14, Color.WHITE);
        TextView wh = text("سلام مدیر 👋", 23, Color.WHITE);
        TextView ws = text(
                "اینجا همه بخش‌های ForiChi را کنترل می‌کنی.",
                12,
                Color.rgb(220, 235, 238)
        );

        welcome.addView(wt);
        welcome.addView(wh, new LinearLayout.LayoutParams(-1, dp(38)));
        welcome.addView(ws);

        LinearLayout.LayoutParams welcomeLp =
                new LinearLayout.LayoutParams(-1, dp(125));
        welcomeLp.setMargins(0, dp(15), 0, 0);

        root.addView(welcome, welcomeLp);

        // Statistics
        addSectionTitle("نمای کلی");

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);

        addStatCard(stats, "👥", "0", "کاربران");
        addStatCard(stats, "🧑‍🔧", "0", "متخصص‌ها");
        addStatCard(stats, "📢", "0", "آگهی‌ها");
        addStatCard(stats, "📦", "0", "سفارش‌ها");

        root.addView(
                stats,
                new LinearLayout.LayoutParams(-1, dp(125))
        );

        // Management
        addSectionTitle("مدیریت بخش‌ها");

        root.addView(menu("👥", "کاربران"));
        root.addView(menu("🧑‍🔧", "متخصص‌ها"));
        root.addView(menu("📢", "آگهی‌ها و خدمات"));
        root.addView(menu("📦", "سفارش‌ها"));
        root.addView(menu("💰", "کیف پول و تراکنش‌ها"));
        root.addView(menu("🔔", "اعلان‌ها"));
        root.addView(menu("🏙️", "شهرها و دسته‌بندی‌ها"));
        root.addView(menu("⚙️", "تنظیمات اپ"));

        // Security
        addSectionTitle("امنیت");

        TextView security = text(
                "🔐  حساب مدیر\n\n" +
                "دسترسی مدیریت فقط برای حساب‌های مجاز فعال خواهد بود.",
                13,
                ForiTheme.MUTED
        );

        security.setPadding(dp(18), dp(18), dp(18), dp(18));
        ForiTheme.card(security);

        root.addView(
                security,
                new LinearLayout.LayoutParams(-1, dp(105))
        );

        setContentView(scroll);
    }

    private void addStatCard(
            LinearLayout parent,
            String icon,
            String number,
            String title) {

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(5, 8, 5, 8);

        ForiTheme.card(box);

        TextView i = text(icon, 22, Color.WHITE);
        i.setGravity(Gravity.CENTER);

        TextView n = text(number, 21, Color.WHITE);
        n.setGravity(Gravity.CENTER);

        TextView t = text(title, 10, ForiTheme.MUTED);
        t.setGravity(Gravity.CENTER);

        box.addView(i, new LinearLayout.LayoutParams(-1, dp(28)));
        box.addView(n, new LinearLayout.LayoutParams(-1, dp(32)));
        box.addView(t, new LinearLayout.LayoutParams(-1, dp(22)));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        0,
                        dp(115),
                        1
                );

        lp.setMargins(dp(4), 0, dp(4), 0);

        parent.addView(box, lp);
    }
}
