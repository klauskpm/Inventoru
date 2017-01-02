package br.com.klauskpm.inventory;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        Button deleteButton  = (Button) findViewById(R.id.delete);
        Button orderSupplyButton = (Button) findViewById(R.id.order_supplies);
        mQuantityTextView = (TextView) findViewById(R.id.quantity);

        incrementQuantityButton.setOnClickListener(view -> {
            int quantity = getQuantity();
            if (quantity < 100) quantity++;
            String quantityText = "" + quantity;
            mQuantityTextView.setText(quantityText);
        });

        decrementQuantityButton.setOnClickListener(view -> {
            int quantity = getQuantity();
            if (quantity > 0) quantity--;
            String quantityText = "" + quantity;
            mQuantityTextView.setText(quantityText);
        });

        deleteButton.setOnClickListener(this::showDeleteConfirmationDialog);

        orderSupplyButton.setOnClickListener(this::orderSupply);
    }

    private int getQuantity() {
        return Integer.parseInt(mQuantityTextView.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    private void orderSupply (View view) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(date);
        String subject = "[SUPPLY] Order at " + formattedDate;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showDeleteConfirmationDialog (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deleting confirmation message");

        builder.setPositiveButton("Delete", (dialog, id) -> deleteProduct());
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            if (dialog != null) dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

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
