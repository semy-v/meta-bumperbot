SUMMARY = "Disable automatic power-on for Bluetooth adapter to save power"

do_install:append() {
    install -d ${D}${sysconfdir}/bluetooth
    if [ -f ${D}${sysconfdir}/bluetooth/main.conf ]; then
        sed -i 's/^#*AutoEnable.*$/AutoEnable=false/' ${D}${sysconfdir}/bluetooth/main.conf
    else
        echo "[Policy]" > ${D}${sysconfdir}/bluetooth/main.conf
        echo "AutoEnable=false" >> ${D}${sysconfdir}/bluetooth/main.conf
    fi
}