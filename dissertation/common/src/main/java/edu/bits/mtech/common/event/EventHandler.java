/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.common.event;

import edu.bits.mtech.common.bo.Event;

/**
 * Event handler for handling events.
 *
 * @author Tushar Phadke
 */
public interface EventHandler {

    void handleEvent(Event event);
}
