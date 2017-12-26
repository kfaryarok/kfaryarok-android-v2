package io.github.kfaryarok.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Methods for interacting with the internet.
 * @author tbsc on 11/11/2017
 */
public class NetworkUtil {

    /**
     * Wrapper {@link #downloadUsingInputStreamReader(URL)} that allows supplying a string URL.
     * @param url Where to connect to
     * @return Contents of the file at the specified URL, or nothing, or null
     * @throws IOException If anything wrong happened during connection
     */
    public static String downloadUsingInputStreamReader(String url) throws IOException {
        return downloadUsingInputStreamReader(new URL(url));
    }

    /**
     * Uses an InputStreamReader to download from a URL to a String, character by character.
     * Taken from developer.android.com, then copied from v1
     * @param url Where to connect to
     * @return Contents of the file at the specified URL, or nothing, or null
     * @throws IOException If anything wrong happened during connection
     */
    public static String downloadUsingInputStreamReader(URL url) {
        if (url == null) {
            return "";
        }

        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(2000);
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String.
                Reader reader = new InputStreamReader(stream, "UTF-8");
                int read;
                StringBuilder builder = new StringBuilder();
                while ((read = reader.read()) != -1) {
                    builder.append((char) read);
                }
                result = builder.toString();
            }
        } catch (IOException e) {
            // connection timed out, return null
            return null;
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // failed closing stream
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

}
