FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-add-pal_statistics-dependency.patch"

ROS_BUILD_DEPENDS:append = " pal-statistics"
