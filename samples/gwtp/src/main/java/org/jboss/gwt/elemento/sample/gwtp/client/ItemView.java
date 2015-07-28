/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.gwt.elemento.sample.gwtp.client;

import com.gwtplatform.mvp.client.ViewImpl;
import elemental.dom.Element;
import elemental.events.Event;
import elemental.events.KeyboardEvent;
import elemental.html.InputElement;
import org.jboss.gwt.elemento.core.DataElement;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.gwt.elemento.core.EventHandler;
import org.jboss.gwt.elemento.core.IsElement;
import org.jboss.gwt.elemento.core.Templated;

import javax.annotation.PostConstruct;

import static elemental.events.KeyboardEvent.KeyCode.ENTER;
import static elemental.events.KeyboardEvent.KeyCode.ESC;
import static org.jboss.gwt.elemento.core.EventType.*;

@Templated("Todo.html#item")
public abstract class ItemView extends ViewImpl implements ItemPresenter.MyView, IsElement {

    static ItemView create(TodoPresenter todoPresenter) {
        return new Templated_ItemView(todoPresenter);
    }

    abstract TodoPresenter todoPresenter();

    @DataElement InputElement toggle;
    @DataElement Element label;
    @DataElement InputElement input;
    boolean escape;

    @PostConstruct
    void init() {
        initWidget(Elements.asWidget(asElement()));
    }

    @Override
    public void setText(final String text) {
        label.setInnerText(text);
    }

    @EventHandler(element = "toggle", on = change)
    void toggle() {
        if (toggle.isChecked()) {
            asElement().getClassList().add("completed");
        } else {
            asElement().getClassList().remove("completed");
        }
        todoPresenter().update();
    }

    @EventHandler(element = "label", on = dblclick)
    void edit() {
        escape = false;
        asElement().getClassList().add("editing");
        input.setValue(label.getInnerText());
        input.focus();
    }

    @EventHandler(element = "destroy", on = click)
    void destroy() {
        asElement().getParentElement().removeChild(asElement());
        todoPresenter().update();
    }

    @EventHandler(element = "input", on = keydown)
    void keyDown(final Event event) {
        KeyboardEvent keyboardEvent = (KeyboardEvent) event;
        if (keyboardEvent.getKeyCode() == ESC) {
            escape = true;
            asElement().getClassList().remove("editing");

        } else if (keyboardEvent.getKeyCode() == ENTER) {
            blur();
        }
    }

    @EventHandler(element = "input", on = blur)
    void blur() {
        String value = input.getValue().trim();
        if (value.length() == 0) {
            destroy();
        } else {
            asElement().getClassList().remove("editing");
            if (!escape) {
                label.setInnerText(value);
            }
        }
    }
}
