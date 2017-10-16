package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.graphics.Color;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.database.Cursor;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.inspector.protocol.module.Database;



import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastDBHelper;
import br.ufpe.cin.if710.podcast.db.PodcastProvider;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;


public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    //TODO teste com outros links de podcast

    private ListView items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = (ListView) findViewById(R.id.items);
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean hasConnection() {
        //checando conexão com a internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && networkInfo.isConnectedOrConnecting();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(this.hasConnection()){
            new DownloadXmlTask().execute(RSS_FEED);
        }
        else {
            new DataBaseTask().execute();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        XmlFeedAdapter adapter = (XmlFeedAdapter) items.getAdapter();
        adapter.clear();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "lendo itens da internet", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(String... params) {
            List<ItemFeed> itemList;
            try {
                itemList = XmlFeedParser.parse(getRssFeed(params[0]));
                for(ItemFeed itemFeed : itemList){

                    ContentValues itemValue = new ContentValues();

                    itemValue.put(PodcastDBHelper.EPISODE_TITLE, itemFeed.getTitle());
                    itemValue.put(PodcastDBHelper.EPISODE_DATE, itemFeed.getPubDate());
                    itemValue.put(PodcastDBHelper.EPISODE_DESC, itemFeed.getDescription());
                    itemValue.put(PodcastDBHelper.EPISODE_DOWNLOAD_LINK, itemFeed.getDownloadLink());
                    itemValue.put(PodcastDBHelper.EPISODE_LINK, itemFeed.getLink());
                    itemValue.put(PodcastDBHelper.EPISODE_FILE_URI, "");

                    Uri uri = getContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI, itemValue);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            new DataBaseTask().execute();
        }
    }

    private class DataBaseTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "lendo itens do banco", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();

            Cursor cursor = getContentResolver().query(
                    PodcastProviderContract.EPISODE_LIST_URI,
                    null, "", null, null
            );
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.TITLE));
                String link = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_LINK));
                String pubDate = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.DATE));
                String description = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.DESCRIPTION));
                String downloadLink = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.DOWNLOAD_LINK));

                itemList.add(new ItemFeed(title, link, pubDate, description, downloadLink));
            }

            return itemList;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            
            Toast.makeText(getApplicationContext(), "itens carregados", Toast.LENGTH_SHORT).show();

            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);

            items.setAdapter(adapter);
            items.setTextFilterEnabled(true);
        }
    }

    //TODO Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }
}
