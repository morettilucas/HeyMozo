package ar.edu.utn.frsf.isi.dam.del2016.heymozo.imagenes;
 
import java.io.InputStream;
import java.io.OutputStream;
 
class Utils {
    static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ignored){}
    }
}