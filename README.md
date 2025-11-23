<p align="center">
    <img src="images/logo.png" height="200" width="220" />
</p>

<h1 align="center">uploadthing4j</h1>
<h3 align="center">An unofficial Java SDK to interact with the <a href="https://uploadthing.com">UploadThing</a> API.</h3>

[![](https://jitpack.io/v/Dev-Siri/uploadthing4j.svg)](https://jitpack.io/#Dev-Siri/uploadthing4j)

## Getting Started

### Using The Library

Use jitpack to add the library from GitHub.

#### Maven

Add this to your `pom.xml`

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```

Then add this library as a dependency.

```xml
<dependency>
    <groupId>com.github.Dev-Siri</groupId>
    <artifactId>uploadthing4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Gradle

Add this in your root `settings.gradle` at the end of repositories:

```groovy
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
        // For settings.gradle.kts, use this instead:
        // maven { url = uri("https://jitpack.io") }
        maven { url 'https://jitpack.io' }
	}
}
```

Then add the library as a dependency:

```groovy
dependencies {
    // For settings.gradle.kts, use this instead:
    // implementation("com.github.Dev-Siri:uploadthing4j:1.0.0")
    implementation 'com.github.Dev-Siri:uploadthing4j:1.0.0'
}
```

### Developing Locally

Clone the repository.

```sh
$ git clone https://github.com/Dev-Siri/uploadthing4j
```

Then get going with your IDE.

## License

This project is [MIT](LICENSE) licensed.