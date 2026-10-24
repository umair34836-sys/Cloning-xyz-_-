// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.VersionChecker
class VersionChecker {
    java.util.logging.Logger logger;
    java.lang.String modrinthProjectId;
    boolean newVersionAvailable;
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public VersionChecker(LifeStealZ plugin, String modrinthProjectId) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.modrinthProjectId = modrinthProjectId;
        checkForUpdates();
    }

    private void checkForUpdates() {
        String latestVersion = fetchLatestVersion();
        if (latestVersion != null) {
            String currentVersion = this.plugin.getDescription().getVersion();
            if (!latestVersion.trim().equals(currentVersion.trim())) {
                this.newVersionAvailable = true;
                String message = "\n\u001b[90m==========================================\u001b[0m\n\u001b[1mA new version of LifeStealZ is available!\u001b[0m\n\u001b[1mNew Version: \u001b[0m\u001b[1m\u001b[31m" + latestVersion + "\u001b[0m\u001b[37m (Your version: " + currentVersion + ")\u001b[0m\n\u001b[1mDownload here: \u001b[0m\u001b[37m\u001b[0mhttps://modrinth.com/plugin/lifestealz/version/" + latestVersion + "\n\u001b[90m==========================================\u001b[0m";
                this.logger.info(message);
            }
        }
    }

    private HttpURLConnection createHttpConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    private JSONArray fetchJsonArrayFromUrl(String urlString) {
        try {
            HttpURLConnection connection = createHttpConnection(urlString);
            if (connection.getResponseCode() == 200) {
                String response = readResponse(connection);
                return (JSONArray) new JSONParser().parse(response);
            }
            this.logger.warning("Failed to retrieve data from " + urlString + " Response code: " + connection.getResponseCode());
            return null;
        } catch (IOException | ParseException e) {
            this.logger.warning("Error fetching data: " + e.getMessage());
            return null;
        }
    }

    private JSONObject fetchJsonFromUrl(String urlString) {
        try {
            HttpURLConnection connection = createHttpConnection(urlString);
            if (connection.getResponseCode() == 200) {
                String response = readResponse(connection);
                return (JSONObject) new JSONParser().parse(response);
            }
            this.logger.warning("Failed to retrieve data from " + urlString + " Response code: " + connection.getResponseCode());
            return null;
        } catch (IOException | ParseException e) {
            this.logger.warning("Error fetching data: " + e.getMessage());
            return null;
        }
    }

    private String fetchLatestVersion() {
        String mcVersion = this.plugin.getServer().getMinecraftVersion();
        String encodedGameVersion = URLEncoder.encode("[\"" + mcVersion + "\"]", StandardCharsets.UTF_8);
        String versionsUrl = getModrinthProjectUrl() + "/version?game_versions=" + encodedGameVersion;
        JSONArray versionsArray = fetchJsonArrayFromUrl(versionsUrl);
        if (versionsArray == null || versionsArray.isEmpty()) {
            return null;
        }
        JSONObject latestVersion = (JSONObject) versionsArray.get(0);
        return (String) latestVersion.get("version_number");
    }

    private String fetchVersionNumber(String versionId) {
        String versionUrl = getModrinthProjectUrl() + "/version/" + versionId;
        JSONObject versionJson = fetchJsonFromUrl(versionUrl);
        if (versionJson != null) {
            return (String) versionJson.get("version_number");
        }
        return null;
    }

    private String getModrinthProjectUrl() {
        return "https://api.modrinth.com/v2/project/" + this.modrinthProjectId;
    }

    public boolean isNewVersionAvailable() {
        return this.newVersionAvailable;
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        try {
            StringBuilder response = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    response.append(line);
                } else {
                    String string = response.toString();
                    reader.close();
                    return string;
                }
            }
        } catch (Throwable th) {
            try {
                reader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

}
