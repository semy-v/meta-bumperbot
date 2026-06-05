SUMMARY = "Preconfigure Wi-Fi network and disable MAC randomization for NetworkManager"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE.MIT;md5=030cb33d2af49ccebca74d0588b84a21"

SRC_URI = " \
    file://wifi.nmconnection.in \
    file://99-disable-mac-random.conf \
"

# Default values to be substituted
WIFI_ID ?= "DEFAULT_ID"
WIFI_SSID ?= "DEFAULT_SSID"
WIFI_PSK ?= "DEFAULT_PSK"

do_install() {
    # Process the template and output a valid nmconnection file to WORKDIR
    sed -e 's|@WIFI_ID@|${WIFI_ID}|g' \
        -e 's|@WIFI_SSID@|${WIFI_SSID}|g' \
        -e 's|@WIFI_PSK@|${WIFI_PSK}|g' \
        ${WORKDIR}/wifi.nmconnection.in > ${WORKDIR}/wifi.nmconnection

    # Install the processed connection profile
    install -d ${D}${sysconfdir}/NetworkManager/system-connections
    install -m 0600 ${WORKDIR}/wifi.nmconnection \
        ${D}${sysconfdir}/NetworkManager/system-connections/

    # Install the global override rules
    install -d ${D}${sysconfdir}/NetworkManager/conf.d
    install -m 0644 ${WORKDIR}/99-disable-mac-random.conf \
        ${D}${sysconfdir}/NetworkManager/conf.d/
}

# Tell BitBake to rebuild this recipe if the ID, SSID or PSK variables change
do_install[vardeps] += "WIFI_ID WIFI_SSID WIFI_PSK"

FILES:${PN} += " \
    ${sysconfdir}/NetworkManager/system-connections/wifi.nmconnection \
    ${sysconfdir}/NetworkManager/conf.d/99-disable-mac-random.conf \
"