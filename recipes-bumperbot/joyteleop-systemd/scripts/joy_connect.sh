#!/bin/bash

readonly INVALID_INDEX=255

joy_connect() {
    local JOY_MAC=$1

    bluetoothctl connect "$JOY_MAC"
    if [ $? -eq 0 ] && bluetoothctl info "$JOY_MAC" | grep -q "Connected: yes"; then
        echo "Success! Device '$JOY_MAC' connected."
        return 0
    fi

    echo "Error: connection to joystick '$JOY_MAC' failed." >&2
    return 1
}

joy_discovered() {
    if bluetoothctl devices | grep -qi "$1"; then
        return 0
    else
        return 1
    fi
}

joy_discover() {
    local -n MAC_LIST=$1
    local SCAN_TIMEOUT=20

    for i in "${!MAC_LIST[@]}"; do
        if joy_discovered "${MAC_LIST[$i]}"; then
            echo "Device '${MAC_LIST[$i]}' is already known. Skipping scan."
            return $i
        fi
    done

    echo "Device(s) '${MAC_LIST[@]}' not found in local cache. Starting background scan..."

    # Start bluetoothctl as an asynchronous co-process
    coproc BT_PROC { bluetoothctl; }

    # Send the scan command into the active process pipe
    echo "scan on" >&"${BT_PROC[1]}"
    
    # Loop until any device from the input list is discovered or timeout is reached
    local ELAPSED=0
    local FOUND_JOY_MAC_INDEX=$INVALID_INDEX

    while [ $ELAPSED -lt $SCAN_TIMEOUT ]; do
        for i in "${!MAC_LIST[@]}"; do
            if joy_discovered "${MAC_LIST[$i]}"; then
                FOUND_JOY_MAC_INDEX=$i
                echo "Device '${MAC_LIST[$FOUND_JOY_MAC_INDEX]}' found."

                # pair and trust the discovered device
                bluetoothctl pair "${MAC_LIST[$FOUND_JOY_MAC_INDEX]}"
                bluetoothctl trust "${MAC_LIST[$FOUND_JOY_MAC_INDEX]}"
                break 2
            fi
        done
        sleep 1
        ((ELAPSED++))
        echo "Scanning... ($ELAPSED/$SCAN_TIMEOUT seconds)"
    done
    
    # Cleanly stop the scan and exit the process loop
    echo "scan off" >&"${BT_PROC[1]}"
    echo "exit" >&"${BT_PROC[1]}"

    # Force kill the background process if it lingers
    kill $BT_PROC_PID > /dev/null 2>&1
    wait $BT_PROC_PID 2>/dev/null

    return $FOUND_JOY_MAC_INDEX
}


if [[ -v JOY_MAC_LIST ]]; then
    echo "JOY_MAC_LIST env value: '$JOY_MAC_LIST'"
    # redefine joystick MAC list as a string array 
    JOY_MAC_LIST=( $JOY_MAC_LIST )
else
    echo "Error: 'JOY_MAC_LIST' environment variable not set." >&2
    exit 1
fi

while [ ${#JOY_MAC_LIST[@]} -gt 0 ]; do
    joy_discover JOY_MAC_LIST
    INDEX=$?

    if [ "$INDEX" != "$INVALID_INDEX" ]; then
        if joy_connect "${JOY_MAC_LIST[$INDEX]}"; then
            exit 0
        else
            unset "JOY_MAC_LIST[$INDEX]"
        fi
    else
        echo "Error: Timeout reached. No devices discovered." >&2
        exit 1
    fi
done
