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
/**
 *
 */
package org.orderofthebee.addons.support.tools.repo.jscript.transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper around an Alfresco {@code UserTransaction} that works on both
 * {@code javax.transaction} (ACS 5–7) and {@code jakarta.transaction} (ACS 23+).
 *
 * @author jgoldhammer
 */
public class ScriptTransaction
{

    private final Object userTransaction;

    public ScriptTransaction(final Object userTransaction)
    {
        this.userTransaction = userTransaction;
    }

    /**
     * Begin a new user transaction.
     */
    public void begin()
    {
        this.invoke("begin");
    }

    /**
     * Commit a user transaction.
     */
    public void commit()
    {
        this.invoke("commit");
    }

    /**
     * Roll back a user transaction.
     */
    public void rollback()
    {
        this.invoke("rollback");
    }

    /**
     * @return transaction status value (see {@code javax.transaction.Status} /
     *         {@code jakarta.transaction.Status})
     */
    public int getStatus()
    {
        return ((Integer) this.invoke("getStatus")).intValue();
    }

    private Object invoke(final String methodName)
    {
        try
        {
            final Method method = this.userTransaction.getClass().getMethod(methodName);
            return method.invoke(this.userTransaction);
        }
        catch (final InvocationTargetException ex)
        {
            final Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException)
            {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error)
            {
                throw (Error) cause;
            }
            throw new IllegalStateException(cause);
        }
        catch (final ReflectiveOperationException ex)
        {
            throw new IllegalStateException("Unable to call transaction method " + methodName, ex);
        }
    }

}
