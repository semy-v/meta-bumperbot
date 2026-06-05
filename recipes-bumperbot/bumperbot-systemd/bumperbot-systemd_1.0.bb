SUMMARY = "Bumperbot Systemd Units"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE.MIT;md5=030cb33d2af49ccebca74d0588b84a21"

inherit systemd

# Provide the files natively in the meta-layer under a /files/ directory
SRC_URI = " \
    file://bumperbot.target \
    file://bumperbot_motion.service \
    file://bumperbot_localization.service \
    file://joint_state_broadcaster.service \
    file://diff_drive_controller.service \
    file://imu.service \
    file://controller_manager.service \
    file://robot_state_publisher.service \
"

# Depend on the ROS bringup package so it is guaranteed to be in the image
RDEPENDS:${PN} += "bumperbot-bringup"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = " \
    bumperbot.target \
    bumperbot_motion.service \
    bumperbot_localization.service \
    joint_state_broadcaster.service \
    diff_drive_controller.service \
    imu.service \
    controller_manager.service \
    robot_state_publisher.service \
"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/bumperbot.target ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${systemd_system_unitdir}"