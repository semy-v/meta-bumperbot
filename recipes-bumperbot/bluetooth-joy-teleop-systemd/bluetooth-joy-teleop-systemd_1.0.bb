SUMMARY = "Bluetooth Joystick Teleoperation Systemd Units"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE.MIT;md5=030cb33d2af49ccebca74d0588b84a21"

inherit systemd

# Provide the files natively in the meta-layer under a /files/ directory
SRC_URI = " \
    file://bluetooth_joy_teleop.target \
    file://bluetooth_joy_connect.service \
    file://joy_teleop_button.service \
    file://joy_teleop.service \
    file://bluetooth_joy_teleop_failure.service \
    file://joy_disconnect.sh \
    file://joy_connect.sh \
    file://joy_env_setup.sh \
    file://joy_teleop_button.py \
"

PACKAGES =+ "${PN}-enable ${PN}-disable"
SYSTEMD_PACKAGES = "${PN}-enable ${PN}-disable"

# Services to be ENABLED at boot
SYSTEMD_SERVICE:${PN}-enable = "joy_teleop_button.service"
SYSTEMD_AUTO_ENABLE:${PN}-enable = "enable"

# Services to be DISABLED at boot
SYSTEMD_SERVICE:${PN}-disable = " \
    bluetooth_joy_connect.service \
    joy_teleop.service \
    bluetooth_joy_teleop_failure.service \
    bluetooth_joy_teleop.target \
"
SYSTEMD_AUTO_ENABLE:${PN}-disable = "disable"

RDEPENDS:${PN} += " \
    ${PN}-enable \
    ${PN}-disable \
    bumperbot-controller \
    bash \
    python3-core \
    python3-gpiozero \
    python3-modules \
    python3-lgpio \
"

joyteleop_dir = "/opt/joyteleop"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/bluetooth_joy_teleop.target ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${joyteleop_dir}
    install -m 0744 ${WORKDIR}/*.sh ${D}${joyteleop_dir}/
    install -m 0744 ${WORKDIR}/*.py ${D}${joyteleop_dir}/
}

# Assign specific unit files to their respective sub-packages
FILES:${PN}-enable = "${systemd_system_unitdir}/joy_teleop_button.service"
FILES:${PN}-disable = " \
    ${systemd_system_unitdir}/bluetooth_joy_connect.service \
    ${systemd_system_unitdir}/joy_teleop.service \
    ${systemd_system_unitdir}/bluetooth_joy_teleop_failure.service \
    ${systemd_system_unitdir}/bluetooth_joy_teleop.target \
"

FILES:${PN} += "${joyteleop_dir}"