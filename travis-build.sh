#!/bin/bash
set -ev

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew clean bugshaker:check example:assembleDebug
else
	./gradlew clean bugshaker:checkAndReport example:assembleDebug -PgitHubAuthToken="${PR_BOT_AUTH_TOKEN}" -PgitHubIssueNumber="${TRAVIS_PULL_REQUEST}"
fi
