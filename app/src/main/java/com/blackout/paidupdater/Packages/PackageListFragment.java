package com.blackout.paidupdater.Packages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
public class PackageListFragment extends Fragment {


    private PackageAdapter adapter;
    private ListView lv;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_TITLE = "title";
    private static final String ARG_URL = "url";
    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PackageListFragment newInstance(String sectionNumber, String url) {
        PackageListFragment fragment = new PackageListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, sectionNumber);
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public PackageListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        getActionBar().setTitle(getArguments().getString(ARG_TITLE));
        getActionBar().setIcon(R.mipmap.ic_launcher);

        try {
            GetAPKList task = new GetAPKList(getArguments().getString(ARG_URL), getActivity());

            lv = (ListView) rootView.findViewById(R.id.listView);

            adapter = new PackageAdapter(getActivity(),
                    R.layout.list_icon_row, task.execute().get());

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,int position, long id)
                {

                    Package data = adapter.getItem(position);
                if (!data.icon.isEmpty())  {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PackageDetailsFragment.newInstance(data.title, data.icon, data.md5, data.download, data.preview, data.description))
                            .addToBackStack(null)
                            .commit();
                }
                }});

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return rootView;
    }
}
