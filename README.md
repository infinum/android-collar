[ ![Download](https://api.bintray.com/packages/infinum/android/collar-plugin/images/download.svg) ](https://bintray.com/infinum/android/collar-plugin/_latestVersion)

# Collar Android plugin

Gradle plugin which collects all analytics screen names, events and user properties for Android projects.  
This plugin has been written in Kotlin but works both inside Kotlin and Java projects.

The project is organized in the following modules:

- `annotations` - contains all annotations necessary for the plugin to work
- `core` - plugin implementation core, depends on the `annotations` module
- `processor` - annotation processor generating and transforming classes for plugin, depends on the `annotations` module
- `plugin` - the Gradle plugin that adds all necessary dependencies to the project
- `ui` - contains a single screen UI that provides visual tracking of sent events
- `ui-no-op` - contains a stub for easy release implementation of UI package
- `sample` - a sample app for testing the Gradle plugin

## Usage

To include plugin to your project, you have to add buildscript dependencies in your project level `build.gradle`:

```gradle
buildscript {
    repositories {
        jcenter()
        // for plugin
        maven { url "http://dl.bintray.com/infinum/android" }
    }
    dependencies {
        classpath "co.infinum.collar:collar-plugin:1.0.0"
    }
}
```

Then apply the plugin in your app `build.gradle`:

```gradle
apply plugin: "co.infinum.collar.plugin"
```

Now you can sync your project.


### Getting started

Create or inject an instance of _Collar_ in your Application class and attach a _Collector_:


```kotlin
Collar.attach(object : Collector {

    override fun onScreen(screen: Screen) =
        analyticsProvider.sendScreenName(activity = screen.activity, screenName = screen.name)

    override fun onEvent(event: Event) =
        analyticsProvider.sendEvent(eventName = event.name, eventParameters = event.params ?: Bundle.EMPTY)

    override fun onProperty(property: Property) =
        analyticsProvider.sendProperty(property.name, property.value)
})
```

_analyticsProvider_ is your own implementation of an analytics delegate class.

#### Screen names

Screen names can be annotated on top of **Activities** or **Fragments**. No other views are eligible as screen name destination holders.

```kotlin
@ScreenName(AnalyticsKeys.ScreenName.BRAND_DETAILS)
class BrandFragment : Fragment(R.layout.fragment_brand) {

    ...

    override fun onResume() = super.onResume().run { trackScreen() }

}
```

Actual screen name annotation values are maintained and provided per project implementing this plugin.  
If a screen name annotation value is not provided then class name will be used as annotation value.  
A convenience extension method will be generated by annotation processor _trackScreen_ which must be called explicitly and preferably in _onResume_ lifecycle callback.  
However, you can always use _Collar_ methods explicitly besides the extension one to track your screen names.  

#### Events

Create a model container **sealed** class with arbitrary name where you see fit in your project and annotate as illustrated below.  
Nested classes represent the events you want to track.   
Class name will be processed into lowercase snake_case appropriate for most analytics dependencies, as well as the containing variable names.  
Variable values will be propagated as event parameter values.  
You can override event name for specific nested class then use _EventName_ annotation with a respective new event name.  
Accordingly, you can override event parameter name using _EventParameterName_ annotation too.
  

```kotlin
@AnalyticsEvents
sealed class AnalyticsEvent {

    data class LanguageSelection(
        val selectedLanguage: String,
        val exhibitorCount: Int
    ) : AnalyticsEvent()
    
    class Login : AnalyticsEvent()
    
    @EventName("login_guest")
    class Guest : AnalyticsEvent()
    
    data class ShortcutLink(
        @EventParameterName("URL")
        val url: String
    ) : AnalyticsEvent()
}
```
You can have multiple annotated sealed classes declared like this through your project.  
A convenience extension method will be generated by annotation processor _trackEvent_ which must be called explicitly with the instance of your previously defined event class.  
However, you can always use _Collar_ methods explicitly besides the extension one to track your analytics events in the same way.
  
```kotlin
@ScreenName(AnalyticsKeys.ScreenName.BRAND_DETAILS)
class BrandFragment : Fragment(R.layout.fragment_brand) {

    ...

    override fun onResume() = super.onResume().run { trackScreen() }
    
    ...
    
    private fun onFavor(exhibitor: Exhibitor) {
        viewModel.favor(exhibitor)
        
        ...
        
        trackEvent(AnalyticsEvent.ExhibitorFavored(exhibitorId = exhibitor.id, exhibitorName = exhibitor.name))
    }

}
```
#### Properties

Create a model container **sealed** class with arbitrary name where you see fit in your project and annotate as illustrated below.  
Nested classes represent the user properties you want to track.   
Class name will be processed into lowercase snake_case appropriate for most analytics dependencies, as well as the containing variable names.  
Variable value will be propagated as user property value.  
You can override user property name for specific nested class then use _PropertyName_ annotation with a respective new property name.  
A property nested class can have only 1 declared variable as value. 
  

```kotlin
@UserProperties
sealed class UserProperty {

    @PropertyName(value = "user_type_corporate")
    data class UserType1(
        val value: String
    ) : UserProperty()
    
    data class UserTypeRetail(
        val someCoolValue: String
    ) : UserProperty()
}
```
You can have multiple annotated sealed classes declared like this through your project.  
A convenience extension method will be generated by annotation processor _trackProperty_ which must be called explicitly with the instance of your previously defined property class.  
However, you can always use _Collar_ methods explicitly besides the extension one to track your user properties in the same way.
  
```kotlin
@ScreenName(AnalyticsKeys.ScreenName.BRAND_DETAILS)
class BrandFragment : Fragment(R.layout.fragment_brand) {
    
    ...

    override fun onCreate() = super.onCreate().run {
        trackProperty(UserProperty.UserTypeRetail("retail"))
    }

    ...

    override fun onResume() = super.onResume().run { trackScreen() }
    
    ...
    
    private fun onFavor(exhibitor: Exhibitor) {
        viewModel.favor(exhibitor)
        
        ...
        
        trackEvent(AnalyticsEvent.ExhibitorFavored(exhibitorId = exhibitor.id, exhibitorName = exhibitor.name))
    }

}
```
## Options

### Annotation processor

_Collar_ plugin has been tuned per default for the most often used Firebase Analytics but additional options can be passed to annotation processor for fine tuning:

```gradle
javaCompileOptions {
    annotationProcessorOptions {
        argument "screen_name_length", "33"
        argument "events_count", "100"
        argument "event_parameters_count", "10"
        argument "event_name_length", "20"
        argument "properties_count", "17"
        argument "reserved_prefixes", "ga_,fb_"
        argument "reserved_properties", "Age,Country"
    }
}
```
### Plugin extension
```gradle
collar {
    version "1.0.0"
    extended true
}
```        
You can set a specific _Collar_ version to be used.
If you set _extended_ to false then _Collar_ will not provide all necessary external dependencies out of the box, like _AndroidX Core-KTX_, but it is your responsibility to provide them in your own project setup.  
These are, at the moment:
```gradle
implementation "androidx.core:core-ktx:1.2.0"
```
## Debug UI
A separate package and no-op package is provided if you want to visually track what has been sent through Collar.  
You can search, filter and clear all sent analytics.  
In your app `build.gradle` add:
```gradle
debugImplementation "co.infinum.collar:collar-ui:1.1.0"
releaseImplementation "co.infinum.collar:collar-ui-no-op:1.1.0"
```
In order to start tracking with UI you must use _LiveCollector_ as in this example:
```kotlin
 Collar.attach(object : LiveCollector(context, true) {

    override fun onScreen(screen: Screen) =
        super.onScreen(screen).run {
            analyticsProvider.sendScreenName(activity = screen.activity, screenName = screen.name)
        }

    override fun onEvent(event: Event) =
        super.onEvent(event).run {
            analyticsProvider.sendEvent(eventName = event.name, eventParameters = event.params ?: Bundle.EMPTY)
        }

    override fun onProperty(property: Property) =
        super.onProperty(property).run {
            analyticsProvider.sendProperty(property.name, property.value)
        }
})
```
A notification will show once analytics are gathered and clicking on it will open a dedicated screen.

![Notification](notification.jpg)![UI](ui.jpg)

## CollarGenerate Gradle Task

This library supports code generation from the `.json` file. To do that you will need to specify `filePath` and `outputPath` in  `collar` plugin extension. For example:

```
collar {
    version "1.1.0"
    extended true
    filePath = "${project.projectDir}/src/main/assets/example.json"
    outputPath = "${project.projectDir}/src/main/kotlin/co/infinum/collar/sample/analytics"
}
```
Important: `outputPath` needs to have `/kotlin` or `/java` so we can know how to prepare package values for new generated classes.

`.json` file has to be formatted in the same way as it is in `Sample` project. 

To run the task you can:

- Open `gradle` panel on right side, find `collarplugin` task group and run `collarGenerate` task 
- Type `./gradlew collarGenerate` in terminal

Gradle task will generate classes prepared for the Collar lib.

## TODO
- Add lifecycle aware screen tracking for AndroidX views
- Provide a separate test artifact
- Provide arbitrary map of key/value event support
- Write tests before public release milestone  

## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](LICENSE).

## License

```
Copyright 2020 Infinum

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Credits

Maintained and sponsored by [Infinum](http://www.infinum.com).

<a href='https://infinum.co'>
  <img src='https://infinum.co/infinum.png' href='https://infinum.com' width='264'>
</a>
