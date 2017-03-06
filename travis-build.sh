#!/bin/bash
set -ev

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew clean :bugshaker:gnagCheck :example:assembleDebug
else
	./gradlew clean :bugshaker:gnagReport :example:assembleDebug -PauthToken="${PR_BOT_AUTH_TOKEN}" -PissueNumber="${TRAVIS_PULL_REQUEST}"
fi
