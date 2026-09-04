package com.forichi.app.theme;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ForiTheme {

    public static final int NAVY = Color.rgb(15, 18, 35);
    public static final int NAVY2 = Color.rgb(25, 29, 52);

    public static final int TURQUOISE = Color.rgb(0, 210, 190);
    public static final int GOLD = Color.rgb(255, 181, 72);

    public static final int WHITE = Color.WHITE;
    public static final int MUTED = Color.rgb(165, 170, 190);

    public static final int CARD = Color.rgb(34, 39, 62);
    public static final int CARD2 = Color.rgb(42, 47, 70);

    public static final int INPUT = Color.rgb(38, 43, 67);

    public static GradientDrawable rounded(
            int color,
            float radius) {

        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);

        return g;
    }

    public static GradientDrawable gradient(
            int first,
            int second,
            float radius) {

        GradientDrawable g = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{first, second}
        );

        g.setCornerRadius(radius);

        return g;
    }

    public static void page(View v) {

        v.setBackground(
                gradient(
                        NAVY,
                        Color.rgb(30, 35, 60),
                        0
                )
        );

        v.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );
    }

    public static void card(View v) {

        v.setBackground(
                rounded(CARD, 28)
        );
    }

    public static void card2(View v) {

        v.setBackground(
                rounded(CARD2, 24)
        );
    }

    public static void input(View v) {

        v.setBackground(
                rounded(INPUT, 22)
        );
    }

    public static void primary(TextView v) {

        v.setTextColor(WHITE);
        v.setTextSize(14);

        v.setGravity(Gravity.CENTER);

        v.setTypeface(
                Typeface.create(
                        "sans",
                        Typeface.BOLD
                )
        );

        v.setBackground(
                gradient(
                        Color.rgb(0, 190, 175),
                        Color.rgb(0, 130, 145),
                        50
                )
        );

        press(v);
    }

    public static void secondary(TextView v) {

        v.setTextColor(TURQUOISE);
        v.setTextSize(14);

        v.setGravity(Gravity.CENTER);

        v.setTypeface(
                Typeface.create(
                        "sans",
                        Typeface.BOLD
                )
        );

        v.setBackground(
                rounded(
                        Color.rgb(35, 42, 68),
                        50
                )
        );

        press(v);
    }

    public static TextView title(
            Context c,
            String s) {

        TextView t = new TextView(c);

        t.setText(s);
        t.setTextColor(WHITE);
        t.setTextSize(25);

        t.setTypeface(
                Typeface.create(
                        "sans",
                        Typeface.BOLD
                )
        );

        t.setGravity(
                Gravity.CENTER_VERTICAL
        );

        t.setIncludeFontPadding(false);

        return t;
    }

    public static TextView subtitle(
            Context c,
            String s) {

        TextView t = new TextView(c);

        t.setText(s);
        t.setTextColor(MUTED);
        t.setTextSize(13);

        t.setGravity(
                Gravity.CENTER_VERTICAL
        );

        t.setIncludeFontPadding(false);

        return t;
    }

    public static void press(View v) {

        v.setOnTouchListener(
                (view, event) -> {

                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {

                        view.setAlpha(0.75f);
                        view.setScaleX(0.97f);
                        view.setScaleY(0.97f);

                    } else if (
                            event.getAction() ==
                                    MotionEvent.ACTION_UP ||
                            event.getAction() ==
                                    MotionEvent.ACTION_CANCEL) {

                        view.setAlpha(1f);
                        view.setScaleX(1f);
                        view.setScaleY(1f);
                    }

                    return false;
                }
        );
    }

    public static LinearLayout.LayoutParams match(
            int height) {

        return new LinearLayout.LayoutParams(
                -1,
                height
        );
    }
}
