package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestDelay implements XTest {
    public static void main(String args[]) throws Exception {
        new TestDelay().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
        int delay = 48;
        midi.play(stop,
            doit(0, "Strings - Viola"),
            doit(delay, "Strings - Viola"),
            doit(2*delay, "Strings - Viola"),
            doit(3*delay, "Strings - Viola"),
            doit(4*delay, "Strings - Viola")
        );
    }
    int channel=0;
    private Player doit(long delay, String instru) {
        return new Player(channel++)
            .setStart(delay)
            .setBeatsPerMinute(90)
            .setReverb(0)
            .instrument(instru)
            .octave(4)
            .r(4)

            .p(8, C)
            .c(4, D_).bend(8, 2).vibrato(8, 64., 8).up()

            .p(8, B)
            .octave(5)
            .p(4, B_, D_-12)

            .octave(6)
            .p(8, D_)
            .p(4, E_, G)

            .p(8, B-12)
            .c(8, C).bend(16, 4).bend(16, -4).up()
            .octave(5)
            .p(8, E_)
            .octave(4)
            .p(8, C)

            .p(8, B_, B_+3, B_+5)
            .p(8, B_+2, B_+4, B_+5)
            .octave(3)
            .r(8)
            .p(8, C)

            .p(8, B_)
            //.octave(2)
            .p(8, E_, G, E_+12)
            .p(8, E_, E_+12)
            .r(4);
    }

}
