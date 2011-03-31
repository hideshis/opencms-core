/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/gwt/client/ui/input/form/Attic/A_CmsFormFieldPanel.java,v $
 * Date   : $Date: 2011/03/31 17:47:13 $
 * Version: $Revision: 1.3 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.gwt.client.ui.input.form;

import org.opencms.gwt.client.ui.input.I_CmsFormField;

import java.util.Collection;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The abstract class for form field container widgets.<p>
 * 
 *  @author Georg Westenberger
 *  
 *  @version $Revision: 1.3 $
 *  
 *  @since 8.0.0
 */
public abstract class A_CmsFormFieldPanel extends Composite {

    /** The label text. */
    protected String m_labelText;

    /**
     * Returns the default group name.<p>
     * 
     * @return the default group name 
     */
    public abstract String getDefaultGroup();

    /**
     * Renders a collection of fields.<p>
     * 
     * This should only be called once, when the form is being built.<p>
     * 
     * @param fields the fields to render 
     */
    public abstract void renderFields(Collection<I_CmsFormField> fields);

    /**
     * Re-renders the fields of a group.<p>
     * 
     * Not supported by all subclasses.<p>
     * 
     * @param group the group whose fields to re-render 
     * 
     * @param fieldsInGroup the  fields to re-render 
     */
    public void rerenderFields(String group, Collection<I_CmsFormField> fieldsInGroup) {

        throw new UnsupportedOperationException();
    }

    /**
     * Sets the info label text.<p>
     * 
     * @param label the text for the info label 
     */
    public void setLabel(String label) {

        m_labelText = label;
    }

    /**
     * Helper method for creating a form row widget.<p>
     * 
     * @param field the field for which to create a form row
     *  
     * @return the newly created form row 
     */
    protected CmsFormRow createRow(I_CmsFormField field) {

        return createRow(
            field.getLabel(),
            field.getDescription(),
            (Widget)field.getWidget(),
            field.getLayoutData().get("info"));
    }

    /**
     * Creates a form row.<p>
     * 
     * @param labelText the label text 
     * @param description the description 
     * @param widget the widget to use 
     * 
     * @return the new form row 
     */
    protected CmsFormRow createRow(String labelText, String description, Widget widget) {

        return createRow(labelText, description, widget, null);
    }

    /**
     * Adds a new row with a given label and input widget to the form.<p>
     * 
     * @param labelText the label text for the form field
     * @param description the description of the form field 
     * @param widget the widget for the form field 
     * @param infoText the text to display on the info icon (may be null)
     *  
     * @return the newly added form row 
     */
    protected CmsFormRow createRow(String labelText, String description, Widget widget, String infoText) {

        CmsFormRow row = new CmsFormRow();
        Label label = row.getLabel();
        label.setText(labelText);
        label.setTitle(description);
        row.setInfo(infoText);
        row.getWidgetContainer().add(widget);

        //getPanel(panelId).add(row);

        return row;
    }

    /**
     * Helper method for adding a border to a widget.<p>
     * 
     * @param widget the widget which a border should be added to 
     */
    protected void setBorder(Widget widget) {

        String cornerAll = org.opencms.gwt.client.ui.css.I_CmsLayoutBundle.INSTANCE.generalCss().cornerAll();
        String border = org.opencms.gwt.client.ui.css.I_CmsLayoutBundle.INSTANCE.generalCss().border();
        widget.addStyleName(border);
        widget.addStyleName(cornerAll);
    }

}
