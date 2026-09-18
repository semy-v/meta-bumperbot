#!/bin/sh

set -eu

SYSTEMCTL=systemctl
RFKILL=rfkill
BLUETOOTHCTL=bluetoothctl

CONTROLLER_WAIT_SEC=5
POWER_WAIT_SEC=5

wait_for_controller()
{
    echo "Waiting for Bluetooth controller..."

    timeout=0

    while ! "${BLUETOOTHCTL}" list 2>/dev/null | grep -q '^Controller '; do
        if [ "${timeout}" -ge "${CONTROLLER_WAIT_SEC}" ]; then
            echo "Bluetooth controller did not appear within ${CONTROLLER_WAIT_SEC}s" >&2
            return 1
        fi

        sleep 0.1
        timeout=$((timeout + 1))
    done

    echo "Bluetooth controller is available"
}

power_on_controller()
{
    echo "Powering Bluetooth controller on..."

    timeout=0

    while :; do
        if "${BLUETOOTHCTL}" --timeout 1 power on 2>/dev/null; then
            if "${BLUETOOTHCTL}" --timeout 1 show 2>/dev/null |
                    grep -q 'Powered: yes'; then
                echo "Bluetooth controller powered on"
                return 0
            fi
        fi

        if [ "${timeout}" -ge "${POWER_WAIT_SEC}" ]; then
            echo "Failed to power Bluetooth controller on within ${POWER_WAIT_SEC}s" >&2
            return 1
        fi

        sleep 0.1
        timeout=$((timeout + 1))
    done
}

power_off_controller()
{
    echo "Powering Bluetooth controller off..."

    # bluetoothctl requires bluetoothd/BlueZ to be running.
    # Do not call it when Bluetooth is already disabled.
    if "${SYSTEMCTL}" is-active --quiet bluetooth.service; then
        echo "Powering Bluetooth controller off..."
        "${BLUETOOTHCTL}" power off 2>/dev/null || true
    else
        echo "Bluetooth service is already stopped; skipping bluetoothctl"
    fi
}

case "${1:-}" in
    start)
        echo "Enabling Bluetooth radio"

        echo "Stopping any stale bthelper instance"
        "${SYSTEMCTL}" stop bthelper@hci0.service 2>/dev/null || true

        echo "Unblocking Bluetooth"
        "${RFKILL}" unblock bluetooth

        echo "Starting BlueZ"
        "${SYSTEMCTL}" start bluetooth.service

        wait_for_controller
        power_on_controller
        ;;

    stop)
        echo "Disabling Bluetooth radio"

        power_off_controller

        # Defensive cleanup if bthelper was started manually or by an old image.
        "${SYSTEMCTL}" stop bthelper@hci0.service 2>/dev/null || true

        echo "Stopping BlueZ"
        "${SYSTEMCTL}" stop bluetooth.service 2>/dev/null || true

        # Defensive cleanup for configurations where hciuart is active.
        echo "Blocking Bluetooth"
        "${RFKILL}" block bluetooth
        ;;

    *)
        echo "Usage: $0 {start|stop}" >&2
        exit 2
        ;;
esac