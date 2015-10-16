package com.blackout.paidupdater.News;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackout.paidupdater.R;

/**
 * This is where we get the package details, apk or zip (it's just not named the right thing)
 */
public class NewsDetails extends Fragment {
    String title;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_TITLE = "title";
    private static final String ARG_DATE = "date";
    private static final String ARG_description = "description";
    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    public static NewsDetails newInstance(String title, String date, String description) {
        NewsDetails fragment = new NewsDetails();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DATE, date);
        args.putString(ARG_description, description);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsDetails() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_details, container, false);
        title = getArguments().getString(ARG_TITLE);
        String date = getArguments().getString(ARG_DATE);
        String description = getArguments().getString(ARG_description);
        getActionBar().setTitle(title);



        TextView descView = (TextView) rootView.findViewById(R.id.newsView);
        descView.setText(description);

        setHasOptionsMenu(true);

        return rootView;
    }
}
