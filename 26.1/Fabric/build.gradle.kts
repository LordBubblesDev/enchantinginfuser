plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-fabric")
}

val minecraftVersion = "26.1.2"
val commonDeobfJar = gradle.gradleUserHomeDir.resolve(
    "caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-common-deobf/$minecraftVersion/minecraft-common-deobf-$minecraftVersion.jar"
)
val clientOnlyDeobfJar = gradle.gradleUserHomeDir.resolve(
    "caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-clientonly-deobf/$minecraftVersion/minecraft-clientonly-deobf-$minecraftVersion.jar"
)

dependencies {
    implementation(project(":Common"))
    modApi("net.fabricmc.fabric-api:fabric-api:0.146.1+26.1.2")
    modApi("fuzs.puzzleslib:puzzleslib-fabric:26.1.0")
    // 26.1 uses unobfuscated client classes; ensure Fabric compile sees them.
    compileOnly(files(commonDeobfJar, clientOnlyDeobfJar))
}

tasks.named("compileJava") {
    dependsOn("genSources")
}
