package com.forichi.app.screens;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.forichi.app.theme.ForiTheme;
import com.forichi.app.LocationHelper;

public class CityActivity extends Activity {

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

    private static class CityLocationCallback
            implements LocationHelper.Callback {

        private final CityActivity activity;

        CityLocationCallback(CityActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onLocation(Location location) {

            activity.getSharedPreferences(
                    "forichi_location",
                    Activity.MODE_PRIVATE
            ).edit()
                    .putString(
                            "city",
                            "موقعیت فعلی"
                    )
                    .putFloat(
                            "latitude",
                            (float) location.getLatitude()
                    )
                    .putFloat(
                            "longitude",
                            (float) location.getLongitude()
                    )
                    .apply();

            Toast.makeText(
                    activity,
                    "موقعیت شما با موفقیت ثبت شد ✓",
                    Toast.LENGTH_LONG
            ).show();

            activity.finish();
        }

        @Override
        public void onError(String message) {

            Toast.makeText(
                    activity,
                    message,
                    Toast.LENGTH_LONG
            ).show();
        }
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

        TextView title = text("انتخاب شهر", 21, ForiTheme.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);

        LinearLayout.LayoutParams titleLp = lp(0, dp(48));
        titleLp.weight = 1;
        titleLp.setMargins(dp(12), 0, 0, 0);

        header.addView(title, titleLp);
        root.addView(header, lp(-1, dp(58)));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        // Intro
        LinearLayout intro = new LinearLayout(this);
        intro.setOrientation(LinearLayout.VERTICAL);
        intro.setGravity(Gravity.CENTER_HORIZONTAL);
        intro.setPadding(dp(20), dp(20), dp(20), dp(20));
        ForiTheme.card(intro);

        TextView locationIcon = text("⌖", 42, ForiTheme.TURQUOISE);
        locationIcon.setGravity(Gravity.CENTER);

        intro.addView(locationIcon, lp(-1, dp(65)));

        TextView introTitle = text(
                "خدمات اطراف خودت را پیدا کن",
                19,
                ForiTheme.WHITE
        );
        introTitle.setGravity(Gravity.CENTER);
        introTitle.setTypeface(Typeface.DEFAULT_BOLD);

        intro.addView(introTitle, lp(-1, dp(35)));

        TextView introText = text(
                "شهر خودت را انتخاب کن تا خدمات و متخصص‌های نزدیک‌تر را ببینی.",
                12,
                ForiTheme.MUTED
        );
        introText.setGravity(Gravity.CENTER);
        introText.setLineSpacing(dp(3), 1f);

        intro.addView(introText, lp(-1, dp(45)));

        content.addView(intro, lp(-1, dp(180)));

        // Current location
        TextView gps = text(
                "⌖   استفاده از موقعیت فعلی",
                15,
                ForiTheme.TURQUOISE
        );
        gps.setGravity(Gravity.CENTER);
        gps.setTypeface(Typeface.DEFAULT_BOLD);
        gps.setBackground(
                ForiTheme.rounded(ForiTheme.INPUT, dp(22))
        );
        ForiTheme.press(gps);

        LinearLayout.LayoutParams gpsLp = lp(-1, dp(58));
        gpsLp.setMargins(0, dp(14), 0, dp(8));

        content.addView(gps, gpsLp);

        gps.setOnClickListener(v -> {

            Toast.makeText(
                    CityActivity.this,
                    "در حال پیدا کردن موقعیت شما... 📍",
                    Toast.LENGTH_SHORT
            ).show();

            LocationHelper.requestLocation(
                    CityActivity.this,
                    new CityLocationCallback(CityActivity.this)
            );
        });

        TextView popular = text(
                "شهرهای فعال",
                17,
                ForiTheme.WHITE
        );
        popular.setTypeface(Typeface.DEFAULT_BOLD);

        LinearLayout.LayoutParams popularLp =
                lp(-1, dp(40));

        popularLp.setMargins(dp(4), dp(10), dp(4), dp(4));

        content.addView(popular, popularLp);

        addCity(content, "چابهار", "سیستان و بلوچستان");
        addCity(content, "کنارک", "سیستان و بلوچستان");

        TextView more = text(
                "شهرهای دیگر به‌زودی فعال می‌شوند",
                12,
                ForiTheme.MUTED
        );
        more.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams moreLp =
                lp(-1, dp(50));

        moreLp.setMargins(0, dp(10), 0, dp(20));

        content.addView(more, moreLp);

        scroll.addView(content);
        root.addView(scroll, lp(-1, 0));

        setContentView(root);
    }

    private void addCity(
            LinearLayout parent,
            String city,
            String province
    ) {
        LinearLayout card = new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(10), dp(16), dp(10));
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ForiTheme.card2(card);

        TextView icon = text("⌖", 24, ForiTheme.TURQUOISE);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(
                ForiTheme.rounded(ForiTheme.INPUT, dp(18))
        );

        card.addView(icon, lp(dp(50), dp(50)));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(dp(12), 0, 0, 0);

        TextView name = text(city, 16, ForiTheme.WHITE);
        name.setTypeface(Typeface.DEFAULT_BOLD);

        TextView sub = text(province, 11, ForiTheme.MUTED);

        info.addView(name, lp(-1, dp(28)));
        info.addView(sub, lp(-1, dp(20)));

        LinearLayout.LayoutParams infoLp =
                lp(0, dp(52));
        infoLp.weight = 1;

        card.addView(info, infoLp);

        TextView arrow = text("‹", 28, ForiTheme.MUTED);
        arrow.setGravity(Gravity.CENTER);

        card.addView(arrow, lp(dp(40), dp(50)));

        ForiTheme.press(card);

        card.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "شهر " + city + " انتخاب شد",
                        Toast.LENGTH_SHORT
                ).show()
        );

        LinearLayout.LayoutParams cardLp =
                lp(-1, dp(72));

        cardLp.setMargins(0, dp(8), 0, 0);

        parent.addView(card, cardLp);
    }
}
