package br.com.klauskpm.inventory;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import br.com.klauskpm.inventory.data.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public static final int PROCUDT_LOADER = 0;
    private ProductCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.list);

        mAdapter = new ProductCursorAdapter(this);

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener((adapterView, view, i, id) -> {
            Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
            startDetailActivity(uri);
        });

        FloatingActionButton createButton = (FloatingActionButton) findViewById(R.id.create);
        createButton.setOnClickListener(view -> startDetailActivity());

        getLoaderManager().initLoader(PROCUDT_LOADER, null, this);
    }

    private void startDetailActivity () {
        startDetailActivity(null);
    }

    private void startDetailActivity (Uri uri) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        if (uri != null) intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.insert_dummy) insertDummyProduct();

        return super.onOptionsItemSelected(item);
    }

    private void insertDummyProduct () {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_TITLE, "PSVR");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 400);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 10);

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case PROCUDT_LOADER:
                String[] projection = {
                        ProductEntry._ID,
                        ProductEntry.COLUMN_PRODUCT_TITLE,
                        ProductEntry.COLUMN_PRODUCT_PRICE,
                        ProductEntry.COLUMN_PRODUCT_QUANTITY
                };

                return new CursorLoader(
                        this,
                        ProductEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
