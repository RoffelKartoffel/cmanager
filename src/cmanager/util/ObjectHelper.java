package cmanager.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectHelper
{
    @SuppressWarnings("unchecked")
    public static <T> T copy(T o)
    {
        T result = null;

        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            bos.close();
            byte[] byteData = bos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            result = (T) new ObjectInputStream(bais).readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }


    static boolean areEqual(Object o1, Object o2)
    {
        return !(o1 == null || o2 == null || !o1.equals(o2));
    }

    public static <T> T getBest(T o1, T o2)
    {
        return areEqual(o1, o2) ? o1 : o2;
    }
}
