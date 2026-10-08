package src.securestudentvault;
/**
 * Tracks failed login attempts and raises an alarm after a threshold is reached.
 */
/** VNA00-J: shared primitives are accessed under a lock (count) or are volatile (alarm flag). */
final class AttemptCounter {
    private static final int ALARM_THRESHOLD = 3;
    private int count;                          // guarded by "this"
    private volatile boolean alarmRaised;       // written under lock, read without; volatile gives visibility

     /**
     * Increases the failed login count and raises the alarm when the threshold is reached.
     */
    synchronized void increment() {             // read-modify-write => needs the lock, volatile is not enough
        count++;
        if (count >= ALARM_THRESHOLD) {
            alarmRaised = true;
        }
    }
     /**
     * Returns the number of failed login attempts.
     *
     * @return the current failed attempt count
     */
    synchronized int get() {
        return count;
    }

    /**
     * Checks whether the failed login alarm has been raised.
     *
     * @return true if the alarm is raised
     */
    boolean isAlarmRaised() {
        return alarmRaised;
    }
}
