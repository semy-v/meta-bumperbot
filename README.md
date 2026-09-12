# meta-bumperbot

A dedicated Yocto Project layer and deployment configuration for **BumperBot**, an autonomous differential-drive mobile robot based on the **Raspberry Pi 5** and **Arduino Nano ESP32**.

This repository is the embedded Linux deployment companion to the [bumperbot-ros2-core](https://github.com/semy-v/bumperbot-ros2-core) application stack. It provides the Yocto/Kas build configuration, BitBake recipes, systemd integration, networking configuration, and required upstream ROS 2 patches to build a custom **ROS 2 Jazzy** image for the Raspberry Pi 5.

The resulting image integrates the BumperBot ROS 2 software stack and boots directly into the robot's systemd-managed runtime environment.

---

## 🎯 Repository Purpose

`meta-bumperbot` bridges the gap between the BumperBot ROS 2 application stack and its target embedded Linux platform.

The layer currently provides:

- **Yocto Scarthgap + ROS 2 Jazzy build configuration** for the Raspberry Pi 5.
- **Cross-compilation of the BumperBot ROS 2 packages** from the companion `bumperbot-ros2-core` repository.
- **ESP32-based robot hardware integration**. The Raspberry Pi communicates with the Arduino Nano ESP32 over USB using the custom BumperBot binary communication protocol implemented by `bumperbot_firmware`.
- **ROS 2 Control deployment**, including the custom hardware interface, differential-drive control, joint-state publication, and MPU6050 IMU integration used by the robot stack.
- **Autonomous system startup** through a custom `bumperbot.target` and dedicated systemd units for the robot state publisher, controllers, localization, and motion-control server.
- **Network provisioning** through NetworkManager, including Ethernet and Wi-Fi connection profiles and disabled MAC randomization for predictable connectivity.
- **Bluetooth enablement** through the BlueZ 5 integration.
- **Upstream ROS 2 build fixes** through `.bbappend` files and source patches required for the embedded build.
- **A containerized Kas build workflow** so the Yocto build can be reproduced without installing the complete cross-compilation environment directly on the host.

---

## 🧩 BumperBot Embedded Architecture

The deployment is split between two repositories:

```text
bumperbot-ros2-core
        │
        │  ROS 2 application / control stack
        ▼
+-----------------------------+
| Raspberry Pi 5              |
|                             |
| Yocto Linux                 |
| ROS 2 Jazzy                 |
|                             |
| bumperbot_* packages        |
| ros2_control                |
| EKF localization            |
| motion control              |
| serial transceiver          |
+--------------┬--------------+
               │ USB
               │ custom binary protocol
               ▼
+-----------------------------+
| Arduino Nano ESP32          |
|                             |
| bumperbot_firmware          |
| wheel control               |
| velocity estimation         |
| encoder / FG pulse handling |
| MPU6050 sensor access       |
+--------------┬--------------+
               │
       +-------+-------+
       │               │
   BLDC motors       MPU6050
```

The Yocto image provides the Raspberry Pi operating environment; the companion ROS 2 repository provides the application and hardware-interface software; the ESP32 firmware is built from the `bumperbot_firmware/arduino/esp32_controller` source tree.

---

## 📂 Repository Structure

```text
.
├── kas
│   ├── bumperbot-scarthgap-jazzy-raspberrypi5.yml
│   └── private-config.yml
├── LICENSE
├── README.md
├── recipes-bumperbot
│   ├── bumperbot-bringup
│   │   └── bumperbot-bringup_0.1.0.bb
│   ├── bumperbot-controller
│   │   └── bumperbot-controller_0.1.0.bb
│   ├── bumperbot-description
│   │   └── bumperbot-description_0.1.0.bb
│   ├── bumperbot-firmware
│   │   └── bumperbot-firmware_0.1.0.bb
│   ├── bumperbot-localization
│   │   └── bumperbot-localization_0.1.0.bb
│   ├── bumperbot-motion
│   │   └── bumperbot-motion_0.1.0.bb
│   ├── bumperbot-msgs
│   │   └── bumperbot-msgs_0.1.0.bb
│   └── bumperbot-systemd
│       ├── bumperbot-systemd
│       │   ├── bumperbot_localization.service
│       │   ├── bumperbot_motion.service
│       │   ├── bumperbot.target
│       │   ├── controller_manager.service
│       │   ├── diff_drive_controller.service
│       │   ├── imu_sensor_broadcaster.service
│       │   ├── joint_state_broadcaster.service
│       │   └── robot_state_publisher.service
│       └── bumperbot-systemd_1.0.bb
├── recipes-connectivity
│   ├── bluez5
│   │   └── bluez5_%.bbappend
│   └── networkmanager
│       ├── files
│       │   ├── 99-disable-mac-random.conf
│       │   ├── eth0.nmconnection
│       │   ├── override.conf
│       │   └── wifi.nmconnection.in
│       └── network-profiles_1.0.bb
├── recipes-control-toolbox
│   └── control-toolbox
│       └── control-toolbox_%.bbappend
├── recipes-ros-append
│   ├── controller-manager
│   │   ├── controller-manager
│   │   │   └── 0001-add-pal_statistics-dependency.patch
│   │   └── controller-manager_%.bbappend
│   ├── hardware-interface
│   │   └── hardware-interface_%.bbappend
│   ├── nav2
│   │   └── nav2-%.bbappend
│   ├── nav2-msgs
│   │   └── nav2-msgs_%.bbappend
│   ├── pal-statistics
│   │   ├── pal-statistics
│   │   │   └── 0001-replace-the-pal_statistics-header_files.patch
│   │   └── pal-statistics_%.bbappend
│   └── ros2-control-cmake
│       ├── ros2-control-cmake
│       │   └── 0001-remove-warning-as-errors-to-satisfy-the-build.patch
│       └── ros2-control-cmake_%.bbappend
├── recipes-support
│   └── libserial
│       ├── libserial
│       │   └── 0001-add-cstdint-header-for-fixed-width-integer-types.patch
│       └── libserial_%.bbappend
└── scripts
    └── kas_container_build.sh
```

### Layer organization

| Directory | Purpose |
|---|---|
| `kas/` | Reproducible Kas build configurations, including the main Raspberry Pi 5 / Scarthgap / Jazzy configuration and private local configuration. |
| `recipes-bumperbot/` | BitBake recipes for the BumperBot ROS 2 packages and systemd integration. |
| `recipes-connectivity/` | NetworkManager connection profiles and BlueZ 5 configuration. |
| `recipes-control-toolbox/` | Build integration/customization for `control-toolbox`. |
| `recipes-ros-append/` | `.bbappend` files and source patches applied to upstream ROS 2 packages. |
| `recipes-support/` | Support-library build fixes, currently including LibSerial. |
| `scripts/` | Helper scripts for running the Kas containerized build. |

---

## 🤖 BumperBot Recipes

The `recipes-bumperbot` directory maps the ROS 2 core stack into Yocto packages:

| Recipe | Source package / function |
|---|---|
| `bumperbot-bringup` | Robot launch and top-level bringup configuration |
| `bumperbot-controller` | ROS 2 Control configuration and teleoperation support |
| `bumperbot-description` | URDF/Xacro robot description and meshes |
| `bumperbot-firmware` | Raspberry Pi serial transceiver, hardware interface, and ESP32 firmware sources |
| `bumperbot-localization` | EKF-based localization configuration |
| `bumperbot-motion` | Motion-control server and controller plugins |
| `bumperbot-msgs` | Custom ROS 2 messages and actions |
| `bumperbot-systemd` | Automatic runtime startup and service orchestration |

The `bumperbot-firmware` recipe packages the software responsible for communicating with the ESP32 controller over USB and exposing the robot hardware to the ROS 2 stack.

---

## ⚙️ Systemd Runtime

BumperBot uses a dedicated systemd target to start the robot software automatically after the operating system boots.

```text
bumperbot.target
    ├── robot_state_publisher.service
    ├── controller_manager.service
    ├── joint_state_broadcaster.service
    ├── diff_drive_controller.service
    ├── imu_sensor_broadcaster.service
    ├── bumperbot_localization.service
    └── bumperbot_motion.service
```

This allows the Raspberry Pi 5 to boot directly into the robot runtime without requiring a user to manually start the ROS 2 nodes over SSH.

---

## 🌐 Network Configuration

Network connectivity is provided through **NetworkManager**.

The layer installs:

- An Ethernet connection profile for `eth0`.
- A Wi-Fi connection profile generated from `wifi.nmconnection.in`.
- NetworkManager configuration to disable MAC-address randomization for predictable robot networking.
- A systemd override/configuration required by the deployed network environment.

### Configure Wi-Fi credentials

Keep credentials out of the public repository. The build supports a separate Kas configuration file:

```text
kas/private-config.yml
```

Use that file for local/private configuration values rather than committing credentials to `bumperbot-scarthgap-jazzy-raspberrypi5.yml`.

The exact variables and overrides used by the current project are defined in the Kas configuration files in `kas/`.

---

## 🛠️ Build Environment

The project uses **Yocto Project Scarthgap**, **ROS 2 Jazzy**, **Kas**, and the **Raspberry Pi 5** BSP.

The preferred build flow uses the Kas container so the build dependencies and toolchain are isolated from the host operating system.

### Host prerequisites

Install the tools required to run the build environment, including:

- Docker or Podman
- Python 3 / `pip`
- Kas
- `git`

### Install Kas

For example:

```bash
pip3 install kas
```

or:

```bash
sudo apt install kas
```

---

## 🧱 Build the Raspberry Pi 5 Image

From the root of the `meta-bumperbot` repository:

```bash
kas-container build kas/bumperbot-scarthgap-jazzy-raspberrypi5.yml
```

The main Kas configuration defines the required layers and build configuration for the BumperBot image, including the Raspberry Pi BSP, OpenEmbedded dependencies, and ROS 2 integration.

A helper script is also available:

```bash
./scripts/kas_container_build.sh
```

### Build output

The final Raspberry Pi 5 deploy artifacts are generated below:

```text
build/tmp-glibc/deploy/images/raspberrypi5/
```

The primary SD-card image is:

```text
ros-image-core-jazzy-raspberrypi5.rootfs.wic.bz2
```

A corresponding `.bmap` file may also be produced by the build configuration.

---

## 💾 Flash the Image to a microSD Card

Install `bmaptool` on the host if necessary, then identify the target SD-card device carefully.

For example, when the SD card is `/dev/sdc`:

```bash
sudo bmaptool copy ~/workspace/ros2/meta-bumperbot/build/tmp-glibc/deploy/images/raspberrypi5/ros-image-core-jazzy-raspberrypi5.rootfs.wic.bz2 /dev/sdc
```

> **⚠️ Warning:** `/dev/sdc` is only an example. Verify the actual SD-card device with `lsblk` or an equivalent disk utility before running `bmaptool`. Writing to the wrong device will overwrite its contents.

A typical workflow is:

```bash
lsblk

sudo bmaptool copy \
  ~/workspace/ros2/meta-bumperbot/build/tmp-glibc/deploy/images/raspberrypi5/ros-image-core-jazzy-raspberrypi5.rootfs.wic.bz2 \
  /dev/sdc
```

After the operation completes:

1. Safely eject the microSD card.
2. Insert it into the Raspberry Pi 5.
3. Power on the robot.
4. The Yocto system boots and starts `bumperbot.target` and its dependent services automatically.

---

## 🚀 Deployment Flow

The complete deployment pipeline is:

```text
bumperbot-ros2-core
        │
        │ source packages
        ▼
meta-bumperbot
        │
        │ Kas + BitBake
        ▼
Yocto Scarthgap
+ ROS 2 Jazzy
+ Raspberry Pi 5 BSP
+ BumperBot packages
+ NetworkManager
+ BlueZ 5
+ systemd services
        │
        │ .wic.bz2
        ▼
     microSD
        │
        ▼
  Raspberry Pi 5
        │
        ├── ROS 2 Control
        ├── localization / EKF
        ├── motion control
        └── USB protocol ↔ Arduino Nano ESP32
```

---

## 🔧 Upstream ROS 2 Patches

The layer contains targeted modifications to upstream ROS 2 components needed by the embedded build.

Current patch areas include:

- `controller-manager`
- `pal-statistics`
- `ros2-control-cmake`
- `hardware-interface`
- `nav2`
- `nav2-msgs`
- `control-toolbox`
- `libserial`

Examples include dependency corrections, header fixes, and build-system adjustments required to complete the cross-compilation successfully.

The patches are intentionally kept in `recipes-ros-append/` and `recipes-support/` so the changes remain isolated from the upstream source repositories.

---

## 🧪 Development and Maintenance

The repository is intended to keep the embedded Linux deployment reproducible while the ROS 2 application stack evolves independently.

Typical development flow:

```text
1. Develop / test in bumperbot-ros2-core
2. Update the corresponding BitBake recipe when required
3. Rebuild with Kas
4. Flash the generated Raspberry Pi 5 image
5. Validate automatic systemd startup and robot runtime
```

When a new BumperBot ROS 2 package, service, or deployment dependency is added to `bumperbot-ros2-core`, the corresponding Yocto recipe and/or systemd configuration should be updated here.

---

## 🔗 Related Repository

**BumperBot ROS 2 Core:**

https://github.com/semy-v/bumperbot-ros2-core

This repository provides the application-level ROS 2 stack, including robot description, controllers, firmware integration, localization, motion control, custom messages, and development tools. `meta-bumperbot` provides the embedded Linux build and deployment layer for that software.

---
