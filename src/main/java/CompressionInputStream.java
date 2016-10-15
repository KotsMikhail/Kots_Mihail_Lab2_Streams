import java.io.IOException;
import java.io.InputStream;

public class CompressionInputStream extends InputStream {

    private static final int BUFFER_SIZE = 4095;
    private static final int WINDOW_SIZE = 4095;
    private static final int WORD_SIZE = 15;

    private InputStream source;
    private byte[] buffer;
    private int count;


    public CompressionInputStream(InputStream source) {
        buffer = new byte[BUFFER_SIZE];
        count = 0;
        this.source = source;
    }

    private String shiftWindow(String searchBuffer) {
        if (searchBuffer.length() > WINDOW_SIZE)
            return searchBuffer.substring(searchBuffer.length() - WINDOW_SIZE, searchBuffer.length());
        else
            return searchBuffer;
    }

    private void refillBuffer() throws IOException {
        String searchBuffer = "",
                result = "";
        byte c;
        while (((c = (byte) source.read()) != -1) && (result.length() < BUFFER_SIZE - WORD_SIZE)) {
            if (c != 0) {
                result += (char) c;
                searchBuffer += (char) c;
            } else {
                int a = source.read();
                if (a < 0)
                    a += 256;
                int b = source.read();
                if (b < 0)
                    b += 256;
                int place = (a * 256 + b) / (WORD_SIZE + 1);
                int size = (a * 256 + b) % (WORD_SIZE + 1);
                result += searchBuffer.substring(place, place + size);
                searchBuffer += searchBuffer.substring(place, place + size);
            }
            searchBuffer = shiftWindow(searchBuffer);
        }
        count = result.length();
        buffer = result.getBytes().clone();
    }

    @Override
    public int read() throws IOException {
        if (count == 0)
            refillBuffer();
        if (count == 0)
            return -1;
        byte c = buffer[count - 1];
        count--;
        return c;
    }

    @Override
    public int read(byte b[]) throws IOException {
        int length = 0,
                max_count = count;
        for (int i = 0; i < b.length; i++) {
            if (count == 0) {
                refillBuffer();
                max_count = count;
            }
            if (count == 0)
                break;
            count--;
            b[i] = buffer[max_count - count - 1];
            length++;
        }
        if (length > 0)
            return length;
        else
            return -1;
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        int length = 0,
                max_count = count;
        for (int i = off; i < off + len - 1; i++) {
            if (count == 0) {
                refillBuffer();
                max_count = count;
            }
            if (count == 0)
                break;
            count--;
            b[i] = buffer[max_count - count - 1];
            length++;
        }
        if (length > 0)
            return length;
        else
            return -1;
    }

}
