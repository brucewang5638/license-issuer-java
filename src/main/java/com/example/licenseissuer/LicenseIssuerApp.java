package com.example.licenseissuer;

import com.example.licenseissuer.util.KeyStoreManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class LicenseIssuerApp extends Application {

    private Stage primaryStage;  // 保存主舞台引用
    private TextField customerNameField;
    private ComboBox<String> licenseTypeCombo;
    private TextField boardSerialField;
    private TextArea macsArea;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextField serialNumberField;
    private KeyStoreManager keyStoreManager = new KeyStoreManager();
    private String selectedKeyId = null;

    private LicenseManager licenseManager = new LicenseManager();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;  // 保存引用

        primaryStage.setTitle("软件授权证书签发工具 v1.0");

        VBox mainLayout = createMainLayout();

        // 用 ScrollPane 包装主布局
        ScrollPane scrollPane = new ScrollPane(mainLayout);

        // 设置滚动条策略（自动出现）
        scrollPane.setFitToWidth(true);  // 让内容宽度适应ScrollPane宽度
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // 不显示水平滚动条
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // 需要时显示垂直滚动条

        Scene scene = new Scene(scrollPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);  // 如果你想允许调整大小的话
        primaryStage.show();

        // 初始化默认值
        initializeDefaults();
    }

    private VBox createMainLayout() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.getStyleClass().add("main-container");

        // 标题
        Label titleLabel = new Label("🔐 软件授权证书签发");
        titleLabel.getStyleClass().add("title-label");

        // 表单区域
        VBox formArea = createFormArea();

        // 按钮区域
        HBox buttonArea = createButtonArea();

        mainLayout.getChildren().addAll(titleLabel, formArea, buttonArea);
        return mainLayout;
    }

    private VBox createFormArea() {
        VBox formArea = new VBox(15);
        formArea.getStyleClass().add("form-area");

        // 客户信息组
        TitledPane customerGroup = new TitledPane("👤 客户信息", createCustomerInfoPane());
        customerGroup.setCollapsible(false);

        // 授权信息组
        TitledPane licenseGroup = new TitledPane("📋 授权配置", createLicenseInfoPane());
        licenseGroup.setCollapsible(false);

        // 硬件绑定组
        TitledPane hardwareGroup = new TitledPane("💻 硬件绑定", createHardwareInfoPane());
        hardwareGroup.setCollapsible(false);

        formArea.getChildren().addAll(customerGroup, licenseGroup, hardwareGroup);
        return formArea;
    }

    private GridPane createCustomerInfoPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // 客户名称
        Label customerLabel = new Label("客户名称:");
        customerLabel.getStyleClass().add("field-label");
        customerNameField = new TextField();
        customerNameField.setPromptText("请输入客户公司名称");
        customerNameField.getStyleClass().add("text-field");

        // 证书序列号
        Label serialLabel = new Label("证书序列号:");
        serialLabel.getStyleClass().add("field-label");
        serialNumberField = new TextField();
        serialNumberField.setPromptText("自动生成UUID");
        serialNumberField.setEditable(false);
        serialNumberField.getStyleClass().addAll("text-field", "readonly-field");

        grid.add(customerLabel, 0, 0);
        grid.add(customerNameField, 1, 0);
        grid.add(serialLabel, 0, 1);
        grid.add(serialNumberField, 1, 1);

        // 设置列宽
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private GridPane createLicenseInfoPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // 授权类型
        Label typeLabel = new Label("授权类型:");
        typeLabel.getStyleClass().add("field-label");
        licenseTypeCombo = new ComboBox<>();
        licenseTypeCombo.getItems().addAll("trial", "standard", "enterprise", "ultimate");
        licenseTypeCombo.setValue("enterprise");
        licenseTypeCombo.getStyleClass().add("combo-box");

        // 生效时间
        Label startLabel = new Label("生效时间:");
        startLabel.getStyleClass().add("field-label");
        startDatePicker = new DatePicker(LocalDate.now());
        startDatePicker.getStyleClass().add("date-picker");

        // 到期时间
        Label endLabel = new Label("到期时间:");
        endLabel.getStyleClass().add("field-label");
        endDatePicker = new DatePicker(LocalDate.now().plusYears(1));
        endDatePicker.getStyleClass().add("date-picker");

        grid.add(typeLabel, 0, 0);
        grid.add(licenseTypeCombo, 1, 0);
        grid.add(startLabel, 0, 1);
        grid.add(startDatePicker, 1, 1);
        grid.add(endLabel, 0, 2);
        grid.add(endDatePicker, 1, 2);

        // 设置列宽
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private GridPane createHardwareInfoPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // 机器ID
        Label machineLabel = new Label("主板序列号:");
        machineLabel.getStyleClass().add("field-label");
        boardSerialField = new TextField();
        boardSerialField.setPromptText("请输入服务器主板序列号");
        boardSerialField.getStyleClass().add("text-field");

        // MAC地址
        Label macLabel = new Label("MAC地址:");
        macLabel.getStyleClass().add("field-label");
        macsArea = new TextArea();
        macsArea.setPromptText("请输入MAC地址，多个地址用换行分隔\n例如：\n00:1A:2B:3C:4D:5E\n00:1A:2B:3C:4D:5F");
        macsArea.setPrefRowCount(4);
        macsArea.getStyleClass().add("text-area");

        grid.add(machineLabel, 0, 0);
        grid.add(boardSerialField, 1, 0);
        grid.add(macLabel, 0, 1);
        grid.add(macsArea, 1, 1);

        // 设置列宽
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private HBox createButtonArea() {
        HBox buttonArea = new HBox(20);
        buttonArea.setAlignment(Pos.CENTER);
        buttonArea.setPadding(new Insets(20, 0, 0, 0));

        // 生成证书按钮
        Button generateButton = new Button("🔐 生成证书");
        generateButton.getStyleClass().addAll("button", "primary-button");
        generateButton.setPrefWidth(150);
        generateButton.setOnAction(e -> generateLicense());

        // 清空表单按钮
        Button clearButton = new Button("🗑️ 清空表单");
        clearButton.getStyleClass().addAll("button", "secondary-button");
        clearButton.setPrefWidth(150);
        clearButton.setOnAction(e -> clearForm());

        // 密钥管理按钮
        Button keyManageButton = new Button("🔑 密钥管理");
        keyManageButton.getStyleClass().addAll("button", "secondary-button");
        keyManageButton.setPrefWidth(150);
        keyManageButton.setOnAction(e -> openKeyManagement());

        buttonArea.getChildren().addAll(generateButton, clearButton, keyManageButton);
        return buttonArea;
    }

    private void initializeDefaults() {
        // 生成新的证书序列号
        serialNumberField.setText(java.util.UUID.randomUUID().toString());
    }

    private void generateLicense() {
        try {
            // 验证表单
            if (!validateForm()) {
                return;
            }

            // 创建证书数据
            LicenseData licenseData = createLicenseData();

            // 选择保存路径
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存授权证书");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("授权证书文件", "*.license")
            );
            fileChooser.setInitialFileName(customerNameField.getText() + "_license.license");

            File file = fileChooser.showSaveDialog(customerNameField.getScene().getWindow());
            if (file != null) {
                // 生成证书
                licenseManager.generateLicense(licenseData, file);

                // 显示成功消息
                Notifications.create()
                        .title("证书生成成功")
                        .text("授权证书已保存到: " + file.getAbsolutePath())
                        .hideAfter(Duration.seconds(3))
                        .showInformation();

                // 生成新的序列号供下次使用
                serialNumberField.setText(java.util.UUID.randomUUID().toString());
            }

        } catch (Exception e) {
            showError("证书生成失败", e.getMessage());
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (customerNameField.getText().trim().isEmpty()) {
            errors.append("• 请输入客户名称\n");
        }

        if (boardSerialField.getText().trim().isEmpty()) {
            errors.append("• 请输入机服务器主板序列号\n");
        }

        if (macsArea.getText().trim().isEmpty()) {
            errors.append("• 请输入至少一个MAC地址\n");
        }

        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            errors.append("• 请选择有效的时间范围\n");
        } else if (!endDatePicker.getValue().isAfter(startDatePicker.getValue())) {
            errors.append("• 到期时间必须晚于生效时间\n");
        }

        if (errors.length() > 0) {
            showError("表单验证失败", errors.toString());
            return false;
        }

        return true;
    }

    private LicenseData createLicenseData() {
        LicenseData data = new LicenseData();

        // metadata
        data.setKid("key-2025-01");
        data.setNotBefore(startDatePicker.getValue().atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        data.setNotAfter(endDatePicker.getValue().atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        data.setSerialNumber(serialNumberField.getText().trim());

        // payload
        data.setCustomerName(customerNameField.getText().trim());
        data.setLicenseType(licenseTypeCombo.getValue());
        data.setMachineId(boardSerialField.getText().trim());

        // 解析MAC地址
        String[] macLines = macsArea.getText().trim().split("\n");
        java.util.List<String> macs = new java.util.ArrayList<>();
        for (String mac : macLines) {
            String cleanMac = mac.trim();
            if (!cleanMac.isEmpty()) {
                macs.add(cleanMac);
            }
        }
        data.setMacs(macs);

        return data;
    }

    private void clearForm() {
        customerNameField.clear();
        licenseTypeCombo.setValue("enterprise");
        boardSerialField.clear();
        macsArea.clear();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusYears(1));
        serialNumberField.setText(java.util.UUID.randomUUID().toString());

        Notifications.create()
                .title("表单已清空")
                .text("所有字段已重置为默认值")
                .hideAfter(Duration.seconds(2))
                .showInformation();
    }

    private void openKeyManagement() {
        KeyManagementDialog dialog = new KeyManagementDialog(keyStoreManager);
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
        selectedKeyId = dialog.getSelectedKeyId();

        if (selectedKeyId != null) {
            Notifications.create()
                    .title("密钥选择成功")
                    .text("已选中私钥：" + selectedKeyId)
                    .showInformation();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("信息");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}