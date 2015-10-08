package com.blackout.paidupdater.Themes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


import com.blackout.paidupdater.XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class GetThemeList extends AsyncTask<ArrayList<ThemeList>, Void, ArrayList<ThemeList>> {
    ProgressDialog dialog;
    // All static variables
    static final String URL = "http://teamblackedout.com/Updates-XML-PAID/updater.parts.xml";
    // XML node keys
    static final String KEY_ITEM = "Tab"; // parent node
    static final String KEY_NAME = "TabName";
    static final String KEY_URL = "Url";
    ArrayList<ThemeList> menuItems = new ArrayList<ThemeList>();
    Context mcontext;

    @Override
    protected void onPreExecute() {
        //this
    }

    public GetThemeList(Context context) {
        mcontext = context;

    }

    protected ArrayList<ThemeList> doInBackground(ArrayList<ThemeList>... passing) {
        XMLParser parser = new XMLParser(mcontext);
        String xml = parser.getXmlFromUrl(URL); // getting XML
        Document doc = parser.getDomElement(xml); // getting DOM element
        NodeList nl = doc.getElementsByTagName(KEY_ITEM);
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            ThemeList themeList = new ThemeList(
                    parser.getValue(e, KEY_NAME),
                    parser.getValue(e, KEY_URL));

            menuItems.add(themeList);
        }
        return menuItems;
    }

    protected void onPostExecute(Void unused) {
    }
}
