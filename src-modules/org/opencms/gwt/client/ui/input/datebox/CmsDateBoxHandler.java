/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/gwt/client/ui/input/datebox/Attic/CmsDateBoxHandler.java,v $
 * Date   : $Date: 2010/10/22 14:07:05 $
 * Version: $Revision: 1.5 $
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

package org.opencms.gwt.client.ui.input.datebox;

import org.opencms.gwt.client.Messages;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This singleton implements the handler for the whole date time piker.<p>
 * 
 * @version 0.1
 * 
 * @author Ruediger Kurz
 */
public final class CmsDateBoxHandler {

    /** The date box. */
    private CmsDateBox m_dateBox;

    /** Signals whether the time field is valid or not. */
    private boolean m_isValidTime;

    /** The old value for fire event decision. */
    private Date m_oldValue;

    /**
     * A private constructor for initializing this class with the date box and the date time picker.<p>
     * 
     * @param dateBox the date box for this handler
     */
    public CmsDateBoxHandler(CmsDateBox dateBox) {

        m_dateBox = dateBox;
        m_isValidTime = true;
    }

    /**
     * If the am or pm radio button is clicked update the date box from the date time picker.<p>
     * 
     * @param event the click event
     */
    public void onAmPmClick(ClickEvent event) {

        updateFromDateTimePicker(m_dateBox.getPicker().getValue());
    }

    /**
     * The date box on blur action.<p>
     * 
     * If the date box loses the focus the date time picker should be updated from the date box value.<p>
     * 
     * @param event the blur event
     */
    public void onDateBoxBlur(BlurEvent event) {

        if (!isDatePickerShowing()) {
            updateDateFromTextBox();
        }
    }

    /**
     * The date box on click action.<p>
     * 
     * If the date box is clicked the time date picker should be shown.<p>
     * 
     * @param event the on click event
     */
    public void onDateBoxClick(ClickEvent event) {

        if (!isDatePickerShowing()) {
            showDatePicker();
        }
    }

    /**
     * The date box on key down action.<p>
     * <ul>
     * <li>If enter or tab is pressed in the date box the date time 
     * picker should be updated with the value from the date box.</li>
     * 
     * <li>If the escape key is pressed the picker should be hided.</li>
     * 
     * <li>If the up key is pressed the value should be taken from the date box.</li>
     * 
     * <li>If the down key is pressed the picker should be hided.</li>
     * </ul>
     *  
     * @param event the key down event
     */
    public void onDateBoxKeyPress(KeyPressEvent event) {

        switch (event.getNativeEvent().getKeyCode()) {
            case KeyCodes.KEY_ENTER:
            case KeyCodes.KEY_TAB:
            case KeyCodes.KEY_ESCAPE:
            case KeyCodes.KEY_UP:
                updateDateFromTextBox();
                hideDatePicker();
                break;
            case KeyCodes.KEY_DOWN:
                showDatePicker();
                break;
            default:
                hideDatePicker();
        }

    }

    /**
     * If the value of the picker changes, the value of the date time picker should be updated.<p> 
     * 
     * @param value the new date selected from the picker
     */
    public void onPickerValueChanged(Date value) {

        updateFromDateTimePicker(value);
    }

    /**
     * If the value of the picker changes, the value of the date time picker should be updated.<p> 
     * 
     * @param event the value change event
     */
    public void onPickerValueChanged(ValueChangeEvent<Date> event) {

        updateFromDateTimePicker(event.getValue());
    }

    /**
     * If the popup closes the new value is fixed and a event should be fired.<p>
     */
    public void onPopupClose() {

        CmsDateChangeEvent.fireIfNotEqualDates(m_dateBox, m_oldValue, m_dateBox.getValue());
    }

    /**
     * If the time field loses the focus the entered time should be checked.<p>
     * 
     * @param event the blur event
     */
    public void onTimeBlur(BlurEvent event) {

        checkTime(getCurrentTimeFieldValue());
        // updateFromDateTimePicker(getValueFromDateTimePicker());

    }

