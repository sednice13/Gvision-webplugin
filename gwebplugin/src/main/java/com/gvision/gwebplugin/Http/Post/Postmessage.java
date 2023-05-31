package com.gvision.gwebplugin.Http.Post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonObject;



public class Postmessage {
    
    public void sendHttpmessageToChat(String message, String player) throws IOException {

     URL url = new URL("https://cscloud8-40.lnu.se/api/mcbackend/mc/mchandle");
     HttpURLConnection http = (HttpURLConnection) url.openConnection();

     http.setRequestMethod("POST");
     http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
     http.setRequestProperty("Accept", "application/json");
     http.setDoOutput(true);

     JsonObject jsonobject = new JsonObject();
     jsonobject.addProperty("message", message);
     jsonobject.addProperty("player", player);

     String jsonString = jsonobject.toString();





     try (OutputStream os = http.getOutputStream()) {
        byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
        os.flush();
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
