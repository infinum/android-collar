Change Log
==========

## Version 1.4.0

_2023-03-27_

* Replace Koin with Kotlin Inject.
* Refactor notifications.

## Version 1.3.9

_2023-03-217_

* Replace Koin with Kotlin Inject.

## Version 1.3.9

_2023-03-21_

* Update Kotlin to 1.8.10.
* Update dependencies.
* Implement Tiramisu post notification permission.

## Version 1.3.8

_2023-01-31_

* Update dependencies.
* Fix Koin backwards compatibility issues.

## Version 1.3.7

_2022-08-24_

* Make core module pure Kotlin.
* Replace Bundle with Map dependency.
* Fix Android 13 compatibility issues.

## Version 1.3.6

_2022-08-09_

* Fix generator value modifiers.
* Avoid reserved Kotlin keywords in generator.
* Fix Kotlin compiler flags.

## Version 1.3.5

_2022-08-05_

* Update dependencies.
* Add Kotlin default parameter option for compiler backend.

## Version 1.3.4

_2022-05-10_

* Expose event and property name Regex to plugin configuration.

## Version 1.3.3

_2022-05-06_

* Fix plugin implicit dependencies.

## Version 1.3.2

_2021-12-24_

* Update Kotlin to 1.6.10.
* Update Gradle wrapper to 7.3.3.
* Update AndroidX dependencies.
* Fix collecting flows on lifecycle.
* Fix core JVM static method calls with Kotlin 1.6.x.
* Fix missing event parameters in session list.
* Fix large event parameters in detail view.
* Fix large event parameters for in app notification.
* Add exhaustive branches in generated code.

## Version 1.3.1

_2021-09-27_

* Deprecate screen name length limit.
* Update Kotlin to 1.5.31.
* Update Kotlin Poet and fix breaking changes.
* Update various dependencies.
* Prepare and optimize for Android 12.
* Use KSP for Room in UI module.
* Refactor AlertDialog to BottomSheetDialogFragment.
* Optimize Snackbar ViewBinding.

## Version 1.3.0

_2021-08-23_

* Add custom Lint checks.
* Fix R8 collisions on obfuscated class names.

## Version 1.2.9

_2021-08-21_

* Update dependencies.

## Version 1.2.8

_2021-07-22_

* Update dependencies.
* Fix memory leaks.

## Version 1.2.7

_2021-07-09_

* Update dependencies.

## Version 1.2.6

_2021-05-21_

* Update dependencies.
* Fix API 19 compatibility issues.

## Version 1.2.5

_2021-05-04_

* Lower min SDK from 21 to 19 per client request.

## Version 1.2.4

_2021-04-10_

* Update Gradle to 7.0.
* Replace dependencies.gradle with version catalogs.
* Update to stable dependencies.
* Remove Bintray configuration.

## Version 1.2.3

_2021-02-13_

* Update Gradle to 6.8.2.
* Fix bugs in processor specs.
* Move to Maven Central.
* Remove version from plugin extension.

## Version 1.2.2

_2021-01-29_

* Update Gradle to 6.8.1.
* Fix a crash bug when shadowing ViewModel clear() method.

## Version 1.2.1

_2021-01-18_

* Enable Kotlin explicit mode.

## Version 1.2.0

_2021-01-13_

* Update Gradle to 6.8.
* Update Kotlin to 1.4.21.
* Make plugin extension public.

## Version 1.1.9

_2020-30-10_

* New: Add collection disabled UI indicator.
* New: Add analytics collection status setter method to Collector.
* Fix Lint warnings.

## Version 1.1.8

_2020-09-28_

* Fix no op package.
* Update Kotlin on 1.4.10

## Version 1.1.7

_2020-09-09_

 * Replace ContentProvider with AndroidX Startup Initializer

## Version 1.1.6

_2020-09-01_

 * **BREAKING**: Removed activity field from Screen class due to Firebase deprecated _setCurrentScreen_ method.
 * **BREAKING**: Implement LiveCollector configuration class.
 * **BREAKING**: Rename Collar plugin extension parameter _filePath_ to _fileName_.
 * Update Kotlin to 1.4.0.
 * Update various dependencies.
 * Implement latest changes on GeneratorTask.
 * Update Gradle wrapper to 6.6.
 * Set explicit dependency version for Kotlin Reflect to avoid adding multiple versions resolution.
 * Add Javadoc to exposed classes and methods.
 * Update KotlinPoet to 1.6.0.
 * Update Detekt to 1.11.0.
 * Implement redaction feature.
 * Replace Moshi with KotlinX Serialization in generators
 * Make description optional in generator models
 * Make members optional in AnalyticsModel
 * Make GenerateTask inherit from SourceTask

## Version 1.1.5

_2020-04-28_

 * Fix: Annotated event parameter names collected properly

## Version 1.1.4

_2020-04-20_

 * New: Add empty state on UI
 * Fix: Fix dependency configurations
 * Fix: Fix crash on running Activity without implementing a Collector

## Version 1.1.3

_2020-04-16_

 * New: Add session settings
 * New: Add deployment tasks
 * Fix: Fix initial code debt
 * Fix: Fix dark mode
 * Fix: Enable ViewBinding

## Version 1.1.2

_2020-04-14_

 * New: Add generate Gradle task support
 * New: Enhance LiveCollector with in app popups
 * Fix: Refactor processor
 * Fix: Remove deprecated Kotlin Metadata dependency
 * Fix: Remove extended AndroidX Core KTX support
 * Fix: Simplify build scripts

## Version 1.1.1

_2020-03-16_

 * New: Allow enums without constants.
 * New: Improved formatting of TypeSpec KDoc.
 * New: Support @property and @param KDoc tags in TypeSpec.
 * Fix: Use pre-formatted strings for arguments to %P.

## Version 1.1.0

_2020-03-01_

 * New: Improved formatting of TypeSpec KDoc.
 * New: Support @property and @param KDoc tags in TypeSpec.
 * Fix: Use pre-formatted strings for arguments to %P.

## Version 1.0.0

_2020-02-24_

 * Initial public release.