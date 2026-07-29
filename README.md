# Vice Module Generator

A generic, configurable module generator for Kotlin and Compose Multiplatform projects.
Generate complete screen modules with navigation, state management, and testing infrastructure
using a library, GUI application, or CLI.

## Overview

Vice Module Generator helps you quickly bootstrap new feature modules following best practices
and your project's conventions. Each screen is generated as a `public`/`impl` module pair:

- `screens/<feature>/public` owns the screen's serializable nav key, so other screens can
  navigate to it without depending on its implementation
- `screens/<feature>/impl` owns the VICE components (Compositor, ViewState, View, Intent) and
  the navigator, plus a `di` subpackage holding the screen's DI graph and a `NavEntryRegistrar`
  that contributes the screen's nav entry into the nav DI scope via a multibinding — so the
  `:nav` module never needs to be edited for a new screen

It generates:

- Navigation setup with type-safe routing
- A DI graph and a nav entry registrar in the impl module's `di` package, the registrar
  contributed to DI via multibinding
- State management (Vice compositor pattern)
- UI composables with previews
- Test infrastructure
- Gradle build configuration
- `settings.gradle.kts` includes and app module dependencies

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
implementation("com.eygraber:vice-module-generator-lib:0.1.5")
```

### 🖥️ GUI (`gui`)
Interactive Compose Desktop application for module generation.

**Run from source**:
```bash
./gradlew :gui:run --args="MyApp com.example"
```

**Download executable JAR**:
```bash
wget https://repo1.maven.org/maven2/com/eygraber/vice-module-generator-gui/0.1.5/vice-module-generator-gui-0.1.5.jar
java -jar vice-module-generator-gui-0.1.5.jar MyApp com.example
```

**Arguments**:
- First argument: Project name (e.g., `MyApp`)
- Second argument: Project package (e.g., `com.example`)

### ⌨️ CLI (`cli`)
Command-line interface for scripting and automation.

**Run from source**:
```bash
./gradlew :cli:run --args="--project-name=MyApp --project-package=com.example --feature=CoolFeature"
```

**Download executable JAR**:
```bash
wget https://repo1.maven.org/maven2/com/eygraber/vice-module-generator-cli/0.1.5/vice-module-generator-cli-0.1.5.jar
java -jar vice-module-generator-cli-0.1.5.jar --project-name=MyApp --project-package=com.example --feature=CoolFeature
```

**Required Arguments**:
- `--project-name=NAME`: The name of the project (e.g., `MyApp`)
- `--project-package=PACKAGE`: The root package for the project (e.g., `com.example`)
- `--feature=NAME`: The name of the feature in PascalCase (e.g., `CoolFeature`)

**Optional Arguments**:
- `--feature-package=PACKAGE`: Custom package name for the feature
- `--with-effects`: Include ViceEffects class in generation
- `--no-preview`: Skip generating Compose preview
- `--no-preview-provider`: Skip generating preview parameter provider
- `--kmp`: Generate for a Kotlin/Compose Multiplatform project
- `--metro`: Use Metro DI instead of kotlin-inject-anvil
- `--test-utils-module=PATH`: Gradle path of the project's test utilities module (default: `:test-utils`)
- `--dry-run`: Dry run, no files will be generated
- `--help`: Show help message

## Quick Start

### Using the Library

Add the dependency to your build automation or Gradle plugin:

```kotlin
dependencies {
  implementation("com.eygraber:vice-module-generator-lib:0.1.5")
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
| `isKmpProject`                           | Boolean | Generate for a KMP/CMP project      | false                      |
| `diFramework`                            | Enum    | KotlinInjectAnvil or Metro          | KotlinInjectAnvil          |
| `testUtilsModulePath`                    | String  | Gradle path of the test utils module | :test-utils               |

## Running from Source

Run the GUI:
```bash
./gradlew :gui:run --args="MyApp com.example"
```

Run the CLI:
```bash
./gradlew :cli:run --args="--project-name=MyApp --project-package=com.example --feature=CoolFeature"
```

Show CLI help:
```bash
./gradlew :cli:run --args="--help"
```

## Creating Wrapper Scripts

For projects that want to use the published JARs without adding dependencies, create a wrapper script:

### CLI Wrapper

```bash
#!/bin/bash
# generate-module.sh

PROJECT_NAME="MyApp"
PROJECT_PACKAGE="com.example"
VERSION="0.1.5"
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
java -jar "$JAR_PATH" --project-name="$PROJECT_NAME" --project-package="$PROJECT_PACKAGE" "$@"
```

Make it executable:
```bash
chmod +x generate-module.sh
```

Use it:
```bash
./generate-module.sh --feature=MyFeature
./generate-module.sh --feature=MyFeature --with-effects --no-preview
```

### GUI Wrapper

```bash
#!/bin/bash
# generate-module-gui.sh

PROJECT_NAME="MyApp"
PROJECT_PACKAGE="com.example"
VERSION="0.1.5"
JAR_NAME="vice-module-generator-gui-$VERSION.jar"
CACHE_DIR="$HOME/.vice-module-generator"
JAR_PATH="$CACHE_DIR/$JAR_NAME"

# Download JAR if not cached
if [ ! -f "$JAR_PATH" ]; then
    mkdir -p "$CACHE_DIR"
    wget -O "$JAR_PATH" \
        "https://repo1.maven.org/maven2/com/eygraber/vice-module-generator-gui/$VERSION/$JAR_NAME"
fi

# Run the generator
java -jar "$JAR_PATH" "$PROJECT_NAME" "$PROJECT_PACKAGE"
```

Make it executable:
```bash
chmod +x generate-module-gui.sh
```

Use it:
```bash
./generate-module-gui.sh
```

## Requirements

- Projects using:
  - [VICE](https://github.com/eygraber/vice) for state management
  - Compose for UI
  - kotlin-inject for dependency injection
