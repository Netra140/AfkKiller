package AfkKiller;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
//TODO Stolen code - https://stackoverflow.com/questions/18037576/how-do-i-check-if-the-user-is-pressing-a-key
public class IsKeyPressed {
    private static volatile boolean qPressed = false;
    public static boolean isQPressed() {
        synchronized (IsKeyPressed.class) {
            return qPressed;
        }
    }

    /*public static void main(String[] args) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (IsKeyPressed.class) {
                    switch (ke.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        if (ke.getKeyCode() == KeyEvent.VK_Q) {
                            qPressed = true;
                        }
                        break;

                    case KeyEvent.KEY_RELEASED:
                        if (ke.getKeyCode() == KeyEvent.VK_Q) {
                            qPressed = false;
                        }
                        break;
                    }
                    return false;
                }
            }
        });
    }*/
}