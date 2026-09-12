#!/bin/bash

cd "$(dirname "$0")/.." || exit 1

if [ -f "kas/private-config.yml" ]; then
    echo "Building with private config"
    kas-container build "kas/bumperbot-scarthgap-jazzy-raspberrypi5.yml:kas/private-config.yml"
else
    echo "No private config found, using default build"
    kas-container build "kas/bumperbot-scarthgap-jazzy-raspberrypi5.yml"
fi