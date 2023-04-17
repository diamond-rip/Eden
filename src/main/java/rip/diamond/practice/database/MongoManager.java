package rip.diamond.practice.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
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

    private static final String PROFILES_COLLECTION_NAME = "profiles";
    private static final String KITS_COLLECTION_NAME = "kits";

    private final Eden plugin;

    private MongoDatabase database;
    private MongoCollection<Document> profiles;
    private MongoCollection<Document> kits;

    public MongoManager(Eden plugin) {
        this.plugin = plugin;

        if (Config.MONGO_ENABLED.toBoolean()) {
            this.init();
        }
    }

    public void init() {
        String uri = Config.MONGO_URI_CONNECTION_STRING.toString();
        if (!Config.MONGO_URI_MODE.toBoolean()) {
            String host = Config.MONGO_NORMAL_HOST.toString();
            int port = Config.MONGO_NORMAL_PORT.toInteger();

            ServerAddress serverAddress = new ServerAddress(host, port);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyToClusterSettings(builder -> builder.hosts(Lists.newArrayList(serverAddress)))
                    .build();

            if (Config.MONGO_NORMAL_AUTH_ENABLED.toBoolean()) {
                String username = Config.MONGO_NORMAL_AUTH_USERNAME.toString();
                String password = Config.MONGO_NORMAL_AUTH_PASSWORD.toString();
                MongoCredential credential = MongoCredential.createCredential(username, Config.MONGO_URI_DATABASE.toString(), password.toCharArray());
                settings = MongoClientSettings.builder()
                        .credential(credential)
                        .applyToClusterSettings(builder -> builder.hosts(Lists.newArrayList(serverAddress)))
                        .build();
            }

            uri = "mongodb://" + host + ":" + port;
        }

        MongoClient client = MongoClients.create(uri);
        this.database = client.getDatabase(Config.MONGO_URI_DATABASE.toString());
        this.loadCollections();
    }

    public void loadCollections() {
        this.profiles = this.database.getCollection(PROFILES_COLLECTION_NAME);
        this.kits = this.database.getCollection(KITS_COLLECTION_NAME);
    }
}
