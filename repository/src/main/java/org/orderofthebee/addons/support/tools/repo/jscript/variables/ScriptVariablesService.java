/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 *
 * This file is part of code forked from the alfresco-jscript-extensions project
 * by Jens Goldhammer, which was licensed under the Apache License, Version 2.0.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jscript.variables;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;

/**
 * Expose global properties to JS scripts.
 *
 * Implements {@link Map} so Rhino index access works:
 * {@code globalProperties['db.pool.max']}. The Java {@code get(String)} /
 * {@code get(String, String)} methods remain for existing scripts.
 */
public class ScriptVariablesService extends BaseScopableProcessorExtension implements Map<Object, Object>
{

    private Properties properties;
    private boolean allowWrite;

    public void setProperties(final Properties globalProperties)
    {
        this.properties = globalProperties;
    }

    public void setAllowWrite(final boolean allowWrite)
    {
        this.allowWrite = allowWrite;
    }

    private void assertWriteAllowed()
    {
        if (!this.allowWrite)
        {
            throw new AlfrescoRuntimeException(
                "Writing global properties is disabled. Set ootbee-support-tools.jscript.globalProperties.allowWrite=true to enable.");
        }
    }

    public Object getProperties()
    {
        return new ScriptProperties(this.properties, this.allowWrite);
    }

    /**
     * @param key property name, e.g. {@code db.pool.max}
     * @return property value, or {@code null} if unset
     */
    public String get(final String key)
    {
        return this.properties.getProperty(key);
    }

    /**
     * @param key property name
     * @param otherwise fallback when the property is unset
     * @return property value or the fallback
     */
    public String get(final String key, final String otherwise)
    {
        return this.properties.getProperty(key, otherwise);
    }

    @Override
    public int size()
    {
        return this.properties.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.properties.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key)
    {
        return this.properties.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value)
    {
        return this.properties.containsValue(value);
    }

    @Override
    public Object get(final Object key)
    {
        return this.properties.get(key);
    }

    @Override
    public Object put(final Object key, final Object value)
    {
        assertWriteAllowed();
        return this.properties.put(key, value);
    }

    @Override
    public Object remove(final Object key)
    {
        assertWriteAllowed();
        return this.properties.remove(key);
    }

    @Override
    public void putAll(final Map<? extends Object, ? extends Object> m)
    {
        assertWriteAllowed();
        this.properties.putAll(m);
    }

    @Override
    public void clear()
    {
        assertWriteAllowed();
        this.properties.clear();
    }

    @Override
    public Set<Object> keySet()
    {
        return this.properties.keySet();
    }

    @Override
    public Collection<Object> values()
    {
        return this.properties.values();
    }

    @Override
    public Set<Entry<Object, Object>> entrySet()
    {
        return this.properties.entrySet();
    }

    /**
     * Map-like wrapper so {@code globalProperties.getProperties()['db.pool.max']}
     * also works, while {@code getProperty(String)} stays available.
     */
    public static class ScriptProperties implements Map<Object, Object>
    {
        private final Properties properties;
        private final boolean allowWrite;

        public ScriptProperties(final Properties p, final boolean allowWrite)
        {
            this.properties = p;
            this.allowWrite = allowWrite;
        }

        private void assertWriteAllowed()
        {
            if (!this.allowWrite)
            {
                throw new AlfrescoRuntimeException(
                    "Writing global properties is disabled. Set ootbee-support-tools.jscript.globalProperties.allowWrite=true to enable.");
            }
        }

        public java.util.Enumeration<?> propertyNames()
        {
            return this.properties.propertyNames();
        }

        public String getProperty(final String key)
        {
            return this.properties.getProperty(key);
        }

        @Override
        public String toString()
        {
            return this.properties.toString();
        }

        @Override
        public int size()
        {
            return this.properties.size();
        }

        @Override
        public boolean isEmpty()
        {
            return this.properties.isEmpty();
        }

        @Override
        public boolean containsKey(final Object key)
        {
            return this.properties.containsKey(key);
        }

        @Override
        public boolean containsValue(final Object value)
        {
            return this.properties.containsValue(value);
        }

        @Override
        public Object get(final Object key)
        {
            return this.properties.get(key);
        }

        @Override
        public Object put(final Object key, final Object value)
        {
            assertWriteAllowed();
            return this.properties.put(key, value);
        }

        @Override
        public Object remove(final Object key)
        {
            assertWriteAllowed();
            return this.properties.remove(key);
        }

        @Override
        public void putAll(final Map<? extends Object, ? extends Object> m)
        {
            assertWriteAllowed();
            this.properties.putAll(m);
        }

        @Override
        public void clear()
        {
            assertWriteAllowed();
            this.properties.clear();
        }

        @Override
        public Set<Object> keySet()
        {
            return this.properties.keySet();
        }

        @Override
        public Collection<Object> values()
        {
            return this.properties.values();
        }

        @Override
        public Set<Entry<Object, Object>> entrySet()
        {
            return this.properties.entrySet();
        }
    }
}
