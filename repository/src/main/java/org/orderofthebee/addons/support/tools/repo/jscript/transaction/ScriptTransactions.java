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

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptMethodType;

/**
 * script object for handling the de.jgoldhammer.alfresco.jscript.transaction service.
 *
 * @author Jens Goldhammer (fme AG)
 */

@ScriptClass(types=ScriptClassType.JavaScriptRootObject, code= "transaction", help="the root object for the transactionservice")
public class ScriptTransactions extends BaseProcessorExtension {

	private Object transactionService;

    public void setTransactionService(Object transactionService) {
		this.transactionService = transactionService;
	}

    @ScriptMethod(
    		help="get a new user transaction object- the transaction is not started yet. Please execute begin, commit, rollback and getStatus on the transaction.",
    		output="void",
    		code="de.jgoldhammer.alfresco.jscript.transaction.getUserTransaction()",
    		type=ScriptMethodType.WRITE)
    public ScriptTransaction getUserTransaction(){
    	return new ScriptTransaction(invoke("getUserTransaction"));
    }
    
    public boolean isReadOnly(){
    	return !((Boolean) invoke("getAllowWrite")).booleanValue();
    }

    private Object invoke(String methodName) {
        try {
            Method method = transactionService.getClass().getMethod(methodName);
            return method.invoke(transactionService);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException(cause);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Unable to call transaction service method " + methodName, ex);
        }
    }
    
    
}
