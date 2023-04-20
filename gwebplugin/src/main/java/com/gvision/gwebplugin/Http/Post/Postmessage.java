package com.gvision.gwebplugin.Http.Post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class Postmessage {
    
    public void sendHttpmessageToChat(String message) throws IOException {

     URL url = new URL("http://localhost:8081/mc/mchandle");
     HttpURLConnection http = (HttpURLConnection) url.openConnection();

     http.setRequestMethod("POST");
     http.setRequestProperty("Content-Type", "application/json; utf-8");
     http.setRequestProperty("Accept", "application/json");
     http.setDoOutput(true);

     JSONObject jsonobject = new JSONObject();

     


     try (OutputStream os = http.getOutputStream()) {
        byte[] input = message.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
    }
    
    
    try (BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8))) {
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        System.out.println(response.toString());


 
    }
}

}
