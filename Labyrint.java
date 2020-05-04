import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Labyrint {
    protected static boolean d = false;
    protected final int K;
    protected final int R;
    protected Rute[][] a;
    private Labyrint(Rute[][] ruter, int kolonner, int rader) {
        K = kolonner;
        R = rader;
        a = ruter;
    }
    public static void gjorDetaljert() {
        d = true;
    }
    static Labyrint lesFraFil(File fil) throws FileNotFoundException {
        Scanner sc;
        try {
            sc = new Scanner(fil);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        }
        Scanner tall = new Scanner(sc.nextLine());
        int rader = tall.nextInt();
        int kolonner = tall.nextInt();
        tall.close();
        Rute[][] ruter = new Rute[rader][kolonner];
        int i = 0;
        while (sc.hasNextLine()) {
            String l = sc.nextLine();
            for (int j = 0; j < l.length(); j++) {
                char x = l.charAt(j);
                if (d) {
                    System.out.print("Ny rute på ("+j+", "+i+"): "+x);
                }
                if (x == ".".charAt(0)) {
                    if (erAapning(rader,kolonner,i,j)) {
                        ruter[i][j] = new Aapning(i,j);
                        if (d) {
                            System.out.println(" - er en åpning.");
                        }
                    } else {
                        ruter[i][j] = new HvitRute(i,j);
                        if (d) {
                            System.out.println(" - er hvit.");
                        }
                    }
                } else if (x == "#".charAt(0)) {
                    ruter[i][j] = new SortRute(i,j);
                    if (d) {
                        System.out.println(" - er sort.");
                    }
                } else {
                    System.out.println("\nUgyldig tegn på ("+j+", "+i+"): "+x);
                    if (d) {
                        System.out.println("Avslutter Java...");
                    }
                    System.exit(0); //hva er den beste måten å avslutte en metode som ikke er void?
                }
            }
            i++;
        }
        sc.close();
        for (int y = 0; y < kolonner; y++) {
            for (int x = 0; x < rader; x++) {
                Rute n = ruter[x][y];
                try {
                    n.settNabo(ruter[x][y-1], 0); //nord
                } finally {
                    try {
                        n.settNabo(ruter[x][y+1], 3); //syd
                    } finally {
                        try {
                            n.settNabo(ruter[x-1][y], 2); //vest
                        } finally {
                            try {
                                n.settNabo(ruter[x+1][y], 1); //ost
                            } finally {
                                continue;
                            }
                        }
                    }
                }
            }
        }
        Labyrint l = new Labyrint(ruter,kolonner,rader);
        if (d) {
            System.out.println("Resulterende labyrint: \n"+l.toString());
        }
        return l;
    }
    private static boolean erAapning(int r, int k, int x, int y) {
        if (d) {
            System.out.println("\nSer på koordinatene:");
            System.out.println(x+" == 0 eller "+(k-1)+", "+y+" == 0 eller "+(r-1));
        }
        return (x == 0 || x == k-1) || (y == 0 || y == r-1);
    }
    public Liste<String> finnUtveiFra(int kol, int rad) {
        if (d) {
            System.out.println("Kjører finnUtveiFra("+kol+", "+rad+")...");
        }
        a[rad][kol].finnUtvei();
        return a[rad][kol].hentUtveier();
    }
    @Override
    public String toString() {
        String str = R + " " + K;
        for (Rute[] k : a) {
            str += "\n";
            for (Rute r : k) {
                str += r.tilTegn();
            }
        }
        return str;
    }
}
