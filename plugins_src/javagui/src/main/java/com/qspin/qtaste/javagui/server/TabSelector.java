/*
    Copyright 2007-2012 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

package com.qspin.qtaste.javagui.server;

import javax.swing.JTabbedPane;
import java.awt.Component;


import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TabSelector extends UpdateComponentCommander {

	public enum SelectorIdentifier {
	    SELECT_BY_INDEX,
	    SELECT_BY_TITLE,
	    SELECT_BY_COMPONENT_ID
	}

	private SelectorIdentifier mSelectorIdentifier;
	protected int mTabIndex;
	protected String mTabIdentifier;

	public TabSelector(SelectorIdentifier aSelectorIdentifier) {
		mSelectorIdentifier = aSelectorIdentifier;
	}

	@Override
	protected void prepareActions() throws QTasteTestFailException {
		if ( mData.length == 0 || mData[0] == null )
			throw new QTasteTestFailException("No tab index, tab title or tab component id provided!");

		switch (mSelectorIdentifier) {
	        case SELECT_BY_INDEX:
	        	mTabIndex = Integer.parseInt(mData[0].toString());
	            break;
	        case SELECT_BY_TITLE:
	        case SELECT_BY_COMPONENT_ID:
	        	mTabIdentifier = mData[0].toString();
	        	break;
	        default:
	        	break;
		}
	}

	@Override
	protected void doActionsInSwingThread() {
		if (component != null && component instanceof JTabbedPane) {

			switch (mSelectorIdentifier) {
		        case SELECT_BY_INDEX:
		        	((JTabbedPane)component).setSelectedIndex(mTabIndex);
		            break;
		        case SELECT_BY_TITLE: {
		        	int index = ((JTabbedPane)component).indexOfTab(mTabIdentifier);
					((JTabbedPane)component).setSelectedIndex(index);
		        	break;
		        }
		        case SELECT_BY_COMPONENT_ID: {
		        	int index = -1;
					int count = ((JTabbedPane)component).getTabCount();
			    	for (int i = 0; i < count; i++) {
			    		Component cmpIter = ((JTabbedPane)component).getComponentAt(i);
			    		if ( cmpIter.getName() != null && cmpIter.getName().equals(mTabIdentifier) ) {
			    			index = ((JTabbedPane)component).indexOfComponent(cmpIter);
			    			break;
			    		}
			    	}
			    	((JTabbedPane)component).setSelectedIndex(index);
			    	break;
		        }
		        default:
		        	break;
			}
		}
	}
}
