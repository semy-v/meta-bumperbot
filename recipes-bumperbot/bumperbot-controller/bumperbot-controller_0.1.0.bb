SUMMARY = "Bumperbot controllers and teleoperation ROS2 package"
DESCRIPTION = "${SUMMARY}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit ros_distro_jazzy
inherit ros_component

SRC_URI = "git://github.com/semy-v/bumperbot-ros2-core;protocol=https;branch=main"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git/bumperbot_controller"

# -------------------------
# Dependencies (from package.xml + CMakeLists.txt)
# -------------------------

ROS_BUILDTOOL_DEPENDS = " \
    ament-cmake-native \
"

ROS_BUILD_DEPENDS = ""

ROS_EXEC_DEPENDS = " \
    ros2launch \
    controller-manager \
    diff-drive-controller \
    joint-state-broadcaster \
    tf-transformations \
    joy \
    joy-teleop \
    bumperbot-firmware \
"

DEPENDS = "${ROS_BUILDTOOL_DEPENDS} ${ROS_BUILD_DEPENDS}"
RDEPENDS:${PN} += "${ROS_EXEC_DEPENDS}"

# Use the standard ROS build type inheritance
ROS_BUILD_TYPE = "ament_cmake"

inherit ros_${ROS_BUILD_TYPE}
