/**
 * Copyright 2007 Wei-ju Wu
 *
 * This file is part of TinyUML.
 *
 * TinyUML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.tinyuml.umldraw.structure;

import org.tinyuml.draw.*;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.DrawingContext.FontType;
import org.tinyuml.model.*;
import org.tinyuml.umldraw.shared.UmlNode;

import java.awt.*;
import java.awt.geom.Dimension2D;

/**
 * This class represents the visual view of a UmlComponent. It delegates
 * almost all size related operations to its main compartment, except for
 * the origin.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class ActorElement extends AbstractCompositeNode
        implements LabelSource, UmlNode {

    private static final long serialVersionUID = 6809406266611984432L;
    private static final double MIN_WIDTH = 40;
    private static final double MIN_HEIGHT = 80;
    private static final double MARGIN_TOP = 20;

    private UmlActor actor;
    private Compartment mainCompartment;

    private static ActorElement prototype = new ActorElement();

    /**
     * Returns the prototype instance.
     * @return the prototype instance
     */
    public static ActorElement getPrototype() { return prototype; }

    /**
     * Private constructor.
     */
    private ActorElement() {
        mainCompartment = new Compartment();
        mainCompartment.setParent(this);
        Label mainLabel = new SimpleLabel();
        mainLabel.setSource(this);
        mainLabel.setFontType(FontType.ELEMENT_NAME);
        mainCompartment.addLabel(mainLabel);
        setMinimumSize(MIN_WIDTH, MIN_HEIGHT);
        mainCompartment.setMarginTop(MARGIN_TOP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() {
        ActorElement cloned = (ActorElement) super.clone();
        if (actor != null) {
            cloned.actor = (UmlActor) actor.clone();
        }
        cloned.mainCompartment = (Compartment) mainCompartment.clone();
        cloned.mainCompartment.setParent(cloned);
        cloned.mainCompartment.getLabels().get(0).setSource(cloned);
        return cloned;
    }

    /**
     * Returns the main compartment. Exposed for testing purposes.
     * @return the main compartment
     */
    public Compartment getMainCompartment() { return mainCompartment; }

    /**
     * Sets the main compartment. Just for testing purposes.
     * @param aCompartment a compartment to replace the main compartment
     */
    public void setMainCompartment(Compartment aCompartment) {
        mainCompartment = aCompartment;
    }

    /**
     * Sets the model element.
     * @param aModelElement the model element
     */
    public void setModelElement(UmlActor aModelElement) {
        actor = aModelElement;
    }

    /**
     * {@inheritDoc}
     */
    public UmlModelElement getModelElement() { return actor; }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recalculateSize(DrawingContext drawingContext) {
        mainCompartment.recalculateSize(drawingContext);
        notifyNodeResized();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension2D getMinimumSize() {
        return mainCompartment.getMinimumSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMinimumSize(double width, double height) {
        mainCompartment.setMinimumSize(width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension2D getSize() {
        return mainCompartment.getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSize(double width, double height) {
        mainCompartment.setSize(width, height);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(DrawingContext drawingContext) {
        if (!isValid()) {
            recalculateSize(drawingContext);
        }
        //mainCompartment.draw(drawingContext);
        drawIcon(drawingContext);
    }

    /**
     * {@inheritDoc}
     */
    public String getLabelText() { return getModelElement().getName(); }

    /**
     * {@inheritDoc}
     */
    public void setLabelText(String aText) { getModelElement().setName(aText); }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidate() { mainCompartment.invalidate(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() { return mainCompartment.isValid(); }

    /**
     * Draws the icon.
     * @param drawingContext the DrawingContext
     */
    private void drawIcon(DrawingContext drawingContext) {
        double x = getAbsoluteX1();
        double y = getAbsoluteY1();
        double width = getSize().getWidth();
        double height = getSize().getHeight();

        // larger ellipse
        drawingContext.drawRectangle(x,y+height/2,width,height/2,Color.WHITE);
        drawingContext.drawCircle(x, y, width, height/2);
        drawingContext.drawLabel(getLabelText(),x+getSize().getWidth()/3,y+getSize().getHeight()+10,FontType.DEFAULT);

    }

    /**
     * {@inheritDoc}
     */
    public Label getLabelAt(double mx, double my) {
        return mainCompartment.getLabelAt(mx, my);
    }

    /**
     * {@inheritDoc}
     */
    public boolean acceptsConnection(RelationType associationType,
                                     RelationEndType as, UmlNode with) {
        // Solicitud A: Cuando se hace la verificaci�n de si SOURCE es v�lido
        // se entrega null. Arreglaremos esta cosa aceptando la conexi�n si
        // la RelationEndType (as) es SOURCE
        if(as == RelationEndType.SOURCE) return true;

        // Solicitud A: La herencia s�lo se puede dar entre clases
        if(associationType == RelationType.INHERITANCE)
            return false;

        // Solicitud A: Componentes se pueden relacionar entre s�
        if(with instanceof ActorElement) return true;

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNestable() { return true; }
}
