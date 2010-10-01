#    Copyright 2007-2009 QSpin - www.qspin.be
#
#    This file is part of QTaste framework.
#
#    QTaste is free software: you can redistribute it and/or modify
#    it under the terms of the GNU Lesser General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    QTaste is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU Lesser General Public License for more details.
#
#    You should have received a copy of the GNU Lesser General Public License
#    along with QTaste. If not, see <http://www.gnu.org/licenses/>.

##
# TestTranslate.
# <p>
# Perform a translation of words defined in testdata and check the result of the translation using multiple browser.
# This demo required the "demo_web" testbed configuration
#
##

from qtaste import *

translate = testAPI.getSelenium(INSTANCE_ID='TranslateApp')

def connectToWeb():
	"""
	@step      Connection to Yahoo babel fish text translation website
	@expected  Check that we are on the Yahoo babel fish website
	"""
	translate.openBrowser(testData.getValue("BROWSER"))
	translate.setTimeout("50000")
	translate.open("translate_txt")
	translate.waitForPageToLoad("15000")
	title = translate.getTitle()
	logger.info(title)
	if title != "Yahoo! Babel Fish - Text Translation and Web Page Translation":
		testAPI.stopTest(Status.FAIL, "Title window name is not as expected")

def checkTranslation():
	"""
	@step      Translate a word specified in the testdata
	@expected  Check that the translation is correct
	"""
	# we can access component using different method (component id, xpath or dom)
	translate.type("trtext", testData.getValue("WORD"))
	#translate.type("//div/form/textarea", testData.getValue("WORD"))
	#translate.type("document.frmTrText.trtext", testData.getValue("WORD"))
	translate.select("lp", "English to French")
	translate.click("btnTrTxt")
	translate.waitForPageToLoad("30000")
	translation = translate.getText("id=result")
	expectedTranslation = testData.getValue("TRANSLATION")
	translate.closeBrowser()
	if translation != expectedTranslation:
		testAPI.stopTest(Status.FAIL, "Expected to get " + expectedTranslation + " but got " + translation)

doStep(connectToWeb)
doStep(checkTranslation)
