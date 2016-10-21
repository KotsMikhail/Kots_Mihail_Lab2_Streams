import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CompressionInputStream extends InputStream {

    private static final int BUFFER_SIZE = 4095;
    private static final int WORD_SIZE = 15;

    private InputStream source;
    private byte[] buffer;
    private int length;


    public CompressionInputStream(InputStream source) {
        buffer = new byte[BUFFER_SIZE];
        length = 0;
        this.source = source;
    }

    private void refillBuffer() throws IOException {
        ArrayList<Byte> result = new ArrayList<>();
        byte c;
        while ((result.size() < BUFFER_SIZE) && ((c = (byte) source.read()) != -1)) {
            if (c != 0) {
                result.add(c);
            } else {
                int a = source.read();
                if (a < 0)
                    a += 256;
                int b = source.read();
                if (b < 0)
                    b += 256;
                int place = (a * 256 + b) / (WORD_SIZE + 1);
                int size = (a * 256 + b) % (WORD_SIZE + 1);
                result.addAll(result.subList(place, place + size));
            }
        }
        length = result.size();
        int i = 0;
        for (Byte b : result)
            buffer[i++] = b;
    }

    @Override
    public int read() throws IOException {
        if (length == 0)
            refillBuffer();
        if (length == 0)
            return -1;
        byte c = buffer[length - 1];
        length--;
        return c;
    }

    @Override
    public int read(byte b[]) throws IOException {
        int count = 0;
        int max_length = length;
        for (int i = 0; i < b.length; i++) {
            if (length == 0) {
                refillBuffer();
                max_length = length;
            }
            if (length == 0)
                break;
            length--;
            b[i] = buffer[max_length - length - 1];
            count++;
        }
        if (count > 0)
            return count;
        else
            return -1;
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        int count = 0;
        int max_length = length;
        for (int i = off; i < off + len - 1; i++) {
            if (length == 0) {
                refillBuffer();
                max_length = length;
            }
            if (length == 0)
                break;
            length--;
            b[i] = buffer[max_length - length - 1];
            count++;
        }
        if (count > 0)
            return count;
        else
            return -1;
    }

}
