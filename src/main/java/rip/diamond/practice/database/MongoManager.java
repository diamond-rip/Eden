package rip.diamond.practice.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;

@Getter
public class MongoManager {

    private final Eden plugin;

    private MongoDatabase database;
    private MongoClient client;
    private MongoCollection<Document> profiles, kits;

    public MongoManager(Eden plugin) {
        this.plugin = plugin;

        if (Config.MONGO_ENABLED.toBoolean()) {
            this.init();
        }
    }

    public void init() {
        if (Config.MONGO_URI_MODE.toBoolean()) {
            this.client = MongoClients.create(Config.MONGO_URI_CONNECTION_STRING.toString());
        } else {
            boolean auth = Config.MONGO_NORMAL_AUTH_ENABLED.toBoolean();
            String host = Config.MONGO_NORMAL_HOST.toString();
            int port = Config.MONGO_NORMAL_PORT.toInteger();

            String uri = "mongodb://" + host + ":" + port;

            if (auth) {
                String username = Config.MONGO_NORMAL_AUTH_USERNAME.toString();
                String password = Config.MONGO_NORMAL_AUTH_PASSWORD.toString();

                password = password
                        .replaceAll("%(?![0-9a-fA-F]{2})", "%25")
                        .replaceAll("\\+", "%2B");

                uri = "mongodb://" + username + ":" + password + "@" + host + ":" + port;
            }

            this.client = MongoClients.create(uri);
        }
        this.database = client.getDatabase(Config.MONGO_URI_DATABASE.toString());
        this.loadCollections();
    }

    public void loadCollections() {
        profiles = this.database.getCollection("profiles");
        kits = this.database.getCollection("kits");
    }

}
