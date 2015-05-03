package de.spruce.imaggaclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class ImageActivity extends ActionBarActivity {

    Bitmap image;
    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        image = decodeImage(MainActivity.imagePath);
        putImagePath(MainActivity.imagePath);
        putImage(image);
    }

    public static Bitmap decodeImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    private void putImage(Bitmap image) {
        if (image != null) {
            imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(image);
        }
    }

    private void putImagePath(String imagePath) {
        if (imagePath != null) {
            textView = (TextView) findViewById(R.id.imagePath);
            textView.setText(imagePath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
