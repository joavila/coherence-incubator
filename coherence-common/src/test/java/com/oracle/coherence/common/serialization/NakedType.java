/*
 * File: NakedType.java
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * The contents of this file are subject to the terms and conditions of 
 * the Common Development and Distribution License 1.0 (the "License").
 *
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License by consulting the LICENSE.txt file
 * distributed with this file, or by consulting https://oss.oracle.com/licenses/CDDL
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file LICENSE.txt.
 *
 * MODIFICATIONS:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 */

package com.oracle.coherence.common.serialization;

import junit.framework.Assert;

/**
 * Class description
 *
 * @version        Enter version here..., 12/08/20
 * @author         Enter your name here...
 */
public class NakedType
{
    private int    bb;
    private String second;
    private int    aa;


    /**
     * Method description
     */
    public void init()
    {
        aa     = 43690;
        bb     = 768955;
        second = "AAABBBCCC";
    }


    /**
     * Method description
     */
    public void verify()
    {
        Assert.assertEquals(aa, 43690);
        Assert.assertEquals(bb, 768955);
        Assert.assertEquals(second, "AAABBBCCC");
    }


    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode()
    {
        final int prime  = 31;
        int       result = 1;

        result = prime * result + aa;
        result = prime * result + bb;
        result = prime * result + ((second == null) ? 0 : second.hashCode());

        return result;
    }


    /**
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof NakedType))
        {
            return false;
        }

        NakedType other = (NakedType) obj;

        if (aa != other.aa)
        {
            return false;
        }

        if (bb != other.bb)
        {
            return false;
        }

        if (second == null)
        {
            if (other.second != null)
            {
                return false;
            }
        }
        else if (!second.equals(other.second))
        {
            return false;
        }

        return true;
    }


    /**
     * @{inheritDoc
     */
    @Override
    public String toString()
    {
        return "NakedType [bb=" + bb + ", second=" + second + ", aa=" + aa + "]";
    }
}
