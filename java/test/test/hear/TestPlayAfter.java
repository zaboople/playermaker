package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

/**
 * 3 players all coming in/out right at each other's beats using Player.getEndTime().
 */
public class TestPlayAfter implements XTest {
    public static void main(String args[]) throws Exception {
        new TestPlayAfter().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
        Player player1=new Player(0)
            .setBeatsPerMinute(60)
            .instrument("Piano - Honky-tonk")
            .setBendSensitivity(4)
            .octave(4)
            .r(4);
        Player player2=new Player(3)
            .instrument("Organ - Harmonica")
            .setBendSensitivity(4)
            .octave(3);
        Player player3=new Player(6)
            .instrument("Square Wave")
            .setBendSensitivity(4)
            .octave(4);

        // First note:
        player1
            .p(4, D);

        // Next notes, same time:
        player2.setStart(player1.getEndTime())
            .p(4, E);
        player1
            .p(4, G);

        // Next notes:
        player3.setStart(player2.getEndTime())
            .p(8, B)
            .p(8, B_)
            .p(4, A)
            ;

        // Plays at the same time as Player 3's first
        // two notes, finishing right before Player 3's last note.
        player1
            .octave(6)
            .p(32, B)
            .p(32, B_)
            .p(32, B_)
            .p(32, A)
            .p(32, A_)
            .p(32, A)
            .p(32, A_)
            .p(32, A)
            ;

        // This should coincide with Player 3's last note.
        player2
            .r(player1.getEndTime() - player2.getEndTime())//tricky
            .p(4, F);
        player1.r(4);
        player2.r(4);
        player3.r(4);
        midi.play(stop, player1, player2, player3);
    }

}
