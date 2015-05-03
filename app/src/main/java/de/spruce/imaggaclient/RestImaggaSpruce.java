package de.spruce.imaggaclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by spruce on 21.03.15.
 */
public class RestImaggaSpruce extends RestEasy {
    // Retrieve a resource from the resteasy web service
    public static JSONObject doGet(String url, String authHeader)
    {
        JSONObject json = null;

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url);

        // Accept JSON
        httpget.addHeader("accept", "application/json");
        httpget.addHeader("Authorization", " Basic " + authHeader);

        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);

            // Get the response entity
            HttpEntity entity = response.getEntity();

            // If response entity is not null
            if (entity != null) {

                // get entity contents and convert it to string
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);

                // construct a JSON object with result
                json=new JSONObject(result);

                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return the json
        return json;
    }
}
