# Do not automatically start hciuart.service.
SYSTEMD_AUTO_ENABLE:pn-pi-bluetooth = "disable"

do_install:append() {
    # The stock rule starts bthelper@hci0 whenever hci0 appears.
    #
    # For BumperBot Bluetooth is explicitly controlled by
    # bluetooth-radio.service instead.
    rm -f ${D}${sysconfdir}/udev/rules.d/90-pi-bluetooth.rules
}