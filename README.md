# BugShaker-Android

Send Android bug reports via email. Shake to summon!

[![Build Status](https://travis-ci.org/stkent/bugshaker-android.svg?branch=master)](https://travis-ci.org/stkent/bugshaker-android)

# Introduction

BugShaker-Android allows your users to simply submit bug reports by shaking their device.
When a shake is detected, the current screen state is captured and the user is
prompted to submit a bug report via a mail composer with the screenshot attached.

The iOS version of BugShaker was written by [Dan Trenz](https://github.com/dtrenz) and is available [here](https://github.com/detroit-labs/BugShaker). This Android version builds on Square's shake-detection library, [seismic](https://github.com/square/seismic).

## Screenshots

TODO

# Getting Started

(1) Specify BugShaker-Android as a dependency in your build.gradle file:

```groovy
dependencies {
    compile 'com.github.stkent:bugshaker:0.3.0'
}
```

(2) Configure `BugShaker` in your custom `Application` class, and call `start()` to begin listening for shakes:

```java
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BugShaker.get(this)
                 .setEmailInfo("someone@example.com")
                 .setLoggingEnabled(BuildConfig.DEBUG)
                 .start();
    }

}
```

The `BugShaker` class contains several alternate `setEmailInfo` methods to allow you to specify multiple recipient email addresses and/or a custom subject line for your bug report emails. It is recommended that logging always be disabled in production builds.

# Contributing

## Issue Tracking

Library issues are tracked using GitHub Issues. Please review all tag types to understand issue categorization.

Always review open issues before opening a new one. If you would like to work on an existing issue, please comment to that effect and assign yourself to the issue.

## Generating Inline Licenses

Before opening a pull request, you must generate license headers in any new source files by executing the Gradle command:

    ./gradlew licenseFormat

<!-- The Travis CI pull request build will fail if any source file is missing this generated header. -->

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
