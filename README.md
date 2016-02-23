<img src="https://raw.githubusercontent.com/stkent/bugshaker-android/master/assets/repo_banner.png?raw=true" title="BugShaker" alt="The BugShaker logo" width="50%" />

Shake to send a bug report!

[![Build Status](https://travis-ci.org/stkent/bugshaker-android.svg?branch=master)](https://travis-ci.org/stkent/bugshaker-android) <a href="https://bintray.com/stkent/android-libraries/bugshaker/"><img src="https://img.shields.io/bintray/v/stkent/android-libraries/bugshaker.svg" /></a> <a href="http://www.detroitlabs.com/"><img src="https://img.shields.io/badge/Sponsor-Detroit%20Labs-000000.svg" /></a>

# Introduction

BugShaker allows your QA team and/or end users to easily submit bug reports by shaking their device. When [a shake is detected]((https://github.com/square/seismic)), the current screen state is captured and the user is prompted to submit a bug report via email with this screenshot attached.

This library is similar to [Telescope](https://github.com/mattprecious/telescope), but aims to be even easier to integrate into your apps and workflows:

- all configuration occurs in your custom [`Application`](http://developer.android.com/reference/android/app/Application.html) subclass (no view hierarchy alterations required);
- no need to request extra permissions;
- **iOS version** of this library is [already available](https://github.com/detroit-labs/BugShaker) (based on the same shake-to-send mechanism).

## Screenshots

<img src="https://raw.githubusercontent.com/stkent/bugshaker-android/master/assets/dialog_screenshot_1.2.0.png" width=420 /> <img src="https://raw.githubusercontent.com/stkent/bugshaker-android/master/assets/compose_screenshot_1.2.0.png" width=420 />

## Play Store Demo App

https://play.google.com/store/apps/details?id=com.github.stkent.bugshaker

# Getting Started

(1) Specify BugShaker-Android as a dependency in your build.gradle file:

```groovy
dependencies {
    compile 'com.github.stkent:bugshaker:{latest-version}'
}
```

(2) Configure the shared `BugShaker` instance in your custom [`Application`](http://developer.android.com/reference/android/app/Application.html) class, then call `assemble` and `start` to begin listening for shakes:

```java
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BugShaker.get(this)
                 .setEmailAddresses("someone@example.com")   // required
                 .setEmailSubjectLine("Custom Subject Line") // optional
                 .setAlertDialogType(AlertDialogType.NATIVE) // optional
                 .setLoggingEnabled(BuildConfig.DEBUG)       // optional
                 .setIgnoreFlagSecure(true)                  // optional
                 .assemble()                                 // required
                 .start();                                   // required
    }

}
```

It is recommended that logging always be disabled in production builds.

## Supporting SDK version &lt; 16

Bugshaker supports SDK >= 16 out of the box. If you try to install it on a build with an earlier SDK you will see an error in the processDebugManifest build task:

```
Error:Execution failed for task ':app:processDebugManifest'.
> Manifest merger failed : uses-sdk:minSdkVersion 15 cannot be smaller than version 16 declared in library [com.github.stkent:bugshaker:1.2.0] /path/to/AndroidManifest.xml
  Suggestion: use tools:overrideLibrary="com.github.stkent.bugshaker" to force usage
```

Just do as the error message suggests, and add the following &lt;uses-sdk&gt; element to the &lt;manifest&gt; element of your AndroidManifest.xml, to resolve the issue:

```
<uses-sdk tools:overrideLibrary="com.github.stkent.bugshaker"/>
```

# Contributing

## Issue Tracking

Library issues are tracked using GitHub Issues. Please review all tag types to understand issue categorization.

Always review open issues before opening a new one. If you would like to work on an existing issue, please comment to that effect and assign yourself to the issue.

## Conventions

Code committed to this project must pass selected style and correctness checks provided by:

- [FindBugs™](http://findbugs.sourceforge.net/);
- [PMD](https://pmd.github.io/);
- [checkstyle](http://checkstyle.sourceforge.net/).

This helps us focus on content only when reviewing contributions.

You can run these checks locally by executing the following Gradle command:

```shell
./gradlew checkLocal
```

Travis CI runs the same checks for each pull request and marks the build as failing if any check does not pass. Detailed information about every detected violation will be automatically posted to the conversation for that pull request. Violation detection and reporting is handled by the [gnag](https://github.com/btkelly/gnag) Gradle plugin.

## Generating Inline Licenses

Before opening a pull request, you must generate license headers in any new source files by executing the Gradle command:

    ./gradlew licenseFormat

The Travis CI pull request build will fail if any source file is missing this generated header.

# License

    Copyright 2016 Stuart Kent
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
