import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

class UtveiMonitor {
    private Lenkeliste<String> utveier = new Lenkeliste<String>();
    ReentrantLock laas = new ReentrantLock();
    Condition ikkeTom = laas.newCondition();
    void leggTil(String x) {
        laas.lock();
        utveier.leggTil(x);
        ikkeTom.signal();
        laas.unlock();
    }
    String fjern() {
        try {
            laas.lock();
            while(utveier.stoerrelse() == 0) {
                ikkeTom.await();
            }
            return utveier.fjern();
        } catch (InterruptedException e) {
            return null;
        } finally {
            laas.unlock();
        }
    }
    Lenkeliste<String> hentAlleUtveier() {
        return utveier;
    }
}
