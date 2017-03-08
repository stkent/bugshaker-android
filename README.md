<img src="https://raw.githubusercontent.com/stkent/bugshaker-android/master/assets/repo_banner.png?raw=true" title="BugShaker" alt="The BugShaker logo" width="50%" />

Shake to send a bug report!

[![Build Status](https://travis-ci.org/stkent/bugshaker-android.svg?branch=master)](https://travis-ci.org/stkent/bugshaker-android) <a href="https://bintray.com/stkent/android-libraries/bugshaker/"><img src="https://img.shields.io/bintray/v/stkent/android-libraries/bugshaker.svg" /></a> <a href="http://www.detroitlabs.com/"><img src="https://img.shields.io/badge/Sponsor-Detroit%20Labs-000000.svg" /></a> [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-BugShaker-green.svg?style=flat)](http://android-arsenal.com/details/1/3299)

# Development Status

**Maintained**

- Not currently under active development.
- Active development may resume in the future.
- Bug reports will be triaged and fixed. No guarantees are made regarding fix timelines.
- Feature requests will be triaged. No guarantees are made regarding acceptance or implementation timelines.
- Pull requests from external contributors are not currently being accepted.

# Introduction

BugShaker allows your QA team and/or end users to easily submit bug reports by shaking their device. When [a shake is detected]((https://github.com/square/seismic)), the current screen state is captured and the user is prompted to submit a bug report via email with this screenshot attached.

This library is similar to [Telescope](https://github.com/mattprecious/telescope), but aims to be even easier to integrate into your apps and workflows:

- all configuration occurs in your custom [`Application`](http://developer.android.com/reference/android/app/Application.html) subclass (no view hierarchy alterations required);
- no need to request extra permissions;
- **iOS version** of this library is [already available](https://github.com/detroit-labs/BugShaker) (based on the same shake-to-send mechanism).

## Screenshots

<img src="https://raw.githubusercontent.com/stkent/bugshaker-android/master/assets/dialog.png" width=420 /> <img src="https://raw.githubusercontent.com/stkent/bugshaker-android/master/assets/compose.png" width=420 />

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
