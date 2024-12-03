module ubb.scs.map {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    opens ubb.scs.map to javafx.fxml;
    opens ubb.scs.map.controller to javafx.fxml;

    exports ubb.scs.map;
    exports ubb.scs.map.domain;
    exports ubb.scs.map.controller;
    exports ubb.scs.map.service;
    exports ubb.scs.map.repository;
    exports ubb.scs.map.utils.observer;
    exports ubb.scs.map.utils.events;
    exports ubb.scs.map.utils.paging;
    exports ubb.scs.map.dto;
}