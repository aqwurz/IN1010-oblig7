import java.util.Scanner;

abstract class Rute {
    protected static boolean d = false;
    protected final int KOLONNE;
    protected final int RAD;
    protected Rute nord;
    protected Rute syd;
    protected Rute vest;
    protected Rute ost;
    protected Rute[] naboer = new Rute[4]; // 0:nord - 1:øst - 2:vest - 3:syd
    protected static UtveiMonitor utveier = new UtveiMonitor();
    public Rute(int x, int y) {
        KOLONNE = y;
        RAD = x;
    }
    public static void gjorDetaljert() {
        d = true;
    }
    public void settNabo(Rute r, int retning) {
        naboer[retning] = r;
    }
    protected void gaa(int fra, String gaastr) throws InterruptedException {
        if (d) {
            System.out.println("Kjører gaa("+fra+", gaastr)... Nåværende rute: "+"("+KOLONNE+", "+RAD+")");
        }
        Scanner sc = new Scanner(gaastr).useDelimiter("\\s*-->\\s*"); // deler opp gaastr etter " --> "
        boolean erTraversert = false;
        while (sc.hasNext()) {
            if (sc.next().equals("("+KOLONNE+", "+RAD+")")) { // dersom en av rutene er lik nåværende, har vi en sykel
                erTraversert = true;
                if (d) {
                    System.out.println("Fant sykel: "+gaastr+" --> "+"("+KOLONNE+", "+RAD+")");
                }
                return;
            }
        }
        gaastr += " --> " + "("+KOLONNE+", "+RAD+")";
        try {
            boolean erBlindvei = true;
            Thread[] trader = new Thread[4];
            boolean forsteKall = true;
            int forsteKallNr = 0;
            for (int i = 0; i < 4; i++) {
                if (naboer[i].tilTegn() == ".".charAt(0) && fra != i) {
                    erBlindvei = false;
                    if (forsteKall) { // gjør at 1. kall på gaa er i den gamle tråden
                        forsteKall = false;
                        forsteKallNr = i;
                        // tar vare på første kall av gaa, sånn at det kjøres etter at alle trådene er satt i gang
                        // SVAR PÅ SPØRSMÅL: Hvis man ikke gjør dette, vil koden kjøre dette første gaa-kallet før 
                        // trådene settes i gang, noe som vil medføre at kallet ikke kjøres parallelt med de andre
                        // kallene, og koden vil kjøre saktere enn med full parallelitet.
                    } else {
                        final int I = i;
                        final String GAASTR = gaastr;
                        trader[i] = new Thread(){ public void run() {
                            try {
                                naboer[I].gaa(3-I,GAASTR); // 3-0=3:syd - 3-1=2:vest - 3-2=1:øst - 3-3=0:syd
                            } catch (InterruptedException e) {

                            }
                        }};
                    }
                }
            }
            for (int i = 0; i < 4; i++) {
                if (trader[i] != null) {
                    trader[i].start();
                }
            }
            if (!forsteKall)
                naboer[forsteKallNr].gaa(3-forsteKallNr,gaastr);
            for (int i = 0; i < 4; i++) {
                if (trader[i] != null) {
                    trader[i].join();
                }
            }
            if (erBlindvei && d) {
                System.out.println("Fant blindvei: "+gaastr);
            }
        } catch (NullPointerException e) { // hvis en nabo ikke finnes, har vi funnet en åpning
            if (d) {
                System.out.println("Fant utvei: "+gaastr);
            }
            utveier.leggTil(gaastr);
        }
    }
    public void finnUtvei() {
        if (d) {
            System.out.println("Kjører finnUtvei()...");
        }
        utveier = new UtveiMonitor();
        try {
            int n = 0;
            for (int i = 0; i < 4; i++) {
                if (naboer[i].tilTegn() == ".".charAt(0)) {
                    n++;
                }
            }
            Thread[] trader = new Thread[4];
            for (int i = 0; i < 4; i++ ) {
                final int I = i;
                if (naboer[i].tilTegn() == ".".charAt(0)) {
                    trader[i] = new Thread(){ public void run(){
                        try {
                            naboer[I].gaa(3-I,"("+KOLONNE+", "+RAD+")");
                        } catch(InterruptedException e) {

                        }
                    }};
                }
            }
            for (int i = 0; i < 4; i++) {
                if (trader[i] != null) {
                    trader[i].start();
                }
            }
            for (int i = 0; i < 4; i++) {
                if (trader[i] != null) {
                    try {
                        trader[i].join();
                    } catch (InterruptedException e) {

                    }
                }
            }
        } catch (NullPointerException e) { // hvis en nabo ikke finnes, er angitt rute en åpning
            if (d) {
                System.out.println("Angitt rute er en åpning.");
            }
            utveier.leggTil("("+KOLONNE+", "+RAD+")");
        }
    }
    public Lenkeliste<String> hentUtveier() {
        if (d) {
            System.out.println("Fant disse utveiene:");
        }
        return utveier.hentAlleUtveier();
    }
    abstract public char tilTegn();
    @Override
    public String toString() {
        return "("+KOLONNE+", "+RAD+")";
    }
}
