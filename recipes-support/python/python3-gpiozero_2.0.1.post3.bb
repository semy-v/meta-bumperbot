SUMMARY = "A simple interface to GPIO devices with Raspberry Pi"
DESCRIPTION = "GPIO Zero provides a simple Python API for controlling Raspberry Pi GPIO devices."
HOMEPAGE = "https://gpiozero.readthedocs.io/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=f7edfe7aeac02cb6c394726db07eb41c"

SRC_URI[sha256sum] = "745feab6df463ac2e9de10c67e2dd9f396e668ba4e281e92381d6c460100a8f7"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-colorzero \
"
