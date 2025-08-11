plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.14" // 推荐使用官方JavaFX插件简化配置
    id("org.beryx.jlink") version "2.26.0"
}

group = "org.abacusflow"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.controlsfx:controlsfx:11.2.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    // Kotlin DSL里设置mainClass用this格式
    mainClass.set("com.example.licenseissuer.LicenseIssuerApp") // 把这里替换成你的主类全限定名
}

tasks.test {
    useJUnitPlatform()
}

jlink {
    imageName.set("LicenseIssuerApp")  // 自定义运行时镜像名称
    launcher {
        name = "LicenseIssuerApp"      // 运行时启动命令
    }
    moduleName.set("com.example.licenseissuer")  // 必须设置
    jpackage {
        moduleName.set("com.example.licenseissuer") // 一定要跟module-info.java里 module名称一致
        installerType = "exe"      // Windows安装包
        installerName = "LicenseIssuerAppInstaller"
        icon = "src/main/resources/icon.ico"
        installerOptions.addAll(
            listOf(
                "--win-menu",
                "--win-shortcut",
                "--win-dir-chooser"
            )
        )
    }
}