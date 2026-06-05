SUMMARY = "Bumperbot localization ROS2 package"
DESCRIPTION = "${SUMMARY}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit ros_distro_jazzy
inherit ros_component

SRC_URI = "git://github.com/semy-v/bumperbot-ros2-core;protocol=https;branch=dev"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git/bumperbot_localization"

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
    sensor-msgs \
    nav-msgs \
"

ROS_EXEC_DEPENDS = " \
    rclcpp \
    rclpy \
    robot-localization \
    bumperbot-controller \
"

DEPENDS = "${ROS_BUILDTOOL_DEPENDS} ${ROS_BUILD_DEPENDS}"
RDEPENDS:${PN} += "${ROS_EXEC_DEPENDS}"

# Use the standard ROS build type inheritance
ROS_BUILD_TYPE = "ament_cmake"

inherit ros_${ROS_BUILD_TYPE}
