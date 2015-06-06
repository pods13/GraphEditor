package com.un1acker.grapheditor.view.dialogs;

import com.un1acker.grapheditor.view.App;
import insidefx.undecorator.UndecoratorScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * un1acker
 * 01.06.2015
 */
public class ErrorDialog {

    private Stage utilityStage;
    private Region root;

    @FXML
    private Text errorTitle;

    @FXML
    private Text errorExplain;

    public ErrorDialog() {
        utilityStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ErrorDialog.fxml"));
            fxmlLoader.setController(this);
            root = fxmlLoader.load();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void showErrorDialog(String errorTitle, String errorExplain) {
        setError(errorTitle, errorExplain);
        utilityStage.setTitle("An ERROR has occured");
        UndecoratorScene scene = new UndecoratorScene(utilityStage, StageStyle.UTILITY, root, null);
        scene.addStylesheet("/css/error_dialog.css");
        utilityStage.setScene(scene);
        utilityStage.initModality(Modality.WINDOW_MODAL);
        utilityStage.initOwner(App.primaryStage);

        utilityStage.setResizable(false);
        utilityStage.show();
    }

    private void setError(String errorTitle, String errorExplain) {
        this.errorTitle.setText(errorTitle);
        this.errorExplain.setText(errorExplain);
    }


    @FXML
    public void handleCloseDialog(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }


}
