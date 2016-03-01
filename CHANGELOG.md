# Change Log

## v1.3.0

_2016-03-01_

- Reduce minimum SDK of the library to 14. Dropping below this is impractical, mainly because `Application.registerActivityLifecycleCallbacks` is only available on 14+.
- Update view hierarchy search algorithm to ensure that map bitmaps are overlaid in the correct order.

## v1.2.0

_2016-02-21_

- Alter internal handling of logging to avoid some log messages never printing.
    - **API change:** consumers must now call `assemble` before calling `start` when initializing the shared `BugShaker` instance. Calling this method "locks in" the current configuration.
- Allow consumers to specify alert dialog styling (native vs AppCompat) using the `setAlertDialogType` configuration method.
- Tweak email body language.

## v1.1.0

_2016-02-09_

- Update default feedback email subject line to include embedding application name.
- Include `MapView` representations in screenshots.
- Provide ProGuard configuration that will be used automatically by embedding applications if they themselves enable ProGuard.

## v1.0.0

_2016-01-22_

v0.9.0 has been stable in testing; promoting to 1.0.0!

- Add final assets for example application.

## v0.9.0

_2016-01-21_

- Update gnag version.
- Add dependency version checking plugin to help identify available dependency updates for future releases.
- Add assets for example application.

## v0.8.3

_2016-01-16_

- Dismiss dialog whenever an `Activity` resumes (fixes dialog showing inappropriately when returning to a backgrounded application)
- Add the string 'bugshaker' as a component of the provider authority for the provider that exposes app screenshots to mail applications (preemptive fix; make it much less likely that embedding applications will accidentally define a provider authority that conflicts with the BugShaker library provider authority).

## v0.8.2

_2016-01-15_

Bugfix release that _actually_ allows multiple applications that included BugShaker to be installed simultaneously. Provider authority is now totally determined by the embedding application's id.

## v0.8.1 [DO NOT USE]

_2016-01-15_

Bugfix release intended to allow multiple applications that included BugShaker to be installed simultaneously. It was discovered that this version causes a run-time error when attempting to report a bug with a screenshot, as the manifest provider authority did not match the in-code provider authority.

## v0.8.0 [DO NOT USE]

_2016-01-15_

Initial release for internal testing at Detroit Labs. It was discovered that this version causes an install-time error when attempting to install a second application that uses BugShaker on a device, due to a conflict in the declared FileProvider authorities.
