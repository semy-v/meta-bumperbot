#! /usr/bin/env python3

from gpiozero import Button
from signal import pause
from enum import Enum, auto
import subprocess
import threading
import sys


class TargetState(Enum):
    STARTING = auto()
    STARTED = auto()
    STOPPING = auto()
    STOPPED = auto()


class TeleopController:
    def __init__(self, unit: str):
        self.unit = unit
        self.state = TargetState.STOPPED
        self.lock = threading.Lock()
        self.active_process = None
        self.monitor_thread = None

    def on_button_pressed(self):
        with self.lock:
            if self.active_process is not None and self.active_process.poll() is None:
                print("Interrupting current action...", flush=True)
                self.active_process.terminate()

            prev_state = self.state
            match prev_state:
                case TargetState.STOPPED | TargetState.STOPPING:
                    action = "start"
                    self.state = TargetState.STARTING
                case TargetState.STARTED | TargetState.STARTING:
                    action = "stop"
                    self.state = TargetState.STOPPING

            print(
                f"Control button pressed: transition '{prev_state.name}' -> '{self.state.name}'",
                flush=True,
            )

            cmd = ["systemctl", action, self.unit]
            self.active_process = subprocess.Popen(
                cmd,
                shell=False,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
            )

            self.monitor_thread = threading.Thread(
                target=self._wait_and_transition,
                args=(self.active_process, action),
                daemon=True,
            )
            self.monitor_thread.start()

    def _wait_and_transition(self, process, action):
        stdout, stderr = process.communicate()
        return_code = process.returncode

        with self.lock:
            if self.active_process is not process:
                return

            if return_code == 0:
                print(
                    f"{action} bluetooth joystick teleoperation succeeded", flush=True
                )
                self.state = (
                    TargetState.STARTED if action == "start" else TargetState.STOPPED
                )
            else:
                print(
                    f"{action} unit failed with error: {stderr.strip()}",
                    file=sys.stderr,
                    flush=True
                )
                self.state = TargetState.STOPPED

            print(f"transitioned to '{self.state.name}'", flush=True)
            self.active_process = None


if __name__ == "__main__":
    controller = TeleopController("bluetooth_joy_teleop.target")

    button = Button(17, pull_up=False)
    button.when_pressed = controller.on_button_pressed

    print("Listening for button presses...", flush=True)
    pause()
