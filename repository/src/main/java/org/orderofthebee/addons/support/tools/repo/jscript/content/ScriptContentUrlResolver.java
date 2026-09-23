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
package org.orderofthebee.addons.support.tools.repo.jscript.content;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyCheck;
import org.springframework.beans.factory.InitializingBean;

/**
 * Resolves the content store URL of a node property. Defaults to {@code cm:content}.
 */
public class ScriptContentUrlResolver extends BaseScopableProcessorExtension implements InitializingBean
{

    private ContentService contentService;
    private NamespaceService namespaceService;

    public void setContentService(final ContentService contentService)
    {
        this.contentService = contentService;
    }

    public void setNamespaceService(final NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public String getContentUrl(final String nodeRef)
    {
        return getContentUrl(nodeRef, null);
    }

    /**
     * @param nodeRef node reference string
     * @param propertyName short or full QName, or {@code null} for {@code cm:content}
     */
    public String getContentUrl(final String nodeRef, final String propertyName)
    {
        ParameterCheck.mandatoryString("nodeRef", nodeRef);
        final QName property = propertyName == null || propertyName.length() == 0
                               ? ContentModel.PROP_CONTENT
                               : QName.resolveToQName(this.namespaceService, propertyName);
        if (property == null)
        {
            throw new AlfrescoRuntimeException("Could not resolve content property " + propertyName);
        }
        final ContentReader reader = this.contentService.getReader(new NodeRef(nodeRef), property);
        if (reader == null)
        {
            throw new AlfrescoRuntimeException("No content for " + nodeRef + " property " + property);
        }
        return reader.getContentUrl();
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "contentService", this.contentService);
        PropertyCheck.mandatory(this, "namespaceService", this.namespaceService);
    }

}
