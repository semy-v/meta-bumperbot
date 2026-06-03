SUMMARY = "Bumperbot firmware ROS2 package"
DESCRIPTION = "ROS2 hardware interface and serial communication for Bumperbot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE.MIT;md5=030cb33d2af49ccebca74d0588b84a21"

inherit ros_distro_jazzy
inherit ros_component

SRC_URI = "git://github.com/semy-v/bumperbot-ros2-core;protocol=https;branch=dev"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git/src/bumperbot_firmware"

# -------------------------
# Dependencies (from package.xml + CMakeLists.txt)
# -------------------------

ROS_BUILDTOOL_DEPENDS = " \
    ament-cmake-native \
    ament-cmake-python-native \
"

ROS_BUILD_DEPENDS = " \
    rclcpp \
    rclpy \
    std-msgs \
    hardware-interface \
    pluginlib \
    rclcpp-lifecycle \
    libserial \
"

ROS_EXEC_DEPENDS = " \
    ros2launch \
    bumperbot-description \
    bumperbot-msgs \
"

DEPENDS = "${ROS_BUILDTOOL_DEPENDS} ${ROS_BUILD_DEPENDS}"
RDEPENDS:${PN} += "${ROS_EXEC_DEPENDS}"

# Use the standard ROS build type inheritance
ROS_BUILD_TYPE = "ament_cmake"

inherit ros_${ROS_BUILD_TYPE}
