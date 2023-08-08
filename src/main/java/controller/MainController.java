package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import plugins.HikVision;
import plugins.YingShi;
import utils.ControllerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public Button hikSearch;
    @FXML
    public Button hikPreview;
    @FXML
    public TextArea hikResult;
    @FXML
    private TextField hikHost;
    @FXML
    private TextField hikAppKey;
    @FXML
    private TextField hikAppSecret;
    @FXML
    public TextField hikCameraIndexCode;
    @FXML
    public Button ysSearch;
    @FXML
    public TextArea ysResult;
    @FXML
    private TextField ysAppKey;
    @FXML
    private TextField ysAppSecret;
    @FXML
    private TextField ysAccessToken;

    public void initialize(URL location, ResourceBundle resources) {
        ControllerFactory.controllers.put(MainController.class.getSimpleName(), this);
    }

    @FXML
    void checkHik(ActionEvent event) {
        String host = this.hikHost.getText();
        String appKey = this.hikAppKey.getText();
        String appSecret = this.hikAppSecret.getText();
        if (host.length() != 0 && appKey.length() != 0 && appSecret.length() != 0) {
            HikVision hikvision = new HikVision(host, appKey, appSecret);
            hikvision.checkValid();
        } else {
            this.hikResult.appendText("输入不能为空\n");
        }
    }

    @FXML
    void searchHik(ActionEvent event) {
        String host = this.hikHost.getText();
        String appKey = this.hikAppKey.getText();
        String appSecret = this.hikAppSecret.getText();
        HikVision hikVision = new HikVision(host, appKey, appSecret);
        hikVision.checkSearch();
    }

    @FXML
    void previewHik(ActionEvent event) {
        String host = this.hikHost.getText();
        String appKey = this.hikAppKey.getText();
        String appSecret = this.hikAppSecret.getText();
        String cameraIndexCodeText = this.hikCameraIndexCode.getText();
        HikVision hikVision = new HikVision(host, appKey, appSecret);
        hikVision.checkPreview(cameraIndexCodeText);
    }

    @FXML
    void checkYs(ActionEvent event) {
        String appKey = this.ysAppKey.getText();
        String appSecret = this.ysAppSecret.getText();
        if (appKey.length() != 0 && appSecret.length() != 0) {
            YingShi yingShi = new YingShi(appKey, appSecret);
            yingShi.checkValid();
        } else {
            this.ysResult.appendText("输入不能为空\n");
        }
    }

    @FXML
    void searchYs(ActionEvent event) {
        String appKey = this.ysAppKey.getText();
        String appSecret = this.ysAppSecret.getText();
        String accessToken = this.ysAccessToken.getText();
        YingShi yingShi = new YingShi(appKey, appSecret);
        yingShi.checkSearch(accessToken);
    }
}
