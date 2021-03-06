/*
    Copyright 2007-2009 QSpin - www.qspin.be

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

/*
*/
package com.qspin.qtaste.ui;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import com.qspin.qtaste.ui.tools.TableSorter;

@SuppressWarnings("serial")
public class SortableJTable extends JTable{

    public SortableJTable(TableSorter tableModel){
        super(tableModel);
        tableModel.setTableHeader(getTableHeader());
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                if (index < 0) {
                	return null;
                }
                int realIndex =
                        columnModel.getColumn(index).getModelIndex();
                return SortableJTable.this.getModel().getColumnName(realIndex);
            }
        };
    }
}
