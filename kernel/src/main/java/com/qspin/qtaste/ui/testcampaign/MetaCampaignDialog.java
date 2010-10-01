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

package com.qspin.qtaste.ui.testcampaign;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.apache.log4j.Logger;

import com.qspin.qtaste.ui.tools.WrappedToolTipUI;
import com.qspin.qtaste.ui.util.QSpinTheme;
import com.qspin.qtaste.util.Log4jLoggerFactory;


/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class MetaCampaignDialog extends JFrame {

    protected static Logger logger = Log4jLoggerFactory.getLogger(MetaCampaignDialog.class);
    protected String title = "QSpin Tailored Automated System Test Environment - MetaCampaignDialog ";
    private QSpinTheme mTheme;

    public MetaCampaignDialog() {
        super();
        setTitle(title);
        setUpFrame();
    }

    private void setUpFrame() {
        setName(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void launch() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {

            public void run() {
                setQSpinTheme();
                genUI();
            }
        });
    }

    public void setQSpinTheme() {
        mTheme = new QSpinTheme();
        MetalLookAndFeel.setCurrentTheme(mTheme);
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
            //Toolkit toolkit = Toolkit.getDefaultToolkit();
            //double dWidth = Math.round((toolkit.getScreenSize().width*0.75)/3);
//            String sWdith = (new Double(dWidth)).toString();
            //int width = (int)dWidth;
            ((WrappedToolTipUI) WrappedToolTipUI.createUI(null)).setMaxWidth(200);
            //UIManager.put("ToolTip.background", Color.cyan);
            //UIManager.put("ToolTip.foreground", Color.blue);
            //UIManager.put("ToolTip.font", new Font("Courier", Font.BOLD, 20));
            //UIManager.put("ToolTip.border", BorderFactory.createLineBorder(Color.red, 3));
            //  Make sure you opdate the path
            UIManager.put("ToolTipUI", "com.qspin.qtaste.ui.tools.WrappedToolTipUI");

        } catch (UnsupportedLookAndFeelException ex) {
        }
    }

    public void genUI() {
        try {

            getContentPane().setLayout(new BorderLayout());

            // prepare the right panels containg the main information:
            // the right pane is selected through the tabbed pane:
            //    - Test cases: management of testcase and test suite
            //    - Test designer: ability to define new testcase
            //    - Interactive: ability to invoke QTaste verbs one by one
            

            TestCampaignMainPanel panel = new TestCampaignMainPanel();
            
            TestCaseTree tct = new TestCaseTree(panel.getTreeTable());
            JScrollPane sp2 = new JScrollPane(tct);
            this.add(sp2, BorderLayout.WEST);
            this.add(panel);
            this.pack();
            this.setExtendedState(Frame.MAXIMIZED_BOTH);
            
            setVisible(true);

        } catch (Exception e) {
            logger.fatal(e);
            System.exit(1);
        }

    }
}
