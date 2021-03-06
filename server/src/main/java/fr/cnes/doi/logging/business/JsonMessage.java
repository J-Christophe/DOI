/*
 * Copyright (C) 2017-2019 Centre National d'Etudes Spatiales (CNES).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package fr.cnes.doi.logging.business;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Converts an Object to a JSON String.
 */
public class JsonMessage implements Message {

    /**
     * version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * MAPPER.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();
    /**
     * object to map.
     */
    private final Object object;

    /**
     * Constructs a JsonMessage.
     *
     * @param object the Object to serialize.
     */
    public JsonMessage(final Object object) {
        this.object = object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedMessage() {
        String result;
        try {
            result = MAPPER.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            StatusLogger.getLogger().catching(e);
            result = object.toString();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormat() {
        return object.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getParameters() {
        return new Object[]{object};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throwable getThrowable() {
        return null;
    }
}
