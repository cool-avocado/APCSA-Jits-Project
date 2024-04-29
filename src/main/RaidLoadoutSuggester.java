import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class RaidLoadoutSuggester {

    private static final String API_KEY = "bbdd5b34b65f48129a16822d7d59c4e6";  // Replace with your Bungie API Key
    private static final String BASE_URL = "https://www.bungie.net/Platform/Destiny2";
    private static final String CLIENT_ID = "46754";
    private static final String REDIRECT_URI = "http://localhost/callback";

    public static void main(String[] args) {
        try {
            // Open the OAuth Authorization URL in the default browser
            Desktop desktop = Desktop.getDesktop();
            String authUrl = "https://www.bungie.net/en/OAuth/Authorize?client_id=" + CLIENT_ID + "&response_type=code&redirect_uri=" + REDIRECT_URI;
            desktop.browse(URI.create(authUrl));
            // Here, assume the user will log in and you will somehow receive the access token and user info
            String accessToken = "exampleAccessToken"; // This should be obtained from the OAuth callback
            String membershipType = "exampleMembershipType"; // This should be fetched from the OAuth response
            String destinyMembershipId = "exampleDestinyMembershipId"; // This should be fetched from the OAuth response

            UserProfile profile = getUserProfile(accessToken, membershipType, destinyMembershipId);
            if (profile != null) {
                System.out.println("Player's class is: " + profile.playerClass);
                suggestLoadout(profile.playerClass);
                System.out.println("This week's raid rotation is: " + profile.raid);
            } else {
                System.out.println("Failed to retrieve the user profile.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static UserProfile getUserProfile(String accessToken, String membershipType, String destinyMembershipId) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s/Profile/%s/?components=200", BASE_URL, membershipType, destinyMembershipId)))
                .header("X-API-Key", API_KEY)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // Parse the JSON response to extract the player class and raid
                return parseUserProfile(response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static UserProfile parseUserProfile(String jsonBody) {
        // Implement JSON parsing logic here to extract details like player class and raid
        // This is a simplified placeholder
        return new UserProfile("Hunter", "Vault of Glass");
    }

    private static void suggestLoadout(String playerClass) {
        Map<String, String> loadouts = new HashMap<>();
        loadouts.put("Hunter", "Sniper Rifle and Hand Cannon");
        loadouts.put("Warlock", "Pulse Rifle and Shotgun");
        loadouts.put("Titan", "Auto Rifle and Fusion Rifle");

        String loadout = loadouts.getOrDefault(playerClass, "No loadout available for this class");
        System.out.println("Suggested Loadout for " + playerClass + ": " + loadout);
    }

    static class UserProfile {
        String playerClass;
        String raid;

        UserProfile(String playerClass, String raid) {
            this.playerClass = playerClass;
            this.raid = raid;
        }
    }
}
