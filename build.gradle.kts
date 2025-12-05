import org.gradle.api.file.DuplicatesStrategy

plugins {
    id("java")
}

group = "com.example"
version = "1.0"

repositories {
    // 添加阿里云Maven镜像
    maven("https://maven.aliyun.com/repository/central/")
    maven("https://maven.aliyun.com/repository/public/")
    // PaperMC仓库
    maven("https://repo.papermc.io/repository/maven-public/")
    // 保留官方仓库作为后备
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    // Folia 使用 Paper API，但有额外的调度器功能
}

// 配置Gradle使用国内镜像下载插件
buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin/")
        mavenCentral()
    }
}

// 禁用自动生成plugin.yml，因为我们已经手动创建了
// bukkit {
//     name = "ClearLag"
//     main = "com.example.clearlag.ClearLag"
//     apiVersion = "1.21"
//     authors = listOf("YourName")
//     description = "Folia服务端凋落物清理插件"
//     load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD
//     // 声明支持Folia
//     foliaSupported = true
// }

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    jar {
        archiveFileName.set("ClearLag1.0.jar")
    }
}