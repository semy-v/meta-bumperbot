SUMMARY = "Preconfigure Wi-Fi network and disable MAC randomization for NetworkManager"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE.MIT;md5=030cb33d2af49ccebca74d0588b84a21"

SRC_URI = " \
    file://wifi.nmconnection \
    file://99-disable-mac-random.conf \
"

do_install() {
    # Install the connection profile
    install -d ${D}${sysconfdir}/NetworkManager/system-connections
    install -m 0600 ${WORKDIR}/wifi.nmconnection \
        ${D}${sysconfdir}/NetworkManager/system-connections/

    # Install the global override rules
    install -d ${D}${sysconfdir}/NetworkManager/conf.d
    install -m 0644 ${WORKDIR}/99-disable-mac-random.conf \
        ${D}${sysconfdir}/NetworkManager/conf.d/
}

FILES:${PN} += " \
    ${sysconfdir}/NetworkManager/system-connections/wifi.nmconnection \
    ${sysconfdir}/NetworkManager/conf.d/99-disable-mac-random.conf \
"