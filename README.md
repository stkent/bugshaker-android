# BugShaker

BugShaker allows your users to simply submit bug reports by shaking the device.
When a shake is detected, the current screen state is captured and the user is
prompted to submit a bug report via a mail composer with the screenshot attached.

## Screenshots

TODO

## Usage

TODO: update this info for the Android version:
<!--To run the example project, clone the repo, and run `pod install` from the Example directory first.

All you have to do to enable bug reporting is import `BugShaker` in your `AppDelegate`
and call the `configure()` method in `application:didFinishLaunchingWithOptions`,
passing in the array of email recipients and an optional custom subject line:-->

```java
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
//            final BugShaker bugShaker = new BugShaker(this, ["stuart@detroitlabs.com"], "Bug Report");
//            bugShaker.start();
        }
    }

}
```

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
