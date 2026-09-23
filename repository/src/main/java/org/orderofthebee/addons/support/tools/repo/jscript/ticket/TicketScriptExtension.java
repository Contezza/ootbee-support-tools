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
 *
 * TicketScriptExtension includes work originally Copyright (C) 2007-2011
 * BlueXML (www.bluexml.com), licensed under the GNU General Public License v3
 * as included in that upstream project.
 */

package org.orderofthebee.addons.support.tools.repo.jscript.ticket;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.mozilla.javascript.Context;

/**
 * retrieves the ticket for the current user.
 */
public class TicketScriptExtension extends BaseScopableProcessorExtension
{

    private AuthenticationService authenticationService;

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public Object getCurrentTicket()
    {
        String ticket = authenticationService.getCurrentTicket();
        return Context.getCurrentContext().newObject(getScope(), "String", new Object[] { ticket });
    }
}