# Vice Module Generator

A generic, configurable module generator for Kotlin and Compose Multiplatform projects.
Generate complete screen modules with navigation, state management, and testing infrastructure
using a library, GUI application, or CLI.

## Overview

Vice Module Generator helps you quickly bootstrap new feature modules following best practices
and your project's conventions. It generates:

- Navigation setup with type-safe routing
- State management (Vice compositor pattern)
- UI composables with previews
- Test infrastructure
- Gradle build configuration
- Integration with your existing project structure

## Modules

### 📚 Library (`lib`)
Core generation logic that can be embedded in any JVM application.

```kotlin
val generator = ModuleGenerator()
val config = ModuleGeneratorConfig(
  projectDir = File("/path/to/project"),
  moduleName = "feature-name",
  packageName = "com.example.feature",
  featureName = "FeatureName",
)
val result = generator.generate(config)
```

**Maven Coordinates**:
```kotlin
implementation("com.eygraber:vice-module-generator-lib:0.1.0")
```

### 🖥️ GUI (`gui`)
Interactive Compose Desktop application for module generation.

```bash
./gradlew :gui:run --args="com.example"
```

**Download executable JAR**:
```bash
wget https://repo1.maven.org/maven2/com/eygraber/vice-module-generator-gui/0.1.0/vice-module-generator-gui-0.1.0.jar
java -jar vice-module-generator-gui-0.1.0.jar com.example
```

### ⌨️ CLI (`cli`)
Command-line interface for scripting and automation.

```bash
./gradlew :cli:run --args="com.example FeatureName --module-name=feature-name"
```

**Download executable JAR**:
```bash
wget https://repo1.maven.org/maven2/com/eygraber/vice-module-generator-cli/0.1.0/vice-module-generator-cli-0.1.0.jar
java -jar vice-module-generator-cli-0.1.0.jar com.example FeatureName --module-name=feature-name
```

## Quick Start

### Using the Library

Add the dependency to your build automation or Gradle plugin:

```kotlin
dependencies {
  implementation("com.eygraber:vice-module-generator-lib:0.1.0")
}
```

Then use the API:

```kotlin
import com.eygraber.vice.module.generator.lib.*

val generator = ModuleGenerator()

// Configure the generation
val config = ModuleGeneratorConfig(
  projectDir = File("."),
  moduleName = "cool-feature",
  packageName = "com.example.app.cool.feature",
  featureName = "CoolFeature",
  shouldIncludeEffects = true,
  shouldGeneratePreview = true,
)

// Validate configuration
when (val validation = generator.validate(config)) {
  is ValidationResult.Valid -> println("Configuration is valid!")
  is ValidationResult.Invalid -> {
    validation.errors.forEach { println("Error: $it") }
  }
}

// Generate the module
when (val result = generator.generate(config)) {
  is GenerationResult.Success -> println("Module generated successfully!")
  is GenerationResult.Failure -> println("Failed: ${result.message}")
}
```

## Configuration

The generator supports the following configuration options:

| Option                                   | Type    | Description                         | Default                    |
|------------------------------------------|---------|-------------------------------------|----------------------------|
| `projectDir`                             | File    | Root directory of your project      | Current directory          |
| `featureName`                            | String  | Feature name (PascalCase)           | Required                   |
| `moduleName`                             | String  | Module name (kebab-case)            | Inferred from feature name |
| `packageName`                            | String  | Package name (dot-separated)        | Inferred from feature name |
| `shouldIncludeEffects`                   | Boolean | Generate Vice effects handler       | false                      |
| `shouldGeneratePreview`                  | Boolean | Generate Compose previews           | true                       |
| `shouldGeneratePreviewParameterProvider` | Boolean | Generate preview parameter provider | true                       |

## Running from Source

Run the GUI:
```bash
./gradlew :gui:run --args="com.example"
```

Run the CLI:
```bash
./gradlew :cli:run --args="--help"
```

## Creating Wrapper Scripts

For projects that want to use the published JARs without adding dependencies, create a wrapper script:

```bash
#!/bin/bash
# generate-module.sh

VERSION="0.1.0"
JAR_NAME="vice-module-generator-cli-$VERSION.jar"
CACHE_DIR="$HOME/.vice-module-generator"
JAR_PATH="$CACHE_DIR/$JAR_NAME"

# Download JAR if not cached
if [ ! -f "$JAR_PATH" ]; then
    mkdir -p "$CACHE_DIR"
    wget -O "$JAR_PATH" \
        "https://repo1.maven.org/maven2/com/eygraber/vice-module-generator-cli/$VERSION/$JAR_NAME"
fi

# Run the generator
java -jar "$JAR_PATH" "com.example $@"
```

Make it executable:
```bash
chmod +x generate-module.sh
```

Use it:
```bash
./generate-module.sh MyFeature --module-name=my-feature
```

## Requirements

- Projects using:
  - [VICE](https://github.com/eygraber/vice) for state management
  - Compose for UI
  - kotlin-inject for dependency injection
