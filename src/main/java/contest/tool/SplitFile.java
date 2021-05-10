package contest.tool;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Timer on 2020/11/15 14:55
 */
public class SplitFile {

    private Set<StartEndPair> startEndPairs;

    private RandomAccessFile rAccessFile;

    private int fileLength;

    private String path;

    private int threadSize;

    public SplitFile(String path, int threadSize) throws IOException {
        long time = System.currentTimeMillis();
        startEndPairs = new HashSet<StartEndPair>();
        this.path = path;
        this.threadSize = threadSize;
        File file = new File(path);
        this.fileLength = (int) file.length();
        this.rAccessFile = new RandomAccessFile(file, "r");
        int everySize = this.fileLength / threadSize;
        calculateStartEnd(0, everySize);
        System.out.println("分割：" + threadSize + " 个文件下标用时：" + (System.currentTimeMillis() - time));
    }

    public Set<StartEndPair> getPairs() throws IOException {
        return startEndPairs;
    }

    private void calculateStartEnd(int start, int size) throws IOException {
        if (start > fileLength - 1) {
            return;
        }
        StartEndPair pair = new StartEndPair();
        pair.start = start;
        int endPosition = start + size - 1;
        if (endPosition >= fileLength - 1) {
            pair.end = fileLength - 1;
            startEndPairs.add(pair);
            return;
        }

        rAccessFile.seek(endPosition);
        byte tmp = (byte) rAccessFile.read();
        while (tmp != '\n') {
            endPosition++;
            if (endPosition >= fileLength - 1) {
                endPosition = fileLength - 1;
                break;
            }
            rAccessFile.seek(endPosition);
            tmp = (byte) rAccessFile.read();
        }
        endPosition++;
        pair.end = endPosition;
        startEndPairs.add(pair);
        calculateStartEnd(endPosition + 1, size);
    }

    public void splitIntoFiles() throws IOException {
        long time = System.currentTimeMillis();
        Set<StartEndPair> pairs = getPairs();
        int count = 0;
        for (StartEndPair pair : pairs) {
            FileChannel read_Channel = FileChannel.open(Paths.get(path));
            FileChannel write_channel = new RandomAccessFile(Const.data_root_dir + "/split_" + count + ".txt", "rw").getChannel();
            count++;
            MappedByteBuffer read_byteBuffer = read_Channel.map(FileChannel.MapMode.READ_ONLY, pair.start, pair.end - pair.start + 1);
            MappedByteBuffer write_byteBuffer = write_channel.map(FileChannel.MapMode.READ_WRITE, 0, pair.end - pair.start + 1);
            write_byteBuffer.put(read_byteBuffer);
            read_Channel.close();
            write_channel.close();
            System.out.println(count);
        }
        System.out.println("分割：" + threadSize + " 个文件用时：" + (System.currentTimeMillis() - time));
    }
}
