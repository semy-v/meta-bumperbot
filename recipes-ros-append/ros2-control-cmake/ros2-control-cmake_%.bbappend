FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:remove = "file://0001-disable-compiler-warnings.patch"
SRC_URI += "file://0001-remove-warning-as-errors-to-satisfy-the-build.patch"

