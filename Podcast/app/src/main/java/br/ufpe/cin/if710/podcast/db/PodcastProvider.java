package br.ufpe.cin.if710.podcast.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class PodcastProvider extends ContentProvider {

    PodcastDBHelper dbHelper;
    public PodcastProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implemented this to handle requests to delete one or more rows.

        int deleted = dbHelper.getWritableDatabase().delete(PodcastDBHelper.DATABASE_TABLE,
                selection,
                selectionArgs);

        return deleted;
    }

    @Override

    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Implemented handle requests to insert a new row.
        Uri inserted = null;

        long id = dbHelper.getWritableDatabase().replace(PodcastDBHelper.DATABASE_TABLE, null, values);
        inserted = Uri.withAppendedPath(PodcastProviderContract.EPISODE_LIST_URI, Long.toString(id));

        return inserted;
    }


    @Override
    public boolean onCreate() {

        dbHelper = PodcastDBHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Implemented this to handle query requests from clients.
        Cursor cursor = null;
        cursor = dbHelper.getReadableDatabase().query(
                PodcastDBHelper.DATABASE_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder );

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int updateRows = dbHelper.getWritableDatabase().update(PodcastDBHelper.DATABASE_TABLE,
                values,
                selection,
                selectionArgs);

        return  updateRows;
    }
}
