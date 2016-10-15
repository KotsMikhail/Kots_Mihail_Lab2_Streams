import com.sun.deploy.util.ArrayUtil;

import java.io.IOException;
import java.io.OutputStream;

public class CompressionOutputStream extends OutputStream {

    private static final int BUFFER_SIZE = 4095;
    private static final int WINDOW_SIZE = 4095;
    private static final int WORD_SIZE = 15;

    private OutputStream target;
    private byte[] buffer;
    private int count;

    public CompressionOutputStream(OutputStream target) {
        buffer = new byte[BUFFER_SIZE];
        count = 0;
        this.target = target;
    }

    private String shiftWindow(String searchBuffer) {
        if (searchBuffer.length() > WINDOW_SIZE)
            return searchBuffer.substring(searchBuffer.length() - WINDOW_SIZE, searchBuffer.length());
        else
            return searchBuffer;
    }

    private void emptyBuffer() throws IOException {
        String searchBuffer = "",
                currentWord = "";
        byte[] result = {};
        int length = 0;
        int index;
        int position = 0;
        for (int i = 0; i < count; i++) {
            index = searchBuffer.indexOf(currentWord + (char) buffer[i]);
            if (index != -1 && currentWord.length() < WORD_SIZE) {
                currentWord += (char) buffer[i];
                position = index;
            } else {
                int code = position * (WORD_SIZE + 1) + currentWord.length();
                String codedString = "" + (char) 0 + (char) (code / 256) + (char) (code % 256) + (char) buffer[i];
                if (codedString.length() < currentWord.length()) {
                    System.arraycopy(codedString.getBytes(), 0, result, length, (codedString + (char)buffer[i]).getBytes().length);
                    length += codedString.getBytes().length + 1;
                    result[length] =  buffer[i];
                } else {
                    System.arraycopy(currentWord.getBytes(), 0, result, length, (currentWord + (char)buffer[i]).getBytes().length);
                    length += currentWord.getBytes().length + 1;
                    result[length] =  buffer[i];
                }
                searchBuffer += currentWord + (char) buffer[i];
                currentWord = "";
                position = 0;
            }
            shiftWindow(searchBuffer);
        }
        int code = position * (WORD_SIZE + 1) + currentWord.length();
        String codedString = "" + (char) 0 + (char) code;
        if (codedString.length() < currentWord.length())
            System.arraycopy(codedString.getBytes(), 0, result, length, codedString.getBytes().length);
        else
            System.arraycopy(currentWord.getBytes(), 0, result, length, currentWord.getBytes().length);
        target.write(result);
        count = 0;
    }

    @Override
    public void write(int b) throws IOException {
        if (count >= BUFFER_SIZE)
            emptyBuffer();
        buffer[count++] = (byte) b;
    }

    public void write(byte b[]) throws IOException {
        for (byte c : b) {
            if (count >= BUFFER_SIZE)
                emptyBuffer();
            buffer[count++] = c;
        }
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        for (int i = off; i < off + len; i++) {
            if (count >= BUFFER_SIZE)
                emptyBuffer();
            buffer[count++] = b[i];
        }
    }

    public void flush() throws IOException {
        emptyBuffer();
        target.flush();
    }

}
