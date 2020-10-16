package com.project.dailydrizzle;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.mediarouter.app.MediaRouteActionProvider;
import androidx.mediarouter.app.MediaRouteButton;

/**
 * Created by Neeraj on 02,October,2020
 */
public class ThemeableMediaRouteActionProvider extends MediaRouteActionProvider {
    public ThemeableMediaRouteActionProvider(Context context) {
        super(context);
    }

    @Override
    public MediaRouteButton onCreateMediaRouteButton() {
        MediaRouteButton button = super.onCreateMediaRouteButton();
        colorWorkaroundForCastIcon(button);
        return button;
    }

    @Nullable
    @Override
    public MediaRouteButton getMediaRouteButton() {
        MediaRouteButton button = super.getMediaRouteButton();
        colorWorkaroundForCastIcon(button);
        return button;
    }

    private void colorWorkaroundForCastIcon(MediaRouteButton button) {
        if (button == null) return;
        Context castContext = new ContextThemeWrapper(getContext(),R.style.Theme_MediaRouter);

        TypedArray a = castContext.obtainStyledAttributes(null,
               R.styleable.MediaRouteButton, R.attr.mediaRouteButtonStyle, 0);
        Drawable drawable = a.getDrawable(R.styleable.MediaRouteButton_externalRouteEnabledDrawable);
        a.recycle();
        DrawableCompat.setTint(drawable, getContext().getColor(R.color.colorPrimaryDark));
        drawable.setState(button.getDrawableState());
        button.setRemoteIndicatorDrawable(drawable);
    }
}