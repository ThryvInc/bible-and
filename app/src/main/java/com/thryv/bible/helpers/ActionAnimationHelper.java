package com.thryv.bible.helpers;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.Button;

/**
 * Created by ell on 4/9/16.
 */
public class ActionAnimationHelper {

    public static void animateButtons(boolean isForward, float fromX, float fromY, float pixelsPerDip,
                                      final View[] buttons, final View exitView){
        int radius = (int) (20 * pixelsPerDip);
        int margin = (int) (4 * pixelsPerDip);
        fromX = fromX - radius;
        fromY = fromY - radius;

        float toX = radius * 4;
        float toY = 0;

        int duration = 50;

        if (isForward){
            for (int i = 0; i < buttons.length; i++){
                buttons[i].setVisibility(View.VISIBLE);
                ViewCompat.setTranslationZ(buttons[i], 21);
                ActionAnimationHelper.animateButtonBy(buttons[i], fromX, -toX, fromY, -toY, duration * i);

                toX = ActionAnimationHelper.nextXGivenPreviousPoint(toX, toY, radius, margin);
                toY = ActionAnimationHelper.toYGivenToX(toX, 4 * radius);
            }

            exitView.setVisibility(View.VISIBLE);
            ViewCompat.setTranslationZ(exitView, 21);
            exitView.setX(fromX);
            exitView.setY(fromY);
            exitView.setAlpha(1f);
            exitView.animate()
                    .rotationBy(180)
                    .setDuration(duration * buttons.length)
                    .start();
        }else {
            for (int i = 0; i < buttons.length; i++){
                buttons[i].setVisibility(View.VISIBLE);
                ViewCompat.setTranslationZ(buttons[i], 21);
                ActionAnimationHelper.animateButtonBy(buttons[i], fromX - toX, toX, fromY - toY, toY, duration * i);

                toX = ActionAnimationHelper.nextXGivenPreviousPoint(toX, toY, radius, margin);
                toY = ActionAnimationHelper.toYGivenToX(toX, 4 * radius);
            }

            exitView.setVisibility(View.VISIBLE);
            ViewCompat.setTranslationZ(exitView, 21);
            exitView.setAlpha(1f);
            exitView.animate()
                    .rotationBy(180)
                    .setDuration(duration * buttons.length)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            exitView.animate()
                                    .alpha(0f)
                                    .setDuration(100)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            exitView.setVisibility(View.GONE);
                                        }
                                    }).start();
                            for (View button : buttons){
                                button.setVisibility(View.GONE);
                            }
                        }
                    }).start();
        }
    }

    public static float toYGivenToX(float toX, float radius){
        return (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(toX, 2));
    }

    //quadratic formula used here
    public static float nextXGivenPreviousPoint(float oldX, float oldY, int radius, int margin){
        double constant = 32 * Math.pow(radius, 2) - Math.pow(2 * radius + margin, 2);
        double b = - 4 * oldX * constant;
        double a = 4 * Math.pow(4 * radius, 2);
        double c = Math.pow(constant, 2) - Math.pow(8 * oldY * radius, 2);
        double radicand = Math.pow(b, 2) - 4 * a * c; // b^2 - 4ac
        double numerator = -b - Math.sqrt(radicand); // -b - root(b^2 - 4ac)
        double denominator = 2 * a;
        return (float) (numerator / denominator);
    }

    public static void animateButtonBy(View button, float fromX, float toX, float fromY, float toY, int offset){
        int duration = 100;
        button.setX(fromX);
        button.setY(fromY);
        button.animate()
                .setDuration(duration)
                .translationXBy(toX)
                .translationYBy(toY)
                .setStartDelay(offset)
                .start();
    }
}
