package ubb.scs.map;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ubb.scs.map.controller.UtilizatoriController;
import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.domain.validators.PrietenieValidator;
import ubb.scs.map.domain.validators.UtilizatorValidator;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.repository.database.PrietenieDbRepository;
import ubb.scs.map.repository.database.UtilizatorDbRepository;
import ubb.scs.map.service.PrietenieService;
import ubb.scs.map.service.UtilizatorService;

import java.io.IOException;

public class SocialNetworkApplication extends Application {
    private UtilizatorService utilizatorService;
    private PrietenieService prietenieService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DATABASE_URL");
        String username = dotenv.get("DATABASE_USERNAME");
        String pasword = dotenv.get("DATABASE_PASSWORD");

        Repository<Long, Utilizator> utilizatorRepository = new UtilizatorDbRepository(url, username, pasword, new UtilizatorValidator());
        Repository<Long, Prietenie> prietenieRepository = new PrietenieDbRepository(url, username, pasword, new PrietenieValidator());
        utilizatorService = UtilizatorService.getInstance(utilizatorRepository);
        prietenieService = PrietenieService.getInstance(utilizatorRepository, prietenieRepository);

        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("views/utilizatori-view.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        UtilizatoriController userController = fxmlLoader.getController();
        userController.setService(utilizatorService, prietenieService);
    }
}