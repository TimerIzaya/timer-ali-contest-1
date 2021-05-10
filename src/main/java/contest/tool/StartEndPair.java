package contest.tool;

import java.util.Objects;

/**
 * Created by Timer on 2020/11/10 21:33
 */
public class StartEndPair {
    public int start;
    public int end;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StartEndPair that = (StartEndPair) o;
        return start == that.start &&
                end == that.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}