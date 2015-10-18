package com.blackout.paidupdater.Credits;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackout.paidupdater.R;

public class Credits extends Preference {

    private ImageView photoView;

    private TextView devName;

    private String nameDev;
    private String googleplusName;

    public Credits(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Credits);
        nameDev = a.getString(R.styleable.Credits_nameDev);
        googleplusName = a.getString(R.styleable.Credits_googleplusHandle);
        a.recycle();
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);

        View layout = View.inflate(getContext(), R.layout.credits, null);

        devName = (TextView) layout.findViewById(R.id.name);
        photoView = (ImageView) layout.findViewById(R.id.photo);

        return layout;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        if (googleplusName != null) {
            final OnPreferenceClickListener openTwitter = new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri googleplusURL = Uri.parse("https://plus.google.com/u/0/" + googleplusName);
                    final Intent intent = new Intent(Intent.ACTION_VIEW, googleplusURL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                    return true;
                }
            };
            this.setOnPreferenceClickListener(openTwitter);
            final String url = "http://themindofalex.com/dl/tbo/" + googleplusName + ".png";
            UrlImageViewHelper.setUrlDrawable(this.photoView, url, R.drawable.ic_null,
                    UrlImageViewHelper.CACHE_DURATION_ONE_WEEK);
        } else {
            photoView.setVisibility(View.GONE);
        }

        devName.setText(nameDev);

    }
}