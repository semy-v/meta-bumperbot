FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://10-eth0-static.network"
SRC_URI += "file://10-wlan0.network"

do_install:append () {
    install -d ${D}${sysconfdir}/systemd/network
    install -m 0644 ${WORKDIR}/10-eth0-static.network ${D}${sysconfdir}/systemd/network
    install -m 0644 ${WORKDIR}/10-wlan0.network ${D}${sysconfdir}/systemd/network
}

FILES:${PN} += "${sysconfdir}/systemd/network/10-eth0-static.network"
FILES:${PN} += "${sysconfdir}/systemd/network/10-wlan0.network"