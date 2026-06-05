# meta-bumperbot

A dedicated Yocto Project board support package (BSP) and configuration layer for **BumperBot**. 

This repository is the deployment companion to the [bumperbot-ros2-core](https://github.com/semy-v/bumperbot-ros2-core) application stack. It provides the BitBake recipes, system configurations, and Kas build definitions required to generate a custom, production-grade embedded Linux OS for the Raspberry Pi 5. The resulting image comes pre-installed with ROS 2 Jazzy, the BumperBot software stack, and a fully configured systemd environment for autonomous operation.

---

## 🎯 Repository Intent & Functionality

The primary goal of `meta-bumperbot` is to bridge the gap between high-level ROS 2 application development and low-level hardware deployment. 

Key functionality provided by this layer includes:
* **ROS 2 Jazzy Cross-Compilation:** Automatically fetches, cross-compiles, and bakes the custom nodes from the `bumperbot-ros2-core` repository.
* **Hardware Enablement:** Configures the Raspberry Pi 5 device tree, enables UART for serial communication with the Arduino, and autoloads `i2c-dev` for the IMU.
* **Headless Connectivity:** Integrates NetworkManager and BlueZ 5, provisioning secure, automatic Wi-Fi and Bluetooth connections on boot.
* **Autonomous Bringup:** Deploys a customized systemd target (`bumperbot.target`) and associated service files. The robot's controllers, kinematics, and localization nodes automatically spin up in the background upon boot—no SSH intervention required.
* **System Patches:** Applies necessary source patches to upstream ROS 2 packages (e.g., `controller-manager`, `pal-statistics`) to resolve cross-compilation errors and dependency mismatches.

---

## 📂 Repository Structure

* **`kas/`**: Contains the primary build configuration yaml (`bumperbot-scarthgap-jazzy-raspberrypi5.yml`) mapping out layer dependencies and global image flags.
* **`recipes-bumperbot/`**: BitBake recipes that fetch and build the individual ROS 2 packages from the companion core repository.
* **`recipes-connectivity/`**: NetworkManager and Wi-Fi provisioning logic.
* **`recipes-ros-append/`**: Modifications (`.bbappend` files and patches) to standard ROS 2 packages required for embedded compilation.
* **`recipes-systemd/`**: Low-level networking and auto-boot service configurations.

---

## 🛠️ Build Environment Setup

This project uses **Kas**, a setup tool for BitBake-based projects, to manage layer dependencies and containerize the build environment. By using the `kas-container` script, you do not need to manually install Yocto host dependencies, compilers, or cross-toolchains on your host machine.

### 1. Install Kas
Ensure you have Docker or Podman installed on your system. Then, install the `kas` tool via Python:

```bash
pip3 install kas
```
or
```bash
sudo apt install kas
```

### 2. Configure Wi-Fi Credentials
The image is designed to automatically connect to your local wireless network on boot. This is handled by the `wpa-wifi_1.0.bb` recipe, which parses a NetworkManager template (`wifi.nmconnection.in`) and disables MAC randomization.

Before building, you must provide your network credentials. You can set these Yocto variables in your local environment or directly append them to the `kas/bumperbot-scarthgap-jazzy-raspberrypi5.yml` file under the `local_conf_header`:
```yaml
local_conf_header:
  wifi_credentials: |
    WIFI_ID = "BumperBot_Network"
    WIFI_SSID = "Your_SSID_Here"
    WIFI_PSK = "Your_Password_Here"
```
BitBake will automatically substitute these values into the final NetworkManager connection profile during the `do_install` task.

### 3. Build the OS Image
To execute the build inside the isolated Yocto container, run the following command from the root of the repository:
```bash
kas-container build kas/bumperbot-scarthgap-jazzy-raspberrypi5.yml
```
The `kas-container` script will pull the standard Kas Docker image, clone all necessary external layers (such as `meta-ros`, `meta-raspberrypi`, and `meta-openembedded`), and initiate the BitBake process.

## 🚀 Flashing and Deployment
Once the BitBake process completes successfully, the final `.wic.bz2` or `.wic.bmap` image file will be located in the `build/tmp/deploy/images/raspberrypi5/` directory.

- Flash the resulting image to a microSD card using `bmaptool` or `balenaEtcher`.

- Insert the microSD card into the Raspberry Pi 5.

- Power on the robot. The system will boot, connect to the configured Wi-Fi network, and start the `bumperbot.target` systemd services autonomously.