package com.zetaplugins.lifestealz.util;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class VersionChecker {
   private final LifeStealZ plugin;
   private final Logger logger;
   private final String modrinthProjectId;
   private boolean newVersionAvailable = false;

   public VersionChecker(LifeStealZ plugin, String modrinthProjectId) {
      this.plugin = plugin;
      this.logger = plugin.getLogger();
      this.modrinthProjectId = modrinthProjectId;
      this.checkForUpdates();
   }

   private String getModrinthProjectUrl() {
      return "https://api.modrinth.com/v2/project/" + this.modrinthProjectId;
   }

   private void checkForUpdates() {
      String latestVersion = this.fetchLatestVersion();
      if (latestVersion != null) {
         String currentVersion = this.plugin.getDescription().getVersion();
         if (!latestVersion.trim().equals(currentVersion.trim())) {
            this.newVersionAvailable = true;
            String reset = "\u001b[0m";
            String bold = "\u001b[1m";
            String darkGray = "\u001b[90m";
            String lightGray = "\u001b[37m";
            String red = "\u001b[31m";
            String message = "\n\u001b[90m==========================================\u001b[0m\n\u001b[1mA new version of LifeStealZ is available!\u001b[0m\n\u001b[1mNew Version: \u001b[0m\u001b[1m\u001b[31m"
               + latestVersion
               + "\u001b[0m\u001b[37m (Your version: "
               + currentVersion
               + ")\u001b[0m\n\u001b[1mDownload here: \u001b[0m\u001b[37m\u001b[0mhttps://modrinth.com/plugin/lifestealz/version/"
               + latestVersion
               + "\n\u001b[90m==========================================\u001b[0m";
            this.logger.info(message);
         }
      }
   }

   private String fetchLatestVersion() {
      String mcVersion = this.plugin.getServer().getMinecraftVersion();
      String encodedGameVersion = URLEncoder.encode("[\"" + mcVersion + "\"]", StandardCharsets.UTF_8);
      String versionsUrl = this.getModrinthProjectUrl() + "/version?game_versions=" + encodedGameVersion;
      JSONArray versionsArray = this.fetchJsonArrayFromUrl(versionsUrl);
      if (versionsArray != null && !versionsArray.isEmpty()) {
         JSONObject latestVersion = (JSONObject)versionsArray.get(0);
         return (String)latestVersion.get("version_number");
      } else {
         return null;
      }
   }

   private String fetchVersionNumber(String versionId) {
      String versionUrl = this.getModrinthProjectUrl() + "/version/" + versionId;
      JSONObject versionJson = this.fetchJsonFromUrl(versionUrl);
      return versionJson != null ? (String)versionJson.get("version_number") : null;
   }

   private JSONObject fetchJsonFromUrl(String urlString) {
      try {
         HttpURLConnection connection = this.createHttpConnection(urlString);
         if (connection.getResponseCode() == 200) {
            String response = this.readResponse(connection);
            return (JSONObject)new JSONParser().parse(response);
         }

         this.logger.warning("Failed to retrieve data from " + urlString + " Response code: " + connection.getResponseCode());
      } catch (ParseException | IOException var4) {
         this.logger.warning("Error fetching data: " + var4.getMessage());
      }

      return null;
   }

   private JSONArray fetchJsonArrayFromUrl(String urlString) {
      try {
         HttpURLConnection connection = this.createHttpConnection(urlString);
         if (connection.getResponseCode() == 200) {
            String response = this.readResponse(connection);
            return (JSONArray)new JSONParser().parse(response);
         }

         this.logger.warning("Failed to retrieve data from " + urlString + " Response code: " + connection.getResponseCode());
      } catch (ParseException | IOException var4) {
         this.logger.warning("Error fetching data: " + var4.getMessage());
      }

      return null;
   }

   private HttpURLConnection createHttpConnection(String urlString) throws IOException {
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      return connection;
   }

   private String readResponse(HttpURLConnection connection) throws IOException {
      String var5;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
         StringBuilder response = new StringBuilder();

         String line;
         while ((line = reader.readLine()) != null) {
            response.append(line);
         }

         var5 = response.toString();
      }

      return var5;
   }

   public boolean isNewVersionAvailable() {
      return this.newVersionAvailable;
   }
}
