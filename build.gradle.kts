plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.14" // 推荐使用官方JavaFX插件简化配置
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
