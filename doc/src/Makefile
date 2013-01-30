# Example:
#
# * gmake all
# * gmake html
# * gmake man
# * gmake pdf
# * gmake xhtml
# * gmake clean
#
# Require: devel/gmake
#
# Support:
#
# * HTML, MAN, PDF and XHTML format
# * Transform all äÄöÖüÜß to xml compatible coding 

CSS_FILE=docbook.css

HTML_TARGETS = \
  widebase-cli.html \
  widebase-plant.html

MAN_TARGETS = \
  widebase-cli.8 \
  widebase-plant.8

PDF_TARGETS = \
  widebase-cli.pdf \
  widebase-plant.pdf

XHTML_TARGETS = \
  widebase-cli.xhtml \
  widebase-plant.xhtml

XSL_HTML = ${CURDIR}/../xsl/freebsd-html.xsl
XSL_MAN = /usr/local/share/xsl/docbook/manpages/docbook.xsl
XSL_FO = /usr/local/share/xsl/docbook/fo/docbook.xsl
XSL_XHTML = /usr/local/share/xsl/docbook/xhtml/docbook.xsl

XSL_IMAGES = /usr/local/share/doc/docbook-xsl/images

all: html man pdf xhtml

html: $(HTML_TARGETS)
	cp -f ${CSS_FILE} ${CURDIR}/../html/

pdf: $(PDF_TARGETS)

man: $(MAN_TARGETS)

xhtml: $(XHTML_TARGETS)

%.html: %.docbook
	sed 's/ä/\&#228;/g' $< | sed 's/Ä/\&#196;/g' | sed 's/ö/\&#246;/g' | sed 's/Ö/\&#214;/g' | sed 's/ü/\&#252;/g' | sed 's/Ü/\&#220;/g' | sed 's/ß/\&#223;/g' | xsltproc --stringparam html.stylesheet ${CSS_FILE} --xinclude -o ${CURDIR}/../html/$@ $(XSL_HTML) -
	cp -f ${CSS_FILE} ${CURDIR}/../html/
	cp -r ${XSL_IMAGES} ${CURDIR}/../html/imagelib

%.8: %.docbook
	sed 's/ä/\&#228;/g' $< | sed 's/Ä/\&#196;/g' | sed 's/ö/\&#246;/g' | sed 's/Ö/\&#214;/g' | sed 's/ü/\&#252;/g' | sed 's/Ü/\&#220;/g' | sed 's/ß/\&#223;/g' | xsltproc --nonet -o ${CURDIR}/../man8/$@ $(XSL_MAN) -

%.pdf: %.docbook
	@mkdir -p ${CURDIR}/../pdf
	sed 's/ä/\&#228;/g' $< | sed 's/Ä/\&#196;/g' | sed 's/ö/\&#246;/g' | sed 's/Ö/\&#214;/g' | sed 's/ü/\&#252;/g' | sed 's/Ü/\&#220;/g' | sed 's/ß/\&#223;/g' | xsltproc --stringparam html.stylesheet ${CSS_FILE} --xinclude $(XSL_FO) - | fop -fo - -pdf ${CURDIR}/../pdf/$@

%.xhtml: %.docbook
	sed 's/ä/\&#228;/g' $< | sed 's/Ä/\&#196;/g' | sed 's/ö/\&#246;/g' | sed 's/Ö/\&#214;/g' | sed 's/ü/\&#252;/g' | sed 's/Ü/\&#220;/g' | sed 's/ß/\&#223;/g' | xsltproc --stringparam html.stylesheet ${CSS_FILE} --xinclude -o ${CURDIR}/../xhtml/$@ $(XSL_XHTML) -
	cp -f ${CSS_FILE} ${CURDIR}/../html/
	cp -r ${XSL_IMAGES} ${CURDIR}/../html/imagelib

clean:
	@rm -fr \
	${CURDIR}/../html \
	${CURDIR}/../man8 \
	${CURDIR}/../pdf \
	${CURDIR}/../xhtml

