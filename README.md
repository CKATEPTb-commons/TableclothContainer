<p align="center">
<h3 align="center">TableclothContainer</h3>

------

<p align="center">
An easy to use IoC Container that takes its inspiration from <a href="https://github.com/spring-projects/spring-framework" target="_blank" href="">Spring</a>
</p>

<p align="center">
<img alt="License" src="https://img.shields.io/github/license/CKATEPTb-commons/TableclothContainer">
<a href="#Download"><img alt="Sonatype Nexus (Snapshots)" src="https://img.shields.io/nexus/s/dev.ckateptb.common/TableclothContainer?label=repo&server=https://repo.animecraft.fun/"></a>
<img alt="Publish" src="https://img.shields.io/github/workflow/status/CKATEPTb-commons/TableclothContainer/Publish/production">
<a href="https://docs.gradle.org/7.5/release-notes.html"><img src="https://img.shields.io/badge/Gradle-7.5-brightgreen.svg?colorB=469C00&logo=gradle"></a>
<a href="https://discord.gg/P7FaqjcATp" target="_blank"><img alt="Discord" src="https://img.shields.io/discord/925686623222505482?label=discord"></a>
</p>

------

# Versioning

We use [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) to manage our releases.

# Features

- [X] Annotation based
- [X] Easy to use
- [X] Easy to include
- [X] Multiple instances
- [X] Custom package filter
- [ ] Documented

# Download

Download from our repository or depend via Gradle:

```kotlin
repositories {
    maven("https://repo.animecraft.fun/repository/maven-snapshots/")
}

dependencies {
    implementation("dev.ckateptb.common:TableclothContainer:<version>")
}
```

# How To

* Import the dependency [as shown above](#Download)
* Use local container `Container container = new Container();` or global container `IoC`
* Use `Container#scan` for scanning all components
* Use `Container#init` once when you should to initialize all scanned components
* Annotate constructor as `@Autowired` if you should choose what class constructor should be used
* Annotate parameter as `@Qualifier("<instance-name>")` if you should use other instance in constructor
* Annotate class as `@Component` if you should to auto-inject this class instance to container
* Need more? Look `Container` class