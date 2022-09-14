package rip.diamond.practice.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import rip.diamond.practice.Eden;

@Getter
public class MongoManager {

    private final Eden plugin;

    private MongoDatabase database;
    private MongoClient client;
    private MongoCollection<Document> profiles, kits;

    public MongoManager(Eden plugin) {
        this.plugin = plugin;

        this.init();
    }

    public void init() {
        if (plugin.getConfigFile().getBoolean("mongo.uri-mode")) {
            this.client = MongoClients.create(plugin.getConfigFile().getString("mongo.uri.connection-string"));
            this.database = client.getDatabase(plugin.getConfigFile().getString("mongo.uri.database"));

            this.loadCollections();
            return;
        }

        boolean auth = plugin.getConfigFile().getBoolean("mongo.normal.auth.enabled");
        String host = plugin.getConfigFile().getString("mongo.normal.host");
        int port = plugin.getConfigFile().getInt("mongo.normal.port");

        String uri = "mongodb://" + host + ":" + port;

        if (auth) {
            String username = plugin.getConfigFile().getString("mongo.normal.auth.username");
            String password = plugin.getConfigFile().getString("mongo.normal.auth.password");

            password = password.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            password = password.replaceAll("\\+", "%2B");

            uri = "mongodb://" + username + ":" + password + "@" + host + ":" + port;
        }

        this.client = MongoClients.create(uri);
        this.database = client.getDatabase(plugin.getConfigFile().getString("mongo.uri.database"));

        this.loadCollections();
    }

    public void loadCollections() {
        profiles = this.database.getCollection("profiles");
        kits = this.database.getCollection("kits");
    }

}
