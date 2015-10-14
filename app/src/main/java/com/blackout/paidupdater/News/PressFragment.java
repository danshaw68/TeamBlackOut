package com.blackout.paidupdater.News;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.blackout.paidupdater.R;

import java.util.concurrent.ExecutionException;

/**
 * Get a list of packages, this is the "in theme view"
 */
public class PressFragment extends Fragment {


    private NewsAdapter adapter;
    private ListView lv;
    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_TITLE = "title";
    private static final String ARG_URL = "url";


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PressFragment newInstance(int sectionNumber) {
        PressFragment fragment = new PressFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    public PressFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);


        getActionBar().setTitle("Press Releases");
        getActionBar().setIcon(R.drawable.ic_launcher);

        try {
            GetUpdatesList task = new GetUpdatesList("http://teamblackedout.com/update_description.xml", getActivity(), false);

            lv = (ListView) rootView.findViewById(R.id.listView);

            adapter = new NewsAdapter(getActivity(),
                    R.layout.list_header_row , task.execute().get());

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,int position, long id)
                {

                    News data = adapter.getItem(position);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, NewsDetails.newInstance(data.title, data.date, data.description))
                            .addToBackStack(null)
                            .commit();
                }});

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return rootView;
    }
}