    /**
     * If the user presses enter in the time field the value of the 
     * picker should be updated and the the popup should be closed.<p>
     * 
     * In any other case the popup should be prevented to being closed.<p>
     * 
     * @param event the key pressed event
     */
    public void onTimeKeyPressed(KeyPressEvent event) {

        switch (event.getCharCode()) {
            case KeyCodes.KEY_ENTER:
                updateFromDateTimePicker(m_dateBox.getPicker().getValue());
                hideDatePicker();
                break;
            default:
                DeferredCommand.addCommand(new Command() {

                    public void execute() {

                        if (!CmsDateConverter.validateTime(getCurrentTimeFieldValue())) {
                            setValidTimeFlag(false);
                            updatePopupCloseBehavior();
                        } else {
                            setValidTimeFlag(true);
                            updatePopupCloseBehavior();
                        }
                    }
                });
                break;
        }
    }

    /**
     * Prevents the popup to close.<p>
     */
    protected void updatePopupCloseBehavior() {

        if (m_isValidTime) {
            m_dateBox.getBox().setText(CmsDateConverter.toString(getValueFromDateTimePicker()));
            m_dateBox.getTimeErr().setText(null);
            m_dateBox.getPopup().getIgnoreList().remove(RootPanel.getBodyElement());
        } else {
            m_dateBox.getPopup().getIgnoreList().add(RootPanel.getBodyElement());
        }
    }

    /**
     * Validates the time and prints out an error message if the time format is incorrect.<p>
     * 
     * @param time the time String to check 
     */
    private void checkTime(String time) {

        if (!CmsDateConverter.validateTime(time)) {
            m_dateBox.getTimeErr().setText(
                Messages.get().key(Messages.ERR_DATEBOX_INVALID_TIME_FORMAT_1, CmsDateConverter.cutSuffix(time)));
            setValidTimeFlag(false);
            updatePopupCloseBehavior();
        } else {
            m_dateBox.getTimeErr().setText(null);
            m_dateBox.getBox().setErrorMessage(null);
            setValidTimeFlag(true);
            updatePopupCloseBehavior();
        }
    }

    /**
     * Returns the current time from the time field together with the am pm information.<p>
     * 
     * @return the current time from the time field together with the am pm information
     */
    private String getCurrentTimeFieldValue() {

        String timeAsString = m_dateBox.getTime().getText().trim();
        if (CmsDateConverter.is12HourPresentation()) {
            timeAsString = getTimeWithAmPmInfo(timeAsString);
        }
        return timeAsString;
    }

    /**
     * Looks up the ui field for the am pm selection and appends this info on a given String with a space before.<p>
     * 
     * @param time the time to append the am pm info
     * 
     * @return the time with the am pm info
     */
    private String getTimeWithAmPmInfo(String time) {

        if (!(time.contains(CmsDateConverter.AM) || time.contains(CmsDateConverter.PM))) {
            if (m_dateBox.getAm().isChecked()) {
                time = time + " " + CmsDateConverter.AM;
            } else {
                time = time + " " + CmsDateConverter.PM;
            }
        }
        return time;
    }

    /**
     * Returns the current Value of the date time picker.<p>
     * 
     * @return the current Value of the date time picker
     */
    private Date getValueFromDateTimePicker() {

        Date date = m_dateBox.getPicker().getValue();
        String time = getCurrentTimeFieldValue();
        return CmsDateConverter.getDateWithTime(date, time);
    }

    /**
     * Hides the date picker.<p>
     */
    private void hideDatePicker() {

        // before hiding the date picker remove the date box popup from the auto hide partners of the parent popup
        if (m_dateBox.getParentPopup() != null) {
            m_dateBox.getParentPopup().removeAutoHidePartner(m_dateBox.getPopup().getElement());
        }
        if (CmsDateConverter.validateTime(getCurrentTimeFieldValue())) {
            m_dateBox.getPopup().hide();
        }
    }

