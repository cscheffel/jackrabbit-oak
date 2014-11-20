/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.jackrabbit.oak.util;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.apache.jackrabbit.oak.api.jmx.CheckpointMBean;
import org.apache.jackrabbit.oak.plugins.segment.SegmentCheckpointMBean;

/**
 * Abstract base class for {@code CheckpointMBean} implementations.
 * This class provides the basic functionality for converting checkpoints
 * into tabular data.
 */
public abstract class AbstractCheckpointMBean implements CheckpointMBean {
    private static final String[] FIELD_NAMES = new String[] { "id", "created", "expires"};
    private static final String[] FIELD_DESCRIPTIONS = FIELD_NAMES;

    @SuppressWarnings("rawtypes")
    private static final OpenType[] FIELD_TYPES = new OpenType[] {
            SimpleType.STRING, SimpleType.STRING, SimpleType.STRING };

    private static final CompositeType TYPE = createCompositeType();

    private static CompositeType createCompositeType() {
        try {
            return new CompositeType(SegmentCheckpointMBean.class.getName(),
                    "Checkpoints", FIELD_NAMES, FIELD_DESCRIPTIONS, FIELD_TYPES);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Called to collect the tabular data for the checkpoints.
     * Each checkpoint should be represented by a single row in {@code tab}.
     * Implementors should use the {@link #toCompositeData} utility method for converting
     * the individual fields associated with a checkpoint into the correct composite data
     * format.
     *
     * @param tab
     * @throws OpenDataException
     */
    protected abstract void collectCheckpoints(TabularDataSupport tab) throws OpenDataException;

    @Override
    public TabularData listCheckpoints() {
        try {
            TabularDataSupport tab = new TabularDataSupport(
                    new TabularType(SegmentCheckpointMBean.class.getName(),
                            "Checkpoints", TYPE, new String[] { "id" }));

            collectCheckpoints(tab);

            return tab;
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Utility method for converting the fields associated with a checkpoint to the
     * composite data format.
     *
     * @param id        id of the checkpoint
     * @param created   creation data of the checkpoint
     * @param expires   expiry data of the checkpoint
     * @return          composite data representation of the fields associated with the
     *                  checkpoint
     * @throws OpenDataException
     */
    protected static CompositeDataSupport toCompositeData(String id, String created, String expires)
            throws OpenDataException {
        return new CompositeDataSupport(TYPE, FIELD_NAMES, new String[] { id, created, expires });
    }

}
