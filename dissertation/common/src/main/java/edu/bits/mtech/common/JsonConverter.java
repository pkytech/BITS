/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.common;

public interface JsonConverter {

    <T> T deserialize(Class<T> type, String message);
    String serialize(Object data);
}
