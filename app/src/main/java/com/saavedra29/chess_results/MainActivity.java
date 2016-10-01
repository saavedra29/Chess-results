package com.saavedra29.chess_results;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new NewPage().execute();
    }
    private class NewPage extends AsyncTask<Void, Void, Void>
    {

        TextView title_v = (TextView)findViewById(R.id.main_title);
        ArrayList<TableEntry> tables = new ArrayList<TableEntry>();
        String title = "";
        Document doc = null;
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                doc = Jsoup.connect("http://chess-results.com/").timeout(6000).get();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            title = doc.title();
            Elements table = doc.select("table.CRs2");
            Elements records1 = table.select("tr.CRg1");
            Elements records = table.select("tr.CRg2");
            records.addAll(records1); // List of wanted rows
            Elements sorted = new Elements(records);
            for (Element e: records)
            {
                sorted.set(Integer.parseInt(e.select("td").get(0).text()) - 1, e);
            }
            for (Element e: sorted)
            {
                // Let's fill the tables

                // Get the index
                int index = Integer.parseInt(e.getElementsByClass("CRc").first().text()) - 1;
                // Get the tournament name and link
                Elements linkName = e.getElementsByTag("a");
                String tournamentName = linkName.text();
                String link = linkName.attr("href");
                // Get the countryCode
                String code = e.getElementsByClass("CR").text();
                // Get the last update
                String lastUpdate = e.getElementsByClass("CRnowrap").first().text();
                // Get the image path
                String imagePath = "tn_" + code.toLowerCase() + ".png";
                // Create the entry in the list
                TableEntry entry = new TableEntry(index, tournamentName, link, code, lastUpdate,
                        imagePath);
                tables.add(index, entry);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            GridLayout output = (GridLayout)findViewById(R.id.main_output);
            for (TableEntry entry: tables)
            {
                /*
                TextView no = new TextView(MainActivity.this);
                TextView tour = new TextView(MainActivity.this);
                TextView link = new TextView(MainActivity.this);
                TextView country = new TextView(MainActivity.this);
                TextView update = new TextView(MainActivity.this);
                */

                View rowGrid = LayoutInflater.from(MainActivity.this).
                        inflate(R.layout.row_element, null);
                TextView no = (TextView)rowGrid.findViewById(R.id.no_l);
                no.setText(Integer.toString(entry.no));
                TextView tour = (TextView)rowGrid.findViewById(R.id.tournament_l);
                tour.setText(entry.tournament);
                TextView link = (TextView)rowGrid.findViewById(R.id.code_l);
                link.setText(entry.countryCode);
                TextView flag = (TextView)rowGrid.findViewById(R.id.flag_l);
                flag.setText(entry.link);
                TextView update = (TextView)rowGrid.findViewById(R.id.update_l);
                update.setText(entry.lastUpdate);
                output.addView(rowGrid);

            }

            /*
            for (TableEntry entry: tables)
            {
                String tmp = "";
                tmp = tmp + "NO: " + Integer.toString(entry.no) + "\n";
                tmp = tmp + "Tournament: " + entry.tournament + "\n";
                tmp = tmp + "Link: " + entry.link + "\n";
                tmp = tmp + "Country: " + entry.countryCode + "\n";
                tmp = tmp + "Last Update: " + entry.lastUpdate + "\n";
                tmp = tmp + "Image Path: " + entry.imagePath + "\n";
                tmp += "------------------------------------\n";
                output += tmp;
            }
            v.setText(output);
            */
        }

    }
    private class TableEntry
    {
        int no = 0;
        String tournament = "";
        String link = "";
        String countryCode = "";
        String lastUpdate = "";
        String imagePath = "";
        public TableEntry(int no, String text, String link, String country, String lastUpdate,
                          String imagePath)
        {
            this.no = no;
            this.tournament = text;
            this.link = link;
            this.countryCode = country;
            this.lastUpdate = lastUpdate;
            this.imagePath = imagePath;
        }
    }

}

