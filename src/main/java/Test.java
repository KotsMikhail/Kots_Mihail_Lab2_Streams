import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        try {
            int size = 10;
            final byte[] arr = {1,1,1,1,1,1,1,1,1,1};
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            CompressionOutputStream cos = new CompressionOutputStream(bos);
            cos.write(arr);
            cos.close();
            System.out.println(arr.length);
            System.out.println(bos.toByteArray().length);

            CompressionInputStream cin = new CompressionInputStream(new ByteArrayInputStream(bos.toByteArray()));
            final byte[] result = new byte[size];
            System.out.println(cin.read(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
