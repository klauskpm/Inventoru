package br.com.klauskpm.inventory;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.klauskpm.inventory.data.ProductContract.ProductEntry;

public class DetailActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int DETAIL_PRODUCT_LOADER = 1;

    public static final int NEW_PRODUCT_TITLE = R.string.new_product_activity_title;
    public static final int UPDATE_PRODUCT_TITLE = R.string.update_product_activity_title;
    public static final int UPDATE_PRODUCT_BUTTON_TITLE = R.string.update_product_button_title;

    private Uri mProductUri;

    private EditText mTitleEditText;
    private EditText mPriceEditText;
    private TextView mQuantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mProductUri = intent.getData();
        int actionTitle = NEW_PRODUCT_TITLE;

        mTitleEditText = (EditText) findViewById(R.id.title);
        mPriceEditText = (EditText) findViewById(R.id.price);
        mQuantityTextView = (TextView) findViewById(R.id.quantity);
        Button incrementQuantityButton = (Button) findViewById(R.id.increment_product_quantity);
        Button decrementQuantityButton = (Button) findViewById(R.id.decrement_product_quantity);
        Button saveButton  = (Button) findViewById(R.id.save);
        Button orderSupplyButton = (Button) findViewById(R.id.order_supplies);

        if (mProductUri != null) {
            actionTitle = UPDATE_PRODUCT_TITLE;
            saveButton.setText(UPDATE_PRODUCT_BUTTON_TITLE);
            getLoaderManager().initLoader(DETAIL_PRODUCT_LOADER, null, this);
        } else {
            invalidateOptionsMenu();
        }

        setTitle(getString(actionTitle));

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

        saveButton.setOnClickListener(this::saveProduct);

        orderSupplyButton.setOnClickListener(this::orderSupply);
    }

    private int getQuantity() {
        int quantity = 0;
        String quantityString = mQuantityTextView.getText().toString().trim();

        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        return quantity;
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

    private void showDeleteConfirmationDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deleting confirmation message");

        builder.setPositiveButton("Delete", (dialog, id) -> deleteProduct());
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            if (dialog != null) dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveProduct (View view) {
        String title = mTitleEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        int quantity = 0;
        int price = 0;

        if (mProductUri == null && TextUtils.isEmpty(title) && TextUtils.isEmpty(quantityString)
                && TextUtils.isEmpty(priceString)) {
            return;
        }

        if (!TextUtils.isEmpty(quantityString)) quantity = Integer.parseInt(quantityString);
        if (!TextUtils.isEmpty(priceString)) price = Integer.parseInt(priceString);

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_TITLE, title);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);

        if (mProductUri != null) updateProduct(values);
        else insertProduct(values);

        finish();
    }

    private void insertProduct(ContentValues values) {
        Uri newProductUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        if (newProductUri == null) {
            Toast.makeText(this, "Failed to insert the product", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Product was successfully inserted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateProduct (ContentValues values) {
        int updatedRows = getContentResolver().update(mProductUri, values, null, null);

        if (updatedRows == 0) {
            Toast.makeText(this, "Failed to update the product", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Product was successfully updated", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteProduct() {
        if (mProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Failed to delete product", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Product deleted", Toast.LENGTH_LONG).show();
                finish();
            }
        }
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
            mPriceEditText.setText(cursor.getInt(columnPriceIndex) + "");
            mQuantityTextView.setText(cursor.getInt(columnQuantityIndex) + "");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleEditText.setText("");
        mQuantityTextView.setText("0");
    }
}
