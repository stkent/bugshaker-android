# Change Log

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
