plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.14" // 推荐使用官方JavaFX插件简化配置
    id("org.beryx.jlink") version "2.26.0"
}

group = "org.abacusflow"
version = "1.0.0"
val targetOs = System.getProperty("os.name").lowercase()
val isWindows = targetOs.contains("windows")
val isLinux = targetOs.contains("linux")
val isMac = targetOs.contains("mac")

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
    mainModule.set("com.example.licenseissuer")
    mainClass.set("com.example.licenseissuer.LicenseIssuerApp") // 把这里替换成你的主类全限定名
}

tasks.test {
    useJUnitPlatform()
}

jlink {
    launcher {
        name = "LicenseIssuerApp"      // 运行时启动命令
    }

    imageName.set("LicenseIssuerApp")  // 自定义运行时镜像名称
    moduleName.set("com.example.licenseissuer")
    mainClass.set("com.example.licenseissuer.Main")

    jpackage {
        installerName = "LicenseIssuerAppInstaller"
        imageOptions = mutableListOf<String>().apply {
            if (isWindows) {
                add("--icon")
                add("src/main/resources/icon.ico")
            }
        }.toList()

        val options = mutableListOf<String>().apply {
            add("--vendor")
            add("MyCompany")
            add("--app-version")
            add("1.0.0")
            add("--description")
            add("License Issuer Application")

            // 平台专属参数
            if (isWindows) {
                add("--win-console")
//                add("--type")
//                add("exe") // 或 msi
            } else if (isLinux) {
            } else if (isMac) {
            }
        }

        installerOptions = options
    }
}