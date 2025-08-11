package com.example.licenseissuer;

import com.example.licenseissuer.util.KeyExportUtil;
import com.example.licenseissuer.util.KeyStoreManager;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.geometry.Insets;

import java.io.File;

public class KeyManagementDialog extends Stage {
    private KeyStoreManager keyStoreManager;
    private ListView<String> keyListView = new ListView<>();
    private Button generateButton = new Button("生成新密钥对");
    private Button exportButton = new Button("导出公钥");
    private Button selectButton = new Button("选择私钥");

    private String selectedKeyId = null;

    public KeyManagementDialog(KeyStoreManager manager) {
        this.keyStoreManager = manager;
        setTitle("密钥管理");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        keyListView.setItems(FXCollections.observableArrayList(keyStoreManager.getAllKeyIds()));
        keyListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        generateButton.setOnAction(e -> {
            try {
                String newKeyId = keyStoreManager.generateNewKeyPair();
                keyListView.getItems().add(newKeyId);
            } catch (Exception ex) {
                showAlert("错误", "生成密钥失败：" + ex.getMessage());
            }
        });

        exportButton.setOnAction(e -> {
            String keyId = keyListView.getSelectionModel().getSelectedItem();
            if (keyId == null) {
                showAlert("提示", "请选择一个密钥导出");
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("导出公钥");
            fileChooser.setInitialFileName(keyId + "_public.der");
            File file = fileChooser.showSaveDialog(this);
            if (file != null) {
                try {
                    KeyExportUtil.exportPublicKey(keyStoreManager.getPublicKey(keyId), file.getAbsolutePath());
                    showAlert("成功", "公钥导出成功");
                } catch (Exception ex) {
                    showAlert("错误", "导出失败：" + ex.getMessage());
                }
            }
        });

        selectButton.setOnAction(e -> {
            selectedKeyId = keyListView.getSelectionModel().getSelectedItem();
            if (selectedKeyId == null) {
                showAlert("提示", "请选择一个私钥");
            } else {
                this.close();
            }
        });

        root.getChildren().addAll(keyListView, generateButton, exportButton, selectButton);

        setScene(new Scene(root, 300, 400));
    }

    public String getSelectedKeyId() {
        return selectedKeyId;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