    /**
     * @return true if date picker is currently showing, false if not
     */
    private boolean isDatePickerShowing() {

        return m_dateBox.getPopup().isVisible();
    }

    /**
     * Parses the date.<p>
     * 
     * @return the date
     */
    private Date parseToDate() {

        String dateAsString = m_dateBox.getBox().getText().trim();
        Date date = null;
        try {
            date = CmsDateConverter.toDate(dateAsString);
            m_dateBox.getBox().setErrorMessage(null);
        } catch (Exception e) {

            m_dateBox.getBox().setErrorMessage(
                Messages.get().key(Messages.ERR_DATEBOX_INVALID_DATE_FORMAT_1, CmsDateConverter.cutSuffix(dateAsString)));
        }
        return date;
    }

    /**
     * Sets the am or pm widget to the value of the given date.<p>
     * 
     * If the given date object is <code>null</code> the current date is used.<p>
     * 
     * @param date which should be used to set the am or pm information into the time widget
     */
    private void setAmPmFromBox(Date date) {

        if (date == null) {
            date = new Date();
        }
        if (CmsDateConverter.isAm(date)) {
            m_dateBox.getAmpmGroup().selectButton(m_dateBox.getAm());
        } else {
            m_dateBox.getAmpmGroup().selectButton(m_dateBox.getPm());
        }
    }

    /**
     * Sets the time widget to the value of the given date.<p>
     * 
     * If the given date object is <code>null</code> the current date is used.<p> 
     * 
     * @param date which should be used to set the time into the time widget
     */
    private void setTime(Date date) {

        if (date == null) {
            date = new Date();
        }
        String time = CmsDateConverter.getTime(date);
        m_dateBox.getTime().setText(CmsDateConverter.cutSuffix(time).trim());
    }

    /**
     * Sets the valid time flag.<p>
     * 
     * Only if the "current time valid state" differs from the "stored time valid state"
     * the value of the flag is changed.<p>
     * 
     * @param valid the "current time valid state"
     */
    private void setValidTimeFlag(boolean valid) {

        if (!m_isValidTime && valid) {
            m_isValidTime = true;
        } else if (m_isValidTime && !valid) {
            m_isValidTime = false;
        }
    }

    /**
     * Parses the current date box's value and shows that date.
     */
    private void showDatePicker() {

        m_dateBox.getTimeErr().setText(null);

        Date date = m_dateBox.getPicker().getValue();
        if (date == null) {
            Date tmpDate = new Date();
            m_dateBox.getPicker().setValue(tmpDate, false);
        }
        updateDateFromTextBox();
        m_oldValue = m_dateBox.getValue();
        m_dateBox.showPopup();

        // after showing the date picker add the date box popup as auto hide partner to the parent popup
        if (m_dateBox.getParentPopup() != null) {
            m_dateBox.getParentPopup().addAutoHidePartner(m_dateBox.getPopup().getElement());
        }
    }

    /**
     * Updates the picker if the user manually modified the date of the text box.<p>
     */
    private void updateDateFromTextBox() {

        Date parsedDate = parseToDate();
        if (parsedDate != null) {
            m_dateBox.getPicker().setValue(parsedDate, true);
        }
        setTime(parsedDate);
        setAmPmFromBox(parsedDate);
        m_dateBox.getTimeErr().setText(null);
        updatePopupCloseBehavior();
    }

    /**
     * Sets the value of the date box.<p>
     * 
     * @param date the new date
     */
    private void updateFromDateTimePicker(Date date) {

        if (date == null) {
            Date tmpDate = new Date();
            m_dateBox.getPicker().setValue(tmpDate, false);
        }
        String timeAsString = getCurrentTimeFieldValue();
        // checkTime(timeAsString);
        date = CmsDateConverter.getDateWithTime(date, timeAsString);
        m_dateBox.getBox().setText(CmsDateConverter.toString(date));
        CmsDateChangeEvent.fireIfNotEqualDates(m_dateBox, m_oldValue, m_dateBox.getValue());
    }
}
