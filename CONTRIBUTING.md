# Issue Tracking

Library issues are tracked using [GitHub Issues](https://github.com/stkent/bugshaker-android/issues). Please review all tag types to understand issue categorization.

Always review open issues before opening a new one. If you would like to work on an existing issue, please comment to that effect and assign yourself to the issue.

# Conventions

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

# Generating Inline Licenses

Before opening a pull request, you must generate license headers in any new source files by executing the Gradle command:

```shell
./gradlew licenseFormat
```

The Travis CI pull request build will fail if any source file is missing this generated header.
