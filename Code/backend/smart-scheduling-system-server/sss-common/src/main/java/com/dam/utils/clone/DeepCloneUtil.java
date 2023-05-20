package com.dam.common.util;

import java.io.*;

public class DeepCloneUtil {
	
	public static Object deepClone(Object srcObject){
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		Object result = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			//将对象写到流里
			oos.writeObject(srcObject);
			//从流里读回来
			bis = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bis);
			result = ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
				oos.close();
				bis.close();
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
