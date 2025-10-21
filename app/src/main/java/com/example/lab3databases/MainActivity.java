package com.example.lab3databases;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView productId;
    EditText productName, productPrice;
    Button addBtn, findBtn, deleteBtn;
    ListView productListView;

    ArrayList<String> productList;
    ArrayAdapter adapter;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productList = new ArrayList<>();

        // info layout
        productId = findViewById(R.id.productId);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);

        //buttons
        addBtn = findViewById(R.id.addBtn);
        findBtn = findViewById(R.id.findBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        // listview
        productListView = findViewById(R.id.productListView);

        // db handler
        dbHandler = new MyDBHandler(this);

        // button listeners
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString();
                double price = Double.parseDouble(productPrice.getText().toString());
                Product product = new Product(name, price);
                dbHandler.addProduct(product);

                productName.setText("");
                productPrice.setText("");

//                Toast.makeText(MainActivity.this, "Add product", Toast.LENGTH_SHORT).show();
                viewProducts();
            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString().trim();
                String priceText = productPrice.getText().toString().trim();

                double price = -1; // Use -1 to indicate no price provided

                // Parse price if available
                if (!priceText.isEmpty()) {
                    try {
                        price = Double.parseDouble(priceText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Check if at least one search criteria is provided
                if (name.isEmpty() && priceText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter either name, price, or both to search", Toast.LENGTH_SHORT).show();
                    return;
                }

                Product product = dbHandler.findProduct(name, price);

                if (product != null) {
                    // Product found - display its details
                    productId.setText(String.valueOf(product.getId()));
                    productName.setText(product.getProductName());
                    productPrice.setText(String.valueOf(product.getProductPrice()));

                    // Show which search was performed
                    if (!name.isEmpty() && price >= 0) {
                        Toast.makeText(MainActivity.this, "Product found by name and price!", Toast.LENGTH_SHORT).show();
                    } else if (!name.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Product found by name!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Product found by price!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Product not found
                    productId.setText("Not Found");
                    Toast.makeText(MainActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString().trim();
                String priceText = productPrice.getText().toString().trim();

                // Check if both fields are provided
                if (name.isEmpty() || priceText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both product name AND price to delete", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double price = Double.parseDouble(priceText);

                    // Delete the product with both name and price
                    dbHandler.deleteProduct(name, price);

                    // Clear the input fields
                    productId.setText("");
                    productName.setText("");
                    productPrice.setText("");

                    Toast.makeText(MainActivity.this, "Product deleted (name: " + name + ", price: " + price + ")", Toast.LENGTH_SHORT).show();

                    // Refresh the product list
                    viewProducts();

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewProducts();
    }

    private void viewProducts() {
        productList.clear();
        Cursor cursor = dbHandler.getData();
        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivity.this, "Nothing to show", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                productList.add(cursor.getString(1) + " (" +cursor.getString(2)+")");
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        productListView.setAdapter(adapter);
    }
}