SUMMARY = "Bluetooth Joystick Teleoperation Systemd Units"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE.MIT;md5=030cb33d2af49ccebca74d0588b84a21"

inherit systemd

# Provide the files natively in the meta-layer under a /files/ directory
SRC_URI = " \
    file://bluetooth_joy_teleop.target \
    file://bluetooth_joy_connect.service \
    file://joy_teleop.service \
    file://joy_disconnect.sh \
    file://joy_connect.sh \
    file://joy_env_setup.sh \
"

# Depend on the Bumperbot controller package so it is guaranteed to be in the image
RDEPENDS:${PN} += " \
    bumperbot-controller \
    bash \
"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_AUTO_ENABLE = "disable"
SYSTEMD_SERVICE:${PN} = " \
    bluetooth_joy_teleop.target \
    bluetooth_joy_connect.service \
    joy_teleop.service \
"

joyteleop_dir = "/opt/joyteleop"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/bluetooth_joy_teleop.target ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${joyteleop_dir}
    install -m 0744 ${WORKDIR}/*.sh ${D}${joyteleop_dir}/
}

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${joyteleop_dir}"