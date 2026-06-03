SUMMARY = "Bumperbot motion ROS2 package"
DESCRIPTION = "Bumperbot motion planner action server"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE.MIT;md5=030cb33d2af49ccebca74d0588b84a21"

inherit ros_distro_jazzy
inherit ros_component

SRC_URI = "git://github.com/semy-v/bumperbot-ros2-core;protocol=https;branch=dev"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git/src/bumperbot_motion"

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
    rclcpp-lifecycle \
    geometry-msgs \
    nav2-core \
    nav2-msgs \
    tf2-ros \
    tf2-geometry-msgs \
    pluginlib \
"

ROS_EXEC_DEPENDS = " \
    ros2launch \
    tf-transformations \
    nav2-core \
    nav2-msgs \
    bumperbot-localization \
"

DEPENDS = "${ROS_BUILDTOOL_DEPENDS} ${ROS_BUILD_DEPENDS}"
RDEPENDS:${PN} += "${ROS_EXEC_DEPENDS}"

# Use the standard ROS build type inheritance
ROS_BUILD_TYPE = "ament_cmake"

inherit ros_${ROS_BUILD_TYPE}
