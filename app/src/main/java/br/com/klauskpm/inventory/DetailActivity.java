package br.com.klauskpm.inventory;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.klauskpm.inventory.data.ProductContract.ProductEntry;

public class DetailActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int DETAIL_PRODUCT_LOADER = 1;

    public static final int NEW_PRODUCT_TITLE = R.string.new_product_activity_title;
    public static final int UPDATE_PRODUCT_TITLE = R.string.update_product_activity_title;

    private Uri mProductUri;

    private EditText mTitleEditText;
    private TextView mQuantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mProductUri = intent.getData();
        int actionTitle = NEW_PRODUCT_TITLE;

        if (mProductUri != null) {
            actionTitle = UPDATE_PRODUCT_TITLE;
            getLoaderManager().initLoader(DETAIL_PRODUCT_LOADER, null, this);
        } else {
            invalidateOptionsMenu();
        }

        setTitle(getString(actionTitle));

        mTitleEditText = (EditText) findViewById(R.id.title);
        Button incrementQuantityButton = (Button) findViewById(R.id.increment_product_quantity);
        Button decrementQuantityButton = (Button) findViewById(R.id.decrement_product_quantity);
        mQuantityTextView = (TextView) findViewById(R.id.quantity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case DETAIL_PRODUCT_LOADER:
                String[] projection = {
                        ProductEntry._ID,
                        ProductEntry.COLUMN_PRODUCT_TITLE,
                        ProductEntry.COLUMN_PRODUCT_PRICE,
                        ProductEntry.COLUMN_PRODUCT_QUANTITY,
                        ProductEntry.COLUMN_PRODUCT_IMAGE
                };

                return new CursorLoader(
                        this,
                        mProductUri,
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
        if (cursor.moveToFirst()) {
            int columnTitleIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TITLE);
            int columnPriceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int columnQuantityIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int columnImageIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

            mTitleEditText.setText(cursor.getString(columnTitleIndex));
            mQuantityTextView.setText(cursor.getInt(columnQuantityIndex) + "");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleEditText.setText("");
        mQuantityTextView.setText("0");
    }
}
