SUMMARY = "Enable automatic power-on for Bluetooth adapter"

do_install:append() {
    # Check if main.conf exists, then uncomment and set AutoEnable to true
    if [ -f ${D}${sysconfdir}/bluetooth/main.conf ]; then
        sed -i 's/^#*AutoEnable.*$/AutoEnable=true/' ${D}${sysconfdir}/bluetooth/main.conf
    else
        # Fallback: create the file and the Policy section if it doesn't exist
        install -d ${D}${sysconfdir}/bluetooth
        echo "[Policy]" > ${D}${sysconfdir}/bluetooth/main.conf
        echo "AutoEnable=true" >> ${D}${sysconfdir}/bluetooth/main.conf
    fi
}