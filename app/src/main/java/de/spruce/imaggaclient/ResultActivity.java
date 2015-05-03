package de.spruce.imaggaclient;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ResultActivity extends ActionBarActivity {
    static final String API_KEY = "acc_ff72e0d15ce51fd";
    static final String API_SECRET = "aa326805e9d295938bdb68662f7139bb";
    static final String API_AUTH_HEADER = "YWNjX2ZmNzJlMGQxNWNlNTFmZDphYTMyNjgwNWU5ZDI5NTkzOGJkYjY4NjYyZjcxMzliYg=="; // base64 encoded key:secret
    static final String API_ENDPOINT_UPLOAD = "http://api.imagga.com/v1/content"; // make post here and get returned url
    static final String API_ENDPOINT_TAGS = "http://api.imagga.com/v1/tagging?url="; // put returned url after this and get tags (should then be JSON)
    ResultActivity self = this; // so we can reference it from inside the upload activity and don't have to use static things for everything
    static Resources res;
    ImageView chosenImageView;
    TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        res = getResources();
        chosenImageView = (ImageView) findViewById(R.id.chosen_image);
        statusTextView = (TextView) findViewById(R.id.status_text);

        // Rotate the loading image.
        chosenImageView.startAnimation(getLoadingRotateAnimation());
        statusTextView.setText(getString(R.string.result_fetching_tags));

//        putImage(MainActivity.imagePath);
        new UploadFileToServerAndGetTags("http://www.natur-server.de/Bilder/MZ/002/mz001191-marienkaefer.jpg").execute(MainActivity.imagePath); // execute upload
        Log.i("called tags", "getting tags");

    }

    private RotateAnimation getLoadingRotateAnimation() {
        RotateAnimation rotation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setDuration(2500);
        rotation.setRepeatCount(Animation.INFINITE);
        return rotation;
    }

    private void putResults(ArrayList<String> results) {
        if (results != null) {
            LinearLayout resultList = (LinearLayout) findViewById(R.id.resultList);
            if (results.size() > 0) {
                for (int i = 0; i < results.size(); i++) {
                    resultList.addView(createButton(results.get(i)));
                }
            } else {
                resultList.addView(createErrorMessage());
            }
        } else {
            goToMain(null);
            Toast.makeText(self, getString(R.string.error_no_tags), Toast.LENGTH_LONG).show();
        }
    }

    private TextView createErrorMessage() {
        TextView errorMessage = new TextView(this);
        errorMessage.setText(getString(R.string.error_no_tags_found));
        errorMessage.setTextColor(res.getColor(R.color.color_red));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        errorMessage.setLayoutParams(params);
        return errorMessage;
    }

    private void putImage(String imagePath) {
        if (chosenImageView == null) {
            chosenImageView = (ImageView) findViewById(R.id.chosen_image);
        }
        if (imagePath != null) {
            chosenImageView.setImageBitmap(ImageActivity.decodeImage(imagePath));
            chosenImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToImage();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.error_imagepath_null), Toast.LENGTH_SHORT).show();
        }
    }

    private void putImage(Bitmap image) {
        if (chosenImageView == null) {
            chosenImageView = (ImageView) findViewById(R.id.chosen_image);
        }
        chosenImageView.setImageBitmap(image);
        chosenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToImage();
            }
        });
    }

    /**
     * Add some sample content to the given ArrayList.
     */
    private ArrayList<String> generateSampleContent(ArrayList<String> list) {
        list.add("Crocus");
        list.add("Flower");
        list.add("Plant");
        list.add("Grass");
        list.add("Iridaceae");
        Log.i("generateSampleContent", "We got called :)");
        return list;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_help) {
            goToHelp(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doGoogleSearch(String searchQuery) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, searchQuery);
        startActivity(intent);
    }

    public void goToHelp(View view) {
        startActivity(new Intent(this, HelpActivity.class));
    }

    public void goToImage() {
        startActivity(new Intent(this, ImageActivity.class));
    }

    public void goToMain(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    private Button createButton(final String title) {
        int marginSmall = res.getDimensionPixelOffset(R.dimen.margin_small);
        int marginDefault = res.getDimensionPixelOffset(R.dimen.margin_default);

        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.button_result);
        button.setText(title);
        button.setTypeface(null, Typeface.BOLD);
        button.setTextColor(res.getColor(R.color.color_white));
        button.setTextSize(res.getDimension(R.dimen.text_size_default));
        button.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(marginDefault, marginSmall, marginDefault, 0);
        button.setLayoutParams(params);
        button.setPadding(marginDefault, marginSmall, marginDefault, marginSmall);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doGoogleSearch(title);
            }
        });
        return button;
    }

    private class UploadFileToServerAndGetTags extends AsyncTask<String, Integer, ArrayList<String>> {
        UploadFileToServerAndGetTags(String url) {
            this.url = url;
        }

        private String url;
        Bitmap image;

        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            // TODO: Wieder das richtige ausführen
//            Log.i("UploadFileToServerAndGetTags", "Starting upload process");
//            return uploadFile();
            Log.i("GetTags_doInBackground", "Getting Tags " + urls[0]);
            this.image = getFakeDisplayImage();
            if (isOnline()) {
                return this.getTagsFromUrl(this.url);
            } else {
                Looper.prepare();
                // FIXME This doesn't get shown.
                Toast.makeText(self, getString(R.string.error_not_online), Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(self.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

        private Bitmap getFakeDisplayImage() {
            try {
                return BitmapFactory.decodeStream(new URL(this.url).openConnection().getInputStream());
            } catch (IOException e) {
                return null;
            }
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {


            Log.i("UploadFileToServer", "Really: " + MainActivity.imagePath);
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            DataInputStream inputStream = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            Log.i("UploadFileToServer", "asd");
            try {
                Log.i("UploadFileToServer", "reading file");
                FileInputStream fileInputStream = new FileInputStream(new File(MainActivity.imagePath));
                Log.i("UploadFileToServer", "after reading file");
                URL url = new URL(ResultActivity.API_ENDPOINT_UPLOAD);
                connection = (HttpURLConnection) url.openConnection();

                // Allow Inputs & Outputs.
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // Set HTTP method to POST.
                connection.setRequestMethod("POST");

                // make authentication
                connection.setRequestProperty("Authorization", "Basic " + ResultActivity.API_AUTH_HEADER);


                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name= \"picture\";filename=\"picture\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
                if (serverResponseCode == 200) {

                    self.overwriteList((String) connection.getContent());
                }
            } catch (Exception ex) {
                //Exception handling
                Log.e("overwriteList", "An error emerged", ex);
            }
            return "";
        }

        private ArrayList<String> getTagsFromUrl(String url) {
            // faking url
            this.url = url;

            ArrayList<String> list = new ArrayList<String>();
            Log.i("tagsFromURL", "Now calling it.");
            JSONObject response = RestImaggaSpruce.doGet(ResultActivity.API_ENDPOINT_TAGS + this.url, API_AUTH_HEADER);

            try {
                // get results
                JSONArray results = response.getJSONArray("results");
                // get tags
                JSONArray tags = ((JSONObject) results.get(0)).getJSONArray("tags");
                // for each tag make arraylist entry for that
                int l = tags.length();
                for (int i = 0; i < l; i++) {
                    list.add(((JSONObject) tags.get(i)).getString("tag"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("tagsFromURL", response.toString());
            return list;
        }

        protected void onPostExecute(ArrayList<String> list) {
            chosenImageView.clearAnimation();
            self.putImage(image);
            statusTextView.setText(getString(R.string.result_found_description));
            self.putResults(list);
        }

    }


    private void overwriteList(String content) {
        // overwrite old entries with new tags (currently it will be the url)
        ArrayList<String> results = new ArrayList<String>();
        results.add(content);
        putResults(results);
    }


}
