#!/bin/bash

if [[ -v JOY_MAC_LIST ]]; then
    echo "JOY_MAC_LIST env value: '$JOY_MAC_LIST'"
    # redefine joystick MAC list as a string array 
    JOY_MAC_LIST=( $JOY_MAC_LIST )

    # disconnect any connected device
    for JOY_MAC in ${JOY_MAC_LIST[@]}; do
        bluetoothctl disconnect $JOY_MAC
    done

    exit 0
else
    echo "Error: 'JOY_MAC_LIST' environment variable not set." >&2
    exit 1
fi
