package contest.tool;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * Created by Timer on 2021/5/10 14:09
 */
public class GenerateData {

    final String path = "./src/main/resources/data/simulation_data.txt";

    final int rows = Const.generate_data_rows;

    final long max_file_size = rows * 45;

    public void generate() throws IOException {
        long time_write = System.currentTimeMillis();

        FileChannel write_channel = new RandomAccessFile(path, "rw").getChannel();
        MappedByteBuffer map = write_channel.map(FileChannel.MapMode.READ_WRITE, 0, max_file_size);
        Random random = new Random();

        map.put(Const.col1_name.getBytes());
        map.putChar(',');
        map.put(Const.col2_name.getBytes());
        map.putChar('\n');

        for (int i = 0; i < rows; i++) {
            map.put(Long.toString(random.nextLong()).getBytes());
            map.putChar(',');
            map.put(Long.toString(random.nextLong()).getBytes());
            map.putChar('\n');
            if (i % 10000 == 0) {
                System.out.println("已生成：" + i + " w条数据");
            }
        }

        write_channel.close();
        System.out.println("生成：" + rows + " 行数据耗时：" + (System.currentTimeMillis() - time_write) + "ms");
    }

    public static void main(String[] args) {
        try {
            GenerateData generateData = new GenerateData();
            generateData.generate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
