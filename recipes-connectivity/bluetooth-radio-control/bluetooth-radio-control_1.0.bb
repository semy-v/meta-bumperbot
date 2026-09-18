SUMMARY = "BumperBot on-demand Bluetooth radio control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE.MIT;md5=030cb33d2af49ccebca74d0588b84a21"

inherit systemd

SRC_URI = " \
    file://bluetooth-radio.service \
    file://bluetooth-default-off.service \
    file://bumperbot-bluetooth.sh \
"

SYSTEMD_SERVICE:${PN} = "bluetooth-default-off.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

RDEPENDS:${PN} += " \
    bluez5 \
    rfkill \
    systemd \
"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sbindir}

    install -m 0644 ${WORKDIR}/bluetooth-radio.service \
        ${D}${systemd_system_unitdir}/bluetooth-radio.service

    install -m 0644 ${WORKDIR}/bluetooth-default-off.service \
        ${D}${systemd_system_unitdir}/bluetooth-default-off.service

    install -m 0755 ${WORKDIR}/bumperbot-bluetooth.sh \
        ${D}${sbindir}/bumperbot-bluetooth.sh
}

FILES:${PN} += " \
    ${systemd_system_unitdir}/bluetooth-radio.service \
    ${sbindir}/bumperbot-bluetooth \
"