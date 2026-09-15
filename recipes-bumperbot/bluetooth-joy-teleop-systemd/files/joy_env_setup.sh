#!/bin/sh

. /opt/ros/jazzy/setup.sh

export JOY_MAC_LIST='AC:36:1B:81:58:91 90:B6:85:54:C0:ED'

env | while IFS='=' read -r name value; do
    case "$name" in
        # Skip variables that should not be inherited by the service.
        PWD|OLDPWD|SHLVL|_|SSH_CONNECTION|SSH_CLIENT|SSH_TTY)
            continue
            ;;
    esac

    # Escape backslashes and double quotes for systemd EnvironmentFile syntax.
    value=$(printf '%s' "$value" | sed 's/\\/\\\\/g; s/"/\\"/g')
    printf '%s="%s"\n' "$name" "$value"
done > /run/joyteleop.env