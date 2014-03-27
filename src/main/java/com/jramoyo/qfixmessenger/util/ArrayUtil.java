/*
 * Copyright (c) 2011, Jan Amoyo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 *
 * - Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer 
 *   in the documentation and/or other materials provided with the
 *   distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 *
 * ArrayUtil.java
 * 8 Jun 2011
 */
package com.jramoyo.qfixmessenger.util;

/**
 * General utility for Arrays
 * 
 * @author jamoyo
 */
public class ArrayUtil
{
	public static boolean isNullOrEmpty(byte[] array)
	{
		if (array == null || array.length <= 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static boolean isNullOrEmpty(char[] array)
	{
		if (array == null || array.length <= 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static boolean isNullOrEmpty(int[] array)
	{
		if (array == null || array.length <= 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static boolean isNullOrEmpty(short[] array)
	{
		if (array == null || array.length <= 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static boolean isNullOrEmpty(long[] array)
	{
		if (array == null || array.length <= 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static boolean isNullOrEmpty(float[] array)
	{
		if (array == null || array.length <= 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static boolean isNullOrEmpty(double[] array)
	{
		if (array == null || array.length <= 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static boolean isNullOrEmpty(Object[] array)
	{
		if (array == null || array.length <= 0)
		{
			return true;
		} else
		{
			return false;
		}
	}
}
