#!/usr/bin/env bash

rm -rf target/
rm -rf resources/META-INF/native-image/tempConfig-*

# build java classes
clojure -T:build ci

# build native image
"$GRAALVM_HOME/bin/native-image" \
    -jar "target/graalvm-twitter-generative-bot-0.1.0-SNAPSHOT.jar" \
    "target/graalvm-twitter-generative-bot" \
    -H:+ReportExceptionStackTraces \
    -H:+PrintClassInitialization \
    --verbose \
    --native-image-info \
    "-J-Xmx3g"
